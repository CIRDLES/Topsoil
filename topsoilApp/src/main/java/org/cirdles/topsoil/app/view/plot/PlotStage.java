package org.cirdles.topsoil.app.view.plot;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author marottajb
 */
public class PlotStage extends Stage {

    // TODO Refactor for better consistency, data privacy
    public PlotStage(TopsoilPlotView plotView) {
        super();
        setScene(new Scene(plotView));
    }

}
