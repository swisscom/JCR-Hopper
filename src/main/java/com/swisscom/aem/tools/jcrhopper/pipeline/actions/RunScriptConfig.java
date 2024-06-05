package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
@Data
public class RunScriptConfig {
	private String code = "";
	private String extension = "js";
}
