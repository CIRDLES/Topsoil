package org.cirdles.topsoil.app.control.plot.panel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.paint.Color;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotProperty;
import org.cirdles.topsoil.plot.DefaultProperties;

import java.io.IOException;
import java.util.Map;

import static org.cirdles.topsoil.plot.PlotProperty.*;

public class PlotPropertiesPanel extends Accordion {

    private static final String CONTROLLER_FXML = "plot-properties-panel.fxml";

	private Plot plot;
	private Map<PlotProperty, Object> properties;

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private AxisStylingController axisStyling;
    @FXML private DataOptionsController dataOptions;
    @FXML private PlotFeaturesController plotFeatures;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    /*
        Start Axis Styling properties...
     */

    private StringProperty plotTitle;
    public StringProperty plotTitleProperty() {
        if (plotTitle == null) {
            plotTitle = new SimpleStringProperty();
            plotTitle.bindBidirectional(axisStyling.plotTitleTextField.textProperty());
        }
        return plotTitle;
    }
    public final String getPlotTitle() {
        return plotTitleProperty().get();
    }
    public final void setPlotTitle(String s) {
        plotTitleProperty().set(s);
    }

	private StringProperty xAxisTitle;
	public StringProperty xAxisTitleProperty() {
		if ( xAxisTitle == null) {
			xAxisTitle = new SimpleStringProperty();
			xAxisTitle.bindBidirectional(axisStyling.xTitleTextField.textProperty());
		}
		return xAxisTitle;
	}
	public final String getXAxisTitle() {
		return xAxisTitleProperty().get();
	}
	public final void setXAxisTitle( String s ) {
		xAxisTitleProperty().set(s);
	}

	private StringProperty yAxisTitle;
	public StringProperty yAxisTitleProperty() {
		if ( yAxisTitle == null) {
			yAxisTitle = new SimpleStringProperty();
			yAxisTitle.bindBidirectional(axisStyling.yTitleTextField.textProperty());
		}
		return yAxisTitle;
	}
	public final String getYAxisTitle() {
		return yAxisTitleProperty().get();
	}
	public final void setYAxisTitle(String s) {
		yAxisTitleProperty().set(s);
	}

    /*
        Start Data Options properties...
     */

    private ObjectProperty<IsotopeSystem> isotopeSystem;
    public ObjectProperty<IsotopeSystem> isotopeSystemProperty() {
    	if (isotopeSystem == null) {
    		isotopeSystem = new SimpleObjectProperty<>();
    		isotopeSystem.bindBidirectional(dataOptions.isotopeSystemComboBox.valueProperty());
	    }
	    return isotopeSystem;
    }
    public final IsotopeSystem getIsotopeSystem() {
    	return isotopeSystemProperty().get();
    }
    public final void setIsotopeSystem(IsotopeSystem system ) {
    	isotopeSystemProperty().set(system);
    }

    private ObjectProperty<Uncertainty> unctFormat;
    public ObjectProperty<Uncertainty> uncertaintyFormatProperty() {
    	if (unctFormat == null) {
    		unctFormat = new SimpleObjectProperty<>();
    		unctFormat.bindBidirectional(dataOptions.uncertaintyFormatComboBox.valueProperty());
	    }
	    return unctFormat;
    }
    public final Uncertainty getUncertaintyFormat() {
    	return uncertaintyFormatProperty().get();
    }
    public final void setUncertaintyFormat(Uncertainty format) {
    	uncertaintyFormatProperty().set(format);
    }

    private BooleanProperty points;
    public BooleanProperty pointsProperty() {
        if ( points == null) {
	        points = new SimpleBooleanProperty();
            points.bindBidirectional(dataOptions.pointsCheckBox.selectedProperty());
        }
        return points;
    }
    public final Boolean getPoints() {
        return pointsProperty().get();
    }
    public final void setPoints(Boolean b) {
        pointsProperty().set(b);
    }

