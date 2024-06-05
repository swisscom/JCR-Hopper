package com.swisscom.aem.tools.jcrhopper.impl;

import java.io.PrintWriter;
import java.util.Date;

import com.swisscom.aem.tools.jcrhopper.api.PortalScriptOutputWriter;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Default output writer which writes the messages to the client response and at the same time to the log file.
 *
 */
@Slf4j
public class DefaultPortalScriptOutput implements PortalScriptOutputWriter {

	private static final FastDateFormat DATE_LOG_FORMAT = FastDateFormat.getInstance("HH:mm:ss.SSS");

	private final LogLevel logLevel;
	private final PrintWriter writer;

	/**
	 * Creates a new default output writer.
	 *
	 * @param writer   Print writer
	 * @param logLevel Log level
	 */
	public DefaultPortalScriptOutput(PrintWriter writer, LogLevel logLevel) {
		this.writer = writer;
		this.logLevel = logLevel;
	}

	@Override
	public void debug(String msg) {
		log.debug(msg);
		write(LogLevel.DEBUG, msg);
	}

	@Override
	public void debug(String string, Object... args) {
		debug(String.format(string, args));
	}

	@Override
	public void info(String msg) {
		log.info(msg);
		write(LogLevel.INFO, msg);
	}

	@Override
	public void info(String string, Object... args) {
		info(String.format(string, args));
	}

	@Override
	public void error(String msg) {
		log.error(msg);
		write(LogLevel.ERROR, msg);
	}

	@Override
	public void error(String string, Object... args) {
		error(String.format(string, args));
	}

	@Override
	public void error(Throwable e) {
		write(LogLevel.ERROR, e.toString());
		log.error("", e);
	}

	@Override
	public void warn(String msg) {
		log.warn(msg);
		write(LogLevel.WARN, msg);
	}

	@Override
	public void warn(String string, Object... args) {
		warn(String.format(string, args));
	}

	@Override
	public void warn(Throwable e) {
		write(LogLevel.WARN, e.toString());
		log.warn("", e);
	}

	@Override
	public PrintWriter getResponseWriter() {
		return writer;
	}

	/**
	 * Writes a message to the client response.
	 *
	 * @param msg Message
	 */
	public void write(String msg) {
		writer.write(msg);
		writer.write("\n");
		writer.flush();
	}

	private void write(LogLevel logLevel, String msg) {
		if (!logLevel.shouldLogTo(this.logLevel)) {
			return;
		}
		final String prefix = createPrefix(logLevel);
		writer.write(prefix);
		writer.write(msg);
		writer.write("\n");
		writer.flush();
	}

	protected String createPrefix(LogLevel logLevel) {
		final String timestamp = DATE_LOG_FORMAT.format(new Date());
		return String.format("%s %5s: ", timestamp, logLevel);
	}
}
