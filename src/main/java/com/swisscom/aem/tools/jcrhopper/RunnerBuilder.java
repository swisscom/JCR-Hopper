package com.swisscom.aem.tools.jcrhopper;

import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
import com.swisscom.aem.tools.impl.RunnerImpl;
import com.swisscom.aem.tools.jcrhopper.config.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.config.File;
import com.swisscom.aem.tools.jcrhopper.config.Hop;
import com.swisscom.aem.tools.jcrhopper.config.HopConfig;
import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;
import com.swisscom.aem.tools.jcrhopper.config.Script;
import com.swisscom.aem.tools.jcrhopper.impl.NoopRunHandler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Accessors(fluent = true, chain = true)
@SuppressFBWarnings(value = "OPM_OVERLY_PERMISSIVE_METHOD", justification = "API surface")
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class RunnerBuilder {
	private static final String JSON_TYPE_PROPERTY = "type";
	private final Set<Hop<?>> knownHops = new HashSet<>();
	private final Map<String, Object> utils = new HashMap<>();
	private final Map<String, Object> variables = new HashMap<>();
	private final Map<String, Function<String, File>> fileTypeSuppliers = new HashMap<>();

	private final Gson gson;

	/**
	 * The listener that gets notified of events that happen during the script run.
	 */
	@Getter
	@Setter
	private RunHandler runHandler = new NoopRunHandler();

	/**
	 * Whether to add some default utils to the map of configured utils.
	 * <p>
	 * These are as follows:
	 * <ul>
	 *     <li><code>arrays</code>: static methods on {@link Arrays}</li>
	 *     <li><code>stream</code>: static methods on {@link Stream}</li>
	 *     <li><code>class</code>: static methods on {@link Class}</li>
	 *     <li><code>collections</code>: static methods on {@link Collections}</li>
	 * </ul>
	 * <p>
	 * Note that the <code>jcr</code> and <code>file</code> utils cannot be disabled.
	 */
	@Getter
	@Setter
	private boolean addDefaultUtils;

	RunnerBuilder() {
		gson = createGson();
	}

	private Gson createGson() {
		final JsonDeserializer<HopConfig> hopConfigJsonDeserializer = (json, typeOfT, context) -> {
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

			final HopConfig config = context.deserialize(json, configClass);
			if (config == null) {
				throw new JsonParseException(String.format(
					"Config for action type %s (class %s) could not be parsed",
					type,
					configClass.getSimpleName()
				));
			}
			return config;
		};
		final JsonSerializer<HopConfig> hopConfigJsonSerializer = (src, typeOfSrc, context) -> {
			final Hop<?> hop = knownHops.stream()
				.filter(h -> h.getConfigType() == src.getClass())
				.findFirst()
				.orElseThrow(() -> new JsonIOException(String.format("No known hop matches config %s", src.getClass().getName())));
			final JsonElement el = context.serialize(src);
			el.getAsJsonObject().addProperty(JSON_TYPE_PROPERTY, hop.getConfigTypeName());
			return el;
		};

		final JsonDeserializer<ConflictResolution> conflictResolutionJsonDeserializer = (json, typeOfT, context) ->
			ConflictResolution.fromName(json.getAsString());
		final JsonSerializer<ConflictResolution> conflictResolutionJsonSerializer = (src, typeOfSrc, context) ->
			context.serialize(src.toName());
		final JsonDeserializer<LogLevel> logLevelJsonDeserializer = (json, typeOfT, context) ->
			LogLevel.fromName(json.getAsString());
		final JsonSerializer<LogLevel> logLevelJsonSerializer = (src, typeOfSrc, context) ->
			context.serialize(src.toName());

		final GsonBuilder builder = new GsonBuilder()
			.registerTypeAdapter(HopConfig.class, hopConfigJsonDeserializer)
			.registerTypeAdapter(HopConfig.class, hopConfigJsonSerializer)
			.registerTypeAdapter(ConflictResolution.class, conflictResolutionJsonDeserializer)
			.registerTypeAdapter(ConflictResolution.class, conflictResolutionJsonSerializer)
			.registerTypeAdapter(LogLevel.class, logLevelJsonDeserializer)
			.registerTypeAdapter(LogLevel.class, logLevelJsonSerializer);

		return builder.create();
	}

	/**
	 * Adds a util to known to the EL expression engine and to scripts.
	 * <p>
	 * Utils are available from EL as <code>name:method()</code> and as <code>utils.name.method()</code> in scripts
	 *
	 * @param name the util name or namespace
	 * @param util the util, either as an object or Class (for static method access)
	 * @return self for chaining
	 */
	public RunnerBuilder addUtil(String name, Object util) {
		utils.put(name, util);
		return this;
	}

	/**
	 * Adds utils to known to the EL expression engine and to scripts.
	 * <p>
	 * Utils are available from EL as <code>name:method()</code> and as <code>utils.name.method()</code> in scripts
	 *
	 * @param utils key-value pairs to make known
	 * @return self for chaining
	 */
	public RunnerBuilder addUtils(Map<String, Object> utils) {
		this.utils.putAll(utils);
		return this;
	}


	/**
	 * Sets an initial variable known to the EL expression engine and to scripts.
	 *
	 * @param name  the name of the variable
	 * @param value the value of the variable
	 * @return self for chaining
	 */
	public RunnerBuilder addVariable(String name, Object value) {
		variables.put(name, value);
		return this;
	}

	/**
	 * Sets initial variables known to the EL expression engine and to scripts.
	 *
	 * @param variables key-value pairs to make known
	 * @return self for chaining
	 */
	public RunnerBuilder addVariables(Map<String, Object> variables) {
		this.variables.putAll(variables);
		return this;
	}

	/**
	 * Makes hop types known to the runner.
	 *
	 * @param hops the array of hop types to make known
	 * @return self for chaining
	 */
	public RunnerBuilder addHop(Hop<?>... hops) {
		return addHops(Arrays.asList(hops));
	}

	/**
	 * Makes hop types known to the runner.
	 *
	 * @param hops the collection of hop types to make known
	 * @return self for chaining
	 */
	public RunnerBuilder addHops(Collection<Hop<?>> hops) {
		knownHops.addAll(hops);
		return this;
	}

	/**
	 * Registers a file supplier.
	 *
	 * @param type         the file type name to register (will become a function in the `file:` util namespace)
	 * @param fileSupplier the file supplier to register
	 * @return self for chaining
	 */
	public RunnerBuilder registerFile(String type, Function<String, File> fileSupplier) {
		fileTypeSuppliers.put(type, fileSupplier);
		return this;
	}

	/**
	 * Registers multiple file suppliers.
	 *
	 * @param suppliers the map of suppliers to add
	 * @return self for chaining
	 */
	public RunnerBuilder registerFiles(Map<String, Function<String, File>> suppliers) {
		fileTypeSuppliers.putAll(suppliers);
		return this;
	}

	/**
	 * Constructs a {@link Runner} with the configuration of this builder.
	 *
	 * @param script the script to run
	 * @return the constructed Runner configured with the given script
	 */
	public Runner build(Script script) {
		final Map<String, Object> utils = new HashMap<>(this.utils);
		if (this.addDefaultUtils) {
			utils.put("arrays", Arrays.class);
			utils.put("stream", Stream.class);
			utils.put("class", Class.class);
			utils.put("collections", Collections.class);
		}
		return new RunnerImpl(
			Collections.unmodifiableSet(new HashSet<>(knownHops)),
			utils,
			new HashMap<>(variables),
			new HashMap<>(fileTypeSuppliers),
			runHandler,
			script
		);
	}

	/**
	 * Constructs a Runner with the configuration this builder knows.
	 *
	 * @param scriptAsJson the JSON string representation of the script to parse
	 * @return the constructed Runner configured with the given script
	 */
	public Runner build(String scriptAsJson) {
		return build(scriptFromJson(scriptAsJson));
	}

	/**
	 * Parses the given JSON into a script. Requires hop types used in the script to be known.
	 *
	 * @param scriptAsJson the JSON string representation of the script to parse
	 * @return the parsed script
	 */
	public Script scriptFromJson(String scriptAsJson) {
		return gson.fromJson(scriptAsJson, Script.class);
	}

	/**
	 * Parses the given JSON into a script. Requires hop types used in the script to be known.
	 *
	 * @param scriptAsJson a reader for the JSON representation of the script to parse
	 * @return the parsed script
	 */
	public Script scriptFromJson(Reader scriptAsJson) {
		return gson.fromJson(scriptAsJson, Script.class);
	}

	/**
	 * Generates the JSON representation of the given script. Requires hop types used in the script to be known.
	 *
	 * @param script the script to turn into JSON
	 * @return the stringified JSON representation of the given script
	 */
	public String scriptToJson(Script script) {
		return gson.toJson(script);
	}
}
