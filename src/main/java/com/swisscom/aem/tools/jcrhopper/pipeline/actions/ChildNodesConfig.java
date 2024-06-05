package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import com.swisscom.aem.tools.jcrhopper.pipeline.Action;

@AllArgsConstructor
@Accessors(fluent = true)
@Data
public class ChildNodesConfig {
	private String namePattern;
	private String counterName = "counter";
	@NonNull
	private List<Action> actions;
}
