package org.cirdles.topsoil.app.control.plot.panel;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.MapChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Lambda;
import org.cirdles.topsoil.app.file.FileChoosers;
import org.cirdles.topsoil.app.file.RecentFiles;
import org.cirdles.topsoil.app.file.serialization.PlotStyleSerializer;
import org.cirdles.topsoil.app.file.serialization.TopsoilFileSerializer;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.plot.PlotFunction;
import org.cirdles.topsoil.plot.PlotOption;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.feature.Concordia;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

import static org.cirdles.topsoil.app.control.plot.panel.OptionChangeEvent.OPTION_CHANGED;
import static org.cirdles.topsoil.app.control.plot.panel.StyleImportEvent.STYLE_IMPORT;
import static org.cirdles.topsoil.plot.PlotOption.*;

public class PlotOptionsPanel extends Accordion {

    private static final String CONTROLLER_FXML = "plot-properties-panel.fxml";

	private PlotView plot;
	private MapProperty<PlotOption<?>, Object> plotOptions;

	private final Map<PlotOption<?>, Consumer<Object>> updateActions = new HashMap<>();
	private static final List<PlotOption<?>> EXCLUDE_FROM_STYLE = Arrays.asList(TITLE, X_MIN, X_MAX, X_AXIS, Y_MIN, Y_MAX, Y_AXIS);

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private AxisStylingController axisStyling;
    @FXML private DataOptionsController dataOptions;
    @FXML private PlotFeaturesController plotFeatures;
    @FXML private PhysicalConstantsController physicalConstants;
	@FXML private ExportPreferencesController exportPreferences;

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

