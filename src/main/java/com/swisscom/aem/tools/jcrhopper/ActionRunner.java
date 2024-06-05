package com.swisscom.aem.tools.jcrhopper;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;

import com.swisscom.aem.tools.jcrhopper.api.PortalScriptContext;
import com.swisscom.aem.tools.jcrhopper.impl.AbstractPortalScript;
import com.swisscom.aem.tools.jcrhopper.impl.LogLevel;
import com.swisscom.aem.tools.jcrhopper.pipeline.Pipeline;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;


public class ActionRunner extends AbstractPortalScript {

	private static final String PIPELINE_PARAM = "pipeline";

	/**
	 * Constructor.
	 *
	 * @param context the context
	 */
	public ActionRunner(PortalScriptContext context) {
		super(context);
	}

	@Override
	public void doProcess() throws Exception {
		final String json = getJsonToProcess();
		final Pipeline pipeline = Pipeline.fromJson(json);
		final Session session = getSession();

		final Node root = session.getRootNode();

		final LogLevel logLevel = LogLevel.fromString(getParameter("log"));
		pipeline.run(root, new PipelineContext(this, getContext().getParameters(), logLevel, session, isDryRun()));

		if (!isDryRun()) {
			session.save();
		}
	}

	private String getJsonToProcess() {
		final String json = getParameter(PIPELINE_PARAM);

		if (StringUtils.isBlank(json)) {
			throw new IllegalArgumentException(String.format("Missing parameter '%s'", PIPELINE_PARAM));
		}

		return json;
	}
}
