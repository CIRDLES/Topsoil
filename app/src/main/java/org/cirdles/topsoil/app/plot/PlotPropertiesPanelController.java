package org.cirdles.topsoil.app.plot;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.TopsoilTableController;
import org.cirdles.topsoil.app.table.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.base.BasePlotDefaultProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.*;

/**
 * A panel that controls the {@code Control}s that manage the various plot properties and features.
 *
 * @author marottajb
 */
public class PlotPropertiesPanelController {

    //***********************
    // Attributes
    //***********************

    /**
     * A {@code ChoiceBox} allowing the user to select the {@code IsotopeType} of the table and plot.
     */
    @FXML private ChoiceBox<String> isotopeSystemChoiceBox;

    /**
     * A {@code ChoiceBox} allowing the user to select the uncertainty format of the plot.
     */
    @FXML private ChoiceBox<String> uncertaintyChoiceBox;

    /**
     * A {@code TextField} for editing the title of the plot.
     */
    @FXML private TextField titleTextField;

    /**
     * A {@code TextField} for editing the title of the plot's X axis.
     */
    @FXML private TextField xAxisTextField;

    /**
     * A {@code TextField} for editing the title of the plot's X axis.
     */
    @FXML private TextField yAxisTextField;

    /**
     * A {@code TextField} for changing the X-axis's minimum point.
     */
    @FXML private TextField xAxisMinTextField;

    /**
     * A {@code TextField} for changing the X-axis's maximum point.
     */
    @FXML private TextField xAxisMaxTextField;

    /**
     * A {@code TextField} for changing the Y-axis's minimum point.
     */
    @FXML private TextField yAxisMinTextField;

    /**
     * A {@code TextField} for changing the Y-axis's maximum point.
     */
    @FXML private TextField yAxisMaxTextField;

    /**
     * A {@code CheckBox} for toggling the visibility of the data points in the plot.
     */
    @FXML private CheckBox pointsCheckBox;

    /**
     * A {@code CheckBox} for toggling the visibility of the uncertainty ellipses in the plot.
     */
    @FXML private CheckBox ellipsesCheckBox;

    /**
     * A {@code CheckBox} for toggling the visibility of the uncertainty crosses in the plot.
     */
    @FXML private CheckBox crossesCheckBox;

    /**
     * A {@code ColorPicker} for selecting the color of the data points in the plot.
     */
    @FXML private ColorPicker pointsColorPicker;

    /**
     * A {@code ColorPicker} for selecting the color of the uncertainty ellipses in the plot.
     */
    @FXML private ColorPicker ellipsesColorPicker;

    /**
     * A {@code ColorPicker} for selecting the color of the uncertainty crosses in the plot.
     */
    @FXML private ColorPicker crossesColorPicker;

    /**
     * A {@code VBox} for organizing various additional plot features.
     */
    @FXML private VBox featureBox;

    /**
     * An {@code Hbox} containing the controls for the concordia line feature.
     */
    @FXML private HBox concordiaFeature;
    @FXML private CheckBox concordiaCheckBox;

    /**
     * An {@code HBox} containing the controls for the evolution matrix feature.
     */
    @FXML private HBox evolutionFeature;
    @FXML private CheckBox evolutionCheckBox;

    /**
     * A {@code Button} that, when pressed, generates a {@link Plot} with the current options for the current table.
     */
    @FXML private Button generatePlotButton;

    /**
     * A {@code Map} of {@code String}s to {@code IsotopeType}s for getting selections from isotopeSystemChoiceBox.
     */
    private static Map<String, IsotopeType> STRING_TO_ISOTOPE_TYPE;

    private static Map<String, UncertaintyFormat> STRING_TO_UNCERTAINTY_FORMAT;
    private static Map<UncertaintyFormat, String> UNCERTAINTY_FORMAT_TO_STRING;
    private static Map<Double, UncertaintyFormat> DOUBLE_TO_UNCERTAINTY_FORMAT;

    /**
     * A {@code Map} of plot properties that is constantly updated with values that can be applied to JavaScript plots.
     */
    private final ObservableMap<String, Object> PROPERTIES = FXCollections.observableMap(new BasePlotDefaultProperties());

