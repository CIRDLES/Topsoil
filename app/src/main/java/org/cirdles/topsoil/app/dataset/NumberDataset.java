package org.cirdles.topsoil.app.dataset;

import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.TopsoilRawData;

import java.util.List;

/**
 * @author marottajb
 */
public class NumberDataset implements Dataset<Number> {

    private final String name;
    private final TopsoilRawData<Number> rawData;

    public NumberDataset(String name, TopsoilRawData<Number> rawData) {
        this.name = name;
        this.rawData = rawData;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Field<Number>> getFields() {
        return rawData.getFields();
    }

    @Override
    public List<Entry> getEntries() {
        return rawData.getEntries();
    }
}
