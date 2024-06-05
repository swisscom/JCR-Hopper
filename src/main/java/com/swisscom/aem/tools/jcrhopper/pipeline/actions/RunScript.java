package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

@RequiredArgsConstructor
public class RunScript implements ConfigurableAction {
	@Getter
	private final RunScriptConfig config;

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final Map<String, Object> args = new HashMap<>();
		// Out params to be used in the descendant pipeline
		final Map<String, ?> out = new HashMap<>();
		args.put("out", out);
		// Put the context into the arguments so the script can log stuff
		args.put("log", context);
		// Allow outputting stuff directly without log format
		args.put("writer", context.getResponseWriter());
		// Compensate for the fact that the namespaced functions are not callable
		args.put("jcr", context.getJcrFunctions());
		args.put("req", context.getRequestParams());

		execScript(vars, args, context);
		// Put out vars back onto the pipeline
		vars.putAll(out);
	}

	private void execScript(Map<String, Object> vars, Map<String, Object> args, PipelineContext context) {
		final String extension = Optional.ofNullable(config.extension()).orElse("js");
		if (StringUtils.equalsIgnoreCase(extension, "jexl")) {
			context.getElExecutor(vars).runScript(config.code(), args);
		} else {
			final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
			final ScriptEngine engine = scriptEngineManager.getEngineByExtension(extension);
			if (engine == null) {
				throw new IllegalArgumentException("Script type " + extension + " not valid");
			}
			final SimpleScriptContext newContext = new SimpleScriptContext();
			newContext.setWriter(context.getResponseWriter());
			newContext.setErrorWriter(context.getResponseWriter());

			newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
			final Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

			engineScope.putAll(vars);
			engineScope.putAll(args);

			try {
				engine.eval(config.code(), newContext);
			} catch (ScriptException e) {
				throw new IllegalArgumentException("Error executing script for code: " + config.code(), e);
			}
		}
	}
}