    static {
        STRING_TO_ISOTOPE_TYPE = new LinkedHashMap<>(IsotopeType.ISOTOPE_TYPES.size());
        for (IsotopeType type : IsotopeType.ISOTOPE_TYPES) {
            STRING_TO_ISOTOPE_TYPE.put(type.getName(), type);
        }

        STRING_TO_UNCERTAINTY_FORMAT = new LinkedHashMap<>(UncertaintyFormat.PLOT_FORMATS.size());
        UNCERTAINTY_FORMAT_TO_STRING = new LinkedHashMap<>(UncertaintyFormat.PLOT_FORMATS.size());
        DOUBLE_TO_UNCERTAINTY_FORMAT = new LinkedHashMap<>(UncertaintyFormat.PLOT_FORMATS.size());
        for (UncertaintyFormat format : UncertaintyFormat.PLOT_FORMATS) {
            STRING_TO_UNCERTAINTY_FORMAT.put(format.getName(), format);
            UNCERTAINTY_FORMAT_TO_STRING.put(format, format.getName());
            DOUBLE_TO_UNCERTAINTY_FORMAT.put(format.getValue(), format);
        }
    }

    /**
     * The {@code Plot} that this panel affects.
     */
    private Plot plot;

    //***********************
    // Properties
    //***********************

    /**
     * An {@code ObjectProperty} containing the {@code IsotopeType} of the table and plot.
     */
    private ObjectProperty<IsotopeType> isotopeType;
    public final ObjectProperty<IsotopeType> isotopeTypeObjectProperty() {
        if (isotopeType == null) {
            isotopeType = new SimpleObjectProperty<>(STRING_TO_ISOTOPE_TYPE.get(isotopeSystemChoiceBox
                                                                                        .getSelectionModel().getSelectedItem()));

            isotopeType.addListener(c -> {
                if (STRING_TO_ISOTOPE_TYPE.get(isotopeSystemChoiceBox.getSelectionModel().getSelectedItem()) != isotopeType.get()) {
                    for (String s : isotopeSystemChoiceBox.getItems()) {
                        if (s.equals(isotopeType.get().getName())) {
                            isotopeSystemChoiceBox.getSelectionModel().select(s);
                            break;
                        }
                    }
                }

                switch (getIsotopeType()) {
                    case Generic:
                        concordiaCheckBox.setDisable(true);
                        evolutionCheckBox.setDisable(true);
                        break;
                    case UPb:
                        concordiaCheckBox.setDisable(false);
                        evolutionCheckBox.setDisable(true);
                        break;
                    case UTh:
                        concordiaCheckBox.setDisable(true);
                        evolutionCheckBox.setDisable(false);
                        break;
                    default:
                        concordiaCheckBox.setDisable(true);
                        evolutionCheckBox.setDisable(true);
                        break;
                }
            });
        }
        return isotopeType;
    }
    public IsotopeType getIsotopeType() {
        return isotopeTypeObjectProperty().get();
    }
    public void setIsotopeType(IsotopeType isotopeType) {
        isotopeTypeObjectProperty().set(isotopeType);
    }

    /**
     * A {@code ObjectProperty} containing the uncertainty format of the plot.
     */
    private ObjectProperty<UncertaintyFormat> uncertainty;
    public final ObjectProperty<UncertaintyFormat> uncertaintyProperty() {
        if (uncertainty == null) {
            uncertainty = new SimpleObjectProperty<>(STRING_TO_UNCERTAINTY_FORMAT.get(uncertaintyChoiceBox
                                                                                         .getSelectionModel().getSelectedItem()));

            uncertainty.addListener(c-> {
                if (STRING_TO_UNCERTAINTY_FORMAT.get(uncertaintyChoiceBox.getSelectionModel().getSelectedItem()) != uncertainty.get()) {
                    uncertaintyChoiceBox.getSelectionModel().select(UNCERTAINTY_FORMAT_TO_STRING.get(uncertainty.get()));
                }
            });
        }
        return uncertainty;
    }
    public UncertaintyFormat getUncertainty() {
        return uncertaintyProperty().get();
    }
    public void setUncertainty(UncertaintyFormat format) {
        uncertaintyChoiceBox.setValue(UNCERTAINTY_FORMAT_TO_STRING.get(format));
    }

