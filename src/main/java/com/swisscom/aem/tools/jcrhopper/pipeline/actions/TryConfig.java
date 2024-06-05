package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import com.swisscom.aem.tools.jcrhopper.pipeline.Action;

@AllArgsConstructor
@Accessors(fluent = true)
@Getter
public class TryConfig {
	private boolean catchGeneric;
	@NonNull
	private final List<Action> actions;
}
