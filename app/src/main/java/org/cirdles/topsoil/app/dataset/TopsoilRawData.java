package org.cirdles.topsoil.app.dataset;

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.table.TopsoilDataTable;

import java.util.List;

/**
 * A class containing a {@code List} of {@link Field} objects, and one of {@link Entry} objects. Its purpose is to
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
     * The {@code List} of {@code Field}s for the {@code TopsoilRawData}.
     */
    private List<Field<T>> fields;

    /**
     * The {@code List} of {@code Entry}s for the {@code TopsoilRawData}.
     */
    private List<Entry> entries;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code TopsoilRawData} with the specified {@code Field}s and {@code Entry}s.
     *
     * @param fields    Fields for the TopsoilRawData
     * @param entries   Entries for the TopsoilRawData
     */
    public TopsoilRawData(List<Field<T>> fields, List<Entry> entries) {
        this.fields = fields;
        this.entries = entries;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Returns the list of {@code Field}s for the {@code TopsoilRawData}.
     *
     * @return  List of Fields
     */
    public List<Field<T>> getFields() {
        return fields;
    }

    /**
     * Returns the list of {@code Entry}s for the {@code TopsoilRawData}.
     *
     * @return  List of Entries
     */
    public List<Entry> getEntries() {
        return entries;
    }
}
