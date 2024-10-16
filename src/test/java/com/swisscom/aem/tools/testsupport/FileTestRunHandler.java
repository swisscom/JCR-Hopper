package com.swisscom.aem.tools.testsupport;

import com.swisscom.aem.tools.jcrhopper.config.File;
import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Marker;

public class FileTestRunHandler implements RunHandler {

	private String lastFile;

	@Override
	public void file(@Nonnull File file) {
		lastFile = file.getName() +
		'.' +
		file.getExtension() +
		" (" +
		file.getMimeType() +
		"):\n" +
		new String(file.getContents(), StandardCharsets.UTF_8);
	}

	@Override
	public void log(@Nonnull LogLevel level, @Nonnull String message, @Nullable Throwable throwable, @Nullable Marker marker) {
		// No-op
	}

	@Override
	public void print(@Nullable String message) {
		// No-op
	}

	public String getLastFile() {
		return this.lastFile;
	}
}
