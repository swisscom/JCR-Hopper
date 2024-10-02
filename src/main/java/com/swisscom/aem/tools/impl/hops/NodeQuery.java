package com.swisscom.aem.tools.impl.hops;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;

import com.swisscom.aem.tools.impl.HopContext;
import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.HopConfig;
import com.swisscom.aem.tools.jcrhopper.HopperException;

@Component(service = Hop.class)
public class NodeQuery implements Hop<NodeQuery.Config> {
	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final Map<String, Object> resultVars = new HashMap<>();
		final String statement = context.evaluateTemplate(config.query);
		resultVars.put("query", statement);
		final QueryResult result = getQueryResult(config, node, context, statement);
		final RowIterator rowIterator = result.getRows();
		final String[] selectors = result.getSelectorNames();
		int counter = 0;
		final String selectorName = getSelectorName(config, context, selectors);

		while (rowIterator.hasNext()) {
			final Row row = rowIterator.nextRow();
			final Node resultNode = StringUtils.isBlank(selectorName) ? row.getNode() : row.getNode(selectorName);
			for (String selector : result.getSelectorNames()) {
				resultVars.put(selector, row.getNode(selector));
			}
			context.debug("Found node {} for {} query {}…", resultNode.getPath(), config.queryType, statement.substring(0, 20));
			resultVars.put(config.counterName, counter);
			context.runHops(resultNode, config.hops, resultVars);
			counter++;
		}
		context.info("Processed {} nodes from {} query {}", counter, config.queryType, statement);
	}

	private String getSelectorName(Config config, Logger context, String... selectors) {
		String selectorName = config.selectorName;
		if (selectors.length > 0 && StringUtils.isBlank(selectorName)) {
			selectorName = selectors[0];
			if (selectors.length > 1) {
				context.warn(
					"There are {} selectors in the {} query. {} will be used for the sub-pipeline. "
						+ "You should use an explicit config since the order is not guaranteed.",
					selectors.length,
					config.queryType,
					selectorName
				);
			}
		}
		return selectorName;
	}

	private QueryResult getQueryResult(
		Config config,
		Node node,
		HopContext context,
		String statement
	)
		throws RepositoryException {
		final QueryManager qm = node.getSession().getWorkspace().getQueryManager();
		final Query query = qm.createQuery(statement, config.queryType);
		if (config.limit > 0) {
			query.setLimit(config.limit);
		}
		if (config.offset > 0) {
			query.setOffset(config.offset);
		}
		for (String bindVar : query.getBindVariableNames()) {
			if (context.has(bindVar)) {
				query.bindValue(bindVar, context.getJcrFunctions().valueFromObject(context.get(bindVar)));
			} else {
				context.error("Could not bind placeholder {} as there is no known variable for it", bindVar);
			}
		}
		return query.execute();
	}

	@Nonnull
	@Override
	public Class<Config> getConfigType() {
		return Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "nodeQuery";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	@SuppressWarnings("PMD.ImmutableField")
	public static final class Config implements HopConfig {
		private String query;
		@Nonnull
		private String queryType = Query.JCR_SQL2;
		@Nonnull
		private String counterName = "counter";
		private String selectorName;
		private int limit;
		private int offset;
		@Nonnull
		private List<HopConfig> hops = Collections.emptyList();
	}
}

