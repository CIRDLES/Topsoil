package org.cirdles.topsoil.app.control.plot.panel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.plot.PlotProperties;

import static org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel.fireEventOnChanged;

import java.io.IOException;

public class AxisStylingController extends AnchorPane {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String CONTROLLER_FXML = "axis-styling-menu.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML TextField plotTitleTextField;

    // X Axis
    @FXML TextField xTitleTextField, xMinTextField, xMaxTextField;
    @FXML Button setXExtentsButton;

    // Y Axis
    @FXML TextField yTitleTextField, yMinTextField, yMaxTextField;
    @FXML Button setYExtentsButton;

    @FXML CheckBox axisLiveUpdateCheckBox;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    final DoubleProperty xAxisMin = new SimpleDoubleProperty(0.0);
    final DoubleProperty xAxisMax = new SimpleDoubleProperty(1.0);

    final DoubleProperty yAxisMin = new SimpleDoubleProperty(0.0);
    final DoubleProperty yAxisMax = new SimpleDoubleProperty(1.0);

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public AxisStylingController() {
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, AxisStylingController.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        // Configure properties that need to have values converted
        xAxisMin.bind(Bindings.createDoubleBinding(() -> getDouble(xMinTextField.getText()), xMinTextField.textProperty()));
        xAxisMax.bind(Bindings.createDoubleBinding(() -> getDouble(xMaxTextField.getText()), xMaxTextField.textProperty()));
        yAxisMin.bind(Bindings.createDoubleBinding(() -> getDouble(yMinTextField.getText()), yMinTextField.textProperty()));
        yAxisMax.bind(Bindings.createDoubleBinding(() -> getDouble(yMaxTextField.getText()), yMaxTextField.textProperty()));

        // Fire property changed events
        fireEventOnChanged(plotTitleTextField.textProperty(), plotTitleTextField, PlotProperties.TITLE);
        fireEventOnChanged(xTitleTextField.textProperty(), xTitleTextField, PlotProperties.X_AXIS);
        fireEventOnChanged(yTitleTextField.textProperty(), yTitleTextField, PlotProperties.Y_AXIS);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private double getDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
