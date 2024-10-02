package com.swisscom.aem.tools.jcrhopper;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.introspection.JexlPermissions;

import com.swisscom.aem.tools.impl.HopContextImpl;
import com.swisscom.aem.tools.impl.JcrFunctionsImpl;
import com.swisscom.aem.tools.impl.file.FileUtils;
import com.swisscom.aem.tools.jcrhopper.config.File;
import com.swisscom.aem.tools.jcrhopper.config.Hop;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import com.swisscom.aem.tools.jcrhopper.config.Script;
import com.swisscom.aem.tools.jcrhopper.context.HopContext;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Getter
@With
@RequiredArgsConstructor
@SuppressFBWarnings(value = "OPM_OVERLY_PERMISSIVE_METHOD", justification = "API surface")
public class Runner {
	private final Set<Hop<?>> knownHops;
	private final Map<String, Object> utils;
	private final Map<String, Object> variables;
	private final Map<String, Function<String, File>> fileTypeSuppliers;

	private final RunHandler runHandler;
	private final Script script;

	/**
	 * @return a {@link RunnerBuilder} for configuring a new {@link Runner}
	 */
	public static RunnerBuilder builder() {
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
	public void run(Session session, boolean commitAfterRun) throws HopperException, RepositoryException {
		run(session.getRootNode(), commitAfterRun);
	}

	/**
	 * Runs the script associated with this runner.
	 *
	 * @param node           the node with which script processing should start
	 * @param commitAfterRun set true to save the JCR session at the end of the script run
	 * @throws HopperException     if one of the hops encounters a node it cannot handle and is configured to throw
	 * @throws RepositoryException if an error occurs during JCR manipulation
	 */
	public void run(Node node, boolean commitAfterRun) throws HopperException, RepositoryException {
		final JexlBuilder jexlBuilder = new JexlBuilder();
		final Session session = node.getSession();

		final Map<String, Object> variables = new HashMap<>(this.variables);
		final List<File> knownFiles = new LinkedList<>();
		final JcrFunctionsImpl jcrFunctions = new JcrFunctionsImpl(session);

		registerUtils(knownFiles, jcrFunctions, variables, jexlBuilder);

		jexlBuilder.antish(false);
		jexlBuilder.permissions(JexlPermissions.UNRESTRICTED);

		final JexlEngine jexlEngine = jexlBuilder.create();
		final HopContext context = new HopContextImpl(
			this,
			jexlEngine,
			jexlEngine.createJxltEngine(),
			jcrFunctions,
			variables
		);

		final long ts = System.currentTimeMillis();
		context.trace("Starting JCR Hopper script on node {} at {}", node.getPath(), ts);
		context.runHops(node, script.getHops());
		context.info("JCR Hopper script finished after {}ms", System.currentTimeMillis() - ts);

		if (commitAfterRun) {
			context.debug("Saving session");
			session.save();
			context.info("Successfully saved changes in session");
		} else {
			context.warn("Not saving changes as dry run is enabled");
		}

		context.trace("Outputting files");
		for (File file : knownFiles) {
			runHandler.file(file);
		}
	}

	private void registerUtils(List<File> knownFiles, JcrFunctionsImpl jcrFunctions, Map<String, Object> variables, JexlBuilder jexlBuilder) {
		final FileUtils fileUtils = new FileUtils(knownFiles::add, fileTypeSuppliers, runHandler);

		Map<String, Object> utils = new HashMap<>(this.utils);
		utils.put("jcr", jcrFunctions);
		utils.put("file", fileUtils);
		utils = Collections.unmodifiableMap(utils);
		// For access in scripting languages other than JEXL
		variables.put("utils", utils);

		jexlBuilder.namespaces(utils);
	}
}
