package com.labor.job.exception;

public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 8780166265931897452L;

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationException(String message) {
		super(message);
	}

}
