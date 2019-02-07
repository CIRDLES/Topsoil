package org.cirdles.topsoil.app.control.plot;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author marottajb
 */
public class PlotStage extends Stage {

    // TODO Refactor for better consistency, model privacy
    public PlotStage(TopsoilPlotView plotView) {
        super();
        setScene(new Scene(plotView));
    }

}
