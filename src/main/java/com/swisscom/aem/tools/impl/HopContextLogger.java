package com.swisscom.aem.tools.impl;

import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessivePublicCount", "PMD.TooManyMethods" })
public class HopContextLogger implements Logger {

	private final LogLevel logLevel;
	private final RunHandler runHandler;

	@Override
	public String getName() {
		return log.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return LogLevel.TRACE.shouldLogTo(logLevel);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return LogLevel.TRACE.shouldLogTo(logLevel);
	}

	@Override
	public void trace(String s) {
		if (isTraceEnabled()) {
			log.trace(s);
			runHandler.log(LogLevel.TRACE, s, null, null);
		}
	}

	@Override
	public void trace(String s, Object o) {
		if (isTraceEnabled()) {
			log.trace(s, o);
			final FormattingTuple format = MessageFormatter.format(s, o);
			runHandler.log(LogLevel.TRACE, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void trace(String s, Object o, Object o1) {
		if (isTraceEnabled()) {
			log.trace(s, o, o1);
			final FormattingTuple format = MessageFormatter.format(s, o, o1);
			runHandler.log(LogLevel.TRACE, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void trace(String s, Object... objects) {
		if (isTraceEnabled()) {
			log.trace(s, objects);
			final FormattingTuple format = MessageFormatter.arrayFormat(s, objects);
			runHandler.log(LogLevel.TRACE, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void trace(String s, Throwable throwable) {
		if (isTraceEnabled()) {
			log.trace(s, throwable);
			final FormattingTuple format = MessageFormatter.format(s, throwable);
			runHandler.log(LogLevel.TRACE, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void trace(Marker marker, String s) {
		if (isTraceEnabled(marker)) {
			log.trace(marker, s);
			runHandler.log(LogLevel.TRACE, s, null, marker);
		}
	}

	@Override
	public void trace(Marker marker, String s, Object o) {
		if (isTraceEnabled(marker)) {
			log.trace(marker, s, o);
			final FormattingTuple format = MessageFormatter.format(s, o);
			runHandler.log(LogLevel.TRACE, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void trace(Marker marker, String s, Object o, Object o1) {
		if (isTraceEnabled(marker)) {
			log.trace(marker, s, o, o1);
			final FormattingTuple format = MessageFormatter.format(s, o, o1);
			runHandler.log(LogLevel.TRACE, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void trace(Marker marker, String s, Object... objects) {
		if (isTraceEnabled(marker)) {
			log.trace(marker, s, objects);
			final FormattingTuple format = MessageFormatter.arrayFormat(s, objects);
			runHandler.log(LogLevel.TRACE, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void trace(Marker marker, String s, Throwable throwable) {
		if (isTraceEnabled(marker)) {
			log.trace(marker, s, throwable);
			final FormattingTuple format = MessageFormatter.format(s, throwable);
			runHandler.log(LogLevel.TRACE, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public boolean isDebugEnabled() {
		return LogLevel.DEBUG.shouldLogTo(logLevel);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return LogLevel.DEBUG.shouldLogTo(logLevel);
	}

	@Override
	public void debug(String s) {
		if (isDebugEnabled()) {
			log.debug(s);
			runHandler.log(LogLevel.DEBUG, s, null, null);
		}
	}

	@Override
	public void debug(String s, Object o) {
		if (isDebugEnabled()) {
			log.debug(s, o);
			final FormattingTuple format = MessageFormatter.format(s, o);
			runHandler.log(LogLevel.DEBUG, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void debug(String s, Object o, Object o1) {
		if (isDebugEnabled()) {
			log.debug(s, o, o1);
			final FormattingTuple format = MessageFormatter.format(s, o, o1);
			runHandler.log(LogLevel.DEBUG, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void debug(String s, Object... objects) {
		if (isDebugEnabled()) {
			log.debug(s, objects);
			final FormattingTuple format = MessageFormatter.arrayFormat(s, objects);
			runHandler.log(LogLevel.DEBUG, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void debug(String s, Throwable throwable) {
		if (isDebugEnabled()) {
			log.debug(s, throwable);
			final FormattingTuple format = MessageFormatter.format(s, throwable);
			runHandler.log(LogLevel.DEBUG, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void debug(Marker marker, String s) {
		if (isDebugEnabled(marker)) {
			log.debug(marker, s);
			runHandler.log(LogLevel.DEBUG, s, null, marker);
		}
	}

	@Override
	public void debug(Marker marker, String s, Object o) {
		if (isDebugEnabled(marker)) {
			log.debug(marker, s, o);
			final FormattingTuple format = MessageFormatter.format(s, o);
			runHandler.log(LogLevel.DEBUG, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void debug(Marker marker, String s, Object o, Object o1) {
		if (isDebugEnabled(marker)) {
			log.debug(marker, s, o, o1);
			final FormattingTuple format = MessageFormatter.format(s, o, o1);
			runHandler.log(LogLevel.DEBUG, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void debug(Marker marker, String s, Object... objects) {
		if (isDebugEnabled(marker)) {
			log.debug(marker, s, objects);
			final FormattingTuple format = MessageFormatter.arrayFormat(s, objects);
			runHandler.log(LogLevel.DEBUG, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void debug(Marker marker, String s, Throwable throwable) {
		if (isDebugEnabled(marker)) {
			log.debug(marker, s, throwable);
			final FormattingTuple format = MessageFormatter.format(s, throwable);
			runHandler.log(LogLevel.DEBUG, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public boolean isInfoEnabled() {
		return LogLevel.INFO.shouldLogTo(logLevel);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return LogLevel.INFO.shouldLogTo(logLevel);
	}

	@Override
	public void info(String s) {
		if (isInfoEnabled()) {
			log.info(s);
			runHandler.log(LogLevel.INFO, s, null, null);
		}
	}

	@Override
	public void info(String s, Object o) {
		if (isInfoEnabled()) {
			log.info(s, o);
			final FormattingTuple format = MessageFormatter.format(s, o);
			runHandler.log(LogLevel.INFO, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void info(String s, Object o, Object o1) {
		if (isInfoEnabled()) {
			log.info(s, o, o1);
			final FormattingTuple format = MessageFormatter.format(s, o, o1);
			runHandler.log(LogLevel.INFO, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void info(String s, Object... objects) {
		if (isInfoEnabled()) {
			log.info(s, objects);
			final FormattingTuple format = MessageFormatter.arrayFormat(s, objects);
			runHandler.log(LogLevel.INFO, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void info(String s, Throwable throwable) {
		if (isInfoEnabled()) {
			log.info(s, throwable);
			final FormattingTuple format = MessageFormatter.format(s, throwable);
			runHandler.log(LogLevel.INFO, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void info(Marker marker, String s) {
		if (isInfoEnabled(marker)) {
			log.info(marker, s);
			runHandler.log(LogLevel.INFO, s, null, marker);
		}
	}

	@Override
	public void info(Marker marker, String s, Object o) {
		if (isInfoEnabled(marker)) {
			log.info(marker, s, o);
			final FormattingTuple format = MessageFormatter.format(s, o);
			runHandler.log(LogLevel.INFO, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void info(Marker marker, String s, Object o, Object o1) {
		if (isInfoEnabled(marker)) {
			log.info(marker, s, o, o1);
			final FormattingTuple format = MessageFormatter.format(s, o, o1);
			runHandler.log(LogLevel.INFO, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void info(Marker marker, String s, Object... objects) {
		if (isInfoEnabled(marker)) {
			log.info(marker, s, objects);
			final FormattingTuple format = MessageFormatter.arrayFormat(s, objects);
			runHandler.log(LogLevel.INFO, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void info(Marker marker, String s, Throwable throwable) {
		if (isInfoEnabled(marker)) {
			log.info(marker, s, throwable);
			final FormattingTuple format = MessageFormatter.format(s, throwable);
			runHandler.log(LogLevel.INFO, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public boolean isWarnEnabled() {
		return LogLevel.WARN.shouldLogTo(logLevel);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return LogLevel.WARN.shouldLogTo(logLevel);
	}

	@Override
	public void warn(String s) {
		if (isWarnEnabled()) {
			log.warn(s);
			runHandler.log(LogLevel.WARN, s, null, null);
		}
	}

	@Override
	public void warn(String s, Object o) {
		if (isWarnEnabled()) {
			log.warn(s, o);
			final FormattingTuple format = MessageFormatter.format(s, o);
			runHandler.log(LogLevel.WARN, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void warn(String s, Object o, Object o1) {
		if (isWarnEnabled()) {
			log.warn(s, o, o1);
			final FormattingTuple format = MessageFormatter.format(s, o, o1);
			runHandler.log(LogLevel.WARN, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void warn(String s, Object... objects) {
		if (isWarnEnabled()) {
			log.warn(s, objects);
			final FormattingTuple format = MessageFormatter.arrayFormat(s, objects);
			runHandler.log(LogLevel.WARN, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void warn(String s, Throwable throwable) {
		if (isWarnEnabled()) {
			log.warn(s, throwable);
			final FormattingTuple format = MessageFormatter.format(s, throwable);
			runHandler.log(LogLevel.WARN, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void warn(Marker marker, String s) {
		if (isWarnEnabled(marker)) {
			log.warn(marker, s);
			runHandler.log(LogLevel.WARN, s, null, marker);
		}
	}

	@Override
	public void warn(Marker marker, String s, Object o) {
		if (isWarnEnabled(marker)) {
			log.warn(marker, s, o);
			final FormattingTuple format = MessageFormatter.format(s, o);
			runHandler.log(LogLevel.WARN, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void warn(Marker marker, String s, Object o, Object o1) {
		if (isWarnEnabled(marker)) {
			log.warn(marker, s, o, o1);
			final FormattingTuple format = MessageFormatter.format(s, o, o1);
			runHandler.log(LogLevel.WARN, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void warn(Marker marker, String s, Object... objects) {
		if (isWarnEnabled(marker)) {
			log.warn(marker, s, objects);
			final FormattingTuple format = MessageFormatter.arrayFormat(s, objects);
			runHandler.log(LogLevel.WARN, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void warn(Marker marker, String s, Throwable throwable) {
		if (isWarnEnabled(marker)) {
			log.warn(marker, s, throwable);
			final FormattingTuple format = MessageFormatter.format(s, throwable);
			runHandler.log(LogLevel.WARN, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public boolean isErrorEnabled() {
		return LogLevel.ERROR.shouldLogTo(logLevel);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return LogLevel.ERROR.shouldLogTo(logLevel);
	}

	@Override
	public void error(String s) {
		if (isErrorEnabled()) {
			log.error(s);
			runHandler.log(LogLevel.ERROR, s, null, null);
		}
	}

	@Override
	public void error(String s, Object o) {
		if (isErrorEnabled()) {
			log.error(s, o);
			final FormattingTuple format = MessageFormatter.format(s, o);
			runHandler.log(LogLevel.ERROR, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void error(String s, Object o, Object o1) {
		if (isErrorEnabled()) {
			log.error(s, o, o1);
			final FormattingTuple format = MessageFormatter.format(s, o, o1);
			runHandler.log(LogLevel.ERROR, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void error(String s, Object... objects) {
		if (isErrorEnabled()) {
			log.error(s, objects);
			final FormattingTuple format = MessageFormatter.arrayFormat(s, objects);
			runHandler.log(LogLevel.ERROR, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void error(String s, Throwable throwable) {
		if (isErrorEnabled()) {
			log.error(s, throwable);
			final FormattingTuple format = MessageFormatter.format(s, throwable);
			runHandler.log(LogLevel.ERROR, format.getMessage(), format.getThrowable(), null);
		}
	}

	@Override
	public void error(Marker marker, String s) {
		if (isErrorEnabled(marker)) {
			log.error(marker, s);
			runHandler.log(LogLevel.ERROR, s, null, marker);
		}
	}

	@Override
	public void error(Marker marker, String s, Object o) {
		if (isErrorEnabled(marker)) {
			log.error(marker, s, o);
			final FormattingTuple format = MessageFormatter.format(s, o);
			runHandler.log(LogLevel.ERROR, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void error(Marker marker, String s, Object o, Object o1) {
		if (isErrorEnabled(marker)) {
			log.error(marker, s, o, o1);
			final FormattingTuple format = MessageFormatter.format(s, o, o1);
			runHandler.log(LogLevel.ERROR, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void error(Marker marker, String s, Object... objects) {
		if (isErrorEnabled(marker)) {
			log.error(marker, s, objects);
			final FormattingTuple format = MessageFormatter.arrayFormat(s, objects);
			runHandler.log(LogLevel.ERROR, format.getMessage(), format.getThrowable(), marker);
		}
	}

	@Override
	public void error(Marker marker, String s, Throwable throwable) {
		if (isErrorEnabled(marker)) {
			log.error(marker, s, throwable);
			final FormattingTuple format = MessageFormatter.format(s, throwable);
			runHandler.log(LogLevel.ERROR, format.getMessage(), format.getThrowable(), marker);
		}
	}
}
