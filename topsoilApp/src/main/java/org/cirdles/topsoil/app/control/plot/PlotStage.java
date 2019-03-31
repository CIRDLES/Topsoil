package org.cirdles.topsoil.app.control.plot;

import javafx.stage.Stage;
import org.cirdles.topsoil.plot.PlotType;

public class PlotStage extends Stage {

    private PlotType plotType;

    public PlotStage(PlotType plotType) {
        super();
        this.plotType = plotType;
    }

    public PlotType getPlotType() {
        return plotType;
    }

}
