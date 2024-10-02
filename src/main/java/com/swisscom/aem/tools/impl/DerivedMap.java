package com.swisscom.aem.tools.impl;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@RequiredArgsConstructor
public final class DerivedMap<K, V> implements Map<K, V> {
	@Nonnull
	private final Map<K, V> backing;
	private final Map<K, V> local = new HashMap<>();

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
	@SuppressFBWarnings(value = "MUI_CONTAINSKEY_BEFORE_GET", justification = "We need to account for null in the local map")
	public V get(Object key) {
		if (local.containsKey(key)) {
			return local.get(key);
		}
		return backing.get(key);
	}

	@Override
	public V put(K key, V value) {
		final V previous = this.get(key);
		local.put(key, value);
		return previous;
	}

	@Override
	public V remove(Object key) {
		final V previous = this.get(key);
		local.remove(key);
		return previous;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.local.putAll(m);
	}

	@Override
	public void clear() {
		this.local.clear();
	}

	@Override
	public Set<K> keySet() {
		final Set<K> result = new HashSet<>();
		result.addAll(backing.keySet());
		result.addAll(local.keySet());
		return result;
	}


	@Override
	public Collection<V> values() {
		return this.keySet()
			.stream()
			.map(this::get)
			.collect(Collectors.toList());
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return this.keySet()
			.stream()
			.map(key -> new AbstractMap.SimpleEntry<>(key, this.get(key)))
			.collect(Collectors.toSet());
	}
}
