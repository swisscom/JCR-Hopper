package com.swisscom.aem.tools.jcrhopper.impl;

import java.io.PrintWriter;

import com.swisscom.aem.tools.jcrhopper.api.PortalScriptContext;
import com.swisscom.aem.tools.jcrhopper.api.PortalScriptOutputWriter;

import lombok.Getter;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.wcm.api.PageManager;

/**
 * Default portal script context configuration.
 *
 */
public class DefaultPortalScriptContext implements PortalScriptContext {

	@Getter
	private final PortalScriptOutputWriter outputWriter;

	private final SlingHttpServletRequest request;

	@Getter
	private final PageManager pageManager;

	/**
	 * Creates a new portal script context with a servlet request as input and a writer for the output.
	 *
	 * @param request Servlet request
	 * @param writer  Print writer
	 */
	public DefaultPortalScriptContext(SlingHttpServletRequest request, PrintWriter writer) {
		this.request = request;
		final LogLevel logLevel = LogLevel.fromString(request.getParameter("log"));
		this.outputWriter = new DefaultPortalScriptOutput(writer, logLevel);
		pageManager = getResourceResolver().adaptTo(PageManager.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RequestParameterMap getParameters() {
		return request.getRequestParameterMap();
	}

	@Override
	public String getParameter(String name) {
		return request.getParameter(name);
	}

	@Override
	public RequestParameter getRequestParameter(String name) {
		return request.getRequestParameter(name);
	}

	@Override
	public ResourceResolver getResourceResolver() {
		return request.getResourceResolver();
	}

	@Override
	public String getPortalScriptServletUrl() {
		return request.getRequestURL().toString();
	}
}
