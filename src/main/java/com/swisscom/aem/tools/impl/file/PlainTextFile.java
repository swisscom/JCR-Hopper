package com.swisscom.aem.tools.impl.file;

import java.nio.charset.StandardCharsets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import com.swisscom.aem.tools.jcrhopper.config.File;

@RequiredArgsConstructor
@Slf4j
public class PlainTextFile implements File {
	@Getter
	private final String name;

	@SuppressWarnings("PMD.AvoidStringBufferField")
	@Delegate(types = StringBuilder.class)
	private final StringBuilder result = new StringBuilder();

	/**
	 * Writes a line break.
	 */
	public void println() {
		result.append('\n');
	}

	@Override
	public String getMimeType() {
		return "text/plain;charset=utf-8";
	}

	@Override
	public String getExtension() {
		return "txt";
	}

	@Override
	public byte[] getContents() {
		return result.toString().getBytes(StandardCharsets.UTF_8);
	}
}
