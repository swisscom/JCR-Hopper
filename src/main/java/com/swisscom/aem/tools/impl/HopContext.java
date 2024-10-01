package com.swisscom.aem.tools.impl;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JxltEngine;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.HopConfig;
import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.LogLevel;
import com.swisscom.aem.tools.jcrhopper.Runner;

@RequiredArgsConstructor
@Slf4j
public class HopContext implements Logger, JexlContext {
	private final Runner runner;

	@Getter
	private final JexlEngine jexlEngine;
	private final JxltEngine templateEngine;
	@Getter
	private final JcrFunctions jcrFunctions;

	@Getter
	private final Map<String, Object> variables;

	public void runHops(Node node, List<HopConfig> hops) throws HopperException, RepositoryException {
		runHops(node, hops, Collections.emptyMap());
	}

	public void runHops(Node node, List<HopConfig> hops, Map<String, Object> additionalVariables) throws HopperException, RepositoryException {
		final HopContext inner = childContext(node, additionalVariables);
		for (HopConfig hopConfig : hops) {
			inner.runHop(node, hopConfig);
		}
	}

	public String evaluateTemplate(String template) {
		return templateEngine.createTemplate(template).prepare(this).asString();
	}

	public Object evaluate(String expression) {
		return jexlEngine.createExpression(expression).evaluate(this);
	}

	/**
	 * Evaluates a JEXL expression and returns whether it evaluates to true.
	 *
	 * @param expression the expression to evaluate, e.g. a &gt; b
	 * @return whether the JEXL expression evaluates to true (or the string "true")
	 */
	public boolean expressionMatches(String expression) {
		final Object result = evaluate(expression);
		if (result instanceof Boolean) {
			return (Boolean) result;
		}
		return Boolean.parseBoolean(result.toString());
	}

	public void setVariable(String varName, Object value) {
		variables.put(varName, value);
	}

	@SuppressWarnings("unchecked")
	public void runHop(Node node, HopConfig hopConfig) throws HopperException, RepositoryException {
		Hop<HopConfig> hop = (Hop<HopConfig>) runner.getHops()
			.stream()
			.filter(h -> h.getConfigType().isInstance(hopConfig))
			.findFirst()
			.orElseThrow(() -> new HopperException("Hop config of type " + hopConfig.getClass() + " is not known"));

		hop.run(hopConfig, node, this);
	}

	private HopContext childContext(Node node, Map<String, Object> additionalVariables) {
		DerivedMap<String, Object> childVariables = new DerivedMap<>(variables);
		childVariables.putAll(additionalVariables);
		childVariables.put("node", node);
		return new HopContext(
			runner,
			jexlEngine,
			jexlEngine.createJxltEngine(),
			jcrFunctions,
			childVariables
		);
	}

	// region JEXL Context Accessors
	@Override
	public Object get(String key) {
		return variables.get(key);
	}

	@Override
	public boolean has(String key) {
		return variables.containsKey(key);
	}

	@Override
	public void set(String key, Object value) {
		variables.put(key, value);
	}
	//endregion

	// region Logging functions
	@Override
	public String getName() {
		return log.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return LogLevel.TRACE.shouldLogTo(runner.getScript().getLogLevel());
	}

	@Override
	public void trace(String s) {
		if (isTraceEnabled()) {
			log.trace(s);
			runner.getRunHandler().log(LogLevel.TRACE, s);
		}
	}

	@Override
	public void trace(String s, Object o) {
		if (isTraceEnabled()) {
			log.trace(s, o);
			runner.getRunHandler().log(LogLevel.TRACE, MessageFormatter.format(s, o).getMessage());
		}
	}

	@Override
	public void trace(String s, Object o, Object o1) {
		if (isTraceEnabled()) {
			log.trace(s, o, o1);
			runner.getRunHandler().log(LogLevel.TRACE, MessageFormatter.format(s, o, o1).getMessage());
		}
	}

	@Override
	public void trace(String s, Object... objects) {
		if (isTraceEnabled()) {
			log.trace(s, objects);
			runner.getRunHandler().log(LogLevel.TRACE, MessageFormatter.format(s, objects).getMessage());
		}
	}

	@Override
	public void trace(String s, Throwable throwable) {
		if (isTraceEnabled()) {
			log.trace(s, throwable);
			runner.getRunHandler().log(LogLevel.TRACE, MessageFormatter.format(s, throwable).getMessage());
		}
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return LogLevel.TRACE.shouldLogTo(runner.getScript().getLogLevel());
	}

