package com.swisscom.aem.tools.jcrhopper.api;

import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.wcm.api.PageManager;

/**
 * The context for all portal scripts.
 *
 * 
 */
public interface PortalScriptContext {

	/**
	 * Returns the output writer. All messages are written to
	 * <ul>
	 * <li>Client (browser/cURL)</li>
	 * <li>Log file</li>
	 * </ul>
	 *
	 * @return Output writer
	 */
	PortalScriptOutputWriter getOutputWriter();

	/**
	 * Returns the parameter map. All request parameters can be obtained from this map.
	 *
	 * @return Parameter map
	 */
	RequestParameterMap getParameters();

	/**
	 * Returns a parameter for the given parameter name.
	 *
	 * @param name Parameter name.
	 * @return Parameter value
	 */
	String getParameter(String name);

	/**
	 * Returns the request parameter for the given name. This can be used if the client needs access to a POSTed file.
	 * <p>
	 * <pre>
	 *
	 * RequestParameter fileParameter = getRequestParameter(&quot;file&quot;);
	 * BufferedReader in = new BufferedReader(new InputStreamReader(fileParameter.getInputStream()));
	 * </pre>
	 *
	 * @param name Request parameter name
	 * @return Request parameter
	 */
	RequestParameter getRequestParameter(String name);

	/**
	 * Returns the portal script servlet URL.
	 *
	 * @return Portal script servlet URL
	 */
	String getPortalScriptServletUrl();

	/**
	 * Returns the resource resolver which is attached with the current session.
	 *
	 * @return Resource Resolver
	 */
	ResourceResolver getResourceResolver();

	/**
	 * Returns the page manager which is attached with the current session.
	 *
	 * @return PageManager
	 */
	PageManager getPageManager();
}
