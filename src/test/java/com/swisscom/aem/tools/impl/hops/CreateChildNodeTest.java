package com.swisscom.aem.tools.impl.hops;

import static com.swisscom.aem.tools.testsupport.AemUtil.childNames;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.Runner;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.config.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.config.Script;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrOakAemContext;
import java.util.Arrays;
import java.util.Collections;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class CreateChildNodeTest {

	public final AemContext context = new JcrOakAemContext();
	private RunnerBuilder builder;
	private Session session;

	@BeforeEach
	public void setUp() {
		context.create().resource("/content");
		context.create().resource("/content/child");
		context.create().resource("/content/child/one");

		builder = Runner.builder().addHop(new CreateChildNode());
		session = context.resourceResolver().adaptTo(Session.class);
	}

	@Test
	public void create_nonexisting() throws RepositoryException, HopperException {
		builder.build(new Script(new CreateChildNode.Config().withName("child-two"))).run(session.getNode("/content"), true);

		assertEquals(Arrays.asList("child", "child-two"), childNames(context.resourceResolver().getResource("/content")));
		assertEquals("nt:unstructured", session.getNode("/content/child-two").getPrimaryNodeType().getName());
	}

	@Test
	public void create_existing_abort() {
		assertThrows(HopperException.class, () -> {
			builder
				.build(
					new Script(
						new CreateChildNode.Config().withName("child").withConflict(ConflictResolution.THROW),
						new CreateChildNode.Config().withName("child2").withConflict(ConflictResolution.THROW)
					)
				)
				.run(session.getNode("/content"), true);
		});

		assertEquals(Collections.singletonList("child"), childNames(context.resourceResolver().getResource("/content")));
	}

	@Test
	public void create_existing_ignore_no_recurse() throws RepositoryException, HopperException {
		builder
			.build(
				new Script(
					new CreateChildNode.Config()
						.withName("child")
						.withConflict(ConflictResolution.IGNORE)
						.withRunOnExistingNode(false)
						.withHops(Collections.singletonList(new CreateChildNode.Config().withName("../child3"))),
					new CreateChildNode.Config().withName("child2").withConflict(ConflictResolution.THROW)
				)
			)
			.run(session.getNode("/content"), true);
		assertEquals(Arrays.asList("child", "child2"), childNames(context.resourceResolver().getResource("/content")));
	}

	@Test
	public void create_existing_ignore_recurse() throws RepositoryException, HopperException {
		builder
			.build(
				new Script(
					new CreateChildNode.Config()
						.withName("child")
						.withConflict(ConflictResolution.IGNORE)
						.withRunOnExistingNode(true)
						.withHops(Collections.singletonList(new CreateChildNode.Config().withName("../child3"))),
					new CreateChildNode.Config().withName("child2").withConflict(ConflictResolution.THROW)
				)
			)
			.run(session.getNode("/content"), true);
		assertEquals(Arrays.asList("child", "child3", "child2"), childNames(context.resourceResolver().getResource("/content")));
	}

	@Test
	public void create_existing_force() throws RepositoryException, HopperException {
		assertEquals("nt:unstructured", session.getNode("/content/child").getPrimaryNodeType().getName());
		builder
			.build(
				new Script(
					new CreateChildNode.Config()
						.withName("child")
						.withPrimaryType("cq:Page")
						.withConflict(ConflictResolution.FORCE)
						.withHops(Collections.singletonList(new CreateChildNode.Config().withName("../child3"))),
					new CreateChildNode.Config().withName("child2").withConflict(ConflictResolution.THROW)
				)
			)
			.run(session.getNode("/content"), true);
		assertEquals(Arrays.asList("child", "child3", "child2"), childNames(context.resourceResolver().getResource("/content")));
		assertEquals("cq:Page", session.getNode("/content/child").getPrimaryNodeType().getName());
	}
}
