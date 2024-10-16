package com.swisscom.aem.tools.jcrhopper.impl;

import com.swisscom.aem.tools.jcrhopper.config.File;
import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Marker;

public class NoopRunHandler implements RunHandler {

	@Override
	public void file(@Nonnull File file) {
		// No-op
	}

	@Override
	public void log(@Nonnull LogLevel level, @Nonnull String message, @Nullable Throwable throwable, @Nullable Marker marker) {
		// No-op
	}

	@Override
	public void print(@Nullable String message) {
		// No-op
	}
}
