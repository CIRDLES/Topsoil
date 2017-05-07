package org.cirdles.topsoil.app.progress;

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;

import java.util.List;

/**
 * Created by benjaminmuldrow on 8/3/16.
 */
public class TopsoilRawData<T> {

    private List<Field<T>> fields;
    private List<Entry> entries;

    public TopsoilRawData(List<Field<T>> fields, List<Entry> entries) {
        this.fields = fields;
        this.entries = entries;
    }

    public List<Field<T>> getFields() {
        return fields;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
