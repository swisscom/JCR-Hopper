package com.swisscom.aem.tools.impl.osgi;

import com.swisscom.aem.tools.jcrhopper.osgi.SampleContributor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

@Component
@Slf4j
public class JcrFolderSampleContributor implements SampleContributor {

	@Override
	public List<Sample> getSamples(ResourceResolver resourceResolver) {
		final Resource sampleRoot = resourceResolver.getResource("/apps/jcr-hopper/script-builder/scripts");
		final List<Sample> samples = new LinkedList<>();
		findSamples(sampleRoot, samples);
		return samples;
	}

	private void findSamples(Resource folder, List<Sample> samples) {
		for (Resource resource : folder.getChildren()) {
			if (isJsonFile(resource)) {
				samples.add(new Sample(getLabel(resource), getConfigJson(resource)));
			} else if (resource.isResourceType("nt:folder")) {
				findSamples(resource, samples);
			}
		}
	}

	private boolean isJsonFile(Resource resource) {
		return resource.isResourceType("nt:file") && resource.getName().endsWith(".json");
	}

	private String getConfigJson(Resource resource) {
		try (
			InputStream inputStream = resource.adaptTo(InputStream.class);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
		) {
			return reader.lines().collect(Collectors.joining("\n"));
		} catch (IOException ignored) {
			log.error("Error reading json file");
		}
		return "";
	}

	private String getLabel(Resource resource) {
		String label = resource.getValueMap().get("jcr:title", String.class);
		if (label == null) {
			label = StringUtils.removeEnd(resource.getName(), ".json");
		}
		return label;
	}
}