    /**
     * A {@code StringProperty} containing the title of the plot.
     */
    private StringProperty title;
    public final StringProperty titleProperty() {
        if (title == null) {
            title = new SimpleStringProperty(titleTextField.getText());
            title.bind(titleTextField.textProperty());
        }
        return title;
    }
    public String getTitle() {
        return titleProperty().get();
    }
    public void setTitle(String s) {
        titleTextField.setText(s);
    }

    /**
     * A {@code StringProperty} containing the title of the plot's X axis.
     */
    private StringProperty xAxisTitle;
    public final StringProperty xAxisTitleProperty() {
        if (xAxisTitle == null) {
            xAxisTitle = new SimpleStringProperty(xAxisTextField.getText());
            xAxisTitle.bind(xAxisTextField.textProperty());
        }
        return xAxisTitle;
    }
    public String getXAxisTitle() {
        return xAxisTitleProperty().get();
    }
    public void setxAxisTitle(String s) {
        xAxisTextField.setText(s);
    }

    /**
     * A {@code StringProperty} containing the title of the plot's Y axis.
     */
    private StringProperty yAxisTitle;
    public final StringProperty yAxisTitleProperty() {
        if (yAxisTitle == null) {
            yAxisTitle = new SimpleStringProperty(yAxisTextField.getText());
            yAxisTitle.bind(yAxisTextField.textProperty());
        }
        return yAxisTitle;
    }
    public String getYAxisTitle() {
        return yAxisTitleProperty().get();
    }
    public void setyAxisTitle(String s) {
        yAxisTextField.setText(s);
    }

    /**
     * A {@code StringProperty} containing the X axis's minimum value.
     */
    private StringProperty xAxisMin;
    public final StringProperty xAxisMinProperty() {
        if (xAxisMin == null) {
            xAxisMin = new SimpleStringProperty(xAxisMinTextField.getText());
            xAxisMin.bind(xAxisMinTextField.textProperty());
        }
        return xAxisMin;
    }
    public String getXAxisMin() {
        return xAxisMinProperty().get();
    }
    public void setXAxisMin(String s) {
        xAxisMinTextField.setText(s);
    }

    /**
     * A {@code StringProperty} containing the X axis's maximum value.
     */
    private StringProperty xAxisMax;
    public final StringProperty xAxisMaxProperty() {
        if (xAxisMax == null) {
            xAxisMax = new SimpleStringProperty(xAxisMaxTextField.getText());
            xAxisMax.bind(xAxisMaxTextField.textProperty());
        }
        return xAxisMax;
    }
    public String getXAxisMax() {
        return xAxisMaxProperty().get();
    }
    public void setXAxisMax(String s) {
        xAxisMaxTextField.setText(s);
    }

    /**
     * A {@code StringProperty} containing the Y axis's minimum value.
     */
    private StringProperty yAxisMin;
    public final StringProperty yAxisMinProperty() {
        if (yAxisMin == null) {
            yAxisMin = new SimpleStringProperty(yAxisMinTextField.getText());
            yAxisMin.bind(yAxisMinTextField.textProperty());
        }
        return yAxisMin;
    }
    public String getYAxisMin() {
        return yAxisMinProperty().get();
    }
    public void setYAxisMin(String s) {
        yAxisMinTextField.setText(s);
    }

    /**
     * A {@code StringProperty} containing the Y axis's maximum value.
     */
    private StringProperty yAxisMax;
    public final StringProperty yAxisMaxProperty() {
        if (yAxisMax == null) {
            yAxisMax = new SimpleStringProperty(yAxisMaxTextField.getText());
            yAxisMax.bind(yAxisMaxTextField.textProperty());
        }
        return yAxisMax;
    }
    public String getYAxisMax() {
        return yAxisMaxProperty().get();
    }
    public void setYAxisMax(String s) {
        yAxisMaxTextField.setText(s);
    }

