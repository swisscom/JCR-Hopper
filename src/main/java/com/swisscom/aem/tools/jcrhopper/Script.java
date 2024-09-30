package com.swisscom.aem.tools.jcrhopper;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Script {
	private final List<HopConfig> hops;
	private final LogLevel logLevel;

	public static Script fromJson(Set<Hop<?>> hops, String scriptJson) {
		return configureGson(hops).fromJson(scriptJson, Script.class);
	}

	public static Script fromJson(Set<Hop<?>> hops, Reader scriptJson) {
		return configureGson(hops).fromJson(scriptJson, Script.class);
	}

	private static Gson configureGson(Set<Hop<?>> hops) {
		final Map<String, Hop<?>> hopsByType = hops.stream().collect(Collectors.toMap(
			Hop::getConfigTypeName,
			Function.identity()
		));

		JsonDeserializer<HopConfig> hopConfigJsonDeserializer = (json, typeOfT, context) -> {
			JsonObject obj = json.getAsJsonObject();
			String type = obj.get("type").getAsString();
			Class<? extends HopConfig> configClass = Optional.ofNullable(hopsByType.get(type)).map(Hop::getConfigType).orElse(null);
			if (configClass == null) {
				throw new JsonParseException(String.format(
					"Action type %s is unknown",
					type
				));
			}
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

		JsonDeserializer<ConflictResolution> conflictResolutionJsonDeserializer = (json, typeOfT, context) -> ConflictResolution.fromName(json.getAsString());
		JsonDeserializer<LogLevel> logLevelJsonDeserializer = (json, typeOfT, context) -> LogLevel.fromString(json.getAsString());

		GsonBuilder builder = new GsonBuilder()
			.registerTypeAdapter(HopConfig.class, hopConfigJsonDeserializer)
			.registerTypeAdapter(ConflictResolution.class, conflictResolutionJsonDeserializer)
			.registerTypeAdapter(LogLevel.class, logLevelJsonDeserializer);

		return builder.create();
	}
}
