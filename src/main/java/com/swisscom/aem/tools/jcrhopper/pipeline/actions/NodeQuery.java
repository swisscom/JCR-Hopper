package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.DerivedMap;
import com.swisscom.aem.tools.jcrhopper.pipeline.Pipeline;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

public class NodeQuery implements ConfigurableAction {
	@Getter
	private final NodeQueryConfig config;
	private final Pipeline pipeline;

	/**
	 * Constructor.
	 *
	 * @param config the configuration
	 */
	public NodeQuery(NodeQueryConfig config) {
		this.config = config;
		this.pipeline = new Pipeline(config.actions());
	}

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final Map<String, Object> resultVars = new DerivedMap<>(vars);
		final String statement = context.evaluateTemplate(vars, config.query());
		resultVars.put("query", statement);
		final QueryResult result = getQueryResult(node, vars, context, statement);
		final RowIterator rowIterator = result.getRows();
		final String[] selectors = result.getSelectorNames();
		int counter = 0;
		final String selectorName = getSelectorName(context, selectors);

		while (rowIterator.hasNext()) {
			final Row row = rowIterator.nextRow();
			final Node resultNode = StringUtils.isBlank(selectorName) ? row.getNode() : row.getNode(selectorName);
			for (String selector : result.getSelectorNames()) {
				resultVars.put(selector, row.getNode(selector));
			}
			context.debug("Found node {} for {} query {}…", resultNode.getPath(), config.queryType(), statement.substring(0, 20));
			resultVars.put(config.counterName(), counter);
			pipeline.runWith(resultNode, resultVars, context);
			counter++;
		}
		context.info("Processed {} nodes from {} query {}", counter, config.queryType(), statement);
	}

	private String getSelectorName(PipelineContext context, String... selectors) {
		String selectorName = config.selectorName();
		if (StringUtils.isBlank(selectorName) && selectors.length > 0) {
			selectorName = selectors[0];
			if (selectors.length > 1) {
				context.warn(
					"There are {} selectors in the {} query. {} will be used for the sub-pipeline. "
						+ "You should use an explicit config since the order is not guaranteed.",
					selectors.length,
					config.queryType(),
					selectorName
				);
			}
		}
		return selectorName;
	}

	private QueryResult getQueryResult(
		Node node, Map<String, Object> vars, PipelineContext context, String statement)
		throws RepositoryException {
		final QueryManager qm = node.getSession().getWorkspace().getQueryManager();
		final Query query = qm.createQuery(statement, config.queryType());
		if (config.limit() > 0) {
			query.setLimit(config.limit());
		}
		if (config.offset() > 0) {
			query.setOffset(config.offset());
		}
		for (String bindVar : query.getBindVariableNames()) {
			if (vars.containsKey(bindVar)) {
				query.bindValue(bindVar, context.getJcrFunctions().valueFromObject(vars.get(bindVar)));
			} else {
				context.error("Could not bind placeholder {} as there is no known variable for it", bindVar);
			}
		}
		return query.execute();
	}
}

