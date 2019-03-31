package org.cirdles.topsoil.app.util;

import javafx.concurrent.Task;
import org.cirdles.topsoil.app.menu.helpers.VisualizationsMenuHelper;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.plot.Plot;

import java.util.Observable;
import java.util.Observer;

public class TableObserver implements Observer {

    private DataTable table;
    private Plot plot;
    private Task<Void> currentUpdate;

    public TableObserver(DataTable table, Plot plot) {
        this.table = table;
        this.plot = plot;
        this.currentUpdate = null;
    }

    @Override
    public void update(Observable o, Object obj) {
        if (currentUpdate == null) {
            currentUpdate = new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        Thread.sleep(10);  // Forces a delay in case of many sequential changes ((de)selecting a segment)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            currentUpdate.setOnSucceeded(event -> {
                plot.setData(VisualizationsMenuHelper.getPlotDataFromTable(table));
                currentUpdate = null;
            });
            currentUpdate.setOnCancelled(event -> currentUpdate = null);
            new Thread(currentUpdate).start();
        }
    }

}