    private ObjectProperty<Color> pointsFill;
    public ObjectProperty<Color> pointsFillProperty() {
        if ( pointsFill == null) {
	        pointsFill = new SimpleObjectProperty<>();
            pointsFill.bindBidirectional(dataOptions.pointsFillColorPicker.valueProperty());
        }
        return pointsFill;
    }
    public final Color getPointsFill() {
        return pointsFillProperty().get();
    }
    public final void setPointsFill(Color c) {
        pointsFillProperty().set(c);
    }

    private DoubleProperty pointsOpacity;
    public DoubleProperty pointsOpacityProperty() {
    	if (pointsOpacity == null) {
    		pointsOpacity = new SimpleDoubleProperty();
    		pointsOpacity.bind(Bindings.createDoubleBinding(() -> {
			    String s = dataOptions.pointsFillColorPicker.getValue().toString();
			    return ((double) Integer.parseInt(s.substring(s.length() - 2).trim(), 16)) / 255;
		    }, dataOptions.pointsFillColorPicker.valueProperty()));
	    }
	    return pointsOpacity;
    }
    public final Double getPointsOpacity() {
    	return pointsOpacityProperty().get();
    }
    public final void setPointsOpacity(double d) {
    	if ((d >= 0.0) && (d <= 1.0)) {
    		String s = getPointsFill().toString();
    		setPointsFill(Color.valueOf(s.substring(0, s.length() - 2) + Integer.toHexString(((int) d) * 255)));
	    }
    }

    private BooleanProperty ellipses;
    public BooleanProperty ellipsesProperty() {
        if (ellipses == null) {
	        ellipses = new SimpleBooleanProperty();
            ellipses.bindBidirectional(dataOptions.ellipsesRadioButton.selectedProperty());
        }
        return ellipses;
    }
    public final Boolean getEllipses() {
        return ellipsesProperty().get();
    }
    public final void setEllipses(Boolean b) {
        ellipsesProperty().set(b);
    }

    private ObjectProperty<Color> ellipsesFill;
    public ObjectProperty<Color> ellipsesFillProperty() {
        if (ellipsesFill == null) {
            ellipsesFill = new SimpleObjectProperty<>();
            ellipsesFill.bindBidirectional(dataOptions.ellipsesFillColorPicker.valueProperty());
        }
        return ellipsesFill;
    }
    public final Color getEllipsesFill() {
        return ellipsesFillProperty().get();
    }
    public final void setEllipsesFill(Color current) {
        ellipsesFillProperty().set(current);
    }

	private DoubleProperty ellipsesOpacity;
	public DoubleProperty ellipsesOpacityProperty() {
		if (ellipsesOpacity == null) {
			ellipsesOpacity = new SimpleDoubleProperty();
			ellipsesOpacity.bind(Bindings.createDoubleBinding(() -> {
				String s = dataOptions.ellipsesFillColorPicker.getValue().toString();
				return ((double) Integer.parseInt(s.substring(s.length() - 2).trim(), 16)) / 255;
			}, dataOptions.ellipsesFillColorPicker.valueProperty()));
		}
		return ellipsesOpacity;
	}
	public final Double getEllipsesOpacity() {
		return ellipsesOpacityProperty().get();
	}
	public final void setEllipsesOpacity(double d) {
		if ((d >= 0.0) && (d <= 1.0)) {
			String s = getPointsFill().toString();
			setPointsFill(Color.valueOf(s.substring(0, s.length() - 2) + Integer.toHexString(((int) d) * 255)));
		}
	}

    private BooleanProperty uncertaintyBars;
    public BooleanProperty uncertaintyBarsProperty() {
        if ( uncertaintyBars == null) {
	        uncertaintyBars = new SimpleBooleanProperty();
            uncertaintyBars.bindBidirectional(dataOptions.unctBarsRadioButton.selectedProperty());
        }
        return uncertaintyBars;
    }
    public final Boolean getUncertaintyBars() {
        return uncertaintyBarsProperty().get();
    }
    public final void setUncertaintyBars(boolean b) {
        uncertaintyBarsProperty().set(b);
    }

