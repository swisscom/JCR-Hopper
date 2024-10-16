package com.swisscom.aem.tools.jcrhopper.osgi;

/**
 * Allows retrieving JCR-Hopper-related configs.
 */
public interface ConfigInfo {
	/**
	 * @return the path under which the {@link com.swisscom.aem.tools.impl.http.HopRunnerServlet} listens
	 */
	String getRunnerServletPath();
}
