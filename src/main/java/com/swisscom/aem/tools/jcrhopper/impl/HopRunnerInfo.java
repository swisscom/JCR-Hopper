package com.swisscom.aem.tools.jcrhopper.impl;

import com.swisscom.aem.tools.jcrhopper.osgi.ConfigInfo;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = Resource.class)
public class HopRunnerInfo {

	@Inject
	@OSGiService
	private ConfigInfo info;

	public String getEndpoint() {
		return info.getRunnerServletPath();
	}
}