    public PlotOptionsPanel(PlotView plot) {
        this.plot = plot;
        this.plotOptions = new SimpleMapProperty<>(plot.getOptions());
		try {
			FXMLUtils.loadController(CONTROLLER_FXML, PlotOptionsPanel.class, this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    @FXML protected void initialize() {
    	setUpdateActions();
    	for (Map.Entry<PlotOption<?>, Object> entry : plot.getOptions().entrySet()) {
    		updateControl(entry.getKey(), entry.getValue());
		}
    	plot.plotOptionsProperty().addListener((MapChangeListener<PlotOption, Object>) c -> {
    		if (c.wasAdded()) {
    			updateControl(c.getKey(), c.getValueAdded());
			} else if (c.wasRemoved()) {
    			updateControl(c.getKey(), null);
			}
		});

    	title.bind(axisStyling.plotTitleTextField.textProperty());

    	// Make sure isotope system is uniform
    	isotopeSystem.bindBidirectional(dataOptions.isotopeSystemComboBox.valueProperty());	// bind isotopeSystem to control
		plotFeatures.isotopeSystemProperty().bind(isotopeSystem);

		// Handle property change events
		EventHandler<OptionChangeEvent> changeEventHandler = event -> {
			plot.getOptions().put(event.getOption(), event.getNewValue());
		};
		axisStyling.addEventFilter(OPTION_CHANGED, changeEventHandler);
		dataOptions.addEventFilter(OPTION_CHANGED, changeEventHandler);
		plotFeatures.addEventFilter(OPTION_CHANGED, changeEventHandler);
		physicalConstants.addEventFilter(OPTION_CHANGED, changeEventHandler);
		// Listens for a filename selected from a style import
		exportPreferences.addEventFilter(STYLE_IMPORT, event -> {
			String fileName = event.getFileName();
			HashMap<String, Object> something = (HashMap<String, Object>) PlotStyleSerializer.importPlotStyle(fileName,true);
			//HashMap<PlotOption<?>, Object> map = new HashMap<>();
			for (Map.Entry<String, Object> entry : something.entrySet()) {
				PlotOption<?> option = PlotOption.forKey(entry.getKey());
				//TODO: Handle possilbe null pointer exception
				plot.getOptions().put(option, option.getType().cast(entry.getValue()));
			}

			//plot.setOptions(map);
		});

		// Update axes when buttons pressed
		axisStyling.setXExtentsButton.setOnAction(event -> {
			plot.call(PlotFunction.Scatter.SET_AXIS_EXTENTS,
					axisStyling.xAxisMin.getValue(), axisStyling.xAxisMax.getValue(), plotOptions.get(Y_MIN), plotOptions.get(Y_MAX), true
			);
		});
		axisStyling.setYExtentsButton.setOnAction(event -> {
			plot.call(PlotFunction.Scatter.SET_AXIS_EXTENTS,
					plotOptions.get(X_MIN), plotOptions.get(X_MAX), axisStyling.yAxisMin.getValue(), axisStyling.yAxisMax.getValue(), true
			);
		});

		// Snap to Corners button action
		plotFeatures.snapToCornersButton.setOnAction(event -> plot.call(PlotFunction.Scatter.SNAP_TO_CORNERS));

		// Export Prefs button
		exportPreferences.function = () -> {
			HashMap<String, Serializable> map = new HashMap<>();
			for (Map.Entry<PlotOption<?>, Object> entry : plotOptions.entrySet()) {
				if (EXCLUDE_FROM_STYLE.contains(entry.getKey())) continue;
				map.put(entry.getKey().getTitle(), (Serializable) entry.getValue());
			}
			try {
				String fileName;
				FileChooser chooser = FileChoosers.saveTopsoilPlotPreferenceFile();
				chooser.setInitialDirectory(RecentFiles.findMRUPlotStyleFolder().toFile());
				Path path = Paths.get(chooser.showSaveDialog((Window) StageHelper.getStages().get(1)).toURI());
				RecentFiles.addPlotStylePath(path);
				fileName = path.toString();
				PlotStyleSerializer.exportPlotStyle(map, fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
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

	static <T> void fireEventOnChanged(Property<T> property, EventTarget target, PlotOption<T> plotOption) {
		property.addListener(((observable, oldValue, newValue) -> {
			Event.fireEvent(target, new OptionChangeEvent<>(plotOption, oldValue, newValue));
		}));
	}

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

	private void setUpdateActions() {
		updateActions.put(TITLE, (value) -> {
			axisStyling.plotTitleTextField.setText(String.valueOf(value));
		});
		updateActions.put(X_AXIS, (value) -> axisStyling.xTitleTextField.setText(String.valueOf(value)));
		updateActions.put(Y_AXIS, (value) -> axisStyling.yTitleTextField.setText(String.valueOf(value)));

		updateActions.put(X_MIN, (value) -> {
			if (liveAxisUpdateActive()) {
				axisStyling.xMinTextField.setText(Double.toString((double) value));
			}
		});
		updateActions.put(Y_MIN, (value) -> {
			if (liveAxisUpdateActive()){
				axisStyling.yMinTextField.setText(Double.toString((double) value));
			}
		});
		updateActions.put(X_MAX, (value) -> {
			if (liveAxisUpdateActive()) {
				axisStyling.xMaxTextField.setText(Double.toString((double) value));
			}
		});
		updateActions.put(Y_MAX, (value) -> {
			if (liveAxisUpdateActive()) {
				axisStyling.yMaxTextField.setText(Double.toString((double) value));
			}
		});

		updateActions.put(ISOTOPE_SYSTEM, (value) -> dataOptions.isotopeSystemComboBox.getSelectionModel().select((IsotopeSystem) value));
		updateActions.put(UNCERTAINTY, (value) -> dataOptions.uncertaintyComboBox.getSelectionModel().select((Uncertainty) value));

		updateActions.put(POINTS, (value) -> dataOptions.pointsCheckBox.setSelected((Boolean) value));
		updateActions.put(POINTS_FILL, (value) -> dataOptions.pointsFillColorPicker.setValue(
				getJavaColor(String.valueOf(value), (Double) plotOptions.get(POINTS_OPACITY))
		));
		updateActions.put(POINTS_OPACITY, (value) -> dataOptions.pointsFillColorPicker.setValue(
				getJavaColor(String.valueOf(plotOptions.get(POINTS_FILL)), (Double) value)
		));

		updateActions.put(ELLIPSES, (value) -> dataOptions.ellipsesRadioButton.setSelected((Boolean) value));
		updateActions.put(ELLIPSES_FILL, (value) -> dataOptions.ellipsesFillColorPicker.setValue(
				getJavaColor(String.valueOf(value), (Double) plotOptions.get(ELLIPSES_OPACITY))
		));
		updateActions.put(ELLIPSES_OPACITY, (value) -> dataOptions.ellipsesFillColorPicker.setValue(
				getJavaColor(String.valueOf(plotOptions.get(ELLIPSES_FILL)), (Double) value)
		));

		updateActions.put(UNCTBARS, (value) -> dataOptions.unctBarsRadioButton.setSelected((Boolean) value));
		updateActions.put(UNCTBARS_FILL, (value) -> dataOptions.unctBarsFillColorPicker.setValue(
				getJavaColor(String.valueOf(value), (Double) plotOptions.get(UNCTBARS_OPACITY))
		));
		updateActions.put(UNCTBARS_OPACITY, (value) -> dataOptions.unctBarsFillColorPicker.setValue(
				getJavaColor(String.valueOf(plotOptions.get(UNCTBARS_FILL)), (Double) value)
		));

		updateActions.put(MCLEAN_REGRESSION, (value) -> plotFeatures.mcLeanRegressionCheckBox.setSelected((Boolean) value));
		updateActions.put(MCLEAN_REGRESSION_ENVELOPE, (value) -> plotFeatures.mcLeanEnvelopeCheckBox.setSelected((Boolean) value));

		updateActions.put(CONCORDIA_TYPE, (value) -> plotFeatures.setConcordiaType((Concordia) value));
		updateActions.put(CONCORDIA_LINE, (value) -> plotFeatures.concordiaLineCheckBox.setSelected((Boolean) value));
		updateActions.put(CONCORDIA_LINE_FILL, (value) -> plotFeatures.concordiaLineColorPicker.setValue(
				getJavaColor(String.valueOf(value), (Double) plotOptions.get(CONCORDIA_LINE_OPACITY))
		));
		updateActions.put(CONCORDIA_LINE_OPACITY, (value) -> plotFeatures.concordiaLineColorPicker.setValue(
				getJavaColor(String.valueOf(plotOptions.get(CONCORDIA_LINE_FILL)), (Double) value)
		));
		updateActions.put(CONCORDIA_ENVELOPE, (value) -> plotFeatures.concordiaEnvelopeCheckBox.setSelected((Boolean) value));
		updateActions.put(CONCORDIA_ENVELOPE_FILL, (value) -> plotFeatures.concordiaEnvelopeColorPicker.setValue(
				getJavaColor(String.valueOf(value), (Double) plotOptions.get(CONCORDIA_ENVELOPE_OPACITY))
		));
		updateActions.put(CONCORDIA_ENVELOPE_OPACITY, (value) -> plotFeatures.concordiaEnvelopeColorPicker.setValue(
				getJavaColor(String.valueOf(plotOptions.get(CONCORDIA_ENVELOPE_FILL)), (Double) value)
		));

		updateActions.put(EVOLUTION, (value) -> plotFeatures.evolutionCheckBox.setSelected((Boolean) value));

		updateActions.put(LAMBDA_TH230, (value) -> physicalConstants.setLambda(Lambda.Th230, (double) value));
		updateActions.put(LAMBDA_U234, (value) -> physicalConstants.setLambda(Lambda.U234, (double) value));
		updateActions.put(LAMBDA_U235, (value) -> physicalConstants.setLambda(Lambda.U235, (double) value));
		updateActions.put(LAMBDA_U238, (value) -> physicalConstants.setLambda(Lambda.U238, (double) value));
	}

	private void updateControl(PlotOption<?> option, Object value) {
		if (updateActions.containsKey(option)) {
			updateActions.get(option).accept(value);
		}
	}

	private static Color getJavaColor(String string, Number opacity) {
		if (string != null && opacity != null) {
			Color c = Color.valueOf(string);
			return new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity.doubleValue());
		}
		return null;
	}
}