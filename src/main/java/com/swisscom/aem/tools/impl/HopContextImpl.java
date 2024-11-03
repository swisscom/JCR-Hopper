package com.swisscom.aem.tools.impl;

import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.config.Hop;
import com.swisscom.aem.tools.jcrhopper.config.HopConfig;
import com.swisscom.aem.tools.jcrhopper.context.HopContext;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.script.ScriptEngineManager;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JxltEngine;
import org.slf4j.Logger;

/**
 * Helper for hops to execute their actions.
 */
@Slf4j
public class HopContextImpl implements JexlContext, HopContext {

	private final RunnerImpl runner;

	@Getter
	private final JexlEngine jexlEngine;

	private final JxltEngine templateEngine;

	@Getter
	private final JcrFunctionsImpl jcrFunctions;

	@SuppressWarnings("PMD.LooseCoupling")
	private final HopVariables variables;

	@Delegate(types = Logger.class)
	private final Logger loggerImpl;

	/**
	 * Create a hop context.
	 *
	 * @param runner         the runner to use for reading the script properties and accessing the run handler
	 * @param jexlEngine     the JEXL engine to use for expressions
	 * @param templateEngine the JEXL template engine to use for string expressions
	 * @param jcrFunctions   the <code>jcr:</code> helper implementation, useful to a few hop types
	 * @param variables      the variable holder for this run
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	public HopContextImpl(
		RunnerImpl runner,
		JexlEngine jexlEngine,
		JxltEngine templateEngine,
		JcrFunctionsImpl jcrFunctions,
		HopVariables variables
	) {
		this.runner = runner;
		this.jexlEngine = jexlEngine;
		this.templateEngine = templateEngine;
		this.jcrFunctions = jcrFunctions;
		this.variables = variables;
		loggerImpl = new HopContextLogger(runner.getScript().getLogLevel(), runner.getRunHandler());
	}

	@Override
	public void runHops(Node node, Iterable<HopConfig> hops) throws HopperException, RepositoryException {
		runHops(node, hops, Collections.emptyMap());
	}

	@Override
	public void runHops(Node node, Iterable<HopConfig> hops, Map<String, Object> additionalVariables)
		throws HopperException, RepositoryException {
		final HopContextImpl inner = childContext(node, additionalVariables);
		for (HopConfig hopConfig : hops) {
			inner.runHop(hopConfig);
		}
	}

	@Override
	public String evaluateTemplate(String template) {
		return templateEngine.createTemplate(template).prepare(this).asString();
	}

	@Override
	public Object evaluate(String expression) {
		return jexlEngine.createExpression(expression).evaluate(this);
	}

	@Override
	public boolean expressionMatches(String expression) {
		final Object result = evaluate(expression);
		if (result instanceof Boolean) {
			return (Boolean) result;
		}
		return Boolean.parseBoolean(result.toString());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void runHop(HopConfig hopConfig) throws HopperException, RepositoryException {
		final Hop<HopConfig> hop = (Hop<HopConfig>) runner
			.getKnownHops()
			.stream()
			.filter(h -> h.getConfigType().isInstance(hopConfig))
			.findFirst()
			.orElseThrow(() -> new HopperException("Hop config of type " + hopConfig.getClass() + " is not known"));

		hop.run(hopConfig, variables.getNode(), this);
	}

	@SuppressWarnings("PMD.LooseCoupling")
	private HopContextImpl childContext(Node node, Map<String, Object> additionalVariables) {
		final HopVariables childVariables = new HopVariables(variables, node);
		childVariables.putAll(additionalVariables);
		return new HopContextImpl(runner, jexlEngine, jexlEngine.createJxltEngine(), jcrFunctions, childVariables);
	}

	@Override
	public void setVariable(String name, Object value) {
		set(name, value);
	}

	@Override
	public Map<String, Object> getVariables() {
		return Collections.unmodifiableMap(variables);
	}

	@Override
	public void print(String message) {
		runner.getRunHandler().print(message);
	}

	@Override
	public ScriptEngineManager getScriptEngineManager() {
		return runner.getScriptEngineManager();
	}

	// region JEXL Context Accessors
	@Override
	public Object get(String key) {
		return variables.get(key);
	}

	@Override
	public boolean has(String key) {
		return variables.containsKey(key);
	}

	@Override
	public void set(String key, Object value) {
		variables.put(key, value);
	}

	//endregion

	// region Writer
	@Override
	public Writer getWriter() {
		return new PrintWriter(
			new Writer() {
				@Override
				public void write(char[] cbuf, int off, int len) {
					print(new String(cbuf, off, len));
				}

				@Override
				public void flush() {
					// No-op
				}

				@Override
				public void close() {
					// No-op
				}
			}
		);
	}
	// endregion
}
