package org.cirdles.topsoil.symbols;

import org.json.JSONObject;
import org.json.JSONString;

import java.util.Map;

public interface SymbolMap<K extends SymbolKey<?>> extends Map<K, Object>, JSONString {

    default void putDefaultValues(K[] keys) {
        if (keys == null) {
            return;
        }

        for (K key : keys) {
            put(key, key.getDefaultValue());
        }
    }

    default <V> V getAndCast(SymbolKey<V> key) {
        if (key == null || key.getType() == null) {
            return null;
        }
        return key.getType().cast(get(key));
    }

    @Override
    default String toJSONString() {
        return getJSONString(this);
    }

    static String getJSONString(Map<? extends SymbolKey<?>, Object> map) {
        if (map == null) {
            return null;
        }

        JSONObject json = new JSONObject();
        SymbolKey<?> key;
        for (Entry<? extends SymbolKey<?>, Object> entry : map.entrySet()) {
            key = entry.getKey();
            json.put(key.getTitle(), key.getJSONCompatibleValue(entry.getValue()));
        }
        return json.toString();
    }
}
