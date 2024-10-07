package com.swisscom.aem.tools.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.jcr.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HopVariables implements Map<String, Object> {

	public static final String NODE_VAR_NAME = "node";

	@Nonnull
	private final Map<String, Object> backing;

	@Nonnull
	@Getter
	private final Node node;

	private final Map<String, Object> local = new HashMap<>();

	@Override
	public int size() {
		return this.keySet().size();
	}

	@Override
	public boolean isEmpty() {
		return backing.isEmpty() && local.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return local.containsKey(key) || backing.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return backing.containsValue(value) || local.containsValue(value);
	}

	@Override
	@SuppressFBWarnings(
		value = { "MUI_CONTAINSKEY_BEFORE_GET", "URV_INHERITED_METHOD_WITH_RELATED_TYPES" },
		justification = "We need to account for null in the local map. Special-casing the node is necessary."
	)
	public Object get(Object key) {
		if (NODE_VAR_NAME.equals(key)) {
			return node;
		}
		if (local.containsKey(key)) {
			return local.get(key);
		}
		return backing.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		final Object previous = this.get(key);
		local.put(key, value);
		return previous;
	}

	@Override
	public Object remove(Object key) {
		final Object previous = this.get(key);
		local.remove(key);
		return previous;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		this.local.putAll(m);
	}

	@Override
	public void clear() {
		this.local.clear();
	}

	@Override
	public Set<String> keySet() {
		final Set<String> result = new HashSet<>();
		result.addAll(backing.keySet());
		result.addAll(local.keySet());
		result.add(NODE_VAR_NAME);
		return result;
	}

	@Override
	public Collection<Object> values() {
		return this.keySet().stream().map(this::get).collect(Collectors.toList());
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return this.keySet().stream().map(key -> new AbstractMap.SimpleEntry<>(key, this.get(key))).collect(Collectors.toSet());
	}
}
