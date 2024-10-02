package com.swisscom.aem.tools.jcrhopper.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Script {
	private final List<HopConfig> hops;
	private final LogLevel logLevel;
}
