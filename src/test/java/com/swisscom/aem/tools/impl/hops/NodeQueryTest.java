package com.swisscom.aem.tools.impl.hops;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
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
import java.util.Collections;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class NodeQueryTest {

	public final AemContext context = new JcrOakAemContext();
	private RunnerBuilder builder;
	private Session session;
	private RunHandler mockRunHandler;

	@BeforeEach
	public void setUp() throws RepositoryException {
		context.create().resource("/content", "jcr:primaryType", "sling:Folder");
		context.create().resource("/content/child", "jcr:primaryType", "cq:Page");
		context.create().resource("/content/child/jcr:content", "jcr:primaryType", "cq:PageContent", "pageId", 1);
		context.create().resource("/content/child/one", "jcr:primaryType", "cq:Page");
		context.create().resource("/content/child/one/jcr:content", "jcr:primaryType", "cq:PageContent", "pageId", 2);

		mockRunHandler = mock(RunHandler.class);
		builder = Runner.builder().addHop(new NodeQuery(), new Declare(), new RunScript()).runHandler(mockRunHandler);
		session = context.resourceResolver().adaptTo(Session.class);
		session.save();
	}

	@Test
	public void xpath() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					LogLevel.DEBUG,
					new NodeQuery.Config().withQueryType("xpath").withQuery("/jcr:root/content//element(*, cq:Page)")
				)
			)
			.run(session, true);

		verify(mockRunHandler).log(LogLevel.DEBUG, "Found node /content/child for xpath query /jcr:root/content//e…", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found node /content/child/one for xpath query /jcr:root/content//e…", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "Processed 2 nodes from xpath query /jcr:root/content//element(*, cq:Page)", null, null);
	}

	@Test
	public void sql2() throws HopperException, RepositoryException {
		builder
			.build(new Script(LogLevel.DEBUG, new NodeQuery.Config().withQueryType(Query.JCR_SQL2).withQuery("SELECT * FROM [cq:Page]")))
			.run(session, true);

		verify(mockRunHandler).log(LogLevel.DEBUG, "Found node /content/child for JCR-SQL2 query SELECT * FROM [cq:Pa…", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found node /content/child/one for JCR-SQL2 query SELECT * FROM [cq:Pa…", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "Processed 2 nodes from JCR-SQL2 query SELECT * FROM [cq:Page]", null, null);
	}

	@Test
	public void sql2_preparedPlaceholder() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					LogLevel.DEBUG,
					new Declare.Config().withDeclarations(Collections.singletonMap("pageId", "1")),
					new NodeQuery.Config()
						.withQueryType(Query.JCR_SQL2)
						.withQuery("SELECT * FROM [cq:PageContent] WHERE pageId = $pageId")
				)
			)
			.run(session, true);

		verify(mockRunHandler).log(
			LogLevel.DEBUG,
			"Found node /content/child/jcr:content for JCR-SQL2 query SELECT * FROM [cq:Pa…",
			null,
			null
		);
		verify(mockRunHandler, never()).log(
			LogLevel.DEBUG,
			"Found node /content/child/one/jcr:content for JCR-SQL2 query SELECT * FROM [cq:Pa…",
			null,
			null
		);
		verify(mockRunHandler).log(
			LogLevel.INFO,
			"Processed 1 nodes from JCR-SQL2 query SELECT * FROM [cq:PageContent] WHERE pageId = $pageId",
			null,
			null
		);
	}

	@Test
	public void sql2_preparedPlaceholder_missing() {
		assertThrows(HopperException.class, () ->
			builder
				.build(
					new Script(
						LogLevel.DEBUG,
						new NodeQuery.Config()
							.withQueryType(Query.JCR_SQL2)
							.withQuery("SELECT * FROM [cq:PageContent] WHERE pageId = $pageId")
					)
				)
				.run(session, true)
		);
	}

	@Test
	public void sql2_templateExpression() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					LogLevel.DEBUG,
					new Declare.Config().withDeclarations(Collections.singletonMap("pageId", "2")),
					new NodeQuery.Config()
						.withQueryType(Query.JCR_SQL2)
						.withQuery("SELECT * FROM [cq:PageContent] WHERE pageId = ${pageId}")
				)
			)
			.run(session, true);

		verify(mockRunHandler).log(
			LogLevel.DEBUG,
			"Found node /content/child/one/jcr:content for JCR-SQL2 query SELECT * FROM [cq:Pa…",
			null,
			null
		);
		verify(mockRunHandler, never()).log(
			LogLevel.DEBUG,
			"Found node /content/child/jcr:content for JCR-SQL2 query SELECT * FROM [cq:Pa…",
			null,
			null
		);
		verify(mockRunHandler).log(
			LogLevel.INFO,
			"Processed 1 nodes from JCR-SQL2 query SELECT * FROM [cq:PageContent] WHERE pageId = 2",
			null,
			null
		);
	}

	@Test
	public void sql2_implicitJoin() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					LogLevel.DEBUG,
					new NodeQuery.Config()
						.withQueryType(Query.JCR_SQL2)
						.withQuery(
							"SELECT * FROM [cq:Page] AS page INNER JOIN [cq:PageContent] AS pageContent ON ISCHILDNODE(pageContent, page)"
						)
				)
			)
			.run(session, true);

		verify(mockRunHandler).log(any(), startsWith("There are 2 selectors in the JCR-SQL2 query. "), any(), any());
		verify(mockRunHandler).log(
			LogLevel.DEBUG,
			"Found node /content/child/jcr:content for JCR-SQL2 query SELECT * FROM [cq:Pa…",
			null,
			null
		);
		verify(mockRunHandler).log(
			LogLevel.DEBUG,
			"Found node /content/child/one/jcr:content for JCR-SQL2 query SELECT * FROM [cq:Pa…",
			null,
			null
		);
		verify(mockRunHandler).log(
			LogLevel.INFO,
			"Processed 2 nodes from JCR-SQL2 query SELECT * FROM [cq:Page] AS page INNER JOIN [cq:PageContent] AS pageContent ON ISCHILDNODE(pageContent, page)",
			null,
			null
		);
	}

	@Test
	public void sql2_explicitJoin() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					LogLevel.DEBUG,
					new NodeQuery.Config()
						.withQueryType(Query.JCR_SQL2)
						.withQuery(
							"SELECT * FROM [cq:Page] AS page INNER JOIN [cq:PageContent] AS pageContent ON ISCHILDNODE(pageContent, page)"
						)
						.withSelectorName("page")
						.withHops(
							Collections.singletonList(
								new RunScript.Config()
									.withCode(
										"writer.print('#' + counter + ' page: ' + page.getPath() + ', pageContent: ' + pageContent.getPath())"
									)
							)
						)
				)
			)
			.run(session, true);

		verify(mockRunHandler, never()).log(any(), startsWith("There are 2 selectors in the JCR-SQL2 query. "), any(), any());
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found node /content/child for JCR-SQL2 query SELECT * FROM [cq:Pa…", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found node /content/child/one for JCR-SQL2 query SELECT * FROM [cq:Pa…", null, null);
		verify(mockRunHandler).log(
			LogLevel.INFO,
			"Processed 2 nodes from JCR-SQL2 query SELECT * FROM [cq:Page] AS page INNER JOIN [cq:PageContent] AS pageContent ON ISCHILDNODE(pageContent, page)",
			null,
			null
		);

		verify(mockRunHandler).print("#0 page: /content/child, pageContent: /content/child/jcr:content");
		verify(mockRunHandler).print("#1 page: /content/child/one, pageContent: /content/child/one/jcr:content");
	}

	@Test
	public void sql2_invalidSelector() {
		assertThrows(IllegalArgumentException.class, () -> {
			builder
				.build(
					new Script(
						LogLevel.DEBUG,
						new NodeQuery.Config()
							.withQueryType(Query.JCR_SQL2)
							.withQuery("SELECT * FROM [cq:Page]")
							.withSelectorName("test1")
					)
				)
				.run(session, true);
		});
	}

	@Test
	public void sql2_customCounter() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					LogLevel.DEBUG,
					new NodeQuery.Config()
						.withQueryType(Query.JCR_SQL2)
						.withQuery("SELECT * FROM [cq:Page]")
						.withCounterName("cnt")
						.withHops(
							Collections.singletonList(
								new RunScript.Config()
									.withExtension("jexl")
									.withCode("writer.print('#' + (cnt+1) + ' node: ' + node.getPath())")
							)
						)
				)
			)
			.run(session, true);

		verify(mockRunHandler).print("#1 node: /content/child");
		verify(mockRunHandler).print("#2 node: /content/child/one");
	}

	@Test
	public void sql2_limitAndOffset() throws HopperException, RepositoryException {
		builder
			.build(
				new Script(
					LogLevel.DEBUG,
					new NodeQuery.Config()
						.withQueryType(Query.JCR_SQL2)
						.withQuery("SELECT * FROM [cq:Page]")
						.withLimit(1)
						.withOffset(1)
				)
			)
			.run(session, true);

		verify(mockRunHandler, never()).log(LogLevel.DEBUG, "Found node /content/child for JCR-SQL2 query SELECT * FROM [cq:Pa…", null, null);
		verify(mockRunHandler).log(LogLevel.DEBUG, "Found node /content/child/one for JCR-SQL2 query SELECT * FROM [cq:Pa…", null, null);
		verify(mockRunHandler).log(LogLevel.INFO, "Processed 1 nodes from JCR-SQL2 query SELECT * FROM [cq:Page]", null, null);
	}
}
