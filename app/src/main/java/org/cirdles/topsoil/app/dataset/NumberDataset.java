package org.cirdles.topsoil.app.dataset;

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;

import java.util.Collection;

/**
 * Stores a {@link TopsoilRawData} of type {@code Number} with a name.
 *
 * @author Jake Marotta
 * @see Dataset
 * @see TopsoilRawData
 */
public class NumberDataset implements Dataset<Number> {

    //***********************
    // Attributes
    //***********************

    /**
     * The name of the {@code NumberDataset}.
     */
    private final String name;

    /**
     * The raw data of the {@code NumberDataset} as {@code TopsoilRawData}.
     */
    private final TopsoilRawData<Number> rawData;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code NumberDataset} with the specified name and {@code TopsoilRawData}.
     *
     * @param name  String name
     * @param rawData   data as TopsoilRawData of type Number
     */
    public NumberDataset(String name, TopsoilRawData<Number> rawData) {
        this.name = name;
        this.rawData = rawData;
    }

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc}
     */
    @Override
    public Collection<Field<Number>> getFields() {
        return rawData.getFields();
    }

    /** {@inheritDoc}
     */
    @Override
    public Collection<Entry> getEntries() {
        return rawData.getEntries();
    }
}
