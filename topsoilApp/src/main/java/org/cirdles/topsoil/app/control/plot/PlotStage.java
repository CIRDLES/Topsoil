package org.cirdles.topsoil.app.control.plot;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotType;

/**
 * A custom {@code Stage} meant to display a {@link PlotView}.
 */
class PlotStage extends Stage {

    private static final double INIT_WIDTH = 1000.0;
    private static final double INIT_HEIGHT = 600.0;

    private PlotView plotView;

    public PlotStage(Plot plot, DataTable table) {
        super();
        this.plotView = new PlotView(plot, table);
        this.setScene(new Scene(plotView, INIT_WIDTH, INIT_HEIGHT));

        // Shut down plot properties observation thread when closed
        this.setOnCloseRequest(event -> plotView.shutdownObserver());

        // Get Topsoil logo
        this.getIcons().add(Topsoil.getLogo());

        // Bind plot stage title to properties panel title
        PlotType plotType = plotView.getPlot().getPlotType();
        this.titleProperty().bind(Bindings.createStringBinding(
                () -> plotType.getName() + ": " + plotView.getPropertiesPanel().titleProperty().get(),
                plotView.getPropertiesPanel().titleProperty()
        ));
    }

}