    private ObjectProperty<Color> uncertaintyBarsFill;
    public ObjectProperty<Color> uncertaintyBarsFillProperty() {
        if ( uncertaintyBarsFill == null) {
	        uncertaintyBarsFill = new SimpleObjectProperty<>();
            uncertaintyBarsFill.bindBidirectional(dataOptions.unctBarsFillColorPicker.valueProperty());
        }
        return uncertaintyBarsFill;
    }
    public final Color getUncertaintyBarsFill() {
        return uncertaintyBarsFillProperty().get();
    }
    public final void setUncertaintyBarsFill( Color c ) {
        uncertaintyBarsFillProperty().set(c);
    }

	private DoubleProperty uncertaintyBarsOpacity;
	public DoubleProperty uncertaintyBarsOpacityProperty() {
		if ( uncertaintyBarsOpacity == null) {
			uncertaintyBarsOpacity = new SimpleDoubleProperty();
			uncertaintyBarsOpacity.bind(Bindings.createDoubleBinding(() -> {
				String s = dataOptions.unctBarsFillColorPicker.getValue().toString();
				return ((double) Integer.parseInt(s.substring(s.length() - 2).trim(), 16)) / 255;
			}, dataOptions.unctBarsFillColorPicker.valueProperty()));
		}
		return uncertaintyBarsOpacity;
	}
	public final Double getUncertaintyBarsOpacity() {
		return uncertaintyBarsOpacityProperty().get();
	}
	public final void setUncertaintyBarsOpacity( double d ) {
		if ((d >= 0.0) && (d <= 1.0)) {
			String s = getPointsFill().toString();
			setPointsFill(Color.valueOf(s.substring(0, s.length() - 2) + Integer.toHexString(((int) d) * 255)));
		}
	}

    /*
        Start Plot Features properties...
     */

    private BooleanProperty mcLeanRegressionLine;
    public BooleanProperty mcLeanRegressionLineProperty() {
        if (mcLeanRegressionLine == null) {
            mcLeanRegressionLine = new SimpleBooleanProperty();
            mcLeanRegressionLine.bindBidirectional(plotFeatures.mcLeanRegressionCheckBox.selectedProperty());
        }
        return mcLeanRegressionLine;
    }
    public final Boolean mcLeanRegressionLine() {
        return mcLeanRegressionLineProperty().get();
    }
    public final void setMcLeanRegressionLine(boolean b) {
        mcLeanRegressionLineProperty().set(b);
    }

    private BooleanProperty mcLeanRegressionEnvelope;
    public BooleanProperty mcLeanRegressionEnvelopeProperty() {
        if ( mcLeanRegressionEnvelope == null) {
	        mcLeanRegressionEnvelope = new SimpleBooleanProperty();
            mcLeanRegressionEnvelope.bindBidirectional(plotFeatures.mcLeanEnvelopeCheckBox.selectedProperty());
        }
        return mcLeanRegressionEnvelope;
    }
    public final Boolean mcLeanRegressionEnvelope() {
        return mcLeanRegressionEnvelopeProperty().get();
    }
    public final void setMcLeanRegressionEnvelope(boolean b) {
        mcLeanRegressionEnvelopeProperty().set(b);
    }

    private BooleanProperty wetherillLine;
    public BooleanProperty wetherillLineProperty() {
        if ( wetherillLine == null) {
	        wetherillLine = new SimpleBooleanProperty();
	        wetherillLine.bind(Bindings.createBooleanBinding(() ->
							plotFeatures.wetherillRadioButton.equals(plotFeatures.concordiaToggleGroup.getSelectedToggle())
							&& plotFeatures.concordiaLineCheckBox.isSelected(),
					plotFeatures.concordiaToggleGroup.selectedToggleProperty(),
					plotFeatures.concordiaLineCheckBox.selectedProperty()
			));
        }
        return wetherillLine;
    }
    public final Boolean wetherillLine() {
        return wetherillLine.get();
    }
    public final void setWetherillLine( boolean b ) {
        plotFeatures.concordiaLineCheckBox.setSelected(b);
        if (b && wasserburgLine()) {
        	plotFeatures.wetherillRadioButton.setSelected(true);
		}
    }

