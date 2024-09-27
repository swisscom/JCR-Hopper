package com.swisscom.aem.tools.jcrhopper.pipeline;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Stream;

import javax.jcr.Session;

import lombok.Getter;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.request.RequestParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import com.swisscom.aem.tools.jcrhopper.api.PortalScriptOutputWriter;
import com.swisscom.aem.tools.jcrhopper.impl.LogLevel;

@SuppressWarnings("PMD")
public class PipelineContext implements Logger {
	private final LogLevel logLevel;
	@Getter
	private final boolean dryRun;

	private final PortalScriptOutputWriter portalScriptContext;
	private final Deque<Logger> inner;

	@Getter
	private final JcrFunctions jcrFunctions;
	@Getter
	private final RequestParameterMap requestParams;
	private final ElExecutor executor;

	/**
	 * Constructor.
	 *
	 * @param portalScriptContext context
	 * @param requestParams       params
	 * @param logLevel            level
	 * @param session             needed for jcr function
	 * @param dryRun              paramter
	 */
	public PipelineContext(
		PortalScriptOutputWriter portalScriptContext,
		RequestParameterMap requestParams,
		LogLevel logLevel,
		Session session,
		boolean dryRun
	) {
		this.portalScriptContext = portalScriptContext;
		this.logLevel = logLevel;
		this.dryRun = dryRun;
		this.jcrFunctions = new JcrFunctions(session);
		this.requestParams = requestParams;
		final JexlBuilder builder = new JexlBuilder();
		final JexlEngine engine = builder.create();
		final JxltEngine templateEngine = engine.createJxltEngine();

		// Create namespaces map
		executor = new ElExecutorImpl(engine, templateEngine);
		executor.registerFunctions("jcr", jcrFunctions);
		executor.registerFunctions("req", requestParams);
		executor.registerFunctions("str", StringUtils.class);
		executor.registerFunctions("arr", ArrayUtils.class);
		executor.registerFunctions("arrays", Arrays.class);
		executor.registerFunctions("stream", Stream.class);
		executor.registerFunctions("class", Class.class);
		this.inner = new LinkedList<>();
		this.pushLogger(LoggerFactory.getLogger(this.getClass()));
	}

	/**
	 * Constructor.
	 *
	 * @param portalScriptContext context
	 * @param requestParams       params
	 * @param logLevel            level
	 * @param session             needed for jcr function
	 */
	public PipelineContext(PortalScriptOutputWriter portalScriptContext, RequestParameterMap requestParams, LogLevel logLevel, Session session) {
		this(portalScriptContext, requestParams, logLevel, session, false);
	}

	@Override
	public String getName() {
		return inner.element().getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return inner.element().isTraceEnabled();
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return inner.element().isTraceEnabled(marker);
	}

	@Override
	public void trace(String msg) {
		inner.element().trace(msg);
	}

	@Override
	public void trace(String format, Object arg) {
		inner.element().trace(format, arg);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		inner.element().trace(format, arg1, arg2);
	}

