package com.swisscom.aem.tools.impl.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.swisscom.aem.tools.jcrhopper.config.File;
import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Marker;

@RequiredArgsConstructor
public class HttpChunkedResponseRunHandler implements RunHandler {

	private final PrintWriter responseWriter;

	private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private final Base64.Encoder base64Encoder = Base64.getEncoder();

	@Override
	public void file(@Nonnull File file) {
		final JsonObject object = new JsonObject();
		object.addProperty("type", "file");
		object.addProperty("name", file.getName() + '.' + file.getExtension());
		object.addProperty("mime", file.getMimeType());
		object.addProperty("data", new String(base64Encoder.encode(file.getContents()), StandardCharsets.UTF_8));
		gson.toJson(object, responseWriter);
		flush();
	}

	@Override
	public void log(@Nonnull LogLevel level, @Nonnull String message, @Nullable Throwable throwable, @Nullable Marker marker) {
		final JsonObject object = new JsonObject();
		object.addProperty("type", "log");
		object.addProperty("level", level.toName());
		object.addProperty("message", message);
		object.add("marker", gson.toJsonTree(marker));
		if (throwable != null) {
			object.addProperty(
				"exception",
				String.format(
					"%s%n%s",
					throwable,
					Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n"))
				)
			);
		}
		gson.toJson(object, responseWriter);
		flush();
	}

	@Override
	public void print(@Nullable String message) {
		if (message != null) {
			gson.toJson(message, responseWriter);
			flush();
		}
	}

	private void flush() {
		responseWriter.println();
		responseWriter.flush();
	}
}