    /**
     * A {@code BooleanProperty} tracking whether or not data points should be shown in the plot.
     */
    private BooleanProperty showPoints;
    public final BooleanProperty showPointsProperty() {
        if (showPoints == null) {
            showPoints = new SimpleBooleanProperty(pointsCheckBox.isSelected());
            showPoints.bind(pointsCheckBox.selectedProperty());
        }
        return showPoints;
    }
    public Boolean shouldShowPoints() {
        return showPointsProperty().get();
    }
    public void setShowPoints(Boolean b) {
        pointsCheckBox.setSelected(b);
    }

    /**
     * A {@code BooleanProperty} tracking whether or not uncertainty ellipses should be shown in the plot.
     */
    private BooleanProperty showEllipses;
    public final BooleanProperty showEllipsesProperty() {
        if (showEllipses == null) {
            showEllipses = new SimpleBooleanProperty(ellipsesCheckBox.isSelected());
            showEllipses.bind(ellipsesCheckBox.selectedProperty());
        }
        return showEllipses;
    }
    public Boolean shouldShowEllipses() {
        return showEllipsesProperty().get();
    }
    public void setshowEllipses(Boolean b) {
        ellipsesCheckBox.setSelected(b);
    }

    /**
     * A {@code BooleanProperty} tracking whether or not uncertainty crosses should be shown in the plot.
     */
    private BooleanProperty showCrosses;
    public final BooleanProperty showCrossesProperty() {
        if (showCrosses == null) {
            showCrosses = new SimpleBooleanProperty(crossesCheckBox.isSelected());
            showCrosses.bind(crossesCheckBox.selectedProperty());
        }
        return showCrosses;
    }
    public Boolean shouldShowCrosses() {
        return showCrossesProperty().get();
    }
    public void setShowCrosses(Boolean b) {
        crossesCheckBox.setSelected(b);
    }

    /**
     * An {@code ObjectProperty} containing the selected {@code Color} of the data points in the plot.
     */
    private ObjectProperty<Color> pointsColor;
    public final ObjectProperty<Color> pointsColorProperty() {
        if (pointsColor == null) {
            pointsColor = new SimpleObjectProperty<>(pointsColorPicker.getValue());
            pointsColor.bind(pointsColorPicker.valueProperty());
        }
        return pointsColor;
    }
    public Color getPointsColor() {
        return pointsColorProperty().get();
    }
    public void setPointsColor(Color c) {
        pointsColorPicker.setValue(c);
    }

    /**
     * An {@code ObjectProperty} containing the selected {@code Color} of the uncertainty ellipses in the plot.
     */
    private ObjectProperty<Color> ellipsesColor;
    public final ObjectProperty<Color> ellipsesColorProperty() {
        if (ellipsesColor == null) {
            ellipsesColor = new SimpleObjectProperty<>(ellipsesColorPicker.getValue());
            ellipsesColor.bind(ellipsesColorPicker.valueProperty());
        }
        return ellipsesColor;
    }
    public Color getEllipsesColor() {
        return ellipsesColorProperty().get();
    }
    public void setEllipsesColor(Color c) {
        ellipsesColorPicker.setValue(c);
    }

    /**
     * An {@code ObjectProperty} containing the selected {@code Color} of the uncertainty crosses in the plot.
     */
    private ObjectProperty<Color> crossesColor;
    public final ObjectProperty<Color> crossesColorProperty() {
        if (crossesColor == null) {
            crossesColor = new SimpleObjectProperty<>(crossesColorPicker.getValue());
            crossesColor.bind(crossesColorPicker.valueProperty());
        }
        return crossesColor;
    }
    public Color getCrossesColor() {
        return crossesColorProperty().get();
    }
    public void setCrossesColor(Color c) {
        crossesColorPicker.setValue(c);
    }

    /**
     * A {@code BooleanProperty} tracking whether or not a concordia line should be shown in the plot.
     */
    private BooleanProperty showConcordia;
    public final BooleanProperty showConcordiaProperty() {
        if (showConcordia == null) {
            showConcordia = new SimpleBooleanProperty(concordiaCheckBox.isSelected());
            showConcordia.bind(concordiaCheckBox.selectedProperty());
        }
        return showConcordia;
    }
    public Boolean shouldShowConcordia() {
        return showConcordiaProperty().get();
    }
    public void setShowConcordia(Boolean b) {
        concordiaCheckBox.setSelected(b);
    }

