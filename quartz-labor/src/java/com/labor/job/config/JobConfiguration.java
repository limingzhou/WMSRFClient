package com.labor.job.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobKey;

public class JobConfiguration implements Cloneable {
	
	// 标识
	private JobKey jobKey;
	
	// 名称
	private String jobName;

	// 组
	private String jobGroup;
	
	// 依赖工作类
	private Class<? extends Job> bizJobclass;
	
	private List<String> onlyIncludeServiceKeys = new ArrayList<String>();
		
	private Map<String, String> tiggerMap  = new HashMap<String, String>();
	
	private Map<String, Object> jobDataMap = new HashMap<String, Object>();
	
	private Map<String, Object> localServicesMap = new HashMap<String, Object>(); 
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public JobKey getJobKey() {
		return jobKey;
	}

	public void setJobKey(JobKey jobKey) {
		this.jobKey = jobKey;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public List<String> getOnlyIncludeServiceKeys() {
		return onlyIncludeServiceKeys;
	}

	public void addOnlyIncludeServiceKeys(String... serviceKeys) {
		this.onlyIncludeServiceKeys.addAll(Arrays.asList(serviceKeys));
	}
	
	public void setOnlyIncludeServiceKeys(List<String> onlyIncludeServiceKeys) {
		this.onlyIncludeServiceKeys = onlyIncludeServiceKeys;
	}

	public Map<String, String> addTiggerMap(String key, String val) {
		if(null == tiggerMap){
			tiggerMap = new HashMap<String, String>();
		}
		tiggerMap.put(key, val);
		return tiggerMap;
	}
	
	public Map<String, String> getTiggerMap() {
		return tiggerMap;
	}

	public void setTiggerMap(Map<String, String> tiggerMap) {
		this.tiggerMap = tiggerMap;
	}

	public Map<String, Object> addJobDataMap(String key, Object obj) {
		if(null == jobDataMap){
			jobDataMap = new HashMap<String, Object>();
		}
		jobDataMap.put(key, obj);
		return jobDataMap;
	}
	
	public Map<String, Object> getJobDataMap() {
		return jobDataMap;
	}

	public void setJobDataMap(Map<String, Object> jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

	public Class<? extends Job> getBizJobclass() {
		return bizJobclass;
	}

	public void setBizJobclass(Class<? extends Job> bizJobclass) {
		this.bizJobclass = bizJobclass;
	}

	public Map<String, Object> getLocalServicesMap() {
		return localServicesMap;
	}

	public void setLocalServicesMap(Map<String, Object> localServicesMap) {
		this.localServicesMap = localServicesMap;
	}

}