package org.cirdles.topsoil.app.util;

import org.cirdles.topsoil.app.view.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotProperty;

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
    private PlotPropertiesPanel propertiesPanel;

    public ScheduledExecutorService initializePlotObservation(Plot plot, PlotPropertiesPanel propertiesPanel ) {
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
            Map<PlotProperty, Object> properties = plot.getProperties();
            propertiesPanel.updateXMin(properties.get(PlotProperty.X_MIN).toString());
            propertiesPanel.updateXMax(properties.get(PlotProperty.X_MAX).toString());
            propertiesPanel.updateYMin(properties.get(PlotProperty.Y_MIN).toString());
            propertiesPanel.updateYMax(properties.get(PlotProperty.Y_MAX).toString());
        }
    }


}
