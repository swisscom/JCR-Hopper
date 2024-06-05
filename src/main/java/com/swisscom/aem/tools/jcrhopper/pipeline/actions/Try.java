package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.swisscom.aem.tools.jcrhopper.pipeline.Action;
import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

@AllArgsConstructor
public class Try implements ConfigurableAction {
	@Getter
	private final TryConfig config;

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException {
		final boolean catchGeneric = config.catchGeneric();
		for (Action action : this.config.actions()) {
			try {
				action.run(node, vars, context);
			} catch (PipelineException e) {
				context.info("Pipeline error {} during action {}, aborting pipeline gracefully",
					e.getMessage(), action.getClass().getSimpleName(), e);
				break;
			} catch (Exception e) {
				if (catchGeneric) {
					context.info("Generic error {} during action {}, aborting pipeline gracefully",
						e.getMessage(), action.getClass().getSimpleName(), e);
					break;
				} else {
					throw e;
				}
			}
		}
	}
}