	private BooleanProperty wetherillEnvelope;
	public BooleanProperty wetherillEnvelopeProperty() {
		if ( wetherillEnvelope == null) {
			wetherillEnvelope = new SimpleBooleanProperty();
			wetherillEnvelope.bind(Bindings.createBooleanBinding(() ->
							plotFeatures.wetherillRadioButton.equals(plotFeatures.concordiaToggleGroup.getSelectedToggle())
									&& plotFeatures.concordiaEnvelopeCheckBox.isSelected(),
					plotFeatures.concordiaToggleGroup.selectedToggleProperty(),
					plotFeatures.concordiaEnvelopeCheckBox.selectedProperty()
			));
		}
		return wetherillEnvelope;
	}
	public final Boolean wetherillEnvelope() {
		return wetherillEnvelope.get();
	}
	public final void setWetherillEnvelope( boolean b ) {
		plotFeatures.concordiaEnvelopeCheckBox.setSelected(b);
		if (b && wasserburgEnvelope()) {
			plotFeatures.wetherillRadioButton.setSelected(true);
		}
	}

    private ObjectProperty<Color> wetherillLineFill;
    public ObjectProperty<Color> wetherillLineFillProperty() {
    	if (wetherillLineFill == null) {
    		wetherillLineFill = new SimpleObjectProperty<>();
    		wetherillLineFill.bindBidirectional(plotFeatures.concordiaLineColorPicker.valueProperty());
	    }
	    return wetherillLineFill;
    }
    public final Color getWetherillLineFill() {
    	return wetherillLineFillProperty().get();
    }
    public final void setWetherillLineFill(Color c) {
    	plotFeatures.concordiaLineColorPicker.setValue(c);
    }

    private ObjectProperty<Color> wetherillEnvelopeFill;
    public ObjectProperty<Color> wetherillEnvelopeFillProperty() {
    	if (wetherillEnvelopeFill == null) {
    		wetherillEnvelopeFill = new SimpleObjectProperty<>();
    		wetherillEnvelopeFill.bindBidirectional(plotFeatures.concordiaEnvelopeColorPicker.valueProperty());
	    }
	    return wetherillEnvelopeFill;
    }
    public final Color getWetherillEnvelopeFill() {
    	return wetherillEnvelopeFillProperty().get();
    }
    public final void setWetherillEnvelopeFill(Color c) {
    	plotFeatures.concordiaEnvelopeColorPicker.setValue(c);
    }

	private BooleanProperty wasserburgLine;
	public BooleanProperty wasserburgLineProperty() {
		if ( wasserburgLine == null) {
			wasserburgLine = new SimpleBooleanProperty();
			wasserburgLine.bind(Bindings.createBooleanBinding(() ->
							plotFeatures.wasserburgRadioButton.equals(plotFeatures.concordiaToggleGroup.getSelectedToggle())
									&& plotFeatures.concordiaLineCheckBox.isSelected(),
					plotFeatures.concordiaToggleGroup.selectedToggleProperty(),
					plotFeatures.concordiaLineCheckBox.selectedProperty()
			));
		}
		return wasserburgLine;
	}
	public final Boolean wasserburgLine() {
		return wasserburgLine.get();
	}
	public final void setWasserburgLine( boolean b ) {
		plotFeatures.concordiaLineCheckBox.setSelected(b);
		if (b && wetherillLine()) {
			plotFeatures.wasserburgRadioButton.setSelected(true);
		}
	}

	private BooleanProperty wasserburgEnvelope;
	public BooleanProperty wasserburgEnvelopeProperty() {
		if ( wasserburgEnvelope == null) {
			wasserburgEnvelope = new SimpleBooleanProperty();
			wasserburgEnvelope.bind(Bindings.createBooleanBinding(() ->
							plotFeatures.wasserburgRadioButton.equals(plotFeatures.concordiaToggleGroup.getSelectedToggle())
									&& plotFeatures.concordiaEnvelopeCheckBox.isSelected(),
					plotFeatures.concordiaToggleGroup.selectedToggleProperty(),
					plotFeatures.concordiaEnvelopeCheckBox.selectedProperty()
			));
		}
		return wasserburgEnvelope;
	}
	public final Boolean wasserburgEnvelope() {
		return wasserburgEnvelope.get();
	}
	public final void setWasserburgEnvelope( boolean b ) {
		plotFeatures.concordiaEnvelopeCheckBox.setSelected(b);
		if (b && wetherillEnvelope()) {
			plotFeatures.wasserburgRadioButton.setSelected(true);
		}
	}

