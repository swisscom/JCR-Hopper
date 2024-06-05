package com.swisscom.aem.tools.jcrhopper.pipeline;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

@FunctionalInterface
public interface Action {

	/**
	 * Runs the action.
	 *
	 * @param node The node to run the action on
	 * @param vars The variables to use
	 * @param context The context to use
	 * @throws RepositoryException If a repository error occurs
	 * @throws PipelineException If a pipeline error occurs
	 */
	void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException;

	/**
	 * Runs the action.
	 *
	 * @param node The node to run the action on
	 * @param context The context to use
	 * @throws RepositoryException If a repository error occurs
	 * @throws PipelineException If a pipeline error occurs
	 */
	default void run(Node node, PipelineContext context) throws PipelineException, RepositoryException {
		final Map<String, Object> usefulStarterVariables = new HashMap<>();
		usefulStarterVariables.put("root", node);
		usefulStarterVariables.put("date", Calendar.getInstance());
		runWith(node, usefulStarterVariables, context);
	}

	/**
	 * Runs the action.
	 *
	 * @param node The node to run the action on
	 * @param vars The variables to use
	 * @param context The context to use
	 * @throws RepositoryException If a repository error occurs
	 * @throws PipelineException If a pipeline error occurs
	 */
	default void runWith(Node node, Map<String, Object> vars, PipelineContext context) throws PipelineException, RepositoryException {
		final Map<String, Object> varsWithNode = new DerivedMap<>(vars);
		varsWithNode.put("node", node);
		run(node, varsWithNode, context);
	}


}
