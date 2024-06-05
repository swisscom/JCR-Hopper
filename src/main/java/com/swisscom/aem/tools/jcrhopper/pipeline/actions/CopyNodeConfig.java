package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import com.swisscom.aem.tools.jcrhopper.pipeline.Action;
import com.swisscom.aem.tools.jcrhopper.pipeline.ConflictResolution;

@AllArgsConstructor
@Accessors(fluent = true)
@Data
public class CopyNodeConfig {
	@NonNull
	private String newName;
	private ConflictResolution conflict = ConflictResolution.IGNORE;
	private List<Action> actions = Collections.emptyList();
}
