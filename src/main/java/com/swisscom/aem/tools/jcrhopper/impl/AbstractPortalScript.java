package com.swisscom.aem.tools.jcrhopper.impl;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.jcr.Session;

import lombok.AccessLevel;
import lombok.Getter;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import com.swisscom.aem.tools.jcrhopper.MyTask;
import com.swisscom.aem.tools.jcrhopper.api.PortalScript;
import com.swisscom.aem.tools.jcrhopper.api.PortalScriptContext;
import com.swisscom.aem.tools.jcrhopper.api.PortalScriptOutputWriter;

/**
 * The base class for all portal scripts which provides the most often used functionality.
 */
public abstract class AbstractPortalScript implements PortalScript, PortalScriptOutputWriter {

	public static final String PARAMETER_DRY_RUN = "dryRun";
	private static final int DEFAULT_MAX_DURATION_IN_SECONDS = 60;
	private static final Set<String> VALID_DRY_RUN_VALUES = new HashSet<>(Arrays.asList("true", "false", "on", "off"));
	private static final Set<String> IGNORED_SYSTEM_QUERY_PARAMS = new HashSet<>(Arrays.asList("run", "submit", ":cq_csrf_token"));

	@Getter(AccessLevel.PROTECTED)
	private final PortalScriptContext context;

	@Getter
	private final long maxDurationInMs;

	/**
	 * Create a new portal script with the given context.
	 *
	 * @param context Script context
	 */
	public AbstractPortalScript(PortalScriptContext context) {
		this(context, secondsToMilliseconds(DEFAULT_MAX_DURATION_IN_SECONDS));
	}

	/**
	 * Creates a new portal script with an maximum duration in milliseconds. To make it clear you can wrap the maximum
	 * duration like this:
	 * <p/>
	 * <pre>
	 *
	 * public ConcretePortalScript(OnetimerContext context) {
	 * 	super(context, maxDurationInMs(100));
	 * }
	 * </pre>
	 *
	 * @param context         Context
	 * @param maxDurationInMs Maximum duration in milliseconds
	 */
	public AbstractPortalScript(PortalScriptContext context, long maxDurationInMs) {
		this.context = context;
		this.maxDurationInMs = maxDurationInMs;
	}

	/**
	 * Returns the given seconds to milliseconds.
	 *
	 * @param seconds Seconds
	 * @return Maximum duration in seconds
	 */
	public static long secondsToMilliseconds(int seconds) {
		return TimeUnit.SECONDS.toMillis(seconds);
	}

	/**
	 * The actual process method which has to be implemented by the concrete portal script.
	 *
	 * @throws Exception Exception
	 */
	public abstract void doProcess() throws Exception;

	@Override
	public void process() throws Exception {
		MyTask.registerTaskName(this.getClass().getName());
		try {
			logParameters();
			checkDryRunValue();
			doProcess();
		}
		finally {
			MyTask.unregisterTaskName();
			// Note: Risky function (MyTask.resetCancellation()) as a second task with the same class name may exist,
			// instead of the intended task. But we accept that, as we assume that only one
			// task at the time is started.
			MyTask.resetCancellation(this.getClass().getName());
			//close the session
			if (getSession() != null && getSession().isLive()) {
				getSession().logout();
			}
		}
	}

	private void logParameters() {
		context.getParameters().entrySet()
			.stream()
			.filter(e -> !IGNORED_SYSTEM_QUERY_PARAMS.contains(e.getKey()))
			.forEach(e -> info("Parameter %s: %s", e.getKey(), Arrays.toString(e.getValue())));
	}


	void checkDryRunValue() {
		if (StringUtils.isNoneBlank(
			context.getParameter(PARAMETER_DRY_RUN))
			&& !VALID_DRY_RUN_VALUES.contains(context.getParameter(PARAMETER_DRY_RUN))) {
			throw new IllegalArgumentException(
				String.format("Dry-run parameter value invalid. Must be one of [%s]!", String.join(",", VALID_DRY_RUN_VALUES))
			);
		}
	}

	@Override
	public String getResponseContentType() {
		return "text/plain";
	}


	/**
	 * Returns the parameter value for the given key. If the parameter is a String array the first element is returned.
	 *
	 * @param name Parameter name
	 * @return Parameter value
	 */
	public String getParameter(String name) {
		return context.getParameter(name);
	}

	/**
	 * Return true if the value of the given parameter is on/true (checkbox checked).
	 *
	 * @param parameter Parameter name
	 * @return True if the parameter value is on
	 */
	private boolean isOn(String parameter) {
		return StringUtils.equalsAny(getParameter(parameter), "true", "on");
	}

	/**
	 * Writes a debug message to the client.
	 *
	 * @param msg Message
	 */
	@Override
	public void debug(String msg) {
		context.getOutputWriter().debug(msg);
	}

	/**
	 * Writes a debug message to the client.
	 *
	 * @param msg Message
	 */
	@Override
	public void debug(String msg, Object... args) {
		context.getOutputWriter().debug(msg, args);
	}

	/**
	 * Writes an info message to the client.
	 *
	 * @param msg Message
	 */
	@Override
	public void info(String msg) {
		context.getOutputWriter().info(msg);
	}

	/**
	 * Writes a info message to the client.
	 *
	 * @param msg Message
	 */
	@Override
	public void info(String msg, Object... args) {
		context.getOutputWriter().info(msg, args);
	}

	/**
	 * Writes an error message to the client.
	 *
	 * @param msg Message
	 */
	@Override
	public void error(String msg) {
		context.getOutputWriter().error(msg);
	}

	/**
	 * Writes a info message to the client.
	 *
	 * @param msg Message
	 */
	@Override
	public void error(String msg, Object... args) {
		context.getOutputWriter().error(msg, args);
	}

	/**
	 * Writes an error message to the client.
	 */
	@Override
	public void error(Throwable e) {
		context.getOutputWriter().error(e);
	}

	@Override
	public void warn(String msg) {
		context.getOutputWriter().warn(msg);
	}

	@Override
	public void warn(String string, Object... args) {
		context.getOutputWriter().warn(string, args);
	}

	@Override
	public void warn(Throwable e) {
		context.getOutputWriter().warn(e);
	}

	/**
	 * Returns the response PrintWriter.
	 * Just use this as a last resort because the log information
	 * is not multiplexed to the log file and the client.
	 *
	 * @return PrintWriter
	 */
	@Override
	public PrintWriter getResponseWriter() {
		return context.getOutputWriter().getResponseWriter();
	}

	/**
	 * Returns the current resource resolver.
	 *
	 * @return ResourceResolver
	 */
	public ResourceResolver getResourceResolver() {
		return context.getResourceResolver();
	}

	/**
	 * Returns the current session.
	 * This framework also makes sure that the session is finally closed after the process.
	 *
	 * @return Session
	 */
	public Session getSession() {
		return context.getResourceResolver().adaptTo(Session.class);
	}

	/**
	 * Returns true if the portal script is started with the parameter dryRun on.
	 *
	 * @return True if it's a dry run
	 */
	public boolean isDryRun() {
		return isOn(PARAMETER_DRY_RUN);
	}
}