	@Override
	public void trace(Marker marker, String s) {
		if (isTraceEnabled(marker)) {
			log.trace(marker, s);
			runner.getRunHandler().log(LogLevel.TRACE, s, marker);
		}
	}

	@Override
	public void trace(Marker marker, String s, Object o) {
		if (isTraceEnabled(marker)) {
			log.trace(marker, s, o);
			runner.getRunHandler().log(LogLevel.TRACE, MessageFormatter.format(s, o).getMessage(), marker);
		}
	}

	@Override
	public void trace(Marker marker, String s, Object o, Object o1) {
		if (isTraceEnabled(marker)) {
			log.trace(marker, s, o, o1);
			runner.getRunHandler().log(LogLevel.TRACE, MessageFormatter.format(s, o, o1).getMessage(), marker);
		}
	}

	@Override
	public void trace(Marker marker, String s, Object... objects) {
		if (isTraceEnabled(marker)) {
			log.trace(marker, s, objects);
			runner.getRunHandler().log(LogLevel.TRACE, MessageFormatter.format(s, objects).getMessage(), marker);
		}
	}

	@Override
	public void trace(Marker marker, String s, Throwable throwable) {
		if (isTraceEnabled(marker)) {
			log.trace(marker, s, throwable);
			runner.getRunHandler().log(LogLevel.TRACE, MessageFormatter.format(s, throwable).getMessage(), marker);
		}
	}

	@Override
	public boolean isDebugEnabled() {
		return LogLevel.DEBUG.shouldLogTo(runner.getScript().getLogLevel());
	}

	@Override
	public void debug(String s) {
		if (isDebugEnabled()) {
			log.debug(s);
			runner.getRunHandler().log(LogLevel.DEBUG, s);
		}
	}

	@Override
	public void debug(String s, Object o) {
		if (isDebugEnabled()) {
			log.debug(s, o);
			runner.getRunHandler().log(LogLevel.DEBUG, MessageFormatter.format(s, o).getMessage());
		}
	}

	@Override
	public void debug(String s, Object o, Object o1) {
		if (isDebugEnabled()) {
			log.debug(s, o, o1);
			runner.getRunHandler().log(LogLevel.DEBUG, MessageFormatter.format(s, o, o1).getMessage());
		}
	}

	@Override
	public void debug(String s, Object... objects) {
		if (isDebugEnabled()) {
			log.debug(s, objects);
			runner.getRunHandler().log(LogLevel.DEBUG, MessageFormatter.format(s, objects).getMessage());
		}
	}

	@Override
	public void debug(String s, Throwable throwable) {
		if (isDebugEnabled()) {
			log.debug(s, throwable);
			runner.getRunHandler().log(LogLevel.DEBUG, MessageFormatter.format(s, throwable).getMessage());
		}
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return LogLevel.DEBUG.shouldLogTo(runner.getScript().getLogLevel());
	}

	@Override
	public void debug(Marker marker, String s) {
		if (isDebugEnabled(marker)) {
			log.debug(marker, s);
			runner.getRunHandler().log(LogLevel.DEBUG, s, marker);
		}
	}

	@Override
	public void debug(Marker marker, String s, Object o) {
		if (isDebugEnabled(marker)) {
			log.debug(marker, s, o);
			runner.getRunHandler().log(LogLevel.DEBUG, MessageFormatter.format(s, o).getMessage(), marker);
		}
	}

	@Override
	public void debug(Marker marker, String s, Object o, Object o1) {
		if (isDebugEnabled(marker)) {
			log.debug(marker, s, o, o1);
			runner.getRunHandler().log(LogLevel.DEBUG, MessageFormatter.format(s, o, o1).getMessage(), marker);
		}
	}

	@Override
	public void debug(Marker marker, String s, Object... objects) {
		if (isDebugEnabled(marker)) {
			log.debug(marker, s, objects);
			runner.getRunHandler().log(LogLevel.DEBUG, MessageFormatter.format(s, objects).getMessage(), marker);
		}
	}

	@Override
	public void debug(Marker marker, String s, Throwable throwable) {
		if (isDebugEnabled(marker)) {
			log.debug(marker, s, throwable);
			runner.getRunHandler().log(LogLevel.DEBUG, MessageFormatter.format(s, throwable).getMessage(), marker);
		}
	}

