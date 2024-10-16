package com.swisscom.aem.tools.impl.extension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.swisscom.aem.tools.impl.hops.Declare;
import com.swisscom.aem.tools.impl.hops.RunScript;
import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import com.swisscom.aem.tools.jcrhopper.config.Script;
import com.swisscom.aem.tools.jcrhopper.osgi.RunnerService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrMockAemContext;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.FrameworkUtil;

@ExtendWith(AemContextExtension.class)
class UtilProviderExtensionTest {

	public final AemContext context = new JcrMockAemContext();
	private RunnerService runnerService;

	private RunHandler mockRunHandler;

	@BeforeEach
	void setUp() {
		context.registerInjectActivateService(Declare.class);
		context.registerInjectActivateService(RunScript.class);

		context.registerInjectActivateService(HopProviderExtension.class);
		context.registerInjectActivateService(UtilProviderExtension.class);

		runnerService = context.registerInjectActivateService(RunnerService.class);
		mockRunHandler = mock(RunHandler.class);
	}

	@Test
	void utilsPresent() throws HopperException, RepositoryException {
		runnerService
			.builder()
			.runHandler(mockRunHandler)
			.build(
				new Script(
					new RunScript.Config()
						.withExtension("js")
						.withCode(
							"log.info('ArrayUtils: {}', utils.arr);" +
							"log.info('IOUtils: {}', utils.io);" +
							"log.info('StringUtils: {}', utils.str);" +
							"log.info('FrameworkUtil: {}', utils.framework);"
						)
				)
			)
			.run(context.resourceResolver().adaptTo(Session.class), false);

		verify(mockRunHandler).log(LogLevel.INFO, "ArrayUtils: " + ArrayUtils.class, null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "IOUtils: " + IOUtils.class, null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "StringUtils: " + StringUtils.class, null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "FrameworkUtil: " + FrameworkUtil.class, null, null);
	}
}
