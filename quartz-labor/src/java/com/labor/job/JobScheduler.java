package com.labor.job;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.labor.job.config.JobConfiguration;

public interface JobScheduler {

	public Scheduler getCurrentScheduler() throws SchedulerException;
	
	public void flushJobScheduler(JobConfiguration configuration) throws SchedulerException;
	
	public JobConfiguration convertToJobConfiguration(Object configuration) throws ClassCastException;

}