	@Override
	public boolean isInfoEnabled() {
		return LogLevel.INFO.shouldLogTo(runner.getScript().getLogLevel());
	}

	@Override
	public void info(String s) {
		if (isInfoEnabled()) {
			log.info(s);
			runner.getRunHandler().log(LogLevel.INFO, s);
		}
	}

	@Override
	public void info(String s, Object o) {
		if (isInfoEnabled()) {
			log.info(s, o);
			runner.getRunHandler().log(LogLevel.INFO, MessageFormatter.format(s, o).getMessage());
		}
	}

	@Override
	public void info(String s, Object o, Object o1) {
		if (isInfoEnabled()) {
			log.info(s, o, o1);
			runner.getRunHandler().log(LogLevel.INFO, MessageFormatter.format(s, o, o1).getMessage());
		}
	}

	@Override
	public void info(String s, Object... objects) {
		if (isInfoEnabled()) {
			log.info(s, objects);
			runner.getRunHandler().log(LogLevel.INFO, MessageFormatter.format(s, objects).getMessage());
		}
	}

	@Override
	public void info(String s, Throwable throwable) {
		if (isInfoEnabled()) {
			log.info(s, throwable);
			runner.getRunHandler().log(LogLevel.INFO, MessageFormatter.format(s, throwable).getMessage());
		}
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return LogLevel.INFO.shouldLogTo(runner.getScript().getLogLevel());
	}

	@Override
	public void info(Marker marker, String s) {
		if (isInfoEnabled(marker)) {
			log.info(marker, s);
			runner.getRunHandler().log(LogLevel.INFO, s, marker);
		}
	}

	@Override
	public void info(Marker marker, String s, Object o) {
		if (isInfoEnabled(marker)) {
			log.info(marker, s, o);
			runner.getRunHandler().log(LogLevel.INFO, MessageFormatter.format(s, o).getMessage(), marker);
		}
	}

	@Override
	public void info(Marker marker, String s, Object o, Object o1) {
		if (isInfoEnabled(marker)) {
			log.info(marker, s, o, o1);
			runner.getRunHandler().log(LogLevel.INFO, MessageFormatter.format(s, o, o1).getMessage(), marker);
		}
	}

	@Override
	public void info(Marker marker, String s, Object... objects) {
		if (isInfoEnabled(marker)) {
			log.info(marker, s, objects);
			runner.getRunHandler().log(LogLevel.INFO, MessageFormatter.format(s, objects).getMessage(), marker);
		}
	}

	@Override
	public void info(Marker marker, String s, Throwable throwable) {
		if (isInfoEnabled(marker)) {
			log.info(marker, s, throwable);
			runner.getRunHandler().log(LogLevel.INFO, MessageFormatter.format(s, throwable).getMessage(), marker);
		}
	}


	@Override
	public boolean isWarnEnabled() {
		return LogLevel.WARN.shouldLogTo(runner.getScript().getLogLevel());
	}

	@Override
	public void warn(String s) {
		if (isWarnEnabled()) {
			log.warn(s);
			runner.getRunHandler().log(LogLevel.WARN, s);
		}
	}

	@Override
	public void warn(String s, Object o) {
		if (isWarnEnabled()) {
			log.warn(s, o);
			runner.getRunHandler().log(LogLevel.WARN, MessageFormatter.format(s, o).getMessage());
		}
	}

	@Override
	public void warn(String s, Object o, Object o1) {
		if (isWarnEnabled()) {
			log.warn(s, o, o1);
			runner.getRunHandler().log(LogLevel.WARN, MessageFormatter.format(s, o, o1).getMessage());
		}
	}

	@Override
	public void warn(String s, Object... objects) {
		if (isWarnEnabled()) {
			log.warn(s, objects);
			runner.getRunHandler().log(LogLevel.WARN, MessageFormatter.format(s, objects).getMessage());
		}
	}

	@Override
	public void warn(String s, Throwable throwable) {
		if (isWarnEnabled()) {
			log.warn(s, throwable);
			runner.getRunHandler().log(LogLevel.WARN, MessageFormatter.format(s, throwable).getMessage());
		}
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return LogLevel.WARN.shouldLogTo(runner.getScript().getLogLevel());
	}

	@Override
	public void warn(Marker marker, String s) {
		if (isWarnEnabled(marker)) {
			log.warn(marker, s);
			runner.getRunHandler().log(LogLevel.WARN, s, marker);
		}
	}

