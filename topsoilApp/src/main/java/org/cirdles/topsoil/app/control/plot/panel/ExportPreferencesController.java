package org.cirdles.topsoil.app.control.plot.panel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.plot.PlotOption;

import java.io.IOException;
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
        if (function != null) {
            function.run();
        }
    }
}
