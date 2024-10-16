package com.swisscom.aem.tools.impl.file;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonObjectFile extends JsonFile {

	@Getter
	private final String name;

	@Getter(AccessLevel.PROTECTED)
	private final JsonObject result = new JsonObject();

	/**
	 * Set a property on the object contents of this file.
	 *
	 * @param property the name of the property
	 * @param value    the value of the property, will be converted to JSON
	 */
	public void set(String property, Object value) {
		result.add(property, gson.toJsonTree(value));
	}
}
