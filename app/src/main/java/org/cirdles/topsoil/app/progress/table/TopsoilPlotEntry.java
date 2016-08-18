package org.cirdles.topsoil.app.progress.table;

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.entry.EntryListener;
import org.cirdles.topsoil.app.dataset.field.Field;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by benjaminmuldrow on 8/8/16.
 */
public class TopsoilPlotEntry implements Entry {

    private Map fieldMap;

    public TopsoilPlotEntry() {
        fieldMap = new HashMap<Field, Double>();
    }

    @Override
    public <T> Optional<T> get(Field<? extends T> field) {
        //Optional.ofNullable((T) fieldsToValues.get(field))
        return Optional.ofNullable((T) fieldMap.get(field));
        //return (Optional<T>) this.fieldMap.get(field);
    }

    @Override
    public <T> void set(Field<? super T> field, T value) {
        this.fieldMap.put(field, value);
    }

    @Override
    public void addListener(EntryListener listener) {

    }

    @Override
    public void removeListener(EntryListener listener) {

    }
}
