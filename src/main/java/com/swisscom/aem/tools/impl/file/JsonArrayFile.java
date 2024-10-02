package com.swisscom.aem.tools.impl.file;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.google.gson.JsonArray;

@RequiredArgsConstructor
public class JsonArrayFile extends JsonFile {
	@Getter
	private final String name;

	@Getter(AccessLevel.PROTECTED)
	private final JsonArray result = new JsonArray();

	/**
	 * Append values to the array contents of this file.
	 *
	 * @param value the values to append, will be converted to JSON
	 */
	public void append(Object... value) {
		Arrays.stream(value).map(gson::toJsonTree).forEach(result::add);
	}
}
