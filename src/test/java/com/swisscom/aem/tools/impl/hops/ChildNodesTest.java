package com.swisscom.aem.tools.impl.hops;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.Runner;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import com.swisscom.aem.tools.jcrhopper.config.Script;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrOakAemContext;
import java.util.Collection;
import java.util.Collections;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class ChildNodesTest {

	public final AemContext context = new JcrOakAemContext();
	private RunnerBuilder builder;
	private Session session;
	private RunHandler mockRunHandler;

	@BeforeEach
	public void setUp() {
		context.create().resource("/content");
		context.create().resource("/content/child-1");
		context.create().resource("/content/child-2");
		context.create().resource("/content/third-child");
		context.create().resource("/content/other-child");

		mockRunHandler = mock(RunHandler.class);
		builder = Runner.builder().addHop(new ChildNodes()).runHandler(mockRunHandler);
		session = context.resourceResolver().adaptTo(Session.class);
	}

	@Test
	public void iterate_all() throws RepositoryException, HopperException {
		builder.build(new Script(Collections.singletonList(new ChildNodes.Config()), LogLevel.DEBUG)).run(session.getNode("/content"), true);

		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node child-1 on /content", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node child-2 on /content", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node third-child on /content", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node other-child on /content", null, null);
	}

	@Test
	public void iterate_wildcard() throws RepositoryException, HopperException {
		builder
			.build(new Script(Collections.singletonList(new ChildNodes.Config().withNamePattern("*")), LogLevel.DEBUG))
			.run(session.getNode("/content"), true);

		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node child-1 on /content", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node child-2 on /content", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node third-child on /content", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node other-child on /content", null, null);
	}

	@Test
	public void iterate_pattern() throws RepositoryException, HopperException {
		builder
			.build(new Script(Collections.singletonList(new ChildNodes.Config().withNamePattern("child-*")), LogLevel.DEBUG))
			.run(session.getNode("/content"), true);

		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node child-1 on /content", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node child-2 on /content", null, null);
		verify(mockRunHandler, never()).log(LogLevel.DEBUG, "Found child node third-child on /content", null, null);
		verify(mockRunHandler, never()).log(LogLevel.DEBUG, "Found child node other-child on /content", null, null);
	}

	@Test
	public void iterate_union() throws RepositoryException, HopperException {
		builder
			.build(
				new Script(
					Collections.singletonList(new ChildNodes.Config().withNamePattern("child-* | third-child")),
					LogLevel.DEBUG
				)
			)
			.run(session.getNode("/content"), true);

		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node child-1 on /content", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node child-2 on /content", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found child node third-child on /content", null, null);
		verify(mockRunHandler, never()).log(LogLevel.DEBUG, "Found child node other-child on /content", null, null);
	}
}
