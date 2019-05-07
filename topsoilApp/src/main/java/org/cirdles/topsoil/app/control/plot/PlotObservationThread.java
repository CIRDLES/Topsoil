package org.cirdles.topsoil.app.control.plot;

import org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotProperties;

import java.text.DecimalFormat;
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

    private DecimalFormat df = new DecimalFormat("0.000");

    public ScheduledExecutorService initializePlotObservation(Plot plot, PlotPropertiesPanel propertiesPanel ) {
        this.plot = plot;
        this.propertiesPanel = propertiesPanel;

        ScheduledExecutorService observer = Executors.newSingleThreadScheduledExecutor();
        observer.scheduleAtFixedRate(() -> {
                //Check if the plot's properties have been updated from the Javascript side
                if(plot.getIfUpdated()) {
                    plot.updateProperties();
                    updateAxes();

                    //PROPERTIES is now in sync with Javascript properties
                    plot.setIfUpdated(false);
                }
        }, 0, 100, TimeUnit.MILLISECONDS);

        return observer;
    }

    private void updateAxes() {
        if(propertiesPanel.liveAxisUpdateActive()) {
            PlotProperties properties = plot.getProperties();

            Number xMin = properties.get(PlotProperties.X_MIN),
                    xMax = properties.get(PlotProperties.X_MAX),
                    yMin = properties.get(PlotProperties.Y_MIN),
                    yMax = properties.get(PlotProperties.Y_MAX);

            propertiesPanel.updateXMin(df.format(xMin.doubleValue()));
            propertiesPanel.updateXMax(df.format(xMax.doubleValue()));
            propertiesPanel.updateYMin(df.format(yMin.doubleValue()));
            propertiesPanel.updateYMax(df.format(yMax.doubleValue()));
        }
    }


}
