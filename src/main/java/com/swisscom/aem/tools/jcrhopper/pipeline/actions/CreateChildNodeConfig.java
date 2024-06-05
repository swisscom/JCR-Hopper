package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import com.day.cq.commons.jcr.JcrConstants;
import com.swisscom.aem.tools.jcrhopper.pipeline.Action;
import com.swisscom.aem.tools.jcrhopper.pipeline.ConflictResolution;

@AllArgsConstructor
@Accessors(fluent = true)
@Data
public class CreateChildNodeConfig {
	@NonNull
	private String name;
	private String primaryType = JcrConstants.NT_UNSTRUCTURED;
	private ConflictResolution conflict = ConflictResolution.IGNORE;
	private List<Action> actions = Collections.emptyList();
}
