package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Collections;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.ElExecutor;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

@RequiredArgsConstructor
public class Declare implements ConfigurableAction {
	@Getter
	private final DeclareConfig config;

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		Map<String, String> declarations = config.declarations();
		if (declarations == null) {
			declarations = Collections.emptyMap();
		}
		final ElExecutor elExecutor = context.getElExecutor(vars);
		for (Map.Entry<String, String> declaration : declarations.entrySet()) {
			try {
				final String key = elExecutor.evaluateTemplate(declaration.getKey());
				final Object value = elExecutor.evaluate(declaration.getValue());
				context.debug("Declaring {}={}", key, value);
				vars.put(
					key,
					value
				);
			} catch (Exception e) {
				context.error("Error evaluating expression " + declaration.getValue(), e);
			}
		}
	}
}
