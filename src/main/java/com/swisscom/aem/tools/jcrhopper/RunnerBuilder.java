package com.swisscom.aem.tools.jcrhopper;

import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.swisscom.aem.tools.jcrhopper.impl.NoopRunHandler;

@Accessors(fluent = true, chain = true)
public class RunnerBuilder {
	private static final String JSON_TYPE_PROPERTY = "type";
	private final Set<Hop<?>> knownHops = new HashSet<>();
	private final Map<String, Object> utils = new HashMap<>();
	private final Map<String, Object> variables = new HashMap<>();

	@Getter
	@Setter
	private RunHandler runHandler = new NoopRunHandler();

	private final Gson gson;

	@Getter
	@Setter
	private boolean addDefaultUtils;

	RunnerBuilder() {
		gson = createGson();
	}

	private Gson createGson() {
		JsonDeserializer<HopConfig> hopConfigJsonDeserializer = (json, typeOfT, context) -> {
			final JsonObject obj = json.getAsJsonObject();
			final String type = obj.get(JSON_TYPE_PROPERTY).getAsString();
			if (type == null) {
				throw new JsonParseException(String.format("Error parsing hop config %s, type missing", json));
			}
			final Class<?> configClass = knownHops.stream()
				.filter(hop -> type.equals(hop.getConfigTypeName()))
				.findFirst()
				.map(Hop::getConfigType)
				.orElseThrow(() -> new JsonParseException(String.format("Action type %s is unknown", type)));

			HopConfig config = context.deserialize(json, configClass);
			if (config == null) {
				throw new JsonParseException(String.format(
					"Config for action type %s (class %s) could not be parsed",
					type,
					configClass.getSimpleName()
				));
			}
			return config;
		};
		JsonSerializer<HopConfig> hopConfigJsonSerializer = (src, typeOfSrc, context) -> {
			final Hop<?> hop = knownHops.stream()
				.filter(h -> h.getConfigType() == src.getClass())
				.findFirst()
				.orElseThrow(() -> new JsonIOException(String.format("No known hop matches config %s", src.getClass().getName())));
			final JsonElement el = context.serialize(src);
			el.getAsJsonObject().addProperty(JSON_TYPE_PROPERTY, hop.getConfigTypeName());
			return el;
		};

		JsonDeserializer<ConflictResolution> conflictResolutionJsonDeserializer = (json, typeOfT, context) -> ConflictResolution.fromName(json.getAsString());
		JsonSerializer<ConflictResolution> conflictResolutionJsonSerializer = (src, typeOfSrc, context) -> context.serialize(src.toName());
		JsonDeserializer<LogLevel> logLevelJsonDeserializer = (json, typeOfT, context) -> LogLevel.fromString(json.getAsString());
		JsonSerializer<LogLevel> logLevelJsonSerializer = (src, typeOfSrc, context) -> context.serialize(src.toName());

		GsonBuilder builder = new GsonBuilder()
			.registerTypeAdapter(HopConfig.class, hopConfigJsonDeserializer)
			.registerTypeAdapter(HopConfig.class, hopConfigJsonSerializer)
			.registerTypeAdapter(ConflictResolution.class, conflictResolutionJsonDeserializer)
			.registerTypeAdapter(ConflictResolution.class, conflictResolutionJsonSerializer)
			.registerTypeAdapter(LogLevel.class, logLevelJsonDeserializer)
			.registerTypeAdapter(LogLevel.class, logLevelJsonSerializer);

		return builder.create();
	}

	public RunnerBuilder addUtil(String name, Object util) {
		utils.put(name, util);
		return this;
	}

	public RunnerBuilder addUtils(Map<String, Object> utils) {
		this.utils.putAll(utils);
		return this;
	}

	public RunnerBuilder addVariable(String name, Object util) {
		variables.put(name, util);
		return this;
	}

	public RunnerBuilder addVariables(Map<String, Object> variables) {
		this.variables.putAll(variables);
		return this;
	}

	public RunnerBuilder addHop(Hop<?>... hops) {
		return addHops(Arrays.asList(hops));
	}

	public RunnerBuilder addHops(Collection<Hop<?>> hops) {
		knownHops.addAll(hops);
		return this;
	}

	public Runner build(Script script) {
		final HashMap<String, Object> utils = new HashMap<>(this.utils);
		if (this.addDefaultUtils) {
			utils.put("arrays", Arrays.class);
			utils.put("stream", Stream.class);
			utils.put("class", Class.class);
		}
		return new Runner(
			script,
			runHandler,
			Collections.unmodifiableSet(new HashSet<>(knownHops)),
			new HashMap<>(variables),
			utils
		);
	}

	public Runner build(String scriptAsJson) {
		return build(scriptFromJson(scriptAsJson));
	}

	public Script scriptFromJson(String scriptAsJson) {
		return gson.fromJson(scriptAsJson, Script.class);
	}

	public Script scriptFromJson(Reader scriptAsJson) {
		return gson.fromJson(scriptAsJson, Script.class);
	}

	public String scriptToJson(Script script) {
		return gson.toJson(script);
	}
}
