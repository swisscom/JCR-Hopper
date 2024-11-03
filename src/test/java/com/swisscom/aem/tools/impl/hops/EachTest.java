package com.swisscom.aem.tools.impl.hops;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.Runner;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import com.swisscom.aem.tools.jcrhopper.config.Script;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrMockAemContext;
import java.util.Collections;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class EachTest {

	public final AemContext context = new JcrMockAemContext();
	private RunnerBuilder builder;
	private RunHandler mockRunHandler;

	@BeforeEach
	public void setUp() {
		context.create().resource("/content");
		context.create().resource("/content/child-1");
		context.create().resource("/content/child-2");
		context.create().resource("/content/third-child");
		context.create().resource("/content/other-child");

		mockRunHandler = mock(RunHandler.class);
		builder = Runner.builder().addHop(new Each(), new RunScript()).runHandler(mockRunHandler);
	}

	@Test
	public void basic() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					new Each.Config()
						.withExpression("['en-gb', 'en-us', 'en-ie']")
						.withIterator("lang")
						.withHops(
							Collections.singletonList(
								new RunScript.Config()
									.withExtension("js")
									.withCode(
										"var items = lang.split('-');\nwriter.print(items[1].toUpperCase() + ': ' + items[0]);"
									)
							)
						)
				)
			)
			.run(context.resourceResolver().getResource("/").adaptTo(Node.class), true);

		verify(mockRunHandler).print("GB: en");
		verify(mockRunHandler).print("US: en");
		verify(mockRunHandler).print("IE: en");
	}

	@Test
	public void primitive() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					new Each.Config()
						.withExpression("[1, 2, 3, 4]")
						.withHops(
							Collections.singletonList(
								new RunScript.Config().withExtension("js").withCode("writer.print(item*100);")
							)
						)
				)
			)
			.run(context.resourceResolver().getResource("/").adaptTo(Node.class), true);

		verify(mockRunHandler).print("100");
		verify(mockRunHandler).print("200");
		verify(mockRunHandler).print("300");
		verify(mockRunHandler).print("400");
	}

	@Test
	public void node_string() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					new Each.Config()
						.withExpression("[jcr:resolve(node, '/content/child-2'), jcr:resolve(node, '/content/third-child')]")
						.withAssumeNodes(true)
						.withHops(
							Collections.singletonList(
								new RunScript.Config().withExtension("js").withCode("writer.print(node.path);")
							)
						)
				)
			)
			.run(context.resourceResolver().getResource("/").adaptTo(Node.class), true);

		verify(mockRunHandler).print("/content/child-2");
		verify(mockRunHandler).print("/content/third-child");
	}

	@Test
	public void node_refs() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					new Each.Config()
						.withExpression("['/content', '/content/child-1', '/content/other-child']")
						.withAssumeNodes(true)
						.withHops(
							Collections.singletonList(
								new RunScript.Config().withExtension("js").withCode("writer.print(node.path);")
							)
						)
				)
			)
			.run(context.resourceResolver().getResource("/").adaptTo(Node.class), true);

		verify(mockRunHandler).print("/content");
		verify(mockRunHandler).print("/content/child-1");
		verify(mockRunHandler).print("/content/other-child");
	}

	@Test
	public void node_invalid() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					new Each.Config()
						.withExpression("[1, 2]")
						.withAssumeNodes(true)
						.withHops(
							Collections.singletonList(
								new RunScript.Config().withExtension("js").withCode("writer.print(node.path);")
							)
						)
				)
			)
			.run(context.resourceResolver().getResource("/").adaptTo(Node.class), true);

		verify(mockRunHandler, never()).print(any());
	}
}
