package org.cirdles.topsoil.app.util;

import org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotProperty;

import java.text.DecimalFormat;
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

    private DecimalFormat df = new DecimalFormat("0.000");

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

            String xMin = properties.get(PlotProperty.X_MIN).toString(),
                    xMax = properties.get(PlotProperty.X_MAX).toString(),
                    yMin = properties.get(PlotProperty.Y_MIN).toString(),
                    yMax = properties.get(PlotProperty.Y_MAX).toString();

            double xMinNumber = (! xMin.isEmpty()) ? Double.parseDouble(xMin) : 0.0,
                    xMaxNumber = (! xMax.isEmpty()) ? Double.parseDouble(xMax) : 0.0,
                    yMinNumber = (! yMin.isEmpty()) ? Double.parseDouble(yMin) : 0.0,
                    yMaxNumber = (! yMax.isEmpty()) ? Double.parseDouble(yMax) : 0.0;

            propertiesPanel.updateXMin(df.format(xMinNumber));
            propertiesPanel.updateXMax(df.format(xMaxNumber));
            propertiesPanel.updateYMin(df.format(yMinNumber));
            propertiesPanel.updateYMax(df.format(yMaxNumber));
        }
    }


}
