package org.cirdles.topsoil.app.control.plot.panel;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.cirdles.topsoil.app.control.FXMLUtils;

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
    //                  ATTRIBUTES                  //
    //**********************************************//

    private PlotPropertiesPanel panel;

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

	//**********************************************//
	//                PUBLIC METHODS                //
	//**********************************************//

	public void setPropertiesPanel(PlotPropertiesPanel panel) {
    	this.panel = panel;
	}

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    @FXML private void setXExtentsButtonAction() {
    	String xMin = testDoubleString(xMinTextField.getText());
    	String xMax = testDoubleString(xMaxTextField.getText());

		panel.setAxes(xMin, xMax, "", "");
    }

    @FXML private void setYExtentsButtonAction() {
	    String yMin = testDoubleString(yMinTextField.getText());
	    String yMax = testDoubleString(yMaxTextField.getText());

		panel.setAxes("", "", yMin, yMax);
    }

    private String testDoubleString(String s) {
    	try {
    		Double.parseDouble(s);
    		return s;
	    } catch (NumberFormatException e) {
    		return "";
	    }
    }
}
