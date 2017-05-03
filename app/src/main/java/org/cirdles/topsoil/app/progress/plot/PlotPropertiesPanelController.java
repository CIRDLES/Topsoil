package org.cirdles.topsoil.app.progress.plot;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.base.BasePlotDefaultProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.cirdles.topsoil.plot.base.BasePlotProperties.*;

/**
 * @author marottajb
 */
public class PlotPropertiesPanelController {

    @FXML ChoiceBox<String> isotopeSystemChoiceBox;
    @FXML TextField titleTextField, xAxisTextField, yAxisTextField;

    // Data Points
    @FXML CheckBox pointsCheckBox, ellipsesCheckBox, crossesCheckBox;
    @FXML ColorPicker pointsColorPicker, ellipsesColorPicker, crossesColorPicker;

    // Common Features
    @FXML GridPane featuresGridPane;
    // McLean Regression
    @FXML CheckBox mcLeanRegressionCheckBox;
    @FXML ColorPicker mcLeanRegressionColorPicker;

    private static Map<String, IsotopeType> STRING_TO_ISOTOPE_TYPE;
    private static Map<String, Object> PROPERTIES;
    static {
        STRING_TO_ISOTOPE_TYPE = new LinkedHashMap<>();
        for (IsotopeType type : IsotopeType.ISOTOPE_TYPES) {
            STRING_TO_ISOTOPE_TYPE.put(type.getName(), type);
        }
        PROPERTIES = new BasePlotDefaultProperties();
    }

    private Plot plot;
    private Node thisNode;

    @FXML
    public void initialize() {
        // TODO Add support for McLean Regression
        featuresGridPane.getChildren().clear();

        // Populate isotope system choice box
        for (String s : STRING_TO_ISOTOPE_TYPE.keySet()) {
            isotopeSystemChoiceBox.getItems().add(s);
        }

        /*
         * Set default Base Plot properties.
         */
        isotopeSystemChoiceBox.getSelectionModel().select(STRING_TO_ISOTOPE_TYPE.get((String) PROPERTIES.get(ISOTOPE_TYPE)).getName());

        titleTextField.setText((String) PROPERTIES.get(TITLE));
        xAxisTextField.setText((String) PROPERTIES.get(X_AXIS));
        yAxisTextField.setText((String) PROPERTIES.get(Y_AXIS));

        pointsCheckBox.setSelected((Boolean) PROPERTIES.get(POINTS));
        // TODO Enable point toggling
        pointsCheckBox.setDisable(true);
        ellipsesCheckBox.setSelected((Boolean) PROPERTIES.get(ELLIPSES));
//        crossesCheckBox.setSelected((Boolean) PROPERTIES.get(CROSSES));
        // TODO Implement Crosses
        crossesCheckBox.setVisible(false);


        pointsColorPicker.setValue(Color.valueOf((String) PROPERTIES.get(POINT_FILL_COLOR)));
        ellipsesColorPicker.setValue(Color.valueOf((String) PROPERTIES.get(ELLIPSE_FILL_COLOR)));
//        crossesColorPicker.setValue(Color.valueOf((String) PROPERTIES.get(CROSS_FILL_COLOR)));
        // TODO Implement Crosses
        crossesColorPicker.setVisible(false);


        // Make sure properties are initialized
        isotopeSystemChoiceBox.getSelectionModel().selectedItemProperty().addListener(c -> {
            if (getIsotopeType() != STRING_TO_ISOTOPE_TYPE.get(isotopeSystemChoiceBox.getSelectionModel().getSelectedItem())) {
                isotopeTypeObjectProperty().set(STRING_TO_ISOTOPE_TYPE.get(isotopeSystemChoiceBox.getSelectionModel().getSelectedItem()));
            }
        });
        isotopeTypeObjectProperty().addListener(c -> {
            if (STRING_TO_ISOTOPE_TYPE.get(isotopeSystemChoiceBox.getSelectionModel().getSelectedItem()) != getIsotopeType()) {
                for (String s : isotopeSystemChoiceBox.getItems()) {
                    if (s.equals(isotopeTypeObjectProperty.get().getName())) {
                        isotopeSystemChoiceBox.getSelectionModel().select(s);
                        break;
                    }
                }
            }
        });

        // Automatically adjust PROPERTIES
        isotopeTypeObjectProperty().addListener(c -> {
            PROPERTIES.put(ISOTOPE_TYPE, isotopeTypeObjectProperty.get().getName());
            updateProperties();
        });
        titleProperty().addListener(c -> {
            PROPERTIES.put(TITLE, titleProperty.get());
            updateProperties();
        });
        xAxisTitleProperty().addListener(c -> {
            PROPERTIES.put(X_AXIS, xAxisTitleProperty.get());
            updateProperties();
        });
        yAxisTitleProperty().addListener(c -> {
            PROPERTIES.put(Y_AXIS, yAxisTitleProperty.get());
            updateProperties();
        });

        showPointsProperty().addListener(c -> {
            PROPERTIES.put(POINTS, showPointsProperty.get());
            updateProperties();
        });
        showEllipsesProperty().addListener(c -> {
            PROPERTIES.put(ELLIPSES, showEllipsesProperty.get());
            updateProperties();
        });
//        showCrossesProperty().addListener(c -> {
//            PROPERTIES.put(CROSSES, showCrossesProperty.get());
//            updateProperties();
//        });

        pointsColorProperty().addListener(c -> {
            PROPERTIES.put(POINT_FILL_COLOR, convertColor(pointsColorProperty.get()));
            updateProperties();
        });
        ellipsesColorProperty().addListener(c -> {
            PROPERTIES.put(ELLIPSE_FILL_COLOR, convertColor(ellipsesColorProperty.get()));
            updateProperties();
        });
//        crossesColorProperty().addListener(c -> {
//            PROPERTIES.put(CROSS_FILL_COLOR, crossesColorProperty.get());
//            updateProperties();
//        });

    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }

