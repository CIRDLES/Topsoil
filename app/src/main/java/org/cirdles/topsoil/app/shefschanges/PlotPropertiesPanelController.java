package org.cirdles.topsoil.app.shefschanges;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import org.cirdles.commons.util.ResourceExtractor;

import java.io.IOException;

public class PlotPropertiesPanelController extends Accordion {

    @FXML
    private AxisStylingController axisS = new AxisStylingController();
    @FXML
    private DataOptionController dataO = new DataOptionController();
    @FXML
    private PlotFeaturesController plotF = new PlotFeaturesController();

    private StringProperty plotTitle;

    public StringProperty plotTitleProperty() {
        if (plotTitle == null) {
            plotTitle = new SimpleStringProperty();
            plotTitle.bindBidirectional(axisS.plotTitleTextField.textProperty());
        }
        return plotTitle;
    }

    public String getPlotTitle() {
        return plotTitle.get();
    }

    public void setPlotTitle(String current) {
        plotTitle.set(current);
    }

    private StringProperty xMinText;

    public StringProperty xMinTextProperty() {
        if (xMinText == null) {
            xMinText = new SimpleStringProperty();
            xMinText.bindBidirectional(axisS.xMinTextField.textProperty());
        }
        return xMinText;
    }

    public String getXMinText() {
        return xMinText.get();
    }

    public void setXMinText(String current) {
        xMinText.set(current);
    }

    private StringProperty xMaxText;

    public StringProperty xMaxTextProperty() {
        if (xMaxText == null) {
            xMaxText = new SimpleStringProperty();
            xMaxText.bindBidirectional(axisS.xMaxTextField.textProperty());
        }
        return xMaxText;
    }

    public String getXMaxText() {
        return xMaxText.get();
    }

    public void setXMaxText(String current) {
        xMaxText.set(current);
    }

    //button
    private StringProperty xSetExtents;

    public StringProperty xSetExtentsProperty() {
        if (xSetExtents == null) {
            xSetExtents = new SimpleStringProperty();
            xSetExtents.bindBidirectional(axisS.xSetExtentsButton.textProperty());
        }
        return xSetExtents;
    }

    public String getXSetExtents() {
        return xSetExtents.get();
    }

    public void setXSetExtents(String current) {
        xSetExtents.set(current);
    }

    private StringProperty xTitleText;

    public StringProperty xTitleTextProperty() {
        if (xTitleText == null) {
            xTitleText = new SimpleStringProperty();
            xTitleText.bindBidirectional(axisS.xTitleTextField.textProperty());
        }
        return xTitleText;
    }

    public String getXTitleText() {
        return xTitleText.get();
    }

    public void setXTitleText(String current) {
        xTitleText.set(current);
    }

    private StringProperty yMinText;

    public StringProperty yMinTextProperty() {
        if (yMinText == null) {
            yMinText = new SimpleStringProperty();
            yMinText.bindBidirectional(axisS.yMinTextField.textProperty());
        }
        return yMinText;
    }

    public String getYMinText() {
        return yMinText.get();
    }

    public void setYMinText(String current) {
        yMinText.set(current);
    }

    private StringProperty yMaxText;

    public StringProperty yMaxTextProperty() {
        if (yMaxText == null) {
            yMaxText = new SimpleStringProperty();
            yMaxText.bindBidirectional(axisS.yMaxTextField.textProperty());
        }
        return yMaxText;
    }

    public String getYMaxText() {
        return yMaxText.get();
    }

    public void setYMaxText(String current) {
        yMaxText.set(current);
    }

    private StringProperty ySetExtents;

    public StringProperty ySetExtentsProperty() {
        if (ySetExtents == null) {
            ySetExtents = new SimpleStringProperty();
            ySetExtents.bindBidirectional(axisS.ySetExtentsButton.textProperty());
        }
        return ySetExtents;
    }

    public String getYSetExtents() {
        return ySetExtents.get();
    }

    public void setYSetExtents(String current) {
        ySetExtents.set(current);
    }

    private StringProperty yTitleText;

    public StringProperty yTitleTextProperty() {
        if (yTitleText == null) {
            yTitleText = new SimpleStringProperty();
            yTitleText.bindBidirectional(axisS.yTitleTextField.textProperty());
        }
        return yTitleText;
    }

    public String getYTitleText() {
        return yTitleText.get();
    }

    public void setYTitleText(String current) {
        yTitleText.set(current);
    }

    //DATA OPTIONS

    private BooleanProperty dataPoints;

    public BooleanProperty dataPointsProperty() {
        if (dataPoints == null) {
            dataPoints = new SimpleBooleanProperty();
            dataPoints.bindBidirectional(dataO.dataPointsCheckBox.selectedProperty());
        }
        return dataPoints;
    }

    public Boolean getDataPoints() {
        return dataPoints.get();
    }

    public void setDataPoints(Boolean current) {
        dataPoints.set(current);
    }

    //done
    private ObjectProperty<Color> pointsFillColor;

    public ObjectProperty<Color> pointsFillColorProperty() {
        if (pointsFillColor == null) {
            pointsFillColor = new SimpleObjectProperty<>();
            pointsFillColor.bindBidirectional(dataO.fillColorPicker.valueProperty());
        }
        return pointsFillColor;
    }

