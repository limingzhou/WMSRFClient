package com.labor.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import com.labor.job.config.JobConfiguration;
import com.labor.job.constant.JobConstant;
import com.labor.job.helper.JobDefaultServiceHelper;
import com.labor.job.util.JobUtil;

public abstract class JobSchedulerCenter implements InitializingBean, JobScheduler{
	
	private boolean autoRun = true;
	private SchedulerFactory schedulerFactory;
	private AtomicBoolean isInit = new AtomicBoolean(true);
	private Map<String, Object> dependencyServicesMap = new HashMap<String, Object>();
	private List<JobConfiguration> jobConfigurations = new ArrayList<JobConfiguration>();
	private Map<String, JobDefaultServiceHelper> defaultServiceHelperMap;

	private final void initialize() throws SchedulerException {
		if (isInit.get()) {
			if(null == schedulerFactory){
				setSchedulerFactory((SchedulerFactory) BeanUtils.instantiateClass(StdSchedulerFactory.class));
			}
			if (autoRun) {
				runScheduler();
			}
			isInit.set(Boolean.FALSE);
		}
	}
	
	private void runScheduler() throws SchedulerException {
		this.preConfigureScheduler(); 
		Scheduler scheduler = getJobScheduler(); 
		if(null != scheduler){
			boolean started = scheduler.isStarted();
			if (!started) {
				scheduler.start(); 
			}
		}
	}
	
	private Scheduler getJobScheduler() throws SchedulerException {
		Scheduler scheduler = getCurrentScheduler();
		if(!CollectionUtils.isEmpty(jobConfigurations)){
			List<JobConfiguration> configurations = new ArrayList<JobConfiguration>();
			for (JobConfiguration configuration : jobConfigurations) {
				boolean exists = scheduler.checkExists(configuration.getJobKey());
				if (!exists) {
					configurations.add(configuration);
				}
			}
			scheduleJobWithTrigger(configurations);
		}
		return scheduler;
	}
	
	private void scheduleJobWithTrigger(List<JobConfiguration> jobConfigurations) throws SchedulerException {
		if(!CollectionUtils.isEmpty(jobConfigurations)){
			Map<JobDetail, List<Trigger>> triggersAndJobs = new HashMap<JobDetail, List<Trigger>>(); // 触发器和工作关系
			for (JobConfiguration configuration : jobConfigurations) {
				JobKey jobKey = JobUtil.createJobKey(configuration);
				JobDetail job = JobBuilder.newJob(configuration.getBizJobclass()).withIdentity(jobKey).build();
				if (!CollectionUtils.isEmpty(configuration.getTiggerMap())) {
					Trigger trigger = null;
					TriggerKey triggerKey = null;
					CronScheduleBuilder scheduleBuilder = null;
					List<Trigger> triggers = new ArrayList<Trigger>();
					for (String cronKey : configuration.getTiggerMap().keySet()) {
						triggerKey = JobUtil.createTriggerKey(cronKey, configuration.getJobGroup());
						scheduleBuilder = CronScheduleBuilder.cronSchedule(configuration.getTiggerMap().get(cronKey));
						trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
						triggers.add(trigger);
					}
					triggersAndJobs.put(job, triggers);
				}
				if (!CollectionUtils.isEmpty(configuration.getJobDataMap())) {
					job.getJobDataMap().put(JobConstant.JOB_DATA_MAP, configuration.getJobDataMap());
				}
				if (!CollectionUtils.isEmpty(dependencyServicesMap)) {
					List<String> serviceKeys = configuration.getOnlyIncludeServiceKeys();
					if (CollectionUtils.isEmpty(serviceKeys)) {
						job.getJobDataMap().put(JobConstant.JOB_SCHEDULER_SERVICE_MAP, dependencyServicesMap);
					} else {
						int size = dependencyServicesMap.size();
						Map<String, Object> servicesMap = new HashMap<String, Object>(size);
						for (String key : serviceKeys) {
							for (Map.Entry<String, Object> dependencyServices : dependencyServicesMap.entrySet()) {
								if (StringUtils.equals(key, dependencyServices.getKey())) {
									servicesMap.put(key, dependencyServices);
								}
							}
						}
						job.getJobDataMap().put(JobConstant.JOB_SCHEDULER_SERVICE_MAP, servicesMap);
					}
				}
				if (!CollectionUtils.isEmpty(configuration.getLocalServicesMap())) {
					job.getJobDataMap().put(JobConstant.JOB_LOCAL_SERVICE_MAP, configuration.getLocalServicesMap());
				}
				getCurrentScheduler().scheduleJobs(triggersAndJobs, true);
			}
		}
	}
	
