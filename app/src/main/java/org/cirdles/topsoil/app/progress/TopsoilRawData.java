package org.cirdles.topsoil.app.progress;

import org.cirdles.topsoil.app.dataset.RawData;
import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.dataset.field.NumberField;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilPlotEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjaminmuldrow on 8/3/16.
 */
public class TopsoilRawData {

    private List<Field<?>> fields;
    private List<Entry> entries;

    public TopsoilRawData(TopsoilTable table) {

        // Initialize fields
        List<Field<?>> fields = new ArrayList<>();
        for (String header : table.getHeaders()) {
            Field<?> field = new NumberField(header);
            fields.add(field);
        }
        this.fields = fields;

        // Initialize entries
        List<Entry> entries = new ArrayList<>();

        // put relevant entries into entry list
        for (int i = 0; i < table.getTable().getItems().size(); i ++) {
            entries.add(new TopsoilPlotEntry());
            for (int j = 0; j < table.getHeaders().length; j++) {
                double currentValue =
                        ((TopsoilDataEntry)table.getTable().getItems().get(i))
                        .getProperties().get(j).getValue();
                entries.get(i).set((NumberField)fields.get(j), currentValue);
            }
        }
        // add value
        this.entries = entries;

    }

    /**
     * Returns a rawData instance of the table data
     * @return RawData instance of table's data
     */
    public RawData getRawData() {
        RawData rawData = new RawData(this.fields, this.entries);
        return rawData;
    }
}
