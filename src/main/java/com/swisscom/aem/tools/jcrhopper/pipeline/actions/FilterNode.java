package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.Getter;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.DerivedMap;
import com.swisscom.aem.tools.jcrhopper.pipeline.ElExecutor;
import com.swisscom.aem.tools.jcrhopper.pipeline.Pipeline;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

public class FilterNode implements ConfigurableAction {
	@Getter
	private final FilterNodeConfig config;
	private final Pipeline pipeline;

	/**
	 * @param config for class
	 */
	public FilterNode(FilterNodeConfig config) {
		this.config = config;
		this.pipeline = new Pipeline(config.actions());
	}

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final ElExecutor elExecutor = context.getElExecutor(vars);
		if (elExecutor.expressionMatches(config.expression())) {
			context.info("Node {} matches filter expression {}", node.getPath(), config.expression());
			final Map<String, Object> childVars = new DerivedMap<>(vars);
			childVars.put("expression", config.expression());
			childVars.put("expressionValue", elExecutor.evaluate(config.expression()));
			pipeline.run(node, childVars, context);
		} else {
			context.debug("Node {} does not match filter expression {}", node.getPath(), config.expression());
		}
	}
}

