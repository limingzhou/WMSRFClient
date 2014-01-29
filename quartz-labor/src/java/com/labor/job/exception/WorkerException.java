package com.labor.job.exception;

public class WorkerException extends RuntimeException {

	private static final long serialVersionUID = -5511097362130069924L;

	public WorkerException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkerException(String message) {
		super(message);
	}

}