	public void afterPropertiesSet() throws Exception {
		if (isInit.get()) {
			initialize();
		}
	}
	
	@Override
	public void flushJobScheduler(JobConfiguration configuration) throws SchedulerException {
		if (null == configuration) {
			return;
		}
		JobKey jobKey = configuration.getJobKey();
		if (null != jobKey) {
			boolean checkExists = getCurrentScheduler().checkExists(jobKey);
			if (checkExists) {
				getCurrentScheduler().pauseJob(jobKey);
				if (!CollectionUtils.isEmpty(configuration.getTiggerMap())) {
					Trigger newTrigger = null;
					TriggerKey triggerKey = null;
					CronScheduleBuilder scheduleBuilder = null;
					JobDetail job = getCurrentScheduler().getJobDetail(jobKey);
					if (null != job) {
						if (!CollectionUtils.isEmpty(configuration.getJobDataMap())) {
							job.getJobDataMap().put(JobConstant.JOB_DATA_MAP, configuration.getJobDataMap());
						}
						if (!CollectionUtils.isEmpty(configuration.getLocalServicesMap())) {
							job.getJobDataMap().put(JobConstant.JOB_LOCAL_SERVICE_MAP, configuration.getLocalServicesMap());
						}
						List<? extends Trigger> triggers = getCurrentScheduler().getTriggersOfJob(jobKey);
						for (String cronKey : configuration.getTiggerMap().keySet()) {
							triggerKey = JobUtil.createTriggerKey(cronKey, configuration.getJobGroup());
							scheduleBuilder = CronScheduleBuilder.cronSchedule(configuration.getTiggerMap().get(cronKey));
							newTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
							TriggerKey oldTriggerKey = null;
							for (Trigger trigger : triggers) {
								if (trigger.getKey().equals(newTrigger.getKey())) {
									oldTriggerKey = trigger.getKey();
									break;
								 }
							}
							if (null == oldTriggerKey) {
								getCurrentScheduler().scheduleJob(job, newTrigger);
							} else {
								getCurrentScheduler().rescheduleJob(oldTriggerKey, newTrigger);
							}
						}
					}
				}
			    getCurrentScheduler().resumeJob(jobKey);
			} else {
				scheduleJobWithTrigger(Arrays.asList(configuration));
			}
		}
	}
	
	@Override
	public Scheduler getCurrentScheduler() throws SchedulerException {
		return schedulerFactory.getScheduler();
	}
	
	public abstract void preConfigureScheduler() throws SchedulerException;
	
	protected final <T> T injectionHelper(Class<T> clz) throws ClassCastException {
		T helper = null;
		if (null != clz) {
			helper = injectionHelper(clz, StringUtils.uncapitalize(clz.getSimpleName()));
		}
		return helper;
	}
	
	@SuppressWarnings("unchecked")
	protected final <T> T injectionHelper(Class<T> clz, String serviceKey) throws ClassCastException {
		T helper = null;
		if (null != clz && !CollectionUtils.isEmpty(defaultServiceHelperMap)) {
			helper = (T) defaultServiceHelperMap.get(serviceKey);
		}
		return helper;
	}
	
	public JobConfiguration getJobConfigurations(String jobName) {
		if (StringUtils.isBlank(jobName)) {
			return null;
		} else {
			JobConfiguration jobConfigure = null;
			for (JobConfiguration configuration : jobConfigurations) {
				if (StringUtils.equals(configuration.getJobName(), jobName)) {
					jobConfigure = configuration;
					break;
				}
			}
			return jobConfigure;
		}
	}
	
	public void setAutoRun(boolean autoRun) {
		this.autoRun = autoRun;
	}

	private void setSchedulerFactory(SchedulerFactory schedulerFactory) {
		this.schedulerFactory = schedulerFactory;
	}
	
	public void setJobConfigurations(List<JobConfiguration> jobConfigurations) {
		this.jobConfigurations = jobConfigurations;
	}

	public void setDependencyServicesMap(Map<String, Object> dependencyServicesMap) {
		this.dependencyServicesMap = dependencyServicesMap;
	}

	public void setDefaultServiceHelperMap(Map<String, JobDefaultServiceHelper> defaultServiceHelperMap) {
		this.defaultServiceHelperMap = defaultServiceHelperMap;
	}
	
}