package com.swisscom.aem.tools.jcrhopper.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.swisscom.aem.tools.impl.extension.HopProviderExtension;
import com.swisscom.aem.tools.impl.hops.Declare;
import com.swisscom.aem.tools.impl.hops.RunScript;
import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.osgi.RunnerService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrMockAemContext;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class ParameterTest {

	public final AemContext context = new JcrMockAemContext();
	private RunHandler mockRunHandler;
	private RunnerBuilder builder;

	@BeforeEach
	void setUp() {
		context.registerInjectActivateService(Declare.class);
		context.registerInjectActivateService(RunScript.class);

		context.registerInjectActivateService(HopProviderExtension.class);

		RunnerService runnerService = context.registerInjectActivateService(RunnerService.class);
		mockRunHandler = mock(RunHandler.class);

		builder = runnerService.builder().runHandler(mockRunHandler);
	}

	@Test
	void defaultValue_string() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					Collections.singletonList(
						new RunScript.Config()
							.withExtension("js")
							.withCode(
								"log.info('nullDefault: {}', args.nullDefault);" +
								"log.info('givenDefault: {}', args.givenDefault);" +
								"log.info('givenDefaultExplicit: {}', args.givenDefaultExplicit);"
							)
					),
					Arrays.asList(
						new Parameter("nullDefault", null, null, null),
						new Parameter("givenDefault", "str", null, null),
						new Parameter("givenDefaultExplicit", "str", null, Parameter.ArgumentEvaluation.STRING)
					)
				)
			)
			.run(context.resourceResolver().adaptTo(Session.class), false);

		verify(mockRunHandler).log(LogLevel.INFO, "nullDefault: null", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "givenDefault: str", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "givenDefaultExplicit: str", null, null);
	}

	@Test
	void defaultValue_lines() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					Collections.singletonList(
						new RunScript.Config()
							.withExtension("js")
							.withCode(
								"log.info('nullDefault: {}', args.nullDefault);" +
								"log.info('givenDefault: {}', args.givenDefault);"
							)
					),
					Arrays.asList(
						new Parameter("nullDefault", null, null, Parameter.ArgumentEvaluation.LINES),
						new Parameter("givenDefault", "str1\nstr2", null, Parameter.ArgumentEvaluation.LINES)
					)
				)
			)
			.run(context.resourceResolver().adaptTo(Session.class), false);

		verify(mockRunHandler).log(LogLevel.INFO, "nullDefault: []", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "givenDefault: [str1, str2]", null, null);
	}

	@Test
	void defaultValue_template() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					Collections.singletonList(
						new RunScript.Config()
							.withExtension("js")
							.withCode(
								"log.info('nullDefault: {}', args.nullDefault);" +
								"log.info('givenDefault: {}', args.givenDefault);"
							)
					),
					Arrays.asList(
						new Parameter("nullDefault", null, null, Parameter.ArgumentEvaluation.TEMPLATE),
						new Parameter("givenDefault", "${1+1}", null, Parameter.ArgumentEvaluation.TEMPLATE)
					)
				)
			)
			.run(context.resourceResolver().adaptTo(Session.class), false);

		verify(mockRunHandler).log(LogLevel.INFO, "nullDefault: null", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "givenDefault: 2", null, null);
	}

	@Test
	void defaultValue_expression() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					Collections.singletonList(
						new RunScript.Config()
							.withExtension("js")
							.withCode(
								"log.info('nullDefault: {}', args.nullDefault);" +
								"log.info('givenDefault: {}', args.givenDefault);"
							)
					),
					Arrays.asList(
						new Parameter("nullDefault", null, null, Parameter.ArgumentEvaluation.EXPRESSION),
						new Parameter("givenDefault", "{'a':1}", null, Parameter.ArgumentEvaluation.EXPRESSION)
					)
				)
			)
			.run(context.resourceResolver().adaptTo(Session.class), false);

		verify(mockRunHandler).log(LogLevel.INFO, "nullDefault: null", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "givenDefault: {a=1}", null, null);
	}

	@Test
	void suppliedValue_string() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					Collections.singletonList(
						new RunScript.Config()
							.withExtension("js")
							.withCode(
								"log.info('nullDefault: {}', args.nullDefault);" +
								"log.info('givenDefault: {}', args.givenDefault);" +
								"log.info('givenDefaultExplicit: {}', args.givenDefaultExplicit);"
							)
					),
					Arrays.asList(
						new Parameter("nullDefault", null, null, null),
						new Parameter("givenDefault", "str", null, null),
						new Parameter("givenDefaultExplicit", "str", null, Parameter.ArgumentEvaluation.STRING)
					)
				)
			)
			.run(
				context.resourceResolver().adaptTo(Session.class),
				false,
				Stream.of(
					new AbstractMap.SimpleEntry<>("nullDefault", "supplied1"),
					new AbstractMap.SimpleEntry<>("givenDefault", "supplied2"),
					new AbstractMap.SimpleEntry<>("givenDefaultExplicit", "supplied3")
				).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
			);

		verify(mockRunHandler).log(LogLevel.INFO, "nullDefault: supplied1", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "givenDefault: supplied2", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "givenDefaultExplicit: supplied3", null, null);
	}

	@Test
	void suppliedValue_lines() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					Collections.singletonList(
						new RunScript.Config()
							.withExtension("js")
							.withCode(
								"log.info('nullDefault: {}', args.nullDefault);" +
								"log.info('givenDefault: {}', args.givenDefault);"
							)
					),
					Arrays.asList(
						new Parameter("nullDefault", null, null, Parameter.ArgumentEvaluation.LINES),
						new Parameter("givenDefault", "str1\nstr2", null, Parameter.ArgumentEvaluation.LINES)
					)
				)
			)
			.run(
				context.resourceResolver().adaptTo(Session.class),
				false,
				Stream.of(
					new AbstractMap.SimpleEntry<>("nullDefault", "supplied1_1\nsupplied1_2"),
					new AbstractMap.SimpleEntry<>("givenDefault", "supplied2_1\r\nsupplied_2_2\n")
				).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
			);

		verify(mockRunHandler).log(LogLevel.INFO, "nullDefault: [supplied1_1, supplied1_2]", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "givenDefault: [supplied2_1, supplied_2_2]", null, null);
	}

	@Test
	void suppliedValue_template() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					Collections.singletonList(
						new RunScript.Config()
							.withExtension("js")
							.withCode(
								"log.info('nullDefault: {}', args.nullDefault);" +
								"log.info('givenDefault: {}', args.givenDefault);"
							)
					),
					Arrays.asList(
						new Parameter("nullDefault", null, null, Parameter.ArgumentEvaluation.TEMPLATE),
						new Parameter("givenDefault", "${1+1}", null, Parameter.ArgumentEvaluation.TEMPLATE)
					)
				)
			)
			.run(
				context.resourceResolver().adaptTo(Session.class),
				false,
				Stream.of(
					new AbstractMap.SimpleEntry<>("nullDefault", "supplied ${1}"),
					new AbstractMap.SimpleEntry<>("givenDefault", "supplied ${1+1}")
				).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
			);

		verify(mockRunHandler).log(LogLevel.INFO, "nullDefault: supplied 1", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "givenDefault: supplied 2", null, null);
	}

	@Test
	void suppliedValue_expression() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					Collections.singletonList(
						new RunScript.Config()
							.withExtension("js")
							.withCode(
								"log.info('nullDefault: {}', args.nullDefault);" +
								"log.info('givenDefault: {}', args.givenDefault);"
							)
					),
					Arrays.asList(
						new Parameter("nullDefault", null, null, Parameter.ArgumentEvaluation.EXPRESSION),
						new Parameter("givenDefault", "{'a':1}", null, Parameter.ArgumentEvaluation.EXPRESSION)
					)
				)
			)
			.run(
				context.resourceResolver().adaptTo(Session.class),
				false,
				Stream.of(
					new AbstractMap.SimpleEntry<>("nullDefault", "['one', 'two']"),
					new AbstractMap.SimpleEntry<>("givenDefault", "2*5")
				).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
			);

		verify(mockRunHandler).log(LogLevel.INFO, "nullDefault: [one, two]", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "givenDefault: 10", null, null);
	}
}
