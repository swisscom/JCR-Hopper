package com.swisscom.aem.tools.jcrhopper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.swisscom.aem.tools.jcrhopper.impl.hops.ChildNodes;
import com.swisscom.aem.tools.jcrhopper.impl.hops.CopyNode;
import com.swisscom.aem.tools.jcrhopper.impl.hops.CreateChildNode;
import com.swisscom.aem.tools.jcrhopper.impl.hops.Declare;
import com.swisscom.aem.tools.jcrhopper.impl.hops.Each;
import com.swisscom.aem.tools.jcrhopper.impl.hops.FilterNode;
import com.swisscom.aem.tools.jcrhopper.impl.hops.MoveNode;
import com.swisscom.aem.tools.jcrhopper.impl.hops.NodeQuery;
import com.swisscom.aem.tools.jcrhopper.impl.hops.RenameProperty;
import com.swisscom.aem.tools.jcrhopper.impl.hops.ReorderNode;
import com.swisscom.aem.tools.jcrhopper.impl.hops.ResolveNode;
import com.swisscom.aem.tools.jcrhopper.impl.hops.RunScript;
import com.swisscom.aem.tools.jcrhopper.impl.hops.SetProperty;
import com.swisscom.aem.tools.jcrhopper.impl.hops.Try;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrOakAemContext;

@ExtendWith(AemContextExtension.class)
class RunnerTest {
	public static final HashSet<Hop<?>> ALL_HOPS = new HashSet<>(Arrays.asList(
		new ChildNodes(),
		new CopyNode(),
		new CreateChildNode(),
		new Declare(),
		new Each(),
		new FilterNode(),
		new MoveNode(),
		new NodeQuery(),
		new RenameProperty(),
		new ReorderNode(),
		new ResolveNode(),
		new RunScript(),
		new SetProperty(),
		new Try()
	));
	public final AemContext context = new JcrOakAemContext();
	public final RunHandler runHandler = Mockito.mock(RunHandler.class);

	@Test
	public void simple() throws RepositoryException, HopperException {
		final Runner runner = new Runner(
			runHandler,
			new Script(
				Arrays.asList(
					new SetProperty.Config().withPropertyName("test").withValue("true"),
					new CreateChildNode.Config().withName("cool-item").withHops(Collections.singletonList(
						new SetProperty.Config().withPropertyName("TestProp").withValue("'TestValue'")
					))
				),
				LogLevel.TRACE
			),
			false,
			ALL_HOPS
		);

		final Resource root = context.resourceResolver().getResource("/");
		final Node rootNode = root.adaptTo(Node.class);
		runner.run(rootNode);

		assertTrue(root.getValueMap().get("test", false));
		assertEquals("true", root.getValueMap().get("test", String.class));
		assertEquals(Boolean.TRUE, root.getValueMap().get("test", Boolean.class));

		Resource coolItem = root.getChild("cool-item");
		assertTrue(coolItem.isResourceType("nt:unstructured"));
		assertEquals("TestValue", coolItem.getValueMap().get("TestProp", String.class));
	}

	@Test
	public void manipulation() throws RepositoryException, HopperException {
		context.load().json("/jcr/simple-jcr.json", "/root");
		Resource root = context.resourceResolver().getResource("/root");

		assertEquals("val1", root.getValueMap().get("prop1", Object.class));

		final Runner runner = new Runner(
			runHandler,
			new Script(
				Arrays.asList(
					new SetProperty.Config().withPropertyName("prop1").withValue("33"),
					new ResolveNode.Config().withName("child1").withHops(Arrays.asList(
						new RenameProperty.Config().withPropertyName("prop1").withNewName("prop33"),
						new CreateChildNode.Config().withName("some-child").withHops(Arrays.asList(
							new MoveNode.Config().withNewName("some-other-child"),
							new CreateChildNode.Config().withName("${node.name}-child1"),
							new CreateChildNode.Config().withName("${node.name}-child2"),
							new CreateChildNode.Config().withName("${node.name}-child3"),
							new CreateChildNode.Config().withName("bastard-child").withHops(Collections.singletonList(
								new ReorderNode.Config().withBefore("some-other-child-child3")
							)),
							new ChildNodes.Config().withNamePattern("${node.name}-*").withHops(Arrays.asList(
								new SetProperty.Config().withPropertyName("node-tokens").withValue("str:splitPreserveAllTokens(node.name, '-', -1)"),
								new SetProperty.Config().withPropertyName("node-index").withValue("counter"),
								new SetProperty.Config().withPropertyName("node-name-number").withValue("str:replacePattern(node.name, '^.*([0-9]+)$', '$1')"),
								new SetProperty.Config().withPropertyName("dummy").withValue("!true"),
								new FilterNode.Config().withExpression("jcr:val(node, 'node-index') < 2").withHops(Arrays.asList(
									new SetProperty.Config().withPropertyName("node-name-number").withValue("jcr:val(node, 'node-name-number')+20"),
									new RenameProperty.Config().withPropertyName("dummy").withNewName("/dev/null")
								))
							)),
							new CreateChildNode.Config().withName("${node.parent.name}-child6").withHops(Collections.singletonList(
								new MoveNode.Config().withNewName("/dev/null")
							))
						))
					))
				),
				LogLevel.TRACE
			),
			false,
			ALL_HOPS
		);

		runner.run(root.adaptTo(Node.class));
		verifyManipulation(root);

		new Runner(
			runHandler,
			new Script(
				Collections.singletonList(new CopyNode.Config().withNewName("/root-2")),
				LogLevel.TRACE
			),
			false,
			ALL_HOPS
		).run(root.adaptTo(Node.class));
		verifyManipulation(context.resourceResolver().getResource("/root-2"));

		new Runner(
			runHandler,
			new Script(
				Collections.singletonList(new CopyNode.Config().withNewName("/root-3")),
				LogLevel.TRACE
			),
			true,
			ALL_HOPS
		).run(root.adaptTo(Node.class));
		verifyManipulation(context.resourceResolver().getResource("/root-3"));
	}

