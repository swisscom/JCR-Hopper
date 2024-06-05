package com.swisscom.aem.tools.jcrhopper.pipeline;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PipelineException extends Exception {

	/**
	 * @param message of exception
	 */
	public PipelineException(String message) {
		super(message);
	}

	/**
	 * @param message of exception
	 * @param cause of exception
	 */
	public PipelineException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause of exception
	 */
	public PipelineException(Throwable cause) {
		super(cause);
	}

	protected PipelineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
