package com.swisscom.aem.tools.jcrhopper.config;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.context.HopContext;

/**
 * Interface for an action that can be run on a node.
 *
 * @param <C> the config for this type of action
 */
public interface Hop<C extends HopConfig> {
	/**
	 * Runs this hop’s action on the given node.
	 *
	 * @param config  the config for this hop
	 * @param node    the node on which to run the action
	 * @param context the context with information about the current run and all EL variables currently known
	 * @throws RepositoryException if running the action caused an issue when changing the node
	 * @throws HopperException     if the hop config is invalid or not applicable to the given node
	 *                             should only be thrown if the config explicitly says so; otherwise, an error should be logged instead
	 */
	void run(C config, Node node, HopContext context) throws RepositoryException, HopperException;

	/**
	 * @return the class used to configure this hop
	 */
	@Nonnull
	Class<C> getConfigType();

	/**
	 * @return the name of the hop’s type, to correctly associate hop configs stored in (or stringified to) JSON
	 * TODO: Consider making this an annotation type to be set on the hop config
	 */
	@Nonnull
	String getConfigTypeName();
}
