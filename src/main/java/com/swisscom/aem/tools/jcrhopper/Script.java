package com.swisscom.aem.tools.jcrhopper;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Script {
	private final List<HopConfig> hops;
	private final LogLevel logLevel;
}
