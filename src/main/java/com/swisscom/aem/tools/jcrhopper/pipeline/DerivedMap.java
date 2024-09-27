package com.swisscom.aem.tools.jcrhopper.pipeline;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DerivedMap<K, V> implements Map<K, V> {
	@NonNull
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
		return backing.containsKey(key) || local.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return backing.containsValue(value) || local.containsValue(value);
	}

	@Override
	public V get(Object key) {
		if (local.containsKey(key)) {
			return local.get(key);
		}
		return backing.get(key);
	}

	@Override
	public V put(K key, V value) {
		V previous = null;
		if (this.containsKey(key)) {
			previous = this.get(key);
		}
		local.put(key, value);
		return previous;
	}

	@Override
	public V remove(Object key) {
		V previous = null;
		if (this.containsKey(key)) {
			previous = this.get(key);
		}
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
