package com.swisscom.aem.tools.jcrhopper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import lombok.Getter;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.swisscom.aem.tools.jcrhopper.impl.HopContext;
import com.swisscom.aem.tools.jcrhopper.impl.JcrFunctions;

@Getter
public class Runner {
	private final RunHandler runHandler;
	private final Set<Hop<?>> hops;
	private final Script script;

	public Runner(RunHandler runHandler, Set<Hop<?>> hops, Script script) {
		this.runHandler = runHandler;
		this.hops = hops;
		this.script = script;
	}

	public Runner(RunHandler runHandler, Set<Hop<?>> hops, String scriptJson) {
		this(runHandler, hops, Script.fromJson(hops, scriptJson));
	}

	public void run(Session session, boolean isDryRun) throws HopperException, RepositoryException {
		run(session.getRootNode(), isDryRun);
	}

	public void run(Node node, boolean isDryRun) throws HopperException, RepositoryException {
		run(node, new HashMap<>(), isDryRun);
	}

	public void run(Node node, Map<String, Object> variables, boolean isDryRun) throws HopperException, RepositoryException {
		final JexlBuilder jexlBuilder = new JexlBuilder();
		final Session session = node.getSession();

		final JcrFunctions jcrFunctions = new JcrFunctions(session);
		final Map<String, Object> utils = getUtils(jcrFunctions);
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
		context.runHops(node, script.getHops());
		if (isDryRun) {
			context.warn("Not saving changes as dry run is enabled");
		}
		else {
			context.debug("Saving session");
			session.save();
			context.info("Successfully saved changes in session");
		}
	}

	private Map<String, Object> getUtils(JcrFunctions jcrFunctions) {
		HashMap<String, Object> result = new HashMap<>();
		result.put("jcr", jcrFunctions);
		result.put("str", StringUtils.class);
		result.put("arr", ArrayUtils.class);
		result.put("arrays", Arrays.class);
		result.put("stream", Stream.class);
		result.put("class", Class.class);
		return Collections.unmodifiableMap(result);
	}
}
