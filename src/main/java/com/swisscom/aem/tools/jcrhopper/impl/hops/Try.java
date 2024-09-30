package com.swisscom.aem.tools.jcrhopper.impl.hops;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.HopConfig;
import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.impl.HopContext;

@AllArgsConstructor
public class Try implements Hop<Try.Config> {
	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final boolean catchGeneric = config.catchGeneric;
		for (HopConfig hopConfig : config.hops) {
			try {
				context.runHop(node, hopConfig);
			}
			catch (HopperException e) {
				context.info(
					"Pipeline error {} during action {}, aborting pipeline gracefully",
					e.getMessage(), hopConfig.getClass().getSimpleName(), e
				);
				break;
			}
			catch (Exception e) {
				if (catchGeneric) {
					context.info(
						"Generic error {} during action {}, aborting pipeline gracefully",
						e.getMessage(), hopConfig.getClass().getSimpleName(), e
					);
					break;
				}
				else {
					throw e;
				}
			}
		}
	}

	@Nonnull
	@Override
	public Class<Config> getConfigType() {
		return Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "try";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	public static class Config implements HopConfig {
		private boolean catchGeneric;
		@Nonnull
		private final List<HopConfig> hops = Collections.emptyList();
	}
}
