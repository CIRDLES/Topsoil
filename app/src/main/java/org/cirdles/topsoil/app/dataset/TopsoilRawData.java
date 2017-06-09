package org.cirdles.topsoil.app.dataset;

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.table.TopsoilDataTable;

import java.util.Collection;

/**
 * A class containing a {@code Collection} of {@link Field} objects, and one of {@link Entry} objects. Its purpose is to
 * contain a "raw" reference to the data stored in a {@link TopsoilDataTable}.
 *
 * @author Benjamin Muldrow
 * @see TopsoilDataTable
 */
public class TopsoilRawData<T> {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code Collection} of {@code Field}s for the {@code TopsoilRawData}.
     */
    private Collection<Field<T>> fields;

    /**
     * The {@code Collection} of {@code Entry}s for the {@code TopsoilRawData}.
     */
    private Collection<Entry> entries;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code TopsoilRawData} with the specified {@code Field}s and {@code Entry}s.
     *
     * @param fields    Fields for the TopsoilRawData
     * @param entries   Entries for the TopsoilRawData
     */
    public TopsoilRawData(Collection<Field<T>> fields, Collection<Entry> entries) {
        this.fields = fields;
        this.entries = entries;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Returns the collection of {@code Field}s for the {@code TopsoilRawData}.
     *
     * @return  Collection of Fields
     */
    public Collection<Field<T>> getFields() {
        return fields;
    }

    /**
     * Returns the collection of {@code Entry}s for the {@code TopsoilRawData}.
     *
     * @return  Collection of Entries
     */
    public Collection<Entry> getEntries() {
        return entries;
    }
}