    public void removePlot() {
        this.plot = null;
    }

    private void updateProperties() {
        if (plot != null) {
            plot.setProperties(PROPERTIES);
        }
    }



    private ObjectProperty<IsotopeType> isotopeTypeObjectProperty;
    public IsotopeType getIsotopeType() {
        return isotopeTypeObjectProperty().get();
    }
    public void setIsotopeType(IsotopeType isotopeType) {
        isotopeTypeObjectProperty.set(isotopeType);
    }
    public ObjectProperty<IsotopeType> isotopeTypeObjectProperty() {
        if (isotopeTypeObjectProperty == null) {
            isotopeTypeObjectProperty = new SimpleObjectProperty<>(this, "isotopeTypeObjectProperty",
                                                                   STRING_TO_ISOTOPE_TYPE.get(isotopeSystemChoiceBox
                                                                                                      .getSelectionModel().getSelectedItem()));
        }
        return isotopeTypeObjectProperty;
    }

//    private StringProperty isotopeTypeStringProperty;
//    public String getIsotopeTypeName() {
//        return isotopeTypeStringProperty().get();
//    }
//    public StringProperty isotopeTypeStringProperty() {
//        if (isotopeTypeStringProperty == null) {
//            isotopeTypeStringProperty = new SimpleStringProperty(this, "isotopeTypeStringProperty",
//                                                                 isotopeSystemChoiceBox.getSelectionModel().getSelectedItem());
//            isotopeTypeStringProperty.bind(isotopeSystemChoiceBox.getSelectionModel().selectedItemProperty());
//        }
//        return isotopeTypeStringProperty;
//    }

    private StringProperty titleProperty;
    public String getTitle() {
        return titleProperty().get();
    }
    public void setTitle(String s) {
        titleTextField.setText(s);
    }
    public StringProperty titleProperty() {
        if (titleProperty == null) {
            titleProperty = new SimpleStringProperty(this, "titleProperty", titleTextField.getText());
            titleProperty.bind(titleTextField.textProperty());
        }
        return titleProperty;
    }

    private StringProperty xAxisTitleProperty;
    public String getxAxisTitle() {
        return xAxisTitleProperty().get();
    }
    public void setxAxisTitle(String s) {
        xAxisTextField.setText(s);
    }
    public StringProperty xAxisTitleProperty() {
        if (xAxisTitleProperty == null) {
            xAxisTitleProperty = new SimpleStringProperty(this, "xAxisTitleProperty", xAxisTextField.getText());
            xAxisTitleProperty.bind(xAxisTextField.textProperty());
        }
        return xAxisTitleProperty;
    }

    private StringProperty yAxisTitleProperty;
    public String getyAxisTitle() {
        return yAxisTitleProperty().get();
    }
    public void setyAxisTitle(String s) {
        yAxisTextField.setText(s);
    }
    public StringProperty yAxisTitleProperty() {
        if (yAxisTitleProperty == null) {
            yAxisTitleProperty = new SimpleStringProperty(this, "yAxisTitleProperty", yAxisTextField.getText());
            yAxisTitleProperty.bind(yAxisTextField.textProperty());
        }
        return yAxisTitleProperty;
    }

    private BooleanProperty showPointsProperty;
    public Boolean shouldShowPoints() {
        return showPointsProperty().get();
    }
    public void setShowPoints(Boolean b) {
        pointsCheckBox.setSelected(b);
    }
    public BooleanProperty showPointsProperty() {
        if (showPointsProperty == null) {
            showPointsProperty = new SimpleBooleanProperty(this, "showPointsProperty", pointsCheckBox.isSelected());
            showPointsProperty.bind(pointsCheckBox.selectedProperty());
        }
        return showPointsProperty;
    }

    private BooleanProperty showEllipsesProperty;
    public Boolean shouldShowEllipses() {
        return showEllipsesProperty().get();
    }
    public void setshowEllipses(Boolean b) {
        ellipsesCheckBox.setSelected(b);
    }
    public BooleanProperty showEllipsesProperty() {
        if (showEllipsesProperty == null) {
            showEllipsesProperty = new SimpleBooleanProperty(this, "showEllipsesProperty", ellipsesCheckBox.isSelected());
            showEllipsesProperty.bind(ellipsesCheckBox.selectedProperty());
        }
        return showEllipsesProperty;
    }