    /**
     * A {@code BooleanProperty} tracking whether or not an evolution matrix should be drawn in the plot.
     */
    private BooleanProperty showEvolutionMatrix;
    public final BooleanProperty showEvolutionMatrixProperty() {
        if (showEvolutionMatrix == null) {
            showEvolutionMatrix = new SimpleBooleanProperty(evolutionCheckBox.isSelected());
            showEvolutionMatrix.bind(evolutionCheckBox.selectedProperty());
        }
        return showEvolutionMatrix;
    }
    public Boolean shouldShowEvolutionMatrix() {
        return showEvolutionMatrixProperty().get();
    }
    public void setShowEvolutionMatrix(Boolean b) {
        evolutionCheckBox.setSelected(b);
    }

    //***********************
    // Methods
    //***********************

    /** {@inheritDoc}
     */
    @FXML public void initialize() {
        // Populate isotope system choice box
        for (String s : STRING_TO_ISOTOPE_TYPE.keySet()) {
            isotopeSystemChoiceBox.getItems().add(s);
        }

        for (String s : STRING_TO_UNCERTAINTY_FORMAT.keySet()) {
            uncertaintyChoiceBox.getItems().add(s);
        }

        // Called to make sure that isotopeType is initialized before isotopeTypeChoiceBox is set, to determine
        // whether the concordia feature should be visible.
        isotopeTypeObjectProperty();

        // Set default Base Plot properties.
        isotopeSystemChoiceBox.getSelectionModel().select(STRING_TO_ISOTOPE_TYPE.get((String) PROPERTIES.get(ISOTOPE_TYPE)).getName());

        for (String s : uncertaintyChoiceBox.getItems()) {
            if (s.equals(UncertaintyFormat.TWO_SIGMA_ABSOLUTE.getName())) {
                uncertaintyChoiceBox.getSelectionModel().select(s);
            }
        }

        titleTextField.setText((String) PROPERTIES.get(TITLE));
        xAxisTextField.setText((String) PROPERTIES.get(X_AXIS));
        yAxisTextField.setText((String) PROPERTIES.get(Y_AXIS));

        xAxisMinTextField.setText(String.valueOf(PROPERTIES.get(X_MIN)));
        xAxisMaxTextField.setText(String.valueOf(PROPERTIES.get(X_MAX)));
        yAxisMinTextField.setText(String.valueOf(PROPERTIES.get(Y_MIN)));
        yAxisMaxTextField.setText(String.valueOf(PROPERTIES.get(Y_MAX)));

        pointsCheckBox.setSelected((Boolean) PROPERTIES.get(POINTS));
        ellipsesCheckBox.setSelected((Boolean) PROPERTIES.get(ELLIPSES));
        crossesCheckBox.setSelected((Boolean) PROPERTIES.get(BARS));
        // TODO Implement Crosses
//        crossesCheckBox.setVisible(false);

        pointsColorPicker.setValue(Color.valueOf((String) PROPERTIES.get(POINT_FILL_COLOR)));
        ellipsesColorPicker.setValue(Color.valueOf((String) PROPERTIES.get(ELLIPSE_FILL_COLOR)));
        crossesColorPicker.setValue(Color.valueOf((String) PROPERTIES.get(BAR_FILL_COLOR)));
        // TODO Implement Crosses
//        crossesColorPicker.setVisible(false);

        // Only one uncertainty option can be selected at a time.
        ellipsesCheckBox.selectedProperty().addListener(c -> {
            if (ellipsesCheckBox.isSelected()) {
                crossesCheckBox.setSelected(false);
            }
        });
        crossesCheckBox.selectedProperty().addListener(c -> {
            if (crossesCheckBox.isSelected()) {
                ellipsesCheckBox.setSelected(false);
            }
        });

        concordiaCheckBox.setSelected((Boolean) PROPERTIES.get(CONCORDIA_LINE));

        evolutionCheckBox.setSelected((Boolean) PROPERTIES.get(EVOLUTION_MATRIX));

        // Automatically adjust PROPERTIES
        isotopeTypeObjectProperty().addListener(c -> {
            PROPERTIES.put(ISOTOPE_TYPE, isotopeTypeObjectProperty().get().getName());
            updateProperties();
        });
        isotopeSystemChoiceBox.getSelectionModel().selectedItemProperty().addListener(c -> {
            if (getIsotopeType() != STRING_TO_ISOTOPE_TYPE.get(isotopeSystemChoiceBox.getSelectionModel()
                                                                                     .getSelectedItem())) {
                isotopeTypeObjectProperty().set(STRING_TO_ISOTOPE_TYPE.get(isotopeSystemChoiceBox.getSelectionModel()
                                                                                            .getSelectedItem()));
            }
        });

        uncertaintyProperty().addListener(c -> {
            PROPERTIES.put(UNCERTAINTY, uncertaintyProperty().get().getValue());
            updateProperties();
        });
        uncertaintyChoiceBox.getSelectionModel().selectedItemProperty().addListener(c -> {
            if (STRING_TO_UNCERTAINTY_FORMAT.get(uncertaintyChoiceBox.getSelectionModel().getSelectedItem()) != getUncertainty()) {
                uncertaintyProperty().set(STRING_TO_UNCERTAINTY_FORMAT.get(uncertaintyChoiceBox.getSelectionModel().getSelectedItem()));
            }
        });

        titleProperty().addListener(c -> {
            PROPERTIES.put(TITLE, titleProperty().get());
            updateProperties();
        });
        xAxisTitleProperty().addListener(c -> {
            PROPERTIES.put(X_AXIS, xAxisTitleProperty().get());
            updateProperties();
        });
        yAxisTitleProperty().addListener(c -> {
            PROPERTIES.put(Y_AXIS, yAxisTitleProperty().get());
            updateProperties();
        });

        xAxisMinProperty().addListener(c -> {
            PROPERTIES.put(X_MIN, xAxisMinProperty().get());
            updateProperties();
        });
        xAxisMaxProperty().addListener(c -> {
            PROPERTIES.put(X_MAX, xAxisMaxProperty().get());
            updateProperties();
        });
        yAxisMinProperty().addListener(c -> {
            PROPERTIES.put(Y_MIN, yAxisMinProperty().get());
            updateProperties();
        });
        yAxisMaxProperty().addListener(c -> {
            PROPERTIES.put(Y_MAX, yAxisMaxProperty().get());
            updateProperties();
        });

        showPointsProperty().addListener(c -> {
            PROPERTIES.put(POINTS, showPointsProperty().get());
            updateProperties();
        });
        showEllipsesProperty().addListener(c -> {
            PROPERTIES.put(ELLIPSES, showEllipsesProperty().get());
            updateProperties();
        });
        showCrossesProperty().addListener(c -> {
            PROPERTIES.put(BARS, showCrossesProperty().get());
            updateProperties();
        });
        pointsColorProperty().addListener(c -> {
            PROPERTIES.put(POINT_FILL_COLOR, convertColor(pointsColorProperty().get()));
            PROPERTIES.put(POINT_OPACITY, convertOpacity(pointsColorProperty().get()));
            updateProperties();
        });
        ellipsesColorProperty().addListener(c -> {
            PROPERTIES.put(ELLIPSE_FILL_COLOR, convertColor(ellipsesColorProperty().get()));
            PROPERTIES.put(ELLIPSE_OPACITY, convertOpacity(ellipsesColorProperty().get()));
            updateProperties();
        });
        crossesColorProperty().addListener(c -> {
            PROPERTIES.put(BAR_FILL_COLOR, convertColor(crossesColorProperty().get()));
            PROPERTIES.put(BAR_OPACITY, convertOpacity(crossesColorProperty().get()));
            updateProperties();
        });
        showConcordiaProperty().addListener(c -> {
            PROPERTIES.put(CONCORDIA_LINE, shouldShowConcordia());
            updateProperties();
        });
        showEvolutionMatrixProperty().addListener(c -> {
            PROPERTIES.put(EVOLUTION_MATRIX, shouldShowEvolutionMatrix());
            updateProperties();
        });
    }