	private ObjectProperty<Color> wasserburgLineFill;
	public ObjectProperty<Color> wasserburgLineFillProperty() {
		if (wasserburgLineFill == null) {
			wasserburgLineFill = new SimpleObjectProperty<>();
			wasserburgLineFill.bindBidirectional(plotFeatures.concordiaLineColorPicker.valueProperty());
		}
		return wasserburgLineFill;
	}
	public final Color getWasserburgLineFill() {
		return wasserburgLineFillProperty().get();
	}
	public final void setWasserburgLineFill(Color c) {
		plotFeatures.concordiaLineColorPicker.setValue(c);
	}

	private ObjectProperty<Color> wasserburgEnvelopeFill;
	public ObjectProperty<Color> wasserburgEnvelopeFillProperty() {
		if (wasserburgEnvelopeFill == null) {
			wasserburgEnvelopeFill = new SimpleObjectProperty<>();
			wasserburgEnvelopeFill.bindBidirectional(plotFeatures.concordiaEnvelopeColorPicker.valueProperty());
		}
		return wasserburgEnvelopeFill;
	}
	public final Color getWasserburgEnvelopeFill() {
		return wasserburgEnvelopeFillProperty().get();
	}
	public final void setWasserburgEnvelopeFill(Color c) {
		plotFeatures.concordiaEnvelopeColorPicker.setValue(c);
	}

