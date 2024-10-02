package com.swisscom.aem.tools.jcrhopper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.introspection.JexlPermissions;

import com.swisscom.aem.tools.impl.HopContext;
import com.swisscom.aem.tools.impl.JcrFunctions;

@Getter
@With
@RequiredArgsConstructor
public class Runner {
	private final Script script;
	private final RunHandler runHandler;
	private final Set<Hop<?>> hops;
	private final Map<String, Object> variables;
	private final Map<String, Object> utils;

	public static RunnerBuilder builder() {
		return new RunnerBuilder();
	}

	public void run(Session session, boolean isDryRun) throws HopperException, RepositoryException {
		run(session.getRootNode(), isDryRun);
	}

	public void run(Node node, boolean isDryRun) throws HopperException, RepositoryException {
		final JexlBuilder jexlBuilder = new JexlBuilder();
		final Session session = node.getSession();

		final JcrFunctions jcrFunctions = new JcrFunctions(session);
		Map<String, Object> utils = new HashMap<>(this.utils);
		utils.put("jcr", jcrFunctions);
		utils = Collections.unmodifiableMap(utils);
		jexlBuilder.antish(false);
		jexlBuilder.permissions(JexlPermissions.UNRESTRICTED);
		jexlBuilder.namespaces(utils);
		// For access in scripting languages other than JEXL
		variables.put("utils", utils);
		final JexlEngine jexlEngine = jexlBuilder.create();

		HopContext context = new HopContext(
			this,
			jexlEngine,
			jexlEngine.createJxltEngine(),
			jcrFunctions,
			variables
		);
		final long ts = System.currentTimeMillis();
		context.trace("Starting JCR Hopper script on node {}", node.getPath());
		context.runHops(node, script.getHops());
		context.info("JCR Hopper script finished after {}ms", System.currentTimeMillis() - ts);
		if (isDryRun) {
			context.warn("Not saving changes as dry run is enabled");
		} else {
			context.debug("Saving session");
			session.save();
			context.info("Successfully saved changes in session");
		}
	}
}
