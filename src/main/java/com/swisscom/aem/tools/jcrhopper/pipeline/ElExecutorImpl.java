package com.swisscom.aem.tools.jcrhopper.pipeline;

import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlScript;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.jexl3.MapContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElExecutorImpl implements ElExecutor {
	private final MapContext context = new MapContext();
	private final JexlEngine engine;
	private final JxltEngine templateEngine;

	/**
	 * Creates a new EL executor.
	 *
	 * @param engine The JEXL engine
	 * @param templateEngine The JXLT engine
	 */
	public ElExecutorImpl(JexlEngine engine, JxltEngine templateEngine) {
		this.engine = engine;
		this.templateEngine = templateEngine;
	}

	@Override
	public void registerFunctions(String namespace, Object obj) {
		context.set(namespace, obj);
	}

	@Override
	public void bindVariable(String name, Object value) {
		context.set(name, value);
	}

	@Override
	public String evaluateTemplate(String el) {
		return templateEngine.createTemplate(el).prepare(context).asString();
	}

	@Override
	public Object evaluate(String expression) {
		return engine.createExpression(expression).evaluate(context);
	}

	@Override
	public void runScript(String script, Map<String, Object> params) {
		final List<Map.Entry<String, Object>> paramsOrdered = new ArrayList<>(params.entrySet());
		final JexlScript preparedScript = engine.createScript(
			script,
			paramsOrdered.stream().map(Map.Entry::getKey).toArray(String[]::new)
		);
		preparedScript.execute(
			context,
			paramsOrdered.stream().map(Map.Entry::getValue).toArray(Object[]::new)
		);
	}
}
