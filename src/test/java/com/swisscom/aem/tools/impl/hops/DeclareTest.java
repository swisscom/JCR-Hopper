package com.swisscom.aem.tools.impl.hops;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.Runner;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import com.swisscom.aem.tools.jcrhopper.config.Script;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrMockAemContext;
import java.util.Arrays;
import java.util.Collections;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class DeclareTest {

	public final AemContext context = new JcrMockAemContext();
	private RunnerBuilder builder;
	private RunHandler mockRunHandler;

	@BeforeEach
	public void setUp() {
		mockRunHandler = mock(RunHandler.class);
		builder = Runner.builder().addHop(new Declare(), new RunScript(), new Each()).runHandler(mockRunHandler);
	}

	@Test
	public void declare_basic() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					new Declare.Config().withDeclarations(Collections.singletonMap("var1", "1")),
					new RunScript.Config().withExtension("jexl").withCode("writer.print(var1)")
				)
			)
			.run(context.resourceResolver().getResource("/").adaptTo(Node.class), true);

		verify(mockRunHandler).print("1");
	}

	@Test
	public void declare_node() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					new Declare.Config().withDeclarations(Collections.singletonMap("node", "{'path': 1}")),
					new RunScript.Config().withExtension("jexl").withCode("writer.print(node.path)")
				)
			)
			.run(context.resourceResolver().getResource("/").adaptTo(Node.class), true);

		verify(mockRunHandler).print("/");
	}

	@Test
	public void declare_override() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					new Declare.Config().withDeclarations(Collections.singletonMap("var1", "1")),
					new RunScript.Config().withExtension("jexl").withCode("writer.print(`outer var1 before: ${var1}`)"),
					new Each.Config()
						.withExpression("[1]")
						.withAssumeNodes(false)
						.withHops(
							Arrays.asList(
								new Declare.Config().withDeclarations(Collections.singletonMap("var1", "2")),
								new RunScript.Config()
									.withExtension("jexl")
									.withCode("writer.print(`inner var1: ${var1}`)")
							)
						),
					new RunScript.Config().withExtension("jexl").withCode("writer.print(`outer var1 after: ${var1}`)")
				)
			)
			.run(context.resourceResolver().getResource("/").adaptTo(Node.class), true);

		verify(mockRunHandler).print("outer var1 before: 1");
		verify(mockRunHandler).print("inner var1: 2");
		verify(mockRunHandler).print("outer var1 after: 1");
	}
}
