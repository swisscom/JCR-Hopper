package com.swisscom.aem.tools.jcrhopper.osgi;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.swisscom.aem.tools.impl.HopProviderExtension;
import com.swisscom.aem.tools.impl.hops.ChildNodes;
import com.swisscom.aem.tools.impl.hops.ResolveNode;
import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.LogLevel;
import com.swisscom.aem.tools.jcrhopper.RunHandler;
import com.swisscom.aem.tools.jcrhopper.Runner;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.Script;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrOakAemContext;

@ExtendWith(AemContextExtension.class)
class RunnerServiceTest {
	public final AemContext context = new JcrOakAemContext();
	private RunnerService runnerService;

	@BeforeEach
	public void setUp() {
		context.registerInjectActivateService(ResolveNode.class);
		context.registerInjectActivateService(ChildNodes.class);
		context.registerInjectActivateService(HopProviderExtension.class);
		runnerService = context.registerInjectActivateService(RunnerService.class);

		context.create().resource("/test");
		context.create().resource("/test/one");
		context.create().resource("/test/two");
	}

	@Test
	public void builder_basic() throws HopperException, RepositoryException {

		final RunHandler runHandler = mock(RunHandler.class);
		final RunnerBuilder runnerBuilder = runnerService.builder().runHandler(runHandler);

		final Runner runner = runnerBuilder
			.build(new Script(Collections.singletonList(
				new ResolveNode.Config().withName("/test").withHops(Collections.singletonList(new ChildNodes.Config()))
			), LogLevel.DEBUG));

		runner.run(context.resourceResolver().adaptTo(Session.class), true);

		verify(runHandler).log(LogLevel.DEBUG, "Selecting node /test");
		verify(runHandler).log(LogLevel.DEBUG, "Found child node one on /test");
		verify(runHandler).log(LogLevel.DEBUG, "Found child node two on /test");
		verify(runHandler).log(eq(LogLevel.INFO), startsWith("JCR Hopper script finished after "));
		verify(runHandler).log(LogLevel.WARN, "Not saving changes as dry run is enabled");

	}

	@Test
	public void builder_trace() throws HopperException, RepositoryException {
		final RunHandler runHandler = mock(RunHandler.class);

		final Runner runner = runnerService.builder().runHandler(runHandler).build(new Script(Collections.singletonList(new ChildNodes.Config()), LogLevel.TRACE));
		runner.run(context.resourceResolver().getResource("/test").adaptTo(Node.class), false);

		verify(runHandler).log(LogLevel.TRACE, "Starting JCR Hopper script on node /test");
		verify(runHandler).log(LogLevel.DEBUG, "Found child node one on /test");
		verify(runHandler).log(LogLevel.DEBUG, "Found child node two on /test");
		verify(runHandler).log(eq(LogLevel.INFO), startsWith("JCR Hopper script finished after "));
		verify(runHandler).log(LogLevel.DEBUG, "Saving session");
		verify(runHandler).log(LogLevel.INFO, "Successfully saved changes in session");
	}
}