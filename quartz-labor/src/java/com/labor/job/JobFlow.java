package com.labor.job;

import com.labor.job.config.WorkerContext;
import com.labor.job.exception.WorkerException;

public interface JobFlow {
	
	public void beforeWorker(WorkerContext context) throws WorkerException;
	
	public void doWorker(WorkerContext context) throws WorkerException;
	
	public void afterWorker(WorkerContext context) throws WorkerException;
	
	public void afterWorker1(WorkerContext context) throws WorkerException;	

}
