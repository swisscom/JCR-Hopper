package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.DerivedMap;
import com.swisscom.aem.tools.jcrhopper.pipeline.Pipeline;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

@AllArgsConstructor
public class Each implements ConfigurableAction {
	private final EachConfig config;
	private final Pipeline pipeline;

	/**
	 * @param config for class
	 */
	public Each(EachConfig config) {
		this.config = config;
		this.pipeline = new Pipeline(config.actions());
	}

	@Override
	public Object getConfig() {
		return null;
	}

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final Object items = context.evaluate(vars, config.expression());
		if (items instanceof Iterable) {
			for (Object item : (Iterable<?>) items) {
				runWith(item, node, vars, context);
			}
		} else if (items instanceof Iterator) {
			while (((Iterator<?>) items).hasNext()) {
				runWith(((Iterator<?>) items).next(), node, vars, context);
			}
		} else if (items.getClass().isArray()) {
			for (Object item : (Object[]) items) {
				runWith(item, node, vars, context);
			}
		} else {
			runWith(items, node, vars, context);
		}
	}

	private void runWith(Object item,
						 Node initialNode,
						 Map<String, Object> vars,
						 PipelineContext context) throws PipelineException, RepositoryException {
		final Map<String, Object> resultVars = new DerivedMap<>(vars);
		resultVars.put(config.iterator(), item);

		Node node = initialNode;
		if (config.assumeNodes()) {
			Node iterationNode = null;
			if (item instanceof Node) {
				iterationNode = (Node) item;
			} else if (item instanceof String) {
				iterationNode = context.getJcrFunctions().resolve((String) item);
			}
			if (iterationNode == null) {
				context.error("Iteration item {} could not be resolved as node", item);
				return;
			}
			context.debug("Iterating node {}", iterationNode.getPath());

			resultVars.put("prevNode", node);
			node = iterationNode;
		} else {
			context.debug("Iterating non-node value {} accessible as {}", item, config.iterator());
		}

		pipeline.runWith(node, resultVars, context);
	}
}
