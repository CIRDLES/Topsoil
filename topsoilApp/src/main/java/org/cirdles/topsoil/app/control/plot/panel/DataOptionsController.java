package org.cirdles.topsoil.app.control.plot.panel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Uncertainty;
import org.cirdles.topsoil.plot.PlotProperties;

import java.io.IOException;

import static org.cirdles.topsoil.app.control.plot.panel.PlotPropertiesPanel.fireEventOnChanged;

public class DataOptionsController extends AnchorPane {

	//**********************************************//
	//                  CONSTANTS                   //
	//**********************************************//

    private static final String CONTROLLER_FXML = "data-options-menu.fxml";

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

	@FXML ComboBox<IsotopeSystem> isotopeSystemComboBox;
	@FXML ComboBox<Uncertainty> uncertaintyComboBox;

    @FXML CheckBox pointsCheckBox;
    @FXML ColorPicker pointsFillColorPicker;

    @FXML RadioButton ellipsesRadioButton;
    @FXML ColorPicker ellipsesFillColorPicker;

    @FXML RadioButton unctBarsRadioButton;
    @FXML ColorPicker unctBarsFillColorPicker;

	private ToggleGroup uncertaintyToggleGroup = new ToggleGroup();

	//**********************************************//
	//                  PROPERTIES                  //
	//**********************************************//

	private StringProperty pointsFillValue = new SimpleStringProperty();
	private DoubleProperty pointsOpacityValue = new SimpleDoubleProperty(1.0);

	private StringProperty ellipsesFillValue = new SimpleStringProperty();
	private DoubleProperty ellipsesOpacityValue = new SimpleDoubleProperty(1.0);

	private StringProperty unctBarsFillValue = new SimpleStringProperty();
	private DoubleProperty unctBarsOpacityValue = new SimpleDoubleProperty(1.0);

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
    	// Populate IsotopeSystem ComboBox
    	isotopeSystemComboBox.getItems().addAll(IsotopeSystem.values());
    	isotopeSystemComboBox.getSelectionModel().select(IsotopeSystem.GENERIC);

    	// Populate Uncertainty ComboBox
		uncertaintyComboBox.getItems().addAll(Uncertainty.ONE_SIGMA_ABSOLUTE, Uncertainty
				.TWO_SIGMA_ABSOLUTE, Uncertainty.NINETY_FIVE_PERCENT_CONFIDENCE);
    	uncertaintyComboBox.getSelectionModel().select(Uncertainty.ONE_SIGMA_ABSOLUTE);

    	// Configure ellipses/unctbars RadioButtons
    	RadioButtonSelectionHandler ellipsesSelectionHandler = new RadioButtonSelectionHandler(ellipsesRadioButton);
        ellipsesRadioButton.setToggleGroup(uncertaintyToggleGroup);
        ellipsesRadioButton.setOnMousePressed(ellipsesSelectionHandler.getOnMousePressed());
        ellipsesRadioButton.setOnMouseReleased(ellipsesSelectionHandler.getOnMouseReleased());
        RadioButtonSelectionHandler unctBarsSelectionHandler = new RadioButtonSelectionHandler(unctBarsRadioButton);
        unctBarsRadioButton.setToggleGroup(uncertaintyToggleGroup);
        unctBarsRadioButton.setOnMousePressed(unctBarsSelectionHandler.getOnMousePressed());
        unctBarsRadioButton.setOnMouseReleased(unctBarsSelectionHandler.getOnMouseReleased());

        // Configure properties that need to have values converted
		pointsFillValue.bind(Bindings.createStringBinding(() -> PlotPropertiesPanel.convertColor(pointsFillColorPicker.getValue()),
				pointsFillColorPicker.valueProperty()));
		pointsOpacityValue.bind(Bindings.createDoubleBinding(() -> PlotPropertiesPanel.convertOpacity(pointsFillColorPicker.getValue()),
				pointsFillColorPicker.valueProperty()));
		ellipsesFillValue.bind(Bindings.createStringBinding(() -> PlotPropertiesPanel.convertColor(ellipsesFillColorPicker.getValue()),
				ellipsesFillColorPicker.valueProperty()));
		ellipsesOpacityValue.bind(Bindings.createDoubleBinding(() -> PlotPropertiesPanel.convertOpacity(ellipsesFillColorPicker.getValue()),
				ellipsesFillColorPicker.valueProperty()));
		unctBarsFillValue.bind(Bindings.createStringBinding(() ->PlotPropertiesPanel. convertColor(unctBarsFillColorPicker.getValue()),
				unctBarsFillColorPicker.valueProperty()));
		unctBarsOpacityValue.bind(Bindings.createDoubleBinding(() -> PlotPropertiesPanel.convertOpacity(unctBarsFillColorPicker.getValue()),
				unctBarsFillColorPicker.valueProperty()));

        // Fire property changed events
		fireEventOnChanged(isotopeSystemComboBox.valueProperty(), isotopeSystemComboBox, PlotProperties.ISOTOPE_SYSTEM);
		fireEventOnChanged(uncertaintyComboBox.valueProperty(), uncertaintyComboBox, PlotProperties.UNCERTAINTY);

        fireEventOnChanged(pointsCheckBox.selectedProperty(), pointsCheckBox, PlotProperties.POINTS);
        fireEventOnChanged(ellipsesRadioButton.selectedProperty(), ellipsesRadioButton, PlotProperties.ELLIPSES);
        fireEventOnChanged(unctBarsRadioButton.selectedProperty(), unctBarsRadioButton, PlotProperties.UNCTBARS);

        fireEventOnChanged(pointsFillValue, pointsFillColorPicker, PlotProperties.POINTS_FILL);
        fireEventOnChanged(pointsOpacityValue, pointsFillColorPicker, PlotProperties.POINTS_OPACITY);
		fireEventOnChanged(ellipsesFillValue, ellipsesFillColorPicker, PlotProperties.ELLIPSES_FILL);
		fireEventOnChanged(ellipsesOpacityValue, ellipsesFillColorPicker, PlotProperties.ELLIPSES_OPACITY);
		fireEventOnChanged(unctBarsFillValue, unctBarsFillColorPicker, PlotProperties.UNCTBARS_FILL);
		fireEventOnChanged(unctBarsOpacityValue, unctBarsFillColorPicker, PlotProperties.UNCTBARS_OPACITY);

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