package com.swisscom.aem.tools.jcrhopper.pipeline;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Pipeline implements Action {
	private static final Type ACTION_LIST_TYPE = new TypeToken<List<Action>>() {
	}.getType();

	private List<Action> actions = new LinkedList<>();

	/**
	 * Serializes the pipeline to JSON.
	 *
	 * @param pipeline The pipeline to serialize
	 * @return The JSON representation of the pipeline
	 */
	public static String toJson(Pipeline pipeline) {
		return configureGson().toJson(pipeline, Pipeline.class);
	}

	/**
	 * Deserializes the pipeline from JSON.
	 *
	 * @param json The JSON representation of the pipeline
	 * @return The pipeline
	 */
	public static Pipeline fromJson(String json) {
		return new Pipeline(configureGson().fromJson(json, ACTION_LIST_TYPE));
	}

	private static Gson configureGson() {
		final JsonDeserializer<Action> actionDeserializer = (json, typeOfT, context) -> {
			final JsonObject obj = json.getAsJsonObject();
			final String type = obj.get("type").getAsString();
			final Class<?> configClass = ActionFactory.actionConfigForType(type);
			if (configClass == null) {
				throw new JsonParseException(String.format(
					"Action type %s is unknown",
					type
				));
			}
			final Object config = context.deserialize(json, configClass);
			if (config == null) {
				throw new JsonParseException(String.format(
					"Config for action type %s (class %s) could not be parsed",
					type,
					configClass.getSimpleName()
				));
			}
			final Action action = ActionFactory.configureAction(config);
			if (action == null) {
				throw new JsonParseException(String.format(
					"Error creating action of type %s (class %s)",
					type,
					configClass.getSimpleName()
				));
			}

			return action;
		};

		final JsonSerializer<ConfigurableAction> configurableActionJsonSerializer = (src, typeOfSrc, context) -> {
			final Object config = src.getConfig();
			final JsonElement configSerialized = context.serialize(config);
			configSerialized.getAsJsonObject().addProperty("type", ActionFactory.configType(config));
			return configSerialized;
		};

		final JsonSerializer<Pipeline> pipelineJsonSerializer = (src, typeOfSrc, context) -> {
			final List<Action> actions = src.actions;
			return context.serialize(actions, ACTION_LIST_TYPE);
		};

		final JsonDeserializer<ConflictResolution> conflictResolutionJsonDeserializer =
			(json, typeOfT, context) -> ConflictResolution.fromName(json.getAsString());

		final JsonSerializer<ConflictResolution> conflictResolutionJsonSerializer =
			(src, typeOfSrc, context) -> context.serialize(src.toName());

		final GsonBuilder builder = new GsonBuilder();

		for (Class<? extends ConfigurableAction> actionClass : ActionFactory.listActionTypes()) {
			builder.registerTypeAdapter(actionClass, configurableActionJsonSerializer);
		}

		return builder
			.registerTypeAdapter(Action.class, actionDeserializer)
			.registerTypeAdapter(ConfigurableAction.class, configurableActionJsonSerializer)
			.registerTypeAdapter(Pipeline.class, pipelineJsonSerializer)
			.registerTypeAdapter(ConflictResolution.class, conflictResolutionJsonDeserializer)
			.registerTypeAdapter(ConflictResolution.class, conflictResolutionJsonSerializer)
			.setPrettyPrinting()

			.create();
	}

	/**
	 * Adds an action to the pipeline.
	 *
	 * @param action The action to add
	 */
	public void addAction(Action action) {
		actions.add(action);
	}

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		if (actions == null || actions.isEmpty()) {
			return;
		}

		final Map<String, Object> entryVars = new DerivedMap<>(vars);

		for (Action action : actions) {
			try {
				context.pushLogger(LoggerFactory.getLogger(action.getClass()));
				action.run(node, entryVars, context);
			} finally {
				context.popLogger();
			}
		}
	}
}
