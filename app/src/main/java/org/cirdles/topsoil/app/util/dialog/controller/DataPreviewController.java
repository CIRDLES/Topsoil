package org.cirdles.topsoil.app.util.dialog.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.table.uncertainty.UncertaintyFormat;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for a screen that allows the user to preview their imported data, as well as choose an {@link
 * UncertaintyFormat} and {@link IsotopeType} for each table.
 *
 * @author Jake Marotta
 */
public class DataPreviewController extends Pane {

    @FXML private GridPane grid;

    @FXML private Label uncLabel;
    @FXML private ComboBox<String> uncComboBox;
    private ObjectProperty<UncertaintyFormat> uncertaintyFormat;
    public ObjectProperty<UncertaintyFormat> uncertaintyFormatProperty() {
        if (uncertaintyFormat == null) {
            uncertaintyFormat = new SimpleObjectProperty<>(STRING_UNCERTAINTY_FORMAT_MAP.get(uncComboBox
                                                                                                     .getSelectionModel().getSelectedItem()));
            uncComboBox.getSelectionModel().selectedItemProperty().addListener(c -> {
                uncertaintyFormat.set(STRING_UNCERTAINTY_FORMAT_MAP.get(uncComboBox.getSelectionModel().getSelectedItem()));
            });
        }
        return uncertaintyFormat;
    }

    /**
     * Returns the selected {@code UncertaintyFormat}, as indicated by the uncertainty format {@code ChoiceBox}.
     *
     * @return  selected UncertaintyFormat
     */
    public UncertaintyFormat getUncertaintyFormat() {
        return uncertaintyFormatProperty().get();
    }

    @FXML private ChoiceBox<String> isoChoiceBox;
    private ObjectProperty<IsotopeType> isotopeType;
    public ObjectProperty<IsotopeType> isotopeTypeProperty() {
        if (isotopeType == null) {
            isotopeType = new SimpleObjectProperty<>(STRING_ISOTOPE_TYPE_MAP.get(isoChoiceBox.getSelectionModel()
                                                                                             .getSelectedItem()));
            isoChoiceBox.getSelectionModel().selectedItemProperty().addListener(c -> {
                isotopeType.set(STRING_ISOTOPE_TYPE_MAP.get(isoChoiceBox.getSelectionModel().getSelectedItem()));
            });
        }
        return isotopeType;
    }
    /**
     * Returns the selected {@code IsotopeType}, as indicated by the isotope type {@code ChoiceBox}.
     *
     * @return  selected IsotopeType
     */
    public IsotopeType getIsotopeType() {
        return isotopeTypeProperty().get() == null ? IsotopeType.Generic : isotopeTypeProperty().get();
    }

    private List<ChoiceBox<String>> columnChoiceBoxes;
    private static final String EMPTY_VALUE = "  ";

    private Map<Variable<Number>, Integer> variableIndexMap;

    private String[] headers;
    private List<TopsoilDataEntry> data;

    // A series of maps for getting values from ChoiceBoxes
    private static final Map<String, Variable<Number>> STRING_VARIABLE_MAP;
    private static final Map<String, UncertaintyFormat> STRING_UNCERTAINTY_FORMAT_MAP;
    private static final Map<String, IsotopeType> STRING_ISOTOPE_TYPE_MAP;
    static {
        STRING_VARIABLE_MAP = new LinkedHashMap<>();
        for (Variable<Number> v : Variables.VARIABLE_LIST) {
            STRING_VARIABLE_MAP.put(v.getName(), v);
        }

        STRING_UNCERTAINTY_FORMAT_MAP = new LinkedHashMap<>();
        for (UncertaintyFormat uf : UncertaintyFormat.ALL) {
            STRING_UNCERTAINTY_FORMAT_MAP.put(uf.getName(), uf);
        }

        STRING_ISOTOPE_TYPE_MAP = new LinkedHashMap<>();
        for (IsotopeType it : IsotopeType.values()) {
            STRING_ISOTOPE_TYPE_MAP.put(it.getName(), it);
        }
    }