	@Override
	public void trace(String format, Object... arguments) {
		inner.element().trace(format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		inner.element().trace(msg, t);
	}

	@Override
	public void trace(Marker marker, String msg) {
		inner.element().trace(marker, msg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		inner.element().trace(marker, format, arg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		inner.element().trace(marker, format, arg1, arg2);
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		inner.element().trace(marker, format, argArray);
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		inner.element().trace(marker, msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return inner.element().isDebugEnabled() || LogLevel.DEBUG.shouldLogTo(logLevel);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return inner.element().isDebugEnabled(marker) || LogLevel.DEBUG.shouldLogTo(logLevel);
	}

	@Override
	public void debug(String msg) {
		portalScriptContext.debug(msg);
		inner.element().debug(msg);
	}

	@Override
	public void debug(String format, Object arg) {
		portalScriptContext.debug(toFormat(format), arg);
		inner.element().debug(format, arg);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		portalScriptContext.debug(toFormat(format), arg1, arg2);
		inner.element().debug(format, arg1, arg2);
	}

	@Override
	public void debug(String format, Object... arguments) {
		portalScriptContext.debug(toFormat(format), arguments);
		inner.element().debug(format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		portalScriptContext.debug(msg, t.getMessage());
		inner.element().debug(msg, t);
	}

	@Override
	public void debug(Marker marker, String msg) {
		portalScriptContext.debug(msg);
		inner.element().debug(marker, msg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		portalScriptContext.debug(toFormat(format), arg);
		inner.element().debug(marker, format, arg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		portalScriptContext.debug(toFormat(format), arg1, arg2);
		inner.element().debug(marker, format, arg1, arg2);
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		portalScriptContext.debug(toFormat(format), arguments);
		inner.element().debug(marker, format, arguments);
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		portalScriptContext.debug(msg + " %s", t.getMessage());
		inner.element().debug(marker, msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return inner.element().isInfoEnabled() || LogLevel.INFO.shouldLogTo(logLevel);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return inner.element().isInfoEnabled(marker) || LogLevel.INFO.shouldLogTo(logLevel);
	}

	@Override
	public void info(String msg) {
		portalScriptContext.info(msg);
		inner.element().info(msg);
	}

	@Override
	public void info(String format, Object arg) {
		portalScriptContext.info(toFormat(format), arg);
		inner.element().info(format, arg);
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		portalScriptContext.info(toFormat(format), arg1, arg2);
		inner.element().info(format, arg1, arg2);
	}

	@Override
	public void info(String format, Object... arguments) {
		portalScriptContext.info(toFormat(format), arguments);
		inner.element().info(format, arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		portalScriptContext.info(msg + " %s", t.getMessage());
		inner.element().info(msg, t);
	}

	@Override
	public void info(Marker marker, String msg) {
		portalScriptContext.info(msg);
		inner.element().info(marker, msg);
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		portalScriptContext.info(toFormat(format), arg);
		inner.element().info(marker, format, arg);
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		portalScriptContext.info(toFormat(format), arg1, arg2);
		inner.element().info(marker, format, arg1, arg2);
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		portalScriptContext.info(toFormat(format), arguments);
		inner.element().info(marker, format, arguments);
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		portalScriptContext.info(msg + " %s", t.getMessage());
		inner.element().info(marker, msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return inner.element().isWarnEnabled() || LogLevel.WARN.shouldLogTo(logLevel);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return inner.element().isWarnEnabled(marker) || LogLevel.WARN.shouldLogTo(logLevel);
	}

	@Override
	public void warn(String msg) {
		portalScriptContext.warn(msg);
		inner.element().warn(msg);
	}

	@Override
	public void warn(String format, Object arg) {
		portalScriptContext.warn(toFormat(format), arg);
		inner.element().warn(format, arg);
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		portalScriptContext.warn(toFormat(format), arg1, arg2);
		inner.element().warn(format, arg1, arg2);
	}

	@Override
	public void warn(String format, Object... arguments) {
		portalScriptContext.warn(toFormat(format), arguments);
		inner.element().warn(format, arguments);
	}

	@Override
	public void warn(String msg, Throwable t) {
		portalScriptContext.warn(msg);
		portalScriptContext.error(t);
		inner.element().warn(msg, t);
	}

	@Override
	public void warn(Marker marker, String msg) {
		portalScriptContext.info(msg);
		inner.element().warn(marker, msg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		portalScriptContext.info(toFormat(format), arg);
		inner.element().warn(marker, format, arg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		portalScriptContext.info(toFormat(format), arg1, arg2);
		inner.element().warn(marker, format, arg1, arg2);
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		portalScriptContext.info(toFormat(format), arguments);
		inner.element().warn(marker, format, arguments);
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		portalScriptContext.info(msg);
		portalScriptContext.error(t);
		inner.element().warn(marker, msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return inner.element().isErrorEnabled() || LogLevel.ERROR.shouldLogTo(logLevel);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return inner.element().isErrorEnabled(marker) || LogLevel.ERROR.shouldLogTo(logLevel);
	}

	@Override
	public void error(String msg) {
		portalScriptContext.error(msg);
		inner.element().error(msg);
	}

	@Override
	public void error(String format, Object arg) {
		portalScriptContext.error(toFormat(format), arg);
		inner.element().error(format, arg);
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		portalScriptContext.error(toFormat(format), arg1, arg2);
		inner.element().error(format, arg1, arg2);
	}

	@Override
	public void error(String format, Object... arguments) {
		portalScriptContext.error(toFormat(format), arguments);
		inner.element().error(format, arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		portalScriptContext.error(msg);
		portalScriptContext.error(t);
		inner.element().error(msg, t);
	}


	@Override
	public void error(Marker marker, String msg) {
		portalScriptContext.error(msg);
		inner.element().error(marker, msg);
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		portalScriptContext.error(toFormat(format), arg);
		inner.element().error(marker, format, arg);
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		portalScriptContext.error(toFormat(format), arg1, arg2);
		inner.element().error(marker, format, arg1, arg2);
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		portalScriptContext.error(toFormat(format), arguments);
		inner.element().error(marker, format, arguments);
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		portalScriptContext.error(msg);
		portalScriptContext.error(t);
		inner.element().error(marker, msg, t);
	}

	private static String toFormat(String slf4jFormatString) {
		return slf4jFormatString.replaceAll("\\{}", "%s");
	}

	/**
	 * Push the logger.
	 *
	 * @param inner the logger
	 */
	void pushLogger(Logger inner) {
		this.inner.push(inner);
	}

	/**
	 * Pop the logger.
	 */
	void popLogger() {
		this.inner.pop();
	}

	/**
	 * Get the EL executor.
	 *
	 * @param vars the variables
	 * @return the EL executor
	 */
	public ElExecutor getElExecutor(Map<String, ?> vars) {
		vars.forEach(executor::bindVariable);
		return executor;
	}

	/**
	 * Evaluate a template.
	 *
	 * @param vars     the variables
	 * @param template the template
	 * @return the result
	 */
	public String evaluateTemplate(Map<String, ?> vars, String template) {
		vars.forEach(executor::bindVariable);
		return executor.evaluateTemplate(template);
	}

	/**
	 * Evaluate an expression.
	 *
	 * @param vars the variables
	 * @param el   the expression
	 * @return the result
	 */
	public Object evaluate(Map<String, ?> vars, String el) {
		vars.forEach(executor::bindVariable);
		return executor.evaluate(el);
	}

	/**
	 * Get the response writer.
	 *
	 * @return the response writer
	 */
	public PrintWriter getResponseWriter() {
		return portalScriptContext.getResponseWriter();
	}
}