	private void verifyManipulation(Resource root) {
		assertEquals(33L, root.getValueMap().get("prop1", Object.class));
		Resource child1 = root.getChild("child1");
		assertNull(child1.getValueMap().get("prop1", String.class));
		assertTrue(child1.getValueMap().get("prop33", false));

		Resource someOtherChild = child1.getChild("some-other-child");
		assertNotNull(someOtherChild);
		List<Resource> children = StreamSupport.stream(Spliterators.spliteratorUnknownSize(someOtherChild.listChildren(), Spliterator.ORDERED), false)
			.collect(Collectors.toList());

		assertEquals(4, children.size());
		assertEquals(Arrays.asList("some-other-child-child1", "some-other-child-child2", "bastard-child", "some-other-child-child3"),
			children.stream().map(Resource::getName).collect(Collectors.toList()));

		assertArrayEquals(
			new String[]{"some", "other", "child", "child1"},
			children.get(0).getValueMap().get("node-tokens", String[].class)
		);
		assertEquals(0, (long) children.get(0).getValueMap().get("node-index", -1));
		assertEquals("120", children.get(0).getValueMap().get("node-name-number", ""));
		assertNull(children.get(0).getValueMap().get("dummy", Boolean.class));

		assertArrayEquals(
			new String[]{"some", "other", "child", "child2"},
			children.get(1).getValueMap().get("node-tokens", String[].class)
		);
		assertEquals(1L, (long) children.get(1).getValueMap().get("node-index", -1));
		assertEquals("220", children.get(1).getValueMap().get("node-name-number", ""));
		assertNull(children.get(1).getValueMap().get("dummy"));

		assertNull(children.get(2).getValueMap().get("node-tokens"));
		assertNull(children.get(2).getValueMap().get("node-index"));
		assertNull(children.get(2).getValueMap().get("node-name-number"));
		assertEquals(Boolean.FALSE, children.get(3).getValueMap().get("dummy", Boolean.class));

		assertArrayEquals(
			new String[]{"some", "other", "child", "child3"},
			children.get(3).getValueMap().get("node-tokens", String[].class)
		);
		assertEquals(2L, (long) children.get(3).getValueMap().get("node-index", -1));
		assertEquals("3", children.get(3).getValueMap().get("node-name-number", ""));
		assertEquals(Boolean.FALSE, children.get(3).getValueMap().get("dummy"));
	}

	@Test
	public void query() throws RepositoryException, HopperException {
		context.load().json("/jcr/query.json", "/root");
		Resource root = context.resourceResolver().getResource("/root");

		final Runner runner = new Runner(
			runHandler,
			new Script(
				Arrays.asList(
					new NodeQuery.Config()
						.withQuery("SELECT * FROM [nt:unstructured] as n WHERE NAME(n) = 'test-item'")
						.withCounterName("item")
						.withHops(Arrays.asList(
							new SetProperty.Config().withPropertyName("index").withValue("item"),
							new ChildNodes.Config().withHops(Collections.singletonList(
								new SetProperty.Config().withPropertyName("query").withValue("query")
							))
						))
				),
				LogLevel.TRACE
			),
			false,
			ALL_HOPS
		);

		final Node rootNode = root.adaptTo(Node.class);
		runner.run(rootNode);

		Resource changedItem;

		changedItem = root.getChild("test-item");
		assertEquals(0, (int) changedItem.getValueMap().get("index", -1));

		assertNotNull(changedItem.getChild("subsub"));
		assertNotNull(changedItem.getChild("subsub").getValueMap().get("query"));

		changedItem = root.getChild("child").getChild("child3").getChild("test-item");
		assertEquals(1, (int) changedItem.getValueMap().get("index", -1));

		assertNull(changedItem.getChild("subsub"));

		changedItem = root.getChild("child").getChild("test-item");
		assertEquals(2, (int) changedItem.getValueMap().get("index", -1));

		assertNotNull(changedItem.getChild("subsub"));
		assertNotNull(changedItem.getChild("subsub").getValueMap().get("query"));

		changedItem = root.getChild("child").getChild("child1").getChild("subchild1").getChild("test-item");
		assertEquals(3, (int) changedItem.getValueMap().get("index", -1));

		assertNotNull(changedItem.getChild("subsub"));
		assertNotNull(changedItem.getChild("subsub").getValueMap().get("query"));
	}
}