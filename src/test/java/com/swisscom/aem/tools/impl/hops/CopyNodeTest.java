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
import java.util.HashSet;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class CopyNodeTest {

	public final AemContext context = new JcrOakAemContext();
	private RunnerBuilder builder;
	private Session session;

	@BeforeEach
	public void setUp() {
		context.create().resource("/content", "prop1", 1, "prop2", "value2");
		context.create().resource("/content/child-1", "jcr:primaryType", "cq:Page");
		context.create().resource("/content/child-2");
		context.create().resource("/content/third-child");
		context.create().resource("/content/other-child");
		context.create().resource("/existing");
		context.create().resource("/existing/existing-child");

		builder = Runner.builder().addHop(new CopyNode(), new CreateChildNode());
		session = context.resourceResolver().adaptTo(Session.class);
	}

	@Test
	public void copy_empty() throws RepositoryException, HopperException {
		builder
			.build(
				new Script(
					new CopyNode.Config()
						.withNewName("content2")
						.withHops(Collections.singletonList(new CreateChildNode.Config().withName("new-child")))
				)
			)
			.run(session.getNode("/content"), true);

		final ResourceResolver resolver = context.resourceResolver();
		assertEquals(
			Arrays.asList("child-1", "child-2", "third-child", "other-child", "new-child"),
			childNames(resolver.getResource("/content2"))
		);

		assertEquals(
			new HashSet<>(Arrays.asList("prop1", "prop2", "jcr:primaryType")),
			resolver.getResource("/content2").getValueMap().keySet()
		);

		assertEquals("cq:Page", session.getNode("/content2/child-1").getPrimaryNodeType().getName());
	}

	@Test
	public void copy_inside() throws RepositoryException, HopperException {
		builder
			.build(
				new Script(
					new CopyNode.Config()
						.withNewName("content/child-2/content2")
						.withHops(Collections.singletonList(new CreateChildNode.Config().withName("new-child")))
				)
			)
			.run(session.getNode("/content"), true);

		final ResourceResolver resolver = context.resourceResolver();
		assertEquals(
			Arrays.asList("child-1", "child-2", "third-child", "other-child", "new-child"),
			childNames(resolver.getResource("/content/child-2/content2"))
		);

		assertEquals(
			new HashSet<>(Arrays.asList("prop1", "prop2", "jcr:primaryType")),
			resolver.getResource("/content/child-2/content2").getValueMap().keySet()
		);

		assertEquals("cq:Page", session.getNode("/content/child-2/content2/child-1").getPrimaryNodeType().getName());
	}

	@Test
	public void copy_root() {
		assertThrows(HopperException.class, () -> {
			builder.build(new Script(new CopyNode.Config().withNewName("test-child"))).run(session.getRootNode(), true);
		});
	}

	@Test
	public void copy_existing_ignore() throws RepositoryException, HopperException {
		builder
			.build(
				new Script(
					new CopyNode.Config()
						.withNewName("existing")
						.withConflict(ConflictResolution.IGNORE)
						.withHops(Collections.singletonList(new CreateChildNode.Config().withName("new-child")))
				)
			)
			.run(session.getNode("/content"), true);

		final ResourceResolver resolver = context.resourceResolver();
		assertEquals(Collections.singletonList("existing-child"), childNames(resolver.getResource("/existing")));
	}

	@Test
	public void copy_existing_force() throws RepositoryException, HopperException {
		builder
			.build(
				new Script(
					new CopyNode.Config()
						.withNewName("existing")
						.withConflict(ConflictResolution.FORCE)
						.withHops(Collections.singletonList(new CreateChildNode.Config().withName("new-child")))
				)
			)
			.run(session.getNode("/content"), true);

		final ResourceResolver resolver = context.resourceResolver();
		assertEquals(
			Arrays.asList("child-1", "child-2", "third-child", "other-child", "new-child"),
			childNames(resolver.getResource("/existing"))
		);

		assertEquals(
			new HashSet<>(Arrays.asList("prop1", "prop2", "jcr:primaryType")),
			resolver.getResource("/existing").getValueMap().keySet()
		);

		assertEquals("cq:Page", session.getNode("/existing/child-1").getPrimaryNodeType().getName());
	}

	@Test
	public void copy_existing_throw() {
		assertThrows(HopperException.class, () -> {
			builder
				.build(
					new Script(
						new CopyNode.Config()
							.withNewName("existing")
							.withConflict(ConflictResolution.THROW)
							.withHops(Collections.singletonList(new CreateChildNode.Config().withName("new-child")))
					)
				)
				.run(session.getNode("/content"), true);
		});
	}

	@Test
	public void copy_existingInside_ignore() throws RepositoryException, HopperException {
		builder
			.build(
				new Script(
					new CopyNode.Config()
						.withNewName("content/child-1")
						.withConflict(ConflictResolution.IGNORE)
						.withHops(Collections.singletonList(new CreateChildNode.Config().withName("new-child")))
				)
			)
			.run(session.getNode("/content"), true);

		final ResourceResolver resolver = context.resourceResolver();
		assertEquals(Collections.emptyList(), childNames(resolver.getResource("/content/child-1")));

		assertEquals("cq:Page", session.getNode("/content/child-1").getPrimaryNodeType().getName());
	}

	@Test
	public void copy_existingInside_force() throws RepositoryException, HopperException {
		builder
			.build(
				new Script(
					new CopyNode.Config()
						.withNewName("content/child-1")
						.withConflict(ConflictResolution.FORCE)
						.withHops(Collections.singletonList(new CreateChildNode.Config().withName("new-child")))
				)
			)
			.run(session.getNode("/content"), true);

		final ResourceResolver resolver = context.resourceResolver();
		assertEquals(
			Arrays.asList("child-1", "child-2", "third-child", "other-child", "new-child"),
			childNames(resolver.getResource("/content/child-1"))
		);

		assertEquals(
			new HashSet<>(Arrays.asList("prop1", "prop2", "jcr:primaryType")),
			resolver.getResource("/content/child-1").getValueMap().keySet()
		);

		assertEquals("cq:Page", session.getNode("/content/child-1/child-1").getPrimaryNodeType().getName());
	}

	@Test
	public void copy_existingInside_throw() {
		assertThrows(HopperException.class, () -> {
			builder
				.build(
					new Script(
						new CopyNode.Config()
							.withNewName("content/child-1")
							.withConflict(ConflictResolution.THROW)
							.withHops(Collections.singletonList(new CreateChildNode.Config().withName("new-child")))
					)
				)
				.run(session.getNode("/content"), true);
		});
	}
}
