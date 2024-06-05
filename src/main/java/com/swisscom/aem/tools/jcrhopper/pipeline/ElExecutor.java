package com.swisscom.aem.tools.jcrhopper.pipeline;

import java.util.Map;

public interface ElExecutor {
	/**
	 * Makes the methods of the given object available as functions to call in the executor.
	 * <p>
	 * E.g. registerFunctions("empty", "") will enable empty:equals(someVar) to be used.
	 * <p>
	 * If a class is passed, the static methods of said class will be available.
	 * E.g. registerFunctions("str", StringUtils.class) will enable str:isEmpty(someVar) to be used.
	 *
	 * @param namespace The namespace to assign (the part before the colon)
	 * @param obj       The object/class whose functions to register.
	 */
	void registerFunctions(String namespace, Object obj);

	/**
	 * Registers a variable to be available for resolution in this executor.
	 *
	 * @param name  the name of the variable
	 * @param value the value of the variable
	 */
	void bindVariable(String name, Object value);

	/**
	 * Registers a map of variables to be available for resolution in this executor.
	 *
	 * @param variables the variables to bind
	 */
	default void bindVariables(Map<String, ?> variables) {
		for (Map.Entry<String, ?> entry : variables.entrySet()) {
			bindVariable(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Evaluates a string as JEXL template.
	 *
	 * @param el a JEXL template string, e.g. “Hello, my name is ${name}”
	 * @return the evaluated template
	 */
	String evaluateTemplate(String el);

	/**
	 * Evaluates a JEXL expression and returns its value.
	 *
	 * @param expression the expression to evaluate, e.g. 1+2
	 * @return the evaluated JEXL expression
	 */
	Object evaluate(String expression);

	/**
	 * Evaluates a JEXL expression and returns whether it evaluates to true.
	 *
	 * @param expression the expression to evaluate, e.g. a &gt; b
	 * @return whether the JEXL expression evaluates to true (or the string "true")
	 */
	default boolean expressionMatches(String expression) {
		final Object result = evaluate(expression);
		if (result instanceof Boolean) {
			return (Boolean) result;
		}
		return Boolean.parseBoolean(result.toString());
	}

	/**
	 * Run a JEXL script.
	 *
	 * @param script the script source
	 * @param params params to pass to the script
	 */
	void runScript(String script, Map<String, Object> params);
}
