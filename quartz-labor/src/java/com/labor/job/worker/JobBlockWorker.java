package com.labor.job.worker;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import com.labor.job.JobFlow;
import com.labor.job.config.WorkerContext;
import com.labor.job.exception.WorkerException;

@SuppressWarnings("deprecation")
public abstract class JobBlockWorker extends JobWorker implements StatefulJob, JobFlow{
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		execute(this, context);
	}

	@Override
	public void beforeWorker(WorkerContext context) throws WorkerException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void afterWorker(WorkerContext context) throws WorkerException {
		// TODO Auto-generated method stub
	}
	
}
