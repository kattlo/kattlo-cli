package com.github.kattlo.util;

import java.util.Map;
import java.util.Objects;

/**
 * @author fabiojose
 */
public class MyMap<K, V> {

    private final Map<K, V> map;
    public MyMap(Map<K, V> map) {
        this.map = Objects.requireNonNull(map, "map must be not null");
    }

    /**
     * Unfold to another {@link Map} instance
     * @return The wrapped {@link Map} instance
     * @throws ClassCastException If value is not an instance of {@link Map}
     */
    @SuppressWarnings("unchecked")
    public MyMap<K, V> unfold(K key){
        return new MyMap<>((Map<K, V>)map.get(key));
    }

    /**
     * Unfold to another {@link Map} instance, changing the type of value
     * @param <NEW_V> New value type to cast
     * @param typeOfValue New value type
     * @return The wrapped {@link Map} instance
     * @throws ClassCastException If value is not an instance of {@link Map}
     */
    @SuppressWarnings("unchecked")
    public <NEW_V> MyMap<K, NEW_V> unfold(K key, Class<NEW_V> typeOfValue){
        return new MyMap<K, NEW_V>((Map<K, NEW_V>)map.get(key));
    }

    public Map<K, V> unbox() {
        return map;
    }

    /**
     *
     * @param <VL> The type of value
     * @param key
     * @return The actual value casted to type
     * @throws ClassCastException If value is not an instace of VL
     */
    @SuppressWarnings("unchecked")
    public <VL> VL get(K key){
        return (VL)map.get(key);
    }
}
