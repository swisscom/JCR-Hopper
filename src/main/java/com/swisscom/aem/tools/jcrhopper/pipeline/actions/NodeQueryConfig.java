package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.List;

import javax.jcr.query.Query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import com.swisscom.aem.tools.jcrhopper.pipeline.Action;

@AllArgsConstructor
@Accessors(fluent = true)
@Data
public class NodeQueryConfig {
	@NonNull
	private String query;
	private String queryType = Query.JCR_SQL2;
	private String counterName = "counter";
	private String selectorName;
	private int limit;
	private int offset;
	@NonNull
	private List<Action> actions;
}
