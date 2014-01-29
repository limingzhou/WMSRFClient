package com.labor.job.config;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.BeanUtils;

public class WorkerContext {

	private Trigger trigger;
	
    private JobDetail jobDetail;
    
	private Map<String, Object> services = new HashMap<String, Object>();
	
	private Map<String, Object> datas = new HashMap<String, Object>();
	
	private WorkerContext(){}
	
	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	public Map<String, Object> getServices() {
		return services;
	}

	public void setServices(Map<String, Object> services) {
		this.services = services;
	}

	public Map<String, Object> getDatas() {
		return datas;
	}

	public void setDatas(Map<String, Object> datas) {
		this.datas = datas;
	}
	
	public final static WorkerContext getInstance(){
		return (WorkerContext) BeanUtils.instantiateClass(WorkerContext.class);
	}
	
}