    /**
     * Sets the plot that this panel manages to the specified {@code Plot}.
     *
     * @param plot  the new Plot
     */
    public void setPlot(Plot plot) {
        this.plot = plot;
    }

    /**
     * Sets the plot that this panel manages to null.
     */
    public void removePlot() {
        this.plot = null;
    }

    /**
     * If a {@code Plot} is being managed by this panel, this method updates the properties of the plot.
     */
    private void updateProperties() {
        if (plot != null) {
            plot.setProperties(PROPERTIES);
        }
    }

    /**
     * Manually sets the X and Y axis extents.
     */
    public void setAxes() {
        if (plot != null) {
            plot.setAxes();
        }
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

    /**
     * Returns {@link #PROPERTIES} as an {@code ObservableMap}.
     *
     * @return  an ObservableMap of properties
     */
    public ObservableMap<String, Object> getProperties() {
        return PROPERTIES;
    }

    /**
     * Sets all of the properties in the panel to the values specified by the supplied {@code Map}.
     *
     * @param plotProperties    a Map containing new property values
     */
    public void setProperties(Map<String, Object> plotProperties) {
        if (plotProperties.containsKey(TITLE)) setTitle((String) plotProperties.get(TITLE));
        if (plotProperties.containsKey(X_AXIS)) setxAxisTitle((String) plotProperties.get(X_AXIS));
        if (plotProperties.containsKey(Y_AXIS)) setyAxisTitle((String) plotProperties.get(Y_AXIS));
        if (plotProperties.containsKey(POINTS)) setShowPoints((Boolean) plotProperties.get(POINTS));
        if (plotProperties.containsKey(ELLIPSES)) setshowEllipses((Boolean) plotProperties.get(ELLIPSES));
        if (plotProperties.containsKey(BARS)) setShowCrosses((Boolean) plotProperties.get(BARS));
        if (plotProperties.containsKey(POINT_FILL_COLOR)) setPointsColor(Color.valueOf((String) plotProperties.get(POINT_FILL_COLOR)));
        if (plotProperties.containsKey(ELLIPSE_FILL_COLOR)) setEllipsesColor(Color.valueOf((String) plotProperties.get(ELLIPSE_FILL_COLOR)));
        if (plotProperties.containsKey(BAR_FILL_COLOR)) setCrossesColor(Color.valueOf((String) plotProperties.get(
                BAR_FILL_COLOR)));
        if (plotProperties.containsKey(ISOTOPE_TYPE)) setIsotopeType(STRING_TO_ISOTOPE_TYPE.get((String) plotProperties.get(ISOTOPE_TYPE)));
        if (plotProperties.containsKey(UNCERTAINTY)) setUncertainty(DOUBLE_TO_UNCERTAINTY_FORMAT.get(
                (Double) plotProperties.get(UNCERTAINTY)));
        if (plotProperties.containsKey(CONCORDIA_LINE)) setShowConcordia((Boolean) plotProperties.get(CONCORDIA_LINE));
        if (plotProperties.containsKey(EVOLUTION_MATRIX)) setShowEvolutionMatrix((Boolean) plotProperties.get(EVOLUTION_MATRIX));
    }

    @FXML private void assignVariablesButtonAction() {
        ((TopsoilTabPane) MainWindow.getPrimaryStage().getScene().lookup("#TopsoilTabPane"))
                .getSelectedTab().getTableController().showVariableChooserDialog(null);
    }

    /**
     * Generates a {@link Plot} with the specified properties for the current tab.
     */
    @FXML private void generatePlotButtonAction() {
        TopsoilTableController tableController = ((TopsoilTabPane) MainWindow.getPrimaryStage().getScene().lookup
                ("#TopsoilTabPane"))
                .getSelectedTab().getTableController();

        // If X and Y aren't specified.
        if (!tableController.getTable().getVariableAssignments().containsKey(Variables.X)
                || !tableController.getTable().getVariableAssignments().containsKey(Variables.Y)) {
            tableController.showVariableChooserDialog(asList(Variables.X, Variables.Y));
        }

        if (tableController.getTable().getVariableAssignments().containsKey(Variables.X)
                && tableController.getTable().getVariableAssignments().containsKey(Variables.Y)) {
            PlotGenerationHandler.handlePlotGenerationForSelectedTab((TopsoilTabPane) generatePlotButton.getScene().lookup
                    ("#TopsoilTabPane"));
        }
    }

}
