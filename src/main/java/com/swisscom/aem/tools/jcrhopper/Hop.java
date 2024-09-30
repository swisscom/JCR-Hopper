package com.swisscom.aem.tools.jcrhopper;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.swisscom.aem.tools.jcrhopper.impl.HopContext;

public interface Hop<C extends HopConfig> {
	void run(C config, Node node, HopContext context) throws RepositoryException, HopperException;

	@Nonnull
	Class<C> getConfigType();

	@Nonnull
	String getConfigTypeName();
}
