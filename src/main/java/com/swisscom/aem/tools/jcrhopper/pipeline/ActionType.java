package com.swisscom.aem.tools.jcrhopper.pipeline;

import com.swisscom.aem.tools.jcrhopper.pipeline.actions.ChildNodes;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.ChildNodesConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.CopyNode;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.CopyNodeConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.CreateChildNode;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.CreateChildNodeConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.Declare;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.DeclareConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.Each;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.EachConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.FilterNode;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.FilterNodeConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.MoveNode;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.MoveNodeConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.NodeQuery;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.NodeQueryConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.RenameProperty;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.RenamePropertyConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.ReorderNode;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.ReorderNodeConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.ResolveNode;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.ResolveNodeConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.RunScript;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.RunScriptConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.SetProperty;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.SetPropertyConfig;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.Try;
import com.swisscom.aem.tools.jcrhopper.pipeline.actions.TryConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
enum ActionType {
	CHILD_NODES("childNodes", ChildNodes.class, ChildNodesConfig.class),
	COPY_NODE("copyNode", CopyNode.class, CopyNodeConfig.class),
	CREATE_CHILD_NODE("createChildNode", CreateChildNode.class, CreateChildNodeConfig.class),
	DECLARE("declare", Declare.class, DeclareConfig.class),
	EACH("each", Each.class, EachConfig.class),
	FILTER_NODE("filterNode", FilterNode.class, FilterNodeConfig.class),
	MOVE_NODE("moveNode", MoveNode.class, MoveNodeConfig.class),
	NODE_QUERY("nodeQuery", NodeQuery.class, NodeQueryConfig.class),
	RENAME_PROPERTY("renameProperty", RenameProperty.class, RenamePropertyConfig.class),
	REORDER_NODE("reorderNode", ReorderNode.class, ReorderNodeConfig.class),
	RESOLVE_NODE("resolveNode", ResolveNode.class, ResolveNodeConfig.class),
	RUN_SCRIPT("runScript", RunScript.class, RunScriptConfig.class),
	SET_PROPERTY("setProperty", SetProperty.class, SetPropertyConfig.class),
	TRY("try", Try.class, TryConfig.class);

	private final String identifier;
	private final Class<? extends ConfigurableAction> actionClass;
	private final Class<?> configClass;

	static ActionType forIdentifier(String identifier) {
		return Stream.of(ActionType.values())
			.filter(at -> StringUtils.equalsIgnoreCase(at.getIdentifier(), identifier))
			.findFirst()
			.orElse(null);
	}

	static ActionType forConfig(Class<?> configClass) {
		return Stream.of(ActionType.values())
			.filter(at -> at.configClass == configClass)
			.findFirst()
			.orElse(null);
	}

	static ActionType forConfig(Object config) {
		return forConfig(config.getClass());
	}

	Action create(Object config) {
		final Class<?> configClass = config.getClass();
		if (!configClass.isAssignableFrom(configClass)) {
			throw new IllegalArgumentException(String.format(
				"Wrong config class %s given for %s, expected %s.",
				configClass.getSimpleName(),
				actionClass.getSimpleName(),
				this.configClass.getSimpleName()
			));
		}

		try {
			final Constructor<? extends Action> constructor = actionClass.getConstructor(configClass);
			return constructor.newInstance(config);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Constructing an action failed", e);
		}
	}
}