    private BooleanProperty evolutionMatrix;
    public BooleanProperty evolutionMatrixProperty() {
        if (evolutionMatrix == null) {
            evolutionMatrix = new SimpleBooleanProperty();
            evolutionMatrix.bindBidirectional(plotFeatures.evolutionCheckBox.selectedProperty());
        }
        return evolutionMatrix;
    }
    public final Boolean evolutionMatrix() {
        return evolutionMatrixProperty().get();
    }
    public final void setEvolutionMatrix(boolean b) {
        evolutionMatrixProperty().set(b);
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public PlotPropertiesPanel(Plot plot) {
        this.plot = plot;
        if (plot.getProperties() == null) {
        	plot.setProperties(new DefaultProperties());
        }
		try {
			FXMLUtils.loadController(CONTROLLER_FXML, PlotPropertiesPanel.class, this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    @FXML protected void initialize() {
    	axisStyling.setPropertiesPanel(this);

	    this.properties = plot.getProperties();
    	setPlotProperties(properties);
	    configureListeners();

	    plotFeatures.isotopeSystemProperty().bind(this.isotopeSystemProperty());
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public Map<PlotProperty, Object> getPlotProperties() {
		return properties;
    }

    public void setPlotProperties(Map<PlotProperty, Object> properties) {
	    if (properties.containsKey(TITLE)) setPlotTitle(String.valueOf(properties.get(TITLE)));
	    if (properties.containsKey(X_AXIS)) setXAxisTitle(String.valueOf(properties.get(X_AXIS)));
	    if (properties.containsKey(Y_AXIS)) setYAxisTitle(String.valueOf(properties.get(Y_AXIS)));

	    if (properties.containsKey(POINTS)) setPoints((Boolean) properties.get(POINTS));
	    if (properties.containsKey(POINTS_FILL)) setPointsFill(
			    Color.valueOf(String.valueOf(properties.get(POINTS_FILL))));
	    if (properties.containsKey(ELLIPSES)) setEllipses((Boolean) properties.get(ELLIPSES));
	    if (properties.containsKey(ELLIPSES_FILL)) setEllipsesFill(Color.valueOf(
			    String.valueOf(properties.get(ELLIPSES_FILL))));
	    if (properties.containsKey(UNCTBARS)) setUncertaintyBars((Boolean) properties.get(UNCTBARS));
	    if (properties.containsKey(UNCTBARS_FILL)) setUncertaintyBarsFill(Color.valueOf(
	    		String.valueOf(properties.get(UNCTBARS_FILL))));

	    if (properties.containsKey(ISOTOPE_SYSTEM)) setIsotopeSystem(IsotopeSystem.fromName(
	    		String.valueOf(properties.get(ISOTOPE_SYSTEM))));
	    if (properties.containsKey(UNCERTAINTY)) setUncertaintyFormat(Uncertainty.fromValue(
			    (Double) properties.get(UNCERTAINTY)));
	    if (properties.containsKey(WETHERILL_LINE)) setWetherillLine((Boolean) properties.get(WETHERILL_LINE));
		if (properties.containsKey(WETHERILL_ENVELOPE)) setWetherillEnvelope((Boolean) properties.get(WETHERILL_ENVELOPE));
	    if (properties.containsKey(WETHERILL_LINE_FILL)) setWetherillLineFill(
	    		Color.valueOf(String.valueOf(properties.get(WETHERILL_LINE_FILL))));
	    if (properties.containsKey(WETHERILL_ENVELOPE_FILL)) setWetherillEnvelopeFill(
	    		Color.valueOf(String.valueOf(properties.get(WETHERILL_ENVELOPE_FILL))));

		if (properties.containsKey(WASSERBURG_LINE)) setWasserburgLine((Boolean) properties.get(WASSERBURG_LINE));
		if (properties.containsKey(WASSERBURG_ENVELOPE)) setWasserburgEnvelope((Boolean) properties.get(WASSERBURG_ENVELOPE));
		if (properties.containsKey(WASSERBURG_LINE_FILL)) setWasserburgLineFill(
				Color.valueOf(String.valueOf(properties.get(WASSERBURG_LINE_FILL))));
		if (properties.containsKey(WASSERBURG_ENVELOPE_FILL)) setWasserburgEnvelopeFill(
				Color.valueOf(String.valueOf(properties.get(WASSERBURG_ENVELOPE_FILL))));


	    if (properties.containsKey(EVOLUTION)) setEvolutionMatrix((Boolean) properties.get(EVOLUTION));
	    if (properties.containsKey(MCLEAN_REGRESSION)) setMcLeanRegressionLine((Boolean) properties.get(MCLEAN_REGRESSION));
	    if (properties.containsKey(MCLEAN_REGRESSION_ENVELOPE)) setMcLeanRegressionEnvelope((Boolean) properties.get
			    (MCLEAN_REGRESSION_ENVELOPE));

	    boolean customExtents = false;
	    String xMin = null, xMax = null, yMin = null, yMax = null;
	    if (properties.containsKey(X_MIN)) {
	    	xMin = String.valueOf(properties.get(X_MIN));
	    	customExtents = true;
	    }
	    if (properties.containsKey(X_MAX)) {
	    	xMax = String.valueOf(properties.get(X_MAX));
		    customExtents = true;
	    }
	    if (properties.containsKey(Y_MIN)) {
	    	yMin = String.valueOf(properties.get(Y_MIN));
		    customExtents = true;
	    }
	    if (properties.containsKey(Y_MAX)) {
	    	yMax = String.valueOf(properties.get(Y_MAX));
		    customExtents = true;
	    }
	    if (customExtents) {
	    	if (xMin == null) xMin = "";
	    	if (xMax == null) xMax = "";
	    	if (yMin == null) yMin = "";
	    	if (yMax == null) yMax = "";
	    	setAxes(xMin, xMax, yMin, yMax);
	    }

	    refreshPlot();
    }

    public void refreshPlot() {
    	plot.setProperties(properties);
    }

	public boolean liveAxisUpdate() {
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

	public void setAxes(String xMin, String xMax, String yMin, String yMax) {
    	plot.setAxes(xMin, xMax, yMin, yMax);
	}

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

	private void configureListeners() {
    	plotTitleProperty().addListener(c -> {
    		properties.put(TITLE, getPlotTitle());
    		refreshPlot();
	    });
    	xAxisTitleProperty().addListener(c -> {
    		properties.put(X_AXIS, getXAxisTitle());
    		refreshPlot();
	    });
    	yAxisTitleProperty().addListener(c -> {
    		properties.put(Y_AXIS, getYAxisTitle());
    		refreshPlot();
	    });

    	isotopeSystemProperty().addListener(c -> {
    		properties.put(ISOTOPE_SYSTEM, getIsotopeSystem().getName());
    		refreshPlot();
	    });
    	uncertaintyFormatProperty().addListener(c -> {
    		properties.put(UNCERTAINTY, getUncertaintyFormat().getMultiplier());
    		refreshPlot();
	    });

    	pointsProperty().addListener(c -> {
    		properties.put(POINTS, getPoints());
    		refreshPlot();
	    });
    	pointsFillProperty().addListener(c -> {
    		properties.put(POINTS_FILL, convertColor(getPointsFill()));
    		properties.put(POINTS_OPACITY, convertOpacity(getPointsFill()));
    		refreshPlot();
	    });

    	ellipsesProperty().addListener(c -> {
    		properties.put(ELLIPSES, getEllipses());
    		refreshPlot();
	    });
    	ellipsesFillProperty().addListener(c -> {
			properties.put(ELLIPSES_FILL, convertColor(getEllipsesFill()));
			properties.put(ELLIPSES_OPACITY, convertOpacity(getEllipsesFill()));
    		refreshPlot();
	    });

    	uncertaintyBarsProperty().addListener(c -> {
			properties.put(UNCTBARS, getUncertaintyBars());
    		refreshPlot();
	    });
    	uncertaintyBarsFillProperty().addListener(c -> {
			properties.put(UNCTBARS_FILL, convertColor(getEllipsesFill()));
			properties.put(UNCTBARS_OPACITY, convertOpacity(getUncertaintyBarsFill()));
    		refreshPlot();
	    });

    	wetherillLineProperty().addListener(c -> {
			properties.put(WETHERILL_LINE, wetherillLine());
    		refreshPlot();
	    });
		wetherillEnvelopeProperty().addListener(c -> {
			properties.put(WETHERILL_ENVELOPE, wetherillEnvelope());
			refreshPlot();
		});
    	wetherillLineFillProperty().addListener(c -> {
    		properties.put(WETHERILL_LINE_FILL, convertColor(getWetherillLineFill()));
    		refreshPlot();
	    });
    	wetherillEnvelopeFillProperty().addListener(c -> {
    		properties.put(WETHERILL_ENVELOPE_FILL, convertColor(getWetherillEnvelopeFill()));
    		refreshPlot();
	    });



		wasserburgLineProperty().addListener(c -> {
			properties.put(WASSERBURG_LINE, wasserburgLine());
			refreshPlot();
		});
		wasserburgEnvelopeProperty().addListener(c -> {
			properties.put(WASSERBURG_ENVELOPE, wasserburgEnvelope());
			refreshPlot();
		});
		wasserburgLineFillProperty().addListener(c -> {
			properties.put(WASSERBURG_LINE_FILL, convertColor(getWasserburgLineFill()));
			refreshPlot();
		});
		wasserburgEnvelopeFillProperty().addListener(c -> {
			properties.put(WASSERBURG_ENVELOPE_FILL, convertColor(getWasserburgEnvelopeFill()));
			refreshPlot();
		});



    	evolutionMatrixProperty().addListener(c -> {
    		properties.put(EVOLUTION, evolutionMatrix());
    		refreshPlot();
	    });
    	mcLeanRegressionLineProperty().addListener(c -> {
			properties.put(MCLEAN_REGRESSION, mcLeanRegressionLine());
    		refreshPlot();
	    });
    	mcLeanRegressionEnvelopeProperty().addListener(c -> {
			properties.put(MCLEAN_REGRESSION_ENVELOPE, mcLeanRegressionEnvelope());
    		refreshPlot();
	    });
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
	private String convertColor(Color c) {
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
	private Double convertOpacity(Color c) {
		String s = c.toString();
		return ((double) Integer.parseInt(s.substring(s.length() - 2).trim(), 16)) / 255;
	}

}