package org.cirdles.topsoil.app.control.plot.panel;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.uncertainty.Uncertainty;

import java.io.IOException;

public class DataOptionsController extends AnchorPane {

	//**********************************************//
	//                  CONSTANTS                   //
	//**********************************************//

    private static final String CONTROLLER_FXML = "data-options-menu.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

	@FXML ComboBox<IsotopeSystem> isotopeSystemComboBox;
	@FXML ComboBox<Uncertainty> uncertaintyFormatComboBox;

    @FXML CheckBox pointsCheckBox;
    @FXML ColorPicker pointsFillColorPicker;

    @FXML RadioButton ellipsesRadioButton;
    @FXML ColorPicker ellipsesFillColorPicker;

    @FXML RadioButton unctBarsRadioButton;
    @FXML ColorPicker unctBarsFillColorPicker;

	private ToggleGroup uncertaintyToggleGroup = new ToggleGroup();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataOptionsController() {
		try {
			FXMLUtils.loadController(CONTROLLER_FXML, DataOptionsController.class, this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    @FXML protected void initialize() {
    	isotopeSystemComboBox.getItems().addAll(IsotopeSystem.values());
    	uncertaintyFormatComboBox.getItems().addAll(Uncertainty.ONE_SIGMA_ABSOLUTE, Uncertainty
			    .TWO_SIGMA_ABSOLUTE, Uncertainty.NINETY_FIVE_PERCENT_CONFIDENCE);

    	RadioButtonSelectionHandler ellipsesSelectionHandler = new RadioButtonSelectionHandler(ellipsesRadioButton);
        ellipsesRadioButton.setToggleGroup(uncertaintyToggleGroup);
        ellipsesRadioButton.setOnMousePressed(ellipsesSelectionHandler.getOnMousePressed());
        ellipsesRadioButton.setOnMouseReleased(ellipsesSelectionHandler.getOnMouseReleased());

        RadioButtonSelectionHandler unctBarsSelectionHandler = new RadioButtonSelectionHandler(unctBarsRadioButton);
        unctBarsRadioButton.setToggleGroup(uncertaintyToggleGroup);
        unctBarsRadioButton.setOnMousePressed(unctBarsSelectionHandler.getOnMousePressed());
        unctBarsRadioButton.setOnMouseReleased(unctBarsSelectionHandler.getOnMouseReleased());
    }

	//**********************************************//
	//                INNER CLASSES                 //
	//**********************************************//

	private static class RadioButtonSelectionHandler {

    	private RadioButton button;
    	private boolean selected;

    	private EventHandler<MouseEvent> onMousePressed = (event) -> {
    		if (button.isSelected()) {
    			selected = true;
		    }
	    };
		private EventHandler<MouseEvent> onMouseReleased = (event) -> {
			if (selected) {
				button.setSelected(false);
			}
			selected = false;
		};

    	RadioButtonSelectionHandler(RadioButton button) {
    		this.button = button;
	    }

		EventHandler<MouseEvent> getOnMousePressed() {
    		return onMousePressed;
		}

		EventHandler<MouseEvent> getOnMouseReleased() {
    		return onMouseReleased;
		}

	}

}