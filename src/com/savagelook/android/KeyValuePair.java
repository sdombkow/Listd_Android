package com.savagelook.android;

import java.util.Map;

public class KeyValuePair<K, V> implements Map.Entry<K, V> {
	private final K key;
	private V value;
	
	public KeyValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public K getKey() {
		return this.key;	
	}
	
	public V getValue() {
		return this.value;	
	}

	public V setValue(V value) {
		V old = this.value;
		this.value = value;
		return old;
	}
	
	public String toString() {
		return this.key.toString();	
	}
}
