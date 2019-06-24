package org.cirdles.topsoil.app.control.plot;

import org.cirdles.topsoil.app.control.plot.panel.PlotOptionsPanel;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotFunction;

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
    private PlotOptionsPanel propertiesPanel;

    private DecimalFormat df = new DecimalFormat("0.000");

    public ScheduledExecutorService initializePlotObservation(Plot plot, PlotOptionsPanel propertiesPanel ) {
        this.plot = plot;
        this.propertiesPanel = propertiesPanel;

        ScheduledExecutorService observer = Executors.newSingleThreadScheduledExecutor();
        observer.scheduleAtFixedRate(() -> updateAxes(), 0, 500, TimeUnit.MILLISECONDS);

        return observer;
    }

    private void updateAxes() {
        if(propertiesPanel.liveAxisUpdateActive()) {
            Double[] axisExtents = (Double[]) plot.call(PlotFunction.Scatter.GET_AXIS_EXTENTS);
            if (axisExtents != null) {
                propertiesPanel.updateXMin(df.format(axisExtents[0]));
                propertiesPanel.updateXMax(df.format(axisExtents[1]));
                propertiesPanel.updateYMin(df.format(axisExtents[2]));
                propertiesPanel.updateYMax(df.format(axisExtents[3]));
            }
        }
    }

}
