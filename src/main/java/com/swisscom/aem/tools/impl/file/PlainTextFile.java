package com.swisscom.aem.tools.impl.file;

import com.swisscom.aem.tools.jcrhopper.config.File;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PlainTextFile implements File {

	@Getter
	private final String name;

	@Getter
	@Setter
	private String mimeType = "text/plain;charset=utf-8";

	@Getter
	@Setter
	private String extension = "txt";

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
	public byte[] getContents() {
		return result.toString().getBytes(StandardCharsets.UTF_8);
	}
}
