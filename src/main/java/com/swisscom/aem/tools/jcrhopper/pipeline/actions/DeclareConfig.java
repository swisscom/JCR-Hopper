package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Collections;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(fluent = true)
@Data
public class DeclareConfig {
	@NonNull
	private Map<String, String> declarations = Collections.emptyMap();
}
