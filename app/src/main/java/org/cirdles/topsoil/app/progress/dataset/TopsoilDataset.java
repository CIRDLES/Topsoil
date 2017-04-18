package org.cirdles.topsoil.app.progress.dataset;

import org.cirdles.topsoil.app.dataset.Dataset;
import org.cirdles.topsoil.app.dataset.RawData;
import org.cirdles.topsoil.app.dataset.entry.Entry;
import org.cirdles.topsoil.app.dataset.field.Field;
import org.cirdles.topsoil.app.progress.TopsoilRawData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 4/7/2017.
 */
public class TopsoilDataset implements Dataset {

        private final String name;
        private final TopsoilRawData<Double> rawData;

        public TopsoilDataset(String name, TopsoilRawData<Double> rawData) {
            this.name = name;
            this.rawData = rawData;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        @Deprecated
        public List<Field<?>> getFields() {
            return new ArrayList<Field<?>>();
        }

        public List<Field<Double>> getUsableFields() {
            return rawData.getFields();
        }

        @Override
        public List<Entry> getEntries() {
            return rawData.getEntries();
        }

}
