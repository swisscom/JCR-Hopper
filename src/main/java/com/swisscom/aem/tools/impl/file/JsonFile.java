package com.swisscom.aem.tools.impl.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.swisscom.aem.tools.jcrhopper.config.File;

@RequiredArgsConstructor
@Slf4j
public abstract class JsonFile implements File {
	protected final Gson gson = new Gson();

	protected abstract JsonElement getResult();

	@Override
	public String getMimeType() {
		return "application/json";
	}

	@Override
	public String getExtension() {
		return "json";
	}

	@Override
	public byte[] getContents() {
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
		gson.toJson(getResult(), writer);
		try {
			writer.flush();
		} catch (IOException e) {
			log.debug("Flushing failed", e);
		}
		return stream.toByteArray();
	}
}
