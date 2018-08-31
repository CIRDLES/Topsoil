package org.cirdles.topsoil.app.plot.panel;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;

import java.io.IOException;

public class DataOptionsController extends AnchorPane {

    private static final String CONTROLLER_FXML = "data-options-menu.fxml";

    private ToggleGroup uncertaintyToggleGroup = new ToggleGroup();

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

	@FXML ComboBox<IsotopeType> isotopeSystemComboBox;
	@FXML ComboBox<UncertaintyFormat> uncertaintyFormatComboBox;

    @FXML CheckBox pointsCheckBox;
    @FXML ColorPicker pointsFillColorPicker;

    @FXML RadioButton ellipsesRadioButton;
    @FXML ColorPicker ellipsesFillColorPicker;

    @FXML RadioButton unctBarsRadioButton;
    @FXML ColorPicker unctBarsFillColorPicker;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataOptionsController() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    new ResourceExtractor(DataOptionsController.class).extractResourceAsPath(CONTROLLER_FXML).toUri().toURL()
            );
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML protected void initialize() {
    	isotopeSystemComboBox.getItems().addAll(IsotopeType.values());
    	uncertaintyFormatComboBox.getItems().addAll(UncertaintyFormat.ONE_SIGMA_ABSOLUTE, UncertaintyFormat
			    .TWO_SIGMA_ABSOLUTE, UncertaintyFormat.NINETY_FIVE_PERCENT_CONFIDENCE);

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

	private class RadioButtonSelectionHandler {

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