package com.swisscom.aem.tools.jcrhopper;

import java.util.Map;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public interface Runner {
	/**
	 * @return a {@link RunnerBuilder} for configuring a new {@link Runner}
	 */
	static RunnerBuilder builder() {
		return new RunnerBuilder();
	}

	/**
	 * Runs the script associated with this runner.
	 * <p>
	 * Will start at the root node of the given session.
	 *
	 * @param session        the JCR session to use
	 * @param commitAfterRun set true to save the JCR session at the end of the script run
	 * @throws HopperException     if one of the hops encounters a node it cannot handle and is configured to throw
	 * @throws RepositoryException if an error occurs during JCR manipulation
	 */
	void run(Session session, boolean commitAfterRun) throws HopperException, RepositoryException;

	/**
	 * Runs the script associated with this runner.
	 *
	 * @param node           the node with which script processing should start
	 * @param commitAfterRun set true to save the JCR session at the end of the script run
	 * @param arguments      the arguments to pass to the script to fill the script’s declared parameters
	 * @throws HopperException     if one of the hops encounters a node it cannot handle and is configured to throw
	 * @throws RepositoryException if an error occurs during JCR manipulation
	 */
	void run(Node node, boolean commitAfterRun, Map<String, String> arguments) throws HopperException, RepositoryException;

	/**
	 * Runs the script associated with this runner.
	 * <p>
	 * Will start at the root node of the given session.
	 *
	 * @param session        the JCR session to use
	 * @param commitAfterRun set true to save the JCR session at the end of the script run
	 * @param arguments      the arguments to pass to the script to fill the script’s declared parameters
	 * @throws HopperException     if one of the hops encounters a node it cannot handle and is configured to throw
	 * @throws RepositoryException if an error occurs during JCR manipulation
	 */
	void run(Session session, boolean commitAfterRun, Map<String, String> arguments) throws HopperException, RepositoryException;

	/**
	 * Runs the script associated with this runner.
	 *
	 * @param node           the node with which script processing should start
	 * @param commitAfterRun set true to save the JCR session at the end of the script run
	 * @throws HopperException     if one of the hops encounters a node it cannot handle and is configured to throw
	 * @throws RepositoryException if an error occurs during JCR manipulation
	 */
	void run(Node node, boolean commitAfterRun) throws HopperException, RepositoryException;
}
