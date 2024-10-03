package com.swisscom.aem.tools.impl.http;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.jcr.Session;
import javax.servlet.Servlet;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.swisscom.aem.tools.jcrhopper.Runner;
import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import com.swisscom.aem.tools.jcrhopper.osgi.RunnerService;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Component(
	service = Servlet.class,
	property = "sling.servlet.methods=post"
)
@ServiceDescription("JCR Hopper: Runner Servlet")
@SlingServletPaths(HopRunnerServlet.DEFAULT_PATH)
@Designate(
	ocd = HopRunnerServlet.HopRunnerServletDelegate.class
)
@Slf4j
public class HopRunnerServlet extends SlingAllMethodsServlet {
	static final String DEFAULT_PATH = "/bin/servlets/jcr-hopper/run";
	private static final String ABORT_ERROR_MESSAGE = "Script execution aborted with exception";

	@Getter
	private String servletPath;

	@Reference
	private RunnerService runnerService;

	@ObjectClassDefinition(name = "JCR Hopper: Runner Servlet")
	@interface HopRunnerServletDelegate {
		@SuppressWarnings("PMD.MethodNamingConventions")
		@AttributeDefinition(
			name = "Path",
			description = "Path under which the hop runner servlet is registered",
			defaultValue = DEFAULT_PATH
		)
		String[] sling_servlet_paths();

	}

	@Activate
	public void activate(HopRunnerServletDelegate config) {
		servletPath = config.sling_servlet_paths()[0];
	}

	@Override
	@SuppressFBWarnings(value = "REC_CATCH_EXCEPTION", justification = "All exception types should be sent to the caller")
	protected void doPost(
		@Nonnull SlingHttpServletRequest request,
		@Nonnull SlingHttpServletResponse response
	) throws IOException {
		response.setContentType("application/x-ndjson");
		response.setHeader("Transfer-Encoding", "chunked");
		final HttpChunkedResponseRunHandler runHandler = new HttpChunkedResponseRunHandler(response.getWriter());

		final String script = request.getParameter("script");
		if (StringUtils.isBlank(script)) {
			runHandler.log(LogLevel.ERROR, "No script to run in call to " + servletPath, null, null);
		}
		final boolean commitAfterRun = Boolean.parseBoolean(request.getParameter("commit"));

		try {
			final Runner runner = runnerService.builder()
				.addUtil("req", request.getRequestParameterMap())
				.runHandler(runHandler)
				.build(script);

			runner.run(request.getResourceResolver().adaptTo(Session.class), commitAfterRun);
		} catch (Exception e) {
			log.error(ABORT_ERROR_MESSAGE, e);
			runHandler.log(
				LogLevel.ERROR,
				ABORT_ERROR_MESSAGE,
				e,
				null
			);
		}
	}
}