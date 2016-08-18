package org.cirdles.topsoil.app.progress.util;

import org.cirdles.topsoil.app.dataset.Dataset;
import org.cirdles.topsoil.app.dataset.SimpleDataset;
import org.cirdles.topsoil.app.progress.TopsoilRawData;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;

/**
 * Created by benjaminmuldrow on 8/9/16.
 */
public class PlotUtil {

    public static Dataset getDataSet(TopsoilTable table, TopsoilRawData data) {
        return new SimpleDataset(table.getTitle(), data.getRawData());
    }

}
