package com.swisscom.aem.tools.jcrhopper.impl;

import com.google.gson.Gson;
import com.swisscom.aem.tools.jcrhopper.osgi.ConfigInfo;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = Resource.class)
public class HopRunnerInfo {

	@Inject
	@OSGiService
	private ScriptEngineManager scriptEngineManager;

	@Inject
	@OSGiService
	private ConfigInfo info;

	public String getEndpoint() {
		return info.getRunnerServletPath();
	}

	/**
	 * @return a map of script extensions to names that can be used with the runScript hop type
	 */
	public String getValidScriptingLanguages() {
		final Map<String, String> extensions = scriptEngineManager
			.getEngineFactories()
			.stream()
			.collect(Collectors.toMap(fac -> fac.getExtensions().get(0), ScriptEngineFactory::getLanguageName));

		extensions.put("jexl", "JEXL"); // JEXL is always supported

		return new Gson().toJson(extensions);
	}
}
