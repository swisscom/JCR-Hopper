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
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class FilterNodeTest {

	public final AemContext context = new JcrMockAemContext();
	private RunnerBuilder builder;
	private RunHandler mockRunHandler;

	@BeforeEach
	public void setUp() {
		context.create().resource("/child-1");
		context.create().resource("/other-child");

		mockRunHandler = mock(RunHandler.class);
		builder = Runner.builder().addHop(new FilterNode(), new RunScript()).runHandler(mockRunHandler).addUtil("str", StringUtils.class);
	}

	@Test
	public void matches() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					new FilterNode.Config()
						.withExpression("str:startsWith(node.name, 'child-')")
						.withHops(
							Collections.singletonList(
								new RunScript.Config().withExtension("jexl").withCode("writer.print(node.path)")
							)
						)
				)
			)
			.run(context.resourceResolver().getResource("/child-1").adaptTo(Node.class), true);

		verify(mockRunHandler).print("/child-1");
	}

	@Test
	public void doesNotMatch() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					new FilterNode.Config()
						.withExpression("str:startsWith(node.name, 'child-')")
						.withHops(
							Collections.singletonList(
								new RunScript.Config().withExtension("jexl").withCode("writer.print(node.path)")
							)
						)
				)
			)
			.run(context.resourceResolver().getResource("/other-child").adaptTo(Node.class), true);

		verify(mockRunHandler, never()).print(any());
	}
}