    private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(DataPreviewController.class);
    private static final String WARNING_ICON_PATH = "warning.png";

    @FXML
    public void initialize() {
        columnChoiceBoxes = new ArrayList<>();
        variableIndexMap = new LinkedHashMap<>(Variables.VARIABLE_LIST.size());

        ImageView warningIcon = new ImageView(RESOURCE_EXTRACTOR.extractResourceAsPath(WARNING_ICON_PATH).toUri().toString());
        warningIcon.setPreserveRatio(true);
        warningIcon.setFitHeight(25.0);

        if (getUncertaintyFormat() == null) {
            uncLabel.setGraphic(warningIcon);
        }
        uncertaintyFormatProperty().addListener(c -> {
            if (getUncertaintyFormat() == null) {
                uncLabel.setGraphic(warningIcon);
            } else {
                uncLabel.setGraphic(null);
            }
        });

        uncComboBox.getItems().addAll(STRING_UNCERTAINTY_FORMAT_MAP.keySet());
        isoChoiceBox.getItems().addAll(STRING_ISOTOPE_TYPE_MAP.keySet());
        isoChoiceBox.getSelectionModel().select(IsotopeType.Generic.getName());
    }

    /**
     * Sets the data in the preview to the specified list of {@code TopsoilDataEntries}. If no array of {@code
     * String} headers is provided, default headers are used.
     *
     * @param h array of String column headers
     * @param d list of TopsoilDataEntries
     */
    public void setData(@Nullable String[] h, List<TopsoilDataEntry> d) {

        this.data = d;

        if (h == null) {
            int maxRowSize = 0;
            for (TopsoilDataEntry row : data) {
                maxRowSize = Math.max(maxRowSize, row.getProperties().size());
            }
            headers = new String[maxRowSize];
            for (int i = 1; i <= headers.length; i++) {
                headers[i - 1] = "Column " + i;
            }
        } else {
            headers = new String[h.length];
            System.arraycopy(h, 0, headers, 0, h.length);
        }

        grid.setMinSize(110.0 * headers.length, 202.0);
        grid.setMaxSize(125.0 * headers.length, 202.0);

        // Sample is <= 5
        List<TopsoilDataEntry> sampleLines = data.subList(0, Math.min(data.size() - 1, 5));

        // Create each column of the data preview.
        Label label;
        for (int i = 0; i < headers.length; i++) {

            // ChoiceBox for the user to select which variable the column represents.
            ChoiceBox<String> choiceBox = new ChoiceBox<>();
            choiceBox.getItems().add(EMPTY_VALUE);
            choiceBox.getItems().addAll(STRING_VARIABLE_MAP.keySet());
            choiceBox.getSelectionModel().select(EMPTY_VALUE);
            choiceBox.setMinSize(100.0, 30.0);
            choiceBox.setMaxSize(100.0, 30.0);
            choiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {

                if (!newValue.equals(EMPTY_VALUE)) {
                    checkOtherChoiceBoxes(choiceBox, STRING_VARIABLE_MAP.get(newValue));
                    variableIndexMap.put(STRING_VARIABLE_MAP.get(newValue), columnChoiceBoxes.indexOf(choiceBox));
                } else {
                    variableIndexMap.remove(STRING_VARIABLE_MAP.get(oldValue));
                }
            });
            grid.add(choiceBox, i, 0);
            GridPane.setConstraints(choiceBox, i, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority
                    .NEVER, new Insets(5.0, 5.0, 5.0, 5.0));
            this.columnChoiceBoxes.add(choiceBox);

            label = new Label(headers[i]);
            label.setStyle("-fx-font-weight: bold");
            label.setMinSize(100.0, 17.0);
            label.setMaxSize(100.0, 17.0);
            grid.add(label, i, 1);
            GridPane.setConstraints(label, i, 1, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER, new Insets(5.0, 5.0, 5.0, 5.0));

            for (int j = 0; j < sampleLines.size(); j++) {
                if (i >= sampleLines.get(j).getProperties().size()) {
                    label = new Label("0.0");
                } else {
                    label = new Label(sampleLines.get(j).getProperties().get(i).getValue().toString());
                }
                label.setFont(Font.font("Monospaced"));
                label.setMinSize(100.0, 17.0);
                label.setMaxSize(100.0, 17.0);
                grid.add(label, i, j + 2);
                GridPane.setConstraints(label, i, j + 2, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority
                        .NEVER, new Insets(5.0, 5.0, 5.0, 5.0));
            }
        }
    }

    /**
     * Returns an array of {@code String} headers for the data columns.
     *
     * @return  array of Strings
     */
    public String[] getHeaders() {
        String[] headersCopy = new String[headers.length];
        System.arraycopy(headers, 0, headersCopy, 0, headers.length);
        return headersCopy;
    }

    /**
     * Returns a {@code List} of {@code TopsoilDataEntry}s containing the imported data values.
     *
     * @return  List of TopsoilDataEntry
     */
    public List<TopsoilDataEntry> getData() {
        // TODO
        return data;
//        List<TopsoilDataEntry> selectedColumns = new ArrayList<>(data.size());
//
//        // Construct new set of entries based on the selected columns.
//        for (int i = 0; i < data.size(); i++) {
//
//            // Add a TopsoilDataEntry to selection if it doesn't have one at this index.
//            if (selectedColumns.size() <= i) {
//                while(selectedColumns.size() <= i) {
//                    selectedColumns.add(new TopsoilDataEntry());
//                }
//            }
//
//            DoubleProperty xValue = variableIndexMap.containsKey(Variables.X) ? data.get(i).getProperties().get(variableIndexMap.get(Variables.X))
//                    : new SimpleDoubleProperty(0.0);
//            selectedColumns.get(i).add(xValue);
//
//            DoubleProperty yValue = variableIndexMap.containsKey(Variables.Y) ? data.get(i).getProperties().get(variableIndexMap.get(Variables.Y))
//                    : new SimpleDoubleProperty(0.0);
//            selectedColumns.get(i).add(yValue);
//
//            DoubleProperty sxValue = variableIndexMap.containsKey(Variables.SIGMA_X) ? data.get(i).getProperties().get(variableIndexMap.get(Variables.SIGMA_X))
//                    : new SimpleDoubleProperty(0.0);
//            selectedColumns.get(i).add(sxValue);
//
//            DoubleProperty syValue = variableIndexMap.containsKey(Variables.SIGMA_Y) ? data.get(i).getProperties().get(variableIndexMap.get(Variables.SIGMA_Y))
//                    : new SimpleDoubleProperty(0.0);
//            selectedColumns.get(i).add(syValue);
//
//            DoubleProperty rValue = variableIndexMap.containsKey(Variables.RHO) ? data.get(i).getProperties().get(variableIndexMap.get(Variables.RHO))
//                    : new SimpleDoubleProperty(0.0);
//            selectedColumns.get(i).add(rValue);
//        }
//        return selectedColumns;
    }

    public Map<Variable<Number>, Integer> getVariableIndexMap() {
        return variableIndexMap;
    }

    /**
     * Checks all {@code ChoiceBox}es other than the one that triggered this method to see if they contain the value
     * that the recently changed {@code ChoiceBox} now contains. If another {@code ChoiceBox} with the same value is
     * found, that {@code ChoiceBox}'s value is set to the empty option.
     *
     * @param changed   the ChoiceBox that was changed
     * @param value the Variable value of the ChoiceBox's new selection
     */
    private void checkOtherChoiceBoxes(ChoiceBox changed, Variable<Number> value) {
        for (ChoiceBox<String> cb : columnChoiceBoxes) {
            if (cb != changed) {
                if (STRING_VARIABLE_MAP.get(cb.getValue()) == value) {
                    cb.getSelectionModel().select(EMPTY_VALUE);
                }
            }
        }
    }

}
