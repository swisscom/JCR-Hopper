package com.swisscom.aem.tools.jcrhopper.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;

import com.swisscom.aem.tools.jcrhopper.ActionRunner;
import com.swisscom.aem.tools.jcrhopper.MyTask;
import com.swisscom.aem.tools.jcrhopper.api.PortalScript;
import com.swisscom.aem.tools.jcrhopper.api.PortalScriptContext;

/**
 * Servlet which is used to execute Onetimer classes. The GET requests shows the usage to the user while the POST
 * requests executes the script.
 */

@Component(service = Servlet.class)
@SlingServletPaths("/bin/actionrunner")
@SlingServletResourceTypes(resourceTypes = "sling/unused", methods = "post")
public class PortalScriptServlet extends SlingAllMethodsServlet {

	private static final String SCRIPT_NAME = "ActionRunner";

	/**
	 * Executes the Onetimer class with the given arguments.
	 */
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		final PortalScriptContext context = new DefaultPortalScriptContext(request, response.getWriter());

		response.setContentType("text/html");

		if (isAbort(request)) {
			handleAbort(context);
		} else {
			executeOnetimerScript(response, context);
		}
	}

	private boolean isAbort(SlingHttpServletRequest request) {
		final String button = request.getParameter("submit");
		return StringUtils.equals(button, "Abort script");
	}

	private void executeOnetimerScript(SlingHttpServletResponse response, PortalScriptContext context) {

		try {

			final PortalScript onetimer = new ActionRunner(context);
			final String contentType = onetimer.getResponseContentType();
			response.setContentType(contentType);
			if (StringUtils.startsWith(contentType, "text/")) {
				response.setCharacterEncoding("utf-8");
			}

			// start timer
			final long start = System.nanoTime();

			context.getOutputWriter().info("Starting " + SCRIPT_NAME);

			onetimer.process();

			// stop timer
			final long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
			context.getOutputWriter().info("Successfully finished %s in %sms", SCRIPT_NAME, duration);

		} catch (Exception e) {
			logException(context, e);
		}
	}

	private void handleAbort(PortalScriptContext context) {
		MyTask.cancel(SCRIPT_NAME);
		context.getOutputWriter().info("Successfully aborted the script: %s", SCRIPT_NAME);
	}

	private void logException(final PortalScriptContext ctx, Exception e) {
		final StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		ctx.getOutputWriter().error(errors.toString());
		ctx.getOutputWriter().error("Error executing script: " + SCRIPT_NAME + "\n");
	}
}
