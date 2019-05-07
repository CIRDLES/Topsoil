package org.cirdles.topsoil.app.control.plot.panel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.paint.Color;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.plot.PlotProperties;
import org.cirdles.topsoil.plot.Plot;

import java.io.IOException;

import static org.cirdles.topsoil.app.control.plot.panel.PropertyChangeEvent.PROPERTY_CHANGED;
import static org.cirdles.topsoil.plot.PlotProperties.*;

public class PlotPropertiesPanel extends Accordion {

    private static final String CONTROLLER_FXML = "plot-properties-panel.fxml";

	private Plot plot;
	private PlotProperties properties;

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private AxisStylingController axisStyling;
    @FXML private DataOptionsController dataOptions;
    @FXML private PlotFeaturesController plotFeatures;

    private final StringProperty title = new SimpleStringProperty();
    public StringProperty titleProperty() {
    	return title;
	}

    private final ObjectProperty<IsotopeSystem> isotopeSystem = new SimpleObjectProperty<>();
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
    	return isotopeSystem;
	}

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public PlotPropertiesPanel(Plot plot) {
        this.plot = plot;
        this.properties = new PlotProperties(plot.getProperties());
		try {
			FXMLUtils.loadController(CONTROLLER_FXML, PlotPropertiesPanel.class, this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    @FXML protected void initialize() {
    	setPlotProperties(properties);

    	title.bind(axisStyling.plotTitleTextField.textProperty());

    	// Make sure isotope system is uniform
    	isotopeSystem.bindBidirectional(dataOptions.isotopeSystemComboBox.valueProperty());	// bind isotopeSystem to control
		plotFeatures.isotopeSystemProperty().bind(isotopeSystem);

		// Handle property change events
		EventHandler<PropertyChangeEvent> changeEventHandler = event -> {
			properties.set(event.getProperty(), event.getNewValue());
			refreshPlot();
		};
		axisStyling.addEventFilter(PROPERTY_CHANGED, changeEventHandler);
		dataOptions.addEventFilter(PROPERTY_CHANGED, changeEventHandler);
		plotFeatures.addEventFilter(PROPERTY_CHANGED, changeEventHandler);

		// Update axes when buttons pressed
		axisStyling.setXExtentsButton.setOnAction(event -> {
			plot.setAxes(axisStyling.xAxisMin.getValue(), axisStyling.xAxisMax.getValue(), null, null);
		});
		axisStyling.setYExtentsButton.setOnAction(event -> {
			plot.setAxes(null, null, axisStyling.yAxisMin.getValue(), axisStyling.yAxisMax.getValue());
		});

		// Snap to Corners button action
		plotFeatures.snapToCornersButton.setOnAction(event -> plot.snapToCorners());
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

	public boolean liveAxisUpdateActive() {
		return axisStyling.axisLiveUpdateCheckBox.isSelected();
	}

	public void updateXMin(String s) {
    	axisStyling.xMinTextField.setText(s);
	}

	public void updateXMax(String s) {
    	axisStyling.xMaxTextField.setText(s);
	}

	public void updateYMin(String s) {
    	axisStyling.yMinTextField.setText(s);
	}

	public void updateYMax(String s) {
    	axisStyling.yMaxTextField.setText(s);
	}

	/**
	 * Converts a Java {@code Color} into a {@code String} format that can be read by D3.js.
	 * <p>
	 * This is done by dropping the last two chars (which represent opacity), and replacing '0x' with '#'. For example,
	 * 0x123456ff would be converted to #123456.
	 *
	 * @param c a Java Color
	 * @return  a String color with format #000000
	 */
	static String convertColor(Color c) {
		String s = c.toString();
		return s.substring(0, s.length() - 2).replaceAll("0x", "#");
	}

	/**
	 * Converts a Java {@code Color} into a {@code Double} format representing the alpha value of the color.
	 * <p>
	 * This is done by converting the last two chars (which represent opacity) into decimal, and dividing the result
	 * by 255. For example, 0x123456ff would be converted to 1.0.
	 *
	 * @param c a Java Color
	 * @return  a String color with format #000000
	 */
	static Double convertOpacity(Color c) {
		String s = c.toString();
		return ((double) Integer.parseInt(s.substring(s.length() - 2).trim(), 16)) / 255;
	}

	static void fireEventOnChanged(Property<?> valueProperty, EventTarget target, PlotProperties.Property<?> plotProperty) {
		valueProperty.addListener(((observable, oldValue, newValue) -> {
			Event.fireEvent(target, new PropertyChangeEvent(plotProperty, oldValue, newValue));
		}));
	}

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

	private void refreshPlot() {
		plot.setProperties(properties);
	}

	private void setPlotProperties(PlotProperties properties) {
		// TITLE
		axisStyling.plotTitleTextField.setText(properties.get(TITLE));

		// X_AXIS
		axisStyling.xTitleTextField.setText(properties.get(X_AXIS));

		// Y_AXIS
		axisStyling.yTitleTextField.setText(properties.get(Y_AXIS));

		// ISOTOPE_SYSTEM
		dataOptions.isotopeSystemComboBox.getSelectionModel().select(properties.get(ISOTOPE_SYSTEM));

		// UNCERTAINTY
		dataOptions.uncertaintyComboBox.getSelectionModel().select(properties.get(UNCERTAINTY));

		// POINTS
		dataOptions.pointsCheckBox.setSelected(properties.get(POINTS));
		dataOptions.pointsFillColorPicker.setValue(getJavaColor(properties.get(POINTS_FILL), properties.get(POINTS_OPACITY)));

		// ELLIPSES
		dataOptions.ellipsesRadioButton.setSelected(properties.get(ELLIPSES));
		dataOptions.ellipsesFillColorPicker.setValue(getJavaColor(properties.get(ELLIPSES_FILL), properties.get(ELLIPSES_OPACITY)));

		// UNCTBARS
		dataOptions.unctBarsRadioButton.setSelected(properties.get(UNCTBARS));
		dataOptions.unctBarsFillColorPicker.setValue(getJavaColor(properties.get(UNCTBARS_FILL), properties.get(UNCTBARS_OPACITY)));

		// MCLEAN_REGRESSION
		plotFeatures.mcLeanRegressionCheckBox.setSelected(properties.get(MCLEAN_REGRESSION));
		plotFeatures.mcLeanEnvelopeCheckBox.setSelected(properties.get(MCLEAN_REGRESSION_ENVELOPE));

		// Concordia
		plotFeatures.setConcordiaType(properties.get(CONCORDIA_TYPE));
		plotFeatures.concordiaLineCheckBox.setSelected(properties.get(CONCORDIA_LINE));
		plotFeatures.concordiaLineColorPicker.setValue(getJavaColor(properties.get(CONCORDIA_LINE_FILL), properties.get(CONCORDIA_LINE_OPACITY)));
		plotFeatures.concordiaEnvelopeCheckBox.setSelected(properties.get(CONCORDIA_ENVELOPE));
		plotFeatures.concordiaEnvelopeColorPicker.setValue(getJavaColor(properties.get(CONCORDIA_ENVELOPE_FILL), properties.get(CONCORDIA_ENVELOPE_OPACITY)));
	}

	private static Color getJavaColor(String string, Number opacity) {
		if (string != null && opacity != null) {
			Color c = Color.valueOf(string);
			return new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity.doubleValue());
		}
		return null;
	}
}