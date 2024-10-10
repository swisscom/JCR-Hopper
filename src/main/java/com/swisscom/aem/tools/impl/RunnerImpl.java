package com.swisscom.aem.tools.impl;

import com.swisscom.aem.tools.impl.file.FileUtils;
import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.Runner;
import com.swisscom.aem.tools.jcrhopper.config.File;
import com.swisscom.aem.tools.jcrhopper.config.Hop;
import com.swisscom.aem.tools.jcrhopper.config.HopConfig;
import com.swisscom.aem.tools.jcrhopper.config.Parameter;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import com.swisscom.aem.tools.jcrhopper.config.Script;
import com.swisscom.aem.tools.jcrhopper.context.HopContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
@SuppressFBWarnings(value = "OPM_OVERLY_PERMISSIVE_METHOD", justification = "API surface")
public class RunnerImpl implements Runner {

	@Getter
	private final Set<Hop<?>> knownHops;

	private final Map<String, Object> utils;
	private final Map<String, Object> variables;
	private final Map<String, Function<String, File>> fileTypeSuppliers;

	@Getter
	private final RunHandler runHandler;

	@Getter
	private final Script script;

	@Override
	public void run(Session session, boolean commitAfterRun) throws HopperException, RepositoryException {
		run(session, commitAfterRun, Collections.emptyMap());
	}

	@Override
	public void run(Node node, boolean commitAfterRun) throws HopperException, RepositoryException {
		run(node, commitAfterRun, Collections.emptyMap());
	}

	@Override
	public void run(Session session, boolean commitAfterRun, Map<String, String> arguments) throws HopperException, RepositoryException {
		run(session.getRootNode(), commitAfterRun, arguments);
	}

	@Override
	@SuppressWarnings("PMD.LooseCoupling")
	public void run(Node node, boolean commitAfterRun, Map<String, String> arguments) throws HopperException, RepositoryException {
		final JexlBuilder jexlBuilder = new JexlBuilder();
		final Session session = node.getSession();

		final HopVariables variables = new HopVariables(this.variables, node);
		final List<File> knownFiles = new LinkedList<>();
		final JcrFunctionsImpl jcrFunctions = new JcrFunctionsImpl(session);

		registerUtils(knownFiles, jcrFunctions, variables, jexlBuilder);

		jexlBuilder.antish(false);
		jexlBuilder.permissions(JexlPermissions.UNRESTRICTED);

		final JexlEngine jexlEngine = jexlBuilder.create();
		final HopContext context = new HopContextImpl(this, jexlEngine, jexlEngine.createJxltEngine(), jcrFunctions, variables);

		fillParameters(context, arguments);

		final long ts = System.currentTimeMillis();
		context.trace("Starting JCR Hopper script on node {} at {}", node.getPath(), ts);
		for (HopConfig hopConfig : script.getHops()) {
			context.runHop(hopConfig);
		}
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

	private void fillParameters(HopContext context, Map<String, String> arguments) {
		final List<Parameter> parameters = script.getParameters();
		final Map<String, Object> argumentMap = new HashMap<>(parameters.size());
		for (Parameter parameter : parameters) {
			String argument = arguments.get(parameter.getName());
			if (argument == null) {
				argument = parameter.getDefaultValue();
			}
			final Object argumentValue;
			switch (Optional.ofNullable(parameter.getEvaluation()).orElse(Parameter.ArgumentEvaluation.STRING)) {
				case LINES:
					argumentValue = Optional.ofNullable(argument)
						.map(str -> str.split("\\R"))
						.map(Arrays::stream)
						.orElseGet(Stream::empty)
						.filter(StringUtils::isNotBlank)
						.collect(Collectors.toList());
					break;
				case TEMPLATE:
					argumentValue = Optional.ofNullable(argument).map(context::evaluateTemplate).orElse(null);
					break;
				case EXPRESSION:
					argumentValue = Optional.ofNullable(argument).map(context::evaluate).orElse(null);
					break;
				case STRING:
				// Fall through
				default:
					argumentValue = argument;
			}
			argumentMap.put(parameter.getName(), argumentValue);
		}
		context.setVariable("args", argumentMap);
	}
}
