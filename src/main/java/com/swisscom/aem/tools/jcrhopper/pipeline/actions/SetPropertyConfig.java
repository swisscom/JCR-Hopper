package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConflictResolution;

@AllArgsConstructor
@Accessors(fluent = true)
@Data
public class SetPropertyConfig {
	@NonNull
	private String propertyName;
	@NonNull
	private String value;
	private ConflictResolution conflict = ConflictResolution.FORCE;
}
