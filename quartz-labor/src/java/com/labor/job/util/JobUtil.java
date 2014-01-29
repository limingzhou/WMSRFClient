package com.labor.job.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.util.CollectionUtils;

import com.labor.job.config.JobConfiguration;
import com.labor.job.config.WorkerContext;
import com.labor.job.exception.ConfigurationException;
import com.labor.job.exception.WorkerException;

public class JobUtil {
	
	public static JobKey createJobKey(JobConfiguration configuration) {
		JobKey jobKey = null;
		if (null != configuration) {
			jobKey = createJobKey(configuration.getJobName(), configuration.getJobGroup());
		}
		return jobKey;
	}
	
	public static JobKey createJobKey(String name, String group) {
		JobKey jobKey = null;
		if (StringUtils.isNotBlank(name)) {
			if (StringUtils.isBlank(group)) {
				jobKey = new JobKey(name);
			} else {
				jobKey = new JobKey(name, group);
			}
		}
		return jobKey;
	}
	
	public static TriggerKey createTriggerKey(JobConfiguration configuration) {
		TriggerKey triggerKey = null;
		if (null != configuration) {
			triggerKey = createTriggerKey(configuration.getJobName(), configuration.getJobGroup());
		}
		return triggerKey;
	}
	
	public static TriggerKey createTriggerKey(String name, String group){
		TriggerKey triggerKey = null;
		if (StringUtils.isNotBlank(name)) {
			if (StringUtils.isBlank(group)) {		
				triggerKey = new TriggerKey(name);
			} else {
				triggerKey = new TriggerKey(name, group);
			}
		}
		return triggerKey;
	}
	
	public static boolean checkJobConfiguration(JobConfiguration configuration) {
		StringBuffer message = new StringBuffer("");
		if (null != configuration) {
			if (StringUtils.isBlank(configuration.getJobName())) {
				message.append(" JOB的名称不能为空 ");
			}
			if (null == configuration.getBizJobclass()) {
				message.append(" JOB的工作者不能为空 ");
			}
			if (null == configuration.getJobKey()) {
				message.append(" JOB的Key不能为空 ");
			}
			if (CollectionUtils.isEmpty(configuration.getTiggerMap())) {
				message.append(" JOB的时间触发器必须指定一个 ");
			}
		} else {
			throw new ConfigurationException("错误：传入的JOB的配置信息对象（JobConfiguration）为null，验证失败！");
		}
		if (StringUtils.isNotBlank(message.toString())) {
			throw new ConfigurationException(message.toString());
		}
		return Boolean.TRUE;
	}
	
	public static Object getServices(WorkerContext context, String serviceKey) {
		if (null != context && StringUtils.isNotBlank(serviceKey)) {
			Map<String, Object> services = context.getServices();
			if (!CollectionUtils.isEmpty(services)) {
				return services.get(serviceKey);
			}
		} else {
			throw new WorkerException("错误：在WorkerContext中获取Service时传入的参数JOB运行上下文或者对应的Key为空！");
		}
		return null;
	}
	
	public static Object getData(WorkerContext context, String dataKey) {
		if (null != context && StringUtils.isNotBlank(dataKey)) {
			Map<String, Object> datas = context.getDatas();
			if (!CollectionUtils.isEmpty(datas)) {
				return datas.get(dataKey);
			}
		} else {
			throw new WorkerException("错误：在WorkerContext中获取Data时传入的参数JOB运行上下文或者对应的Key为空！");
		}
		return null;
	}
	
	public static Object getJobData(JobExecutionContext context, String dataKey) {
		if (null != context && StringUtils.isNotBlank(dataKey)) {
			JobDetail job = context.getJobDetail();
			if (null != job) {
				JobDataMap dataMap = job.getJobDataMap();
				if (null != dataMap) {
					return dataMap.get(dataKey);
				}
			}
		} else {
			throw new WorkerException("错误：在JobExecutionContext中获取Data时传入的参数JOB运行上下文或者对应的Key为空！");
		}
		return null;
	}
}