package org.cirdles.topsoil.app.control.plot.panel;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.control.plot.PlotStage;
import org.cirdles.topsoil.app.file.FileChoosers;
import org.cirdles.topsoil.plot.PlotOption;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import static org.cirdles.topsoil.app.control.plot.panel.PlotOptionsPanel.fireEventOnChanged;

public class ExportPreferencesController extends AnchorPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "property-preferences.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML
    Button exportButton;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//
    Runnable function;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public ExportPreferencesController() {
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, ExportPreferencesController.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    @FXML
    private void exportPrefs() {
        // refer to PlotOptionsPanel.java
        if (function != null) {
            function.run();
        }
    }

    @FXML
    private void readPrefs(ActionEvent event) {
        String fileName;
        // PLEASE NOTE (Window) StageHelper.getStages().get(1) is not a rigorous solution and assumes that there is only one plot window open
        // TODO: make it so that the FileChooser specifically blocks the associated Plot window
        Path path = Paths.get(FileChoosers.topsoilPlotPreferenceFileChooser().showOpenDialog((Window) StageHelper.getStages().get(1)).toURI());
        fileName = path.getFileName().toString();
        Event.fireEvent(event.getTarget(), new StyleImportEvent(fileName));
    }
}
