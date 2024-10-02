package com.swisscom.aem.tools.impl.hops;

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

import com.swisscom.aem.tools.impl.HopContext;
import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.HopConfig;
import com.swisscom.aem.tools.jcrhopper.HopperException;

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
		final Map<String, Object> jexlContext = new HashMap<>();
		final List<Map.Entry<String, Object>> paramsOrdered = new ArrayList<>(params.entrySet());
		context.getJexlEngine().createScript(
			config.code,
			paramsOrdered.stream().map(Map.Entry::getKey).toArray(String[]::new)
		).execute(
			new MapContext(jexlContext),
			paramsOrdered.stream().map(Map.Entry::getValue).toArray(Object[]::new)
		);
		if (config.putLocalsBackIntoScope) {
			context.getVariables().putAll(jexlContext);
		}
	}

	private static void runGenericScript(
		Config config,
		HopContext context,
		Map<String, Object> params,
		String extension,
		Writer writer
	) throws HopperException {
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

		engineScope.putAll(context.getVariables());
		engineScope.putAll(params);

		try {
			engine.eval(config.code, scriptContext);
			if (config.putLocalsBackIntoScope) {
				// Only allow added variables back into the context, not overridden variables or params
				engineScope.keySet().removeAll(params.keySet());
				engineScope.keySet().removeAll(context.getVariables().keySet());
				context.getVariables().putAll(engineScope);
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
