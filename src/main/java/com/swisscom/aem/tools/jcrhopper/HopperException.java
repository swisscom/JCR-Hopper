package com.swisscom.aem.tools.jcrhopper;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HopperException extends Exception {

	/**
	 * @param message of exception
	 */
	public HopperException(String message) {
		super(message);
	}

	/**
	 * @param message of exception
	 * @param cause   of exception
	 */
	public HopperException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause of exception
	 */
	public HopperException(Throwable cause) {
		super(cause);
	}

	protected HopperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
