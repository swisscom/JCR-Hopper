package com.swisscom.aem.tools.jcrhopper.config;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A script parameter definition.
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public final class Parameter {

	/**
	 * The parameter name.
	 * Used both as the name of the argument that needs to be passed to the script as well as the name of the variable inside the script.
	 */
	private String name;

	/**
	 * The default value to use if the argument for this parameter wasn’t passed.
	 * Will be evaluated the same way the argument would.
	 */
	private String defaultValue;

	/**
	 * A type hint to render the argument input, e.g. in HTML.
	 */
	private String type;

	/**
	 * How the argument is evaluated.
	 */
	private ArgumentEvaluation evaluation;

	public enum ArgumentEvaluation {
		/**
		 * Evaluate the passed argument as a string.
		 */
		STRING,
		/**
		 * Split the lines of the argument into a string array.
		 */
		LINES,
		/**
		 * Evaluate the passed argument as a JEXL template that results in a string.
		 */
		TEMPLATE,
		/**
		 * Evaluate the passed argument as a JEXL expression with an arbitrary result type.
		 */
		EXPRESSION,
	}
}