    private BooleanProperty showCrossesProperty;
    public Boolean shouldShowCrosses() {
        return showCrossesProperty().get();
    }
    public void setshowCrosses(Boolean b) {
        crossesCheckBox.setSelected(b);
    }
    public BooleanProperty showCrossesProperty() {
        if (showCrossesProperty == null) {
            showCrossesProperty = new SimpleBooleanProperty(this, "showCrossesProperty", crossesCheckBox.isSelected());
            showCrossesProperty.bind(crossesCheckBox.selectedProperty());
        }
        return showCrossesProperty;
    }

    private ObjectProperty<Color> pointsColorProperty;
    public Color getPointsColor() {
        return pointsColorProperty().get();
    }
    public void setPointsColor(Color c) {
        pointsColorPicker.setValue(c);
    }
    public ObjectProperty<Color> pointsColorProperty() {
        if (pointsColorProperty == null) {
            pointsColorProperty = new SimpleObjectProperty<>(this, "pointsColorProperty", pointsColorPicker.getValue());
            pointsColorProperty.bind(pointsColorPicker.valueProperty());
        }
        return pointsColorProperty;
    }

    private ObjectProperty<Color> ellipsesColorProperty;
    public Color getEllipsesColor() {
        return ellipsesColorProperty().get();
    }
    public void setEllipsesColor(Color c) {
        ellipsesColorPicker.setValue(c);
    }
    public ObjectProperty<Color> ellipsesColorProperty() {
        if (ellipsesColorProperty == null) {
            ellipsesColorProperty = new SimpleObjectProperty<>(this, "ellipsesColorProperty", ellipsesColorPicker
                    .getValue());
            ellipsesColorProperty.bind(ellipsesColorPicker.valueProperty());
        }
        return ellipsesColorProperty;
    }

    private ObjectProperty<Color> crossesColorProperty;
    public Color getCrossesColor() {
        return crossesColorProperty().get();
    }
    public void setCrossesColor(Color c) {
        crossesColorPicker.setValue(c);
    }
    public ObjectProperty<Color> crossesColorProperty() {
        if (crossesColorProperty == null) {
            crossesColorProperty = new SimpleObjectProperty<>(this, "crossesColorProperty", crossesColorPicker
                    .getValue());
            crossesColorProperty.bind(crossesColorPicker.valueProperty());
        }
        return crossesColorProperty;
    }

    private String convertColor(Color c) {
        String s = c.toString();
        return s.substring(0, s.length() - 2).replaceAll("0x", "#");
    }

    public Map<String, Object> getProperties() {
        PROPERTIES.put(TITLE, titleProperty.get());
        PROPERTIES.put(X_AXIS, xAxisTitleProperty.get());
        PROPERTIES.put(Y_AXIS, yAxisTitleProperty.get());
        PROPERTIES.put(POINTS, showPointsProperty.get());
        PROPERTIES.put(ELLIPSES, showEllipsesProperty.get());
//        PROPERTIES.put(CROSSES, showCrossesProperty.get());
        PROPERTIES.put(POINT_FILL_COLOR, convertColor(pointsColorProperty.get()));
        PROPERTIES.put(ELLIPSE_FILL_COLOR, convertColor(ellipsesColorProperty.get()));
//        PROPERTIES.put(CROSS_FILL_COLOR, convertColor(crossesColorProperty.get()));
        PROPERTIES.put(ISOTOPE_TYPE, isotopeTypeObjectProperty.get().getName());

        return PROPERTIES;
    }

    public void setProperties(Map<String, Object> plotProperties) {
        if (plotProperties.containsKey(TITLE)) setTitle((String) plotProperties.get(TITLE));
        if (plotProperties.containsKey(X_AXIS)) setxAxisTitle((String) plotProperties.get(X_AXIS));
        if (plotProperties.containsKey(Y_AXIS)) setyAxisTitle((String) plotProperties.get(Y_AXIS));
        if (plotProperties.containsKey(POINTS)) setShowPoints((Boolean) plotProperties.get(POINTS));
        if (plotProperties.containsKey(ELLIPSES)) setshowEllipses((Boolean) plotProperties.get(ELLIPSES));
//        if (plotProperties.containsKey(CROSSES)) setshowCrosses((Boolean) plotProperties.get(CROSSES));
        if (plotProperties.containsKey(POINT_FILL_COLOR)) setPointsColor(Color.valueOf((String) plotProperties.get(POINT_FILL_COLOR)));
        if (plotProperties.containsKey(ELLIPSE_FILL_COLOR)) setEllipsesColor(Color.valueOf((String) plotProperties.get(ELLIPSE_FILL_COLOR)));
//        if (plotProperties.containsKey(CROSS_FILL_COLOR)) setCrossesColor(Color.valueOf((String) plotProperties.get(CROSS_FILL_COLOR)));
        if (plotProperties.containsKey(ISOTOPE_TYPE)) setIsotopeType(STRING_TO_ISOTOPE_TYPE.get((String) plotProperties.get(ISOTOPE_TYPE)));
    }

}
