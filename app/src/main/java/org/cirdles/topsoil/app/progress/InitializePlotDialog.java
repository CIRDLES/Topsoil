package org.cirdles.topsoil.app.progress;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.dataset.Dataset;
import org.cirdles.topsoil.app.plot.PlotType;
import org.cirdles.topsoil.app.plot.Variable;
import org.cirdles.topsoil.app.plot.VariableBindingDialog;
import org.cirdles.topsoil.app.plot.Variables;
import org.cirdles.topsoil.app.plot.PlotWindow;
import org.cirdles.topsoil.plot.Plot;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by sbunce on 7/25/2016.
 */
public class InitializePlotDialog {

    public InitializePlotDialog (PlotType plotType, Dataset dataset) {
        this.initializeAndShow(plotType, dataset);
    }

    public void initializeAndShow(PlotType plotType, Dataset dataset) {
        List<Variable> variables = asList(
                Variables.X,
                Variables.SIGMA_X,
                Variables.Y,
                Variables.SIGMA_Y,
                Variables.RHO);

        new VariableBindingDialog(variables, dataset).showAndWait()
                .ifPresent(data -> {
                    Plot plot = plotType.newInstance();
                    plot.setData(data);

                    Parent plotWindow = new PlotWindow(
                            plot, plotType.newPropertiesPanel(plot));

                    Scene scene = new Scene(plotWindow, 1200, 800);

                    Stage plotStage = new Stage();
                    plotStage.setScene(scene);
                    plotStage.show();
                });
    }
}