    public Color getPointsFillColor() {
        return pointsFillColor.get();
    }

    public void setPointsFillColor(Color current) {
        pointsFillColor.set(current);
    }

    private StringProperty uncertaintyEllipses;

    public StringProperty uncertaintyEllipsesProperty() {
        if (uncertaintyEllipses == null) {
            uncertaintyEllipses = new SimpleStringProperty();
            uncertaintyEllipses.bindBidirectional(dataO.uncertaintyEllipsesRadioButton.textProperty());
        }
        return uncertaintyEllipses;
    }

    public String getUncertaintyEllipses() {
        return uncertaintyEllipses.get();
    }

    public void setUncertaintyEllipses(String current) {
        uncertaintyEllipses.set(current);
    }

    private ObjectProperty<Color> ellipsesFill;

    public ObjectProperty<Color> ellipsesFillProperty() {
        if (ellipsesFill == null) {
            ellipsesFill = new SimpleObjectProperty();
            ellipsesFill.bindBidirectional(dataO.ellipsesFillColorPicker.valueProperty());
        }
        return ellipsesFill;
    }

    public Color getEllipsesFill() {
        return ellipsesFill.get();
    }

    public void setEllipsesFill(Color current) {
        ellipsesFill.set(current);
    }


    private StringProperty uncertaintyBars;

    public StringProperty uncertaintyBarsProperty() {
        if (uncertaintyBars == null) {
            uncertaintyBars = new SimpleStringProperty();
            uncertaintyBars.bindBidirectional(dataO.uncertaintyBarsRadioButton.textProperty());
        }
        return uncertaintyBars;
    }

    public String getUncertaintyBars() {
        return uncertaintyBars.get();
    }

    public void setUncertaintyBars(String current) {
        uncertaintyBars.set(current);
    }

    //done
    private ObjectProperty<Color> barColor;

    public ObjectProperty<Color> barColorProperty() {
        if (barColor == null) {
            barColor = new SimpleObjectProperty();
            barColor.bindBidirectional(dataO.barColorPicker.valueProperty());
        }
        return barColor;
    }

    public Color getBarColor() {
        return barColor.get();
    }

    public void setBarColor(Color current) {
        barColor.set(current);
    }


    //PLOT FEATURES

    private BooleanProperty mcLeanRegressionLine;

    public BooleanProperty mcLeanRegressionLineProperty() {
        if (mcLeanRegressionLine == null) {
            mcLeanRegressionLine = new SimpleBooleanProperty();
            mcLeanRegressionLine.bindBidirectional(plotF.mcLeanRegressionCheckBox.selectedProperty());
        }
        return mcLeanRegressionLine;
    }

    public Boolean showMcLeanRegressionLine() {
        return mcLeanRegressionLineProperty().get();
    }

    public void setShowMcLeanRegressionLine(Boolean current) {
        mcLeanRegressionLineProperty().set(current);
    }

    private BooleanProperty mcLeanUncertaintyEnv;

    public BooleanProperty mcLeanUncertaintyEnvProperty() {
        if (mcLeanUncertaintyEnv == null) {
            mcLeanUncertaintyEnv = new SimpleBooleanProperty();
            mcLeanUncertaintyEnv.bindBidirectional(plotF.mcLeanUncertaintyCheckBox.selectedProperty());
        }
        return mcLeanUncertaintyEnv;
    }

    public Boolean showMcLeanUncertaintyEnv() {
        return mcLeanUncertaintyEnvProperty().get();
    }

    public void setShowMcLeanUncertaintyEnv(Boolean current) {
        mcLeanUncertaintyEnvProperty().set(current);
    }

    private BooleanProperty wetherillConcordiaLine;

    public BooleanProperty wetherillConcordiaLineProperty() {
        if (wetherillConcordiaLine == null) {
            wetherillConcordiaLine = new SimpleBooleanProperty();
            wetherillConcordiaLine.bindBidirectional(plotF.wetherillCheckBox.selectedProperty());
        }
        return wetherillConcordiaLine;
    }

    public Boolean getWetherillConcordiaLine() {
        return wetherillConcordiaLine.get();
    }

    public void setWetherillConcordiaLine(Boolean current) {
        wetherillConcordiaLine.set(current);
    }

    private BooleanProperty evolutionMatrix;

    public BooleanProperty evolutionMatrixProperty() {
        if (evolutionMatrix == null) {
            evolutionMatrix = new SimpleBooleanProperty();
            evolutionMatrix.bindBidirectional(plotF.evolutionCheckBox.selectedProperty());
        }
        return evolutionMatrix;
    }

    public Boolean showEvolutionMatrix() {
        return evolutionMatrixProperty().get();
    }

    public void setShowEvolutionMatrix(Boolean current) {
        evolutionMatrixProperty().set(current);
    }

    public PlotPropertiesPanelController() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    new ResourceExtractor(PlotPropertiesPanelController.class).extractResourceAsPath("plot-properties-panel.fxml").toUri().toURL()
            );
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Before controls loaded
    }

    @FXML
    public void initialize() {
        // After controls loaded
    }
}