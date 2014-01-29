package com.labor.job.worker;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.labor.job.JobFlow;
import com.labor.job.config.WorkerContext;
import com.labor.job.constant.JobConstant;
import com.labor.job.util.JobUtil;

class JobWorker {
	
	protected transient JobExecutionContext jobContext = null;

	private transient WorkerContext workerContext = WorkerContext.getInstance();
	
	public <T> T getService(Class<T> clz) {
		return getService(clz, StringUtils.uncapitalize(clz.getSimpleName()));
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> clz, String serviceKey) {
		T service = null;
		if (null != clz && StringUtils.isNotBlank(serviceKey)) {
			service = (T) JobUtil.getServices(workerContext, serviceKey);
		}
		return service;
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(Class<T> clz, String dataKey) {
		T data = null;
		if (null != clz && StringUtils.isNotBlank(dataKey)) {
			data = (T) JobUtil.getData(workerContext, dataKey);
		}
		return data;
	}

	public Object getData(String dataKey) {
		return getData(Object.class, dataKey);
	}

	protected void execute(JobFlow jobFlow, JobExecutionContext context) {
		WorkerContext workerContext = createWorkerContext(context);
		jobFlow.beforeWorker(workerContext);
		jobFlow.doWorker(workerContext);
		jobFlow.afterWorker(workerContext);
	}

	@SuppressWarnings("unchecked")
	protected WorkerContext createWorkerContext(JobExecutionContext context) {
		workerContext.setTrigger(context.getTrigger());
		workerContext.setJobDetail(context.getJobDetail());
		Object datas = JobUtil.getJobData(context, JobConstant.JOB_DATA_MAP);
		if (null != datas) {
			workerContext.getDatas().putAll((Map<String, Object>) datas);
		}
		Object localServices = JobUtil.getJobData(context, JobConstant.JOB_LOCAL_SERVICE_MAP);
		if (null != localServices) {
			workerContext.getServices().putAll((Map<String, Object>) localServices);
		}
		Object schedulerServices = JobUtil.getJobData(context, JobConstant.JOB_SCHEDULER_SERVICE_MAP);
		if (null != schedulerServices) {
			workerContext.getServices().putAll((Map<String, Object>) schedulerServices);
		}
		this.jobContext = context;
		return workerContext;
	}

}