	@Override
	public void warn(Marker marker, String s, Object o) {
		if (isWarnEnabled(marker)) {
			log.warn(marker, s, o);
			runner.getRunHandler().log(LogLevel.WARN, MessageFormatter.format(s, o).getMessage(), marker);
		}
	}

	@Override
	public void warn(Marker marker, String s, Object o, Object o1) {
		if (isWarnEnabled(marker)) {
			log.warn(marker, s, o, o1);
			runner.getRunHandler().log(LogLevel.WARN, MessageFormatter.format(s, o, o1).getMessage(), marker);
		}
	}

	@Override
	public void warn(Marker marker, String s, Object... objects) {
		if (isWarnEnabled(marker)) {
			log.warn(marker, s, objects);
			runner.getRunHandler().log(LogLevel.WARN, MessageFormatter.format(s, objects).getMessage(), marker);
		}
	}

	@Override
	public void warn(Marker marker, String s, Throwable throwable) {
		if (isWarnEnabled(marker)) {
			log.warn(marker, s, throwable);
			runner.getRunHandler().log(LogLevel.WARN, MessageFormatter.format(s, throwable).getMessage(), marker);
		}
	}


	@Override
	public boolean isErrorEnabled() {
		return LogLevel.ERROR.shouldLogTo(runner.getScript().getLogLevel());
	}

	@Override
	public void error(String s) {
		if (isErrorEnabled()) {
			log.error(s);
			runner.getRunHandler().log(LogLevel.ERROR, s);
		}
	}

	@Override
	public void error(String s, Object o) {
		if (isErrorEnabled()) {
			log.error(s, o);
			runner.getRunHandler().log(LogLevel.ERROR, MessageFormatter.format(s, o).getMessage());
		}
	}

	@Override
	public void error(String s, Object o, Object o1) {
		if (isErrorEnabled()) {
			log.error(s, o, o1);
			runner.getRunHandler().log(LogLevel.ERROR, MessageFormatter.format(s, o, o1).getMessage());
		}
	}

	@Override
	public void error(String s, Object... objects) {
		if (isErrorEnabled()) {
			log.error(s, objects);
			runner.getRunHandler().log(LogLevel.ERROR, MessageFormatter.format(s, objects).getMessage());
		}
	}

	@Override
	public void error(String s, Throwable throwable) {
		if (isErrorEnabled()) {
			log.error(s, throwable);
			runner.getRunHandler().log(LogLevel.ERROR, MessageFormatter.format(s, throwable).getMessage());
		}
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return LogLevel.ERROR.shouldLogTo(runner.getScript().getLogLevel());
	}

	@Override
	public void error(Marker marker, String s) {
		if (isErrorEnabled(marker)) {
			log.error(marker, s);
			runner.getRunHandler().log(LogLevel.ERROR, s, marker);
		}
	}

	@Override
	public void error(Marker marker, String s, Object o) {
		if (isErrorEnabled(marker)) {
			log.error(marker, s, o);
			runner.getRunHandler().log(LogLevel.ERROR, MessageFormatter.format(s, o).getMessage(), marker);
		}
	}

	@Override
	public void error(Marker marker, String s, Object o, Object o1) {
		if (isErrorEnabled(marker)) {
			log.error(marker, s, o, o1);
			runner.getRunHandler().log(LogLevel.ERROR, MessageFormatter.format(s, o, o1).getMessage(), marker);
		}
	}

	@Override
	public void error(Marker marker, String s, Object... objects) {
		if (isErrorEnabled(marker)) {
			log.error(marker, s, objects);
			runner.getRunHandler().log(LogLevel.ERROR, MessageFormatter.format(s, objects).getMessage(), marker);
		}
	}

	@Override
	public void error(Marker marker, String s, Throwable throwable) {
		if (isErrorEnabled(marker)) {
			log.error(marker, s, throwable);
			runner.getRunHandler().log(LogLevel.ERROR, MessageFormatter.format(s, throwable).getMessage(), marker);
		}
	}
	// endregion


	// region Writer
	public Writer getWriter() {
		return new PrintWriter(new Writer() {
			@Override
			public void write(char[] cbuf, int off, int len) {
				runner.getRunHandler().print(new String(cbuf, off, len));
			}

			@Override
			public void flush() {
				// No-op
			}

			@Override
			public void close() {
				// No-op
			}
		});
	}
	// endregion
}
