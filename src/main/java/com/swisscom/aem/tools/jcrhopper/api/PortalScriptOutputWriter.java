package com.swisscom.aem.tools.jcrhopper.api;

import java.io.PrintWriter;

/**
 * Output interface for portal scripts. Keep in mind that the debug level is for <b>Development</b> while info and error
 * is for <b>Operations</b> and <b>Development</b>. Keep info/error messages to a minimum. Just print messages which are
 * useful to <b>Operations</b> on those two levels.
 *
 * 
 */
public interface PortalScriptOutputWriter {

	/**
	 * Writes a debug message. Messages are intended for development only.
	 *
	 * @param msg Log message
	 */
	void debug(String msg);

	/**
	 * Writes a formatted debug message. The string is formatted with String.format(string, args). Messages are intended
	 * for development only.
	 *
	 * @param string Log message
	 * @param args   Arguments
	 */
	void debug(String string, Object... args);

	/**
	 * Writes an info message. Messages are intended for Operations so try to keep those to a minimum.
	 *
	 * @param msg Log message
	 */
	void info(String msg);

	/**
	 * Writes a formatted info message. The string is formatted with String.format(string, args). Messages are intended
	 * for Operations so try to keep those to a minimum.
	 *
	 * @param string Log message
	 * @param args   Arguments
	 */
	void info(String string, Object... args);

	/**
	 * Writes an error message.
	 *
	 * @param msg Log message
	 */
	void error(String msg);

	/**
	 * Writes a formatted error message. The string is formatted with String.format(string, args).
	 *
	 * @param string Log message
	 * @param args   Arguments
	 */
	void error(String string, Object... args);

	/**
	 * Writes a warning message.
	 *
	 * @param e Exception
	 */
	void error(Throwable e);

	/**
	 * Writes an error message.
	 *
	 * @param msg Log message
	 */
	void warn(String msg);

	/**
	 * Writes a formatted warning message. The string is formatted with String.format(string, args).
	 *
	 * @param string Log message
	 * @param args   Arguments
	 */
	void warn(String string, Object... args);

	/**
	 * Writes a warning message.
	 *
	 * @param e Exception
	 */
	void warn(Throwable e);


	/**
	 * Returns the repsonse PrintWriter. Just use this as a last resort.
	 *
	 * @return PrintWriter
	 */
	PrintWriter getResponseWriter();
}
