package org.cirdles.topsoil.app.dataset.entry;

import org.cirdles.topsoil.app.dataset.field.Field;

import java.util.*;

/**
 * An {@code Entry} containing a {@code Map} of {@link Field}s to {@code Double}s. Each entry in the {@code Map} details which
 * data value belongs to which {@code Field} within a {@code TopsoilPlotEntry}. For example, the {@code Double} value
 * 1.12 may be mapped to the field 'X', 1.56 to field 'Y', etc.
 *
 * @author Benjamin Muldrow
 */
public class TopsoilPlotEntry implements Entry {

    private Map fieldsToValues;
    private List<EntryListener> listeners;

    /**
     * Constructs an empty {@code TopsoilPlotEntry}.
     */
    public TopsoilPlotEntry() {
        fieldsToValues = new HashMap<Field, Double>();
        listeners = new ArrayList<>();
    }

    /** {@inheritDoc}
     */
    @Override
    public <T> Optional<T> get(Field<? extends T> field) {
        //Optional.ofNullable((T) fieldsToValues.get(field))
        return Optional.ofNullable((T) fieldsToValues.get(field));
        //return (Optional<T>) this.fieldsToValues.get(field);
    }

    /** {@inheritDoc}
     */
    @Override
    public <T> void set(Field<? super T> field, T value) {
        this.fieldsToValues.put(field, value);

        listeners.forEach(listener -> {
            listener.changed(this, field);
        });
    }

    /** {@inheritDoc}
     */
    @Override
    public void addListener(EntryListener listener) {
        listeners.add(listener);
    }

    /** {@inheritDoc}
     */
    @Override
    public void removeListener(EntryListener listener) {
        listeners.remove(listener);
    }
}
