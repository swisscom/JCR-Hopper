package com.swisscom.aem.tools.testsupport;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.sling.api.resource.Resource;

public final class AemUtil {

	public static List<String> childNames(Resource parent) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(parent.listChildren(), Spliterator.ORDERED), false)
			.map(Resource::getName)
			.collect(Collectors.toList());
	}
}
