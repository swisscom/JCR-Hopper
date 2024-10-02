package com.swisscom.aem.tools.jcrhopper.config;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.ImmutableField")
public class Script {
	private List<HopConfig> hops = Collections.emptyList();
	private LogLevel logLevel = LogLevel.DEFAULT;
}
