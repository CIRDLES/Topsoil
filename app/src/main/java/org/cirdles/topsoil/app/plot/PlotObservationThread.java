package org.cirdles.topsoil.app.plot;

import org.cirdles.topsoil.plot.Plot;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A thread that updates the Java plot based on changes in the Javascript
 * Executes continually every 100 milliseconds
 */
public class PlotObservationThread {
    private Plot plot;
    private PlotPropertiesPanelController propertiesPanel;

    public ScheduledExecutorService initializePlotObservation(Plot plot, PlotPropertiesPanelController propertiesPanel) {
        this.plot = plot;
        this.propertiesPanel = propertiesPanel;

        ScheduledExecutorService observer = Executors.newSingleThreadScheduledExecutor();
        observer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //Check if the plot's properties have been updated from the Javascript side
                if(plot.getIfUpdated()) {
                    plot.updateProperties();
                    updateAxes();

                    //PROPERTIES is now in sync with Javascript properties
                    plot.setIfUpdated(false);
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        return observer;
    }

    private void updateAxes() {
        if(propertiesPanel.liveAxisUpdate()) {
            Map<String, Object> properties = plot.getProperties();
            propertiesPanel.setXAxisMinTextField(properties.get("X Min").toString());
            propertiesPanel.setXAxisMaxTextField(properties.get("X Max").toString());
            propertiesPanel.setYAxisMinTextField(properties.get("Y Min").toString());
            propertiesPanel.setYAxisMaxTextField(properties.get("Y Max").toString());
        }
    }


}
