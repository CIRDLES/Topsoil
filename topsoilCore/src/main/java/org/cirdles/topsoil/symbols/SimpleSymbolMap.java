package org.cirdles.topsoil.symbols;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleSymbolMap<K extends SymbolKey<?>> extends AbstractMap<K, Object> implements SymbolMap<K> {

    private List<Entry<K, Object>> entryList;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SimpleSymbolMap() {
        this(null);
    }

    public SimpleSymbolMap(Map<K, Object> map) {
        entryList = new ArrayList<>();

        if (map != null) {
            putAll(map);
        }
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public Set<Entry<K, Object>> entrySet() {
        return new AbstractSet<Entry<K, Object>>() {
            @Override
            public Iterator<Entry<K, Object>> iterator() {
                return entryList.iterator();
            }

            @Override
            public int size() {
                return entryList.size();
            }

            @Override
            public boolean remove(Object o) {
                return entryList.remove(o);
            }
        };
    }

    @Override
    public final Object put(K key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }

        if (! key.match(value)) {
            throw new IllegalArgumentException(
                    "Value \"" + value + "\" must be of the same type as the " +
                            key.getClass().getSimpleName() + " \"" + key.getTitle() + "\"" +
                            " (" + key.getType().getSimpleName() + ")."
            );
        }

        Entry<K, Object> entry = null;
        for (Entry<K, Object> e : entryList) {
            if (key.equals(e.getKey())) {
                entry = e;
                break;
            }
        }

        Object oldValue = null;
        if (entry != null) {
            oldValue = entry.getValue();
            entry.setValue(value);
        } else {
            entryList.add(new SimpleEntry<>(key, value));
        }
        return oldValue;
    }

}
