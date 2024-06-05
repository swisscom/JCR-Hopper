package com.swisscom.aem.tools.jcrhopper.pipeline;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ActionFactory {

	private ActionFactory() {
		// prevent instantiation
	}

	static Class<?> actionConfigForType(String type) {
		final ActionType actionType = ActionType.forIdentifier(type);
		if (actionType == null) {
			return null;
		}
		return actionType.getConfigClass();
	}

	static Action configureAction(Object config) {
		final ActionType actionType = ActionType.forConfig(config);
		if (actionType == null) {
			return null;
		}

		return actionType.create(config);
	}

	static String configType(Object config) {
		final ActionType actionType = ActionType.forConfig(config);
		if (actionType == null) {
			return null;
		}

		return actionType.getIdentifier();
	}

	static List<Class<? extends ConfigurableAction>> listActionTypes() {
		return Stream.of(ActionType.values())
			.map(ActionType::getActionClass)
			.collect(Collectors.toList());
	}
}
