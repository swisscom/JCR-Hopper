package com.swisscom.aem.tools.impl.hops;

import com.swisscom.aem.tools.impl.HopContextImpl;
import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.config.Hop;
import com.swisscom.aem.tools.jcrhopper.config.HopConfig;
import com.swisscom.aem.tools.jcrhopper.context.HopContext;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

@RequiredArgsConstructor
@Component(service = Hop.class)
public class RunScript implements Hop<RunScript.Config> {

	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		try (Writer writer = context.getWriter()) {
			final Map<String, Object> params = new HashMap<>(context.getVariables());
			params.put("log", context);
			params.put("writer", writer);

			final String extension = Optional.ofNullable(config.extension).orElse("js");
			if (StringUtils.equalsIgnoreCase(extension, "jexl")) {
				runJexl(config, context, params);
			} else {
				runGenericScript(config, context, params, extension, writer);
			}
		} catch (IOException ex) {
			context.warn("Error closing writer after script run, should never happen", ex);
		}
	}

	private static void runJexl(Config config, HopContext context, Map<String, Object> params) {
		if (!(context instanceof HopContextImpl)) {
			// Only our HopContext implementation has a JEXL engine accessor
			context.error("Cannot run jexl with {} context", context.getClass());
			return;
		}
		final Map<String, Object> jexlContext = new HashMap<>();
		final List<Map.Entry<String, Object>> paramsOrdered = new ArrayList<>(params.entrySet());
		((HopContextImpl) context).getJexlEngine()
			.createScript(config.code, paramsOrdered.stream().map(Map.Entry::getKey).toArray(String[]::new))
			.execute(new MapContext(jexlContext), paramsOrdered.stream().map(Map.Entry::getValue).toArray(Object[]::new));
		if (config.putLocalsBackIntoScope) {
			for (Map.Entry<String, Object> entry : jexlContext.entrySet()) {
				context.setVariable(entry.getKey(), entry.getValue());
			}
		}
	}

	private static void runGenericScript(Config config, HopContext context, Map<String, Object> params, String extension, Writer writer)
		throws HopperException {
		final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		final ScriptEngine engine = scriptEngineManager.getEngineByExtension(extension);
		if (engine == null) {
			throw new IllegalArgumentException("Script type " + extension + " not valid");
		}
		final SimpleScriptContext scriptContext = new SimpleScriptContext();
		scriptContext.setWriter(writer);
		scriptContext.setErrorWriter(writer);

		scriptContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
		final Bindings engineScope = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);

		final Map<String, Object> knownVariables = context.getVariables();
		engineScope.putAll(knownVariables);
		engineScope.putAll(params);

		try {
			engine.eval(config.code, scriptContext);
			if (config.putLocalsBackIntoScope) {
				for (Map.Entry<String, Object> entry : engineScope.entrySet()) {
					if (knownVariables.containsKey(entry.getKey()) || params.containsKey(entry.getKey())) {
						// Don’t allow overriding existing variables
						continue;
					}
					context.setVariable(entry.getKey(), entry.getValue());
				}
			}
		} catch (ScriptException e) {
			throw new HopperException("Error executing script for code: " + config.code, e);
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
		return "runScript";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	@SuppressWarnings("PMD.ImmutableField")
	public static final class Config implements HopConfig {

		@Nonnull
		private String code = "";

		@Nonnull
		private String extension = "js";

		private boolean putLocalsBackIntoScope;
	}
}
