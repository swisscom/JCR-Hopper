package com.swisscom.aem.tools.impl.hops;

import static com.swisscom.aem.tools.testsupport.AemUtil.childNames;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.Runner;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.config.Script;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrOakAemContext;
import java.util.Arrays;
import java.util.Collections;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class MoveNodeTest {

	public final AemContext context = new JcrOakAemContext();
	private RunnerBuilder builder;
	private ResourceResolver resolver;
	private Session session;

	@BeforeEach
	public void setUp() {
		context.create().resource("/content");
		context.create().resource("/content/test");
		context.create().resource("/content/test/child2");
		context.create().resource("/content/test/child1");

		builder = Runner.builder().addHop(new MoveNode());

		resolver = context.resourceResolver();
		session = resolver.adaptTo(Session.class);
	}

	@Test
	public void move_sameParent() throws RepositoryException, HopperException {
		assertEquals(Arrays.asList("child2", "child1"), childNames(resolver.getResource("/content/test")));

		builder.build(new Script(new MoveNode.Config().withNewName("child3"))).run(session.getNode("/content/test/child2"), true);

		assertEquals(Arrays.asList("child3", "child1"), childNames(resolver.getResource("/content/test")));
	}

	@Test
	public void move_newParent() throws RepositoryException, HopperException {
		builder.build(new Script(new MoveNode.Config().withNewName("../test"))).run(session.getNode("/content/test"), true);

		assertEquals(Arrays.asList("child2", "child1"), childNames(resolver.getResource("/test")));
		assertEquals(Collections.emptyList(), childNames(resolver.getResource("/content")));
	}
}
