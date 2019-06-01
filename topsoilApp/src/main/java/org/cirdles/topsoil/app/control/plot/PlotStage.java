package org.cirdles.topsoil.app.control.plot;

import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.plot.PlotType;
import org.cirdles.topsoil.javafx.PlotView;

/**
 * A custom {@code Stage} meant to display a {@link PlotControlView}.
 */
public class PlotStage extends Stage {

    private static final double INIT_WIDTH = 1000.0;
    private static final double INIT_HEIGHT = 600.0;

    private PlotControlView plotControlView;

    public PlotStage(PlotView plot, FXDataTable table) {
        super();
        this.plotControlView = new PlotControlView(plot, table);
        this.setScene(new Scene(plotControlView, INIT_WIDTH, INIT_HEIGHT));

        // Shut down plot properties observation thread when closed
        this.setOnCloseRequest(event -> plotControlView.shutdownObserver());

        // Get Topsoil logo
        this.getIcons().add(Topsoil.getLogo());

        // Bind plot stage title to properties panel title
        PlotType plotType = plotControlView.getPlot().getPlotType();
        this.titleProperty().bind(Bindings.createStringBinding(
                () -> plotType.getName() + ": " + plotControlView.getPropertiesPanel().titleProperty().get(),
                plotControlView.getPropertiesPanel().titleProperty()
        ));
    }

}
