package org.cirdles.topsoil.app.util.dialog.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;

import java.io.IOException;
import java.util.*;

/**
 * Controller for a screen that allows the user to preview their imported data, as well as choose an {@link
 * UncertaintyFormat} and {@link IsotopeType} for each table.
 *
 * @author marottajb
 */
public class DataPreviewController extends VBox {

    private static final String DATA_PREVIEW_FXML = "data-preview.fxml";
    private static final String WARNING_ICON_PATH = "warning.png";
    private final ResourceExtractor resourceExtractor = new ResourceExtractor(DataPreviewController.class);

    @FXML private GridPane grid;
    @FXML private Label uncLabel;
    @FXML private ComboBox<UncertaintyFormat> unctComboBox;
    @FXML private ComboBox<IsotopeType> isoComboBox;

    private List<ComboBox<Variable<Number>>> columnComboBoxes;

    private Map<Variable<Number>, Integer> variableIndexMap;

    private String[] headers;
    private Double[][] data;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private ObjectProperty<UncertaintyFormat> uncertaintyFormat;
    public ObjectProperty<UncertaintyFormat> uncertaintyFormatProperty() {
        if (uncertaintyFormat == null) {
            uncertaintyFormat = new SimpleObjectProperty<>();
            uncertaintyFormat.bind(unctComboBox.getSelectionModel().selectedItemProperty());
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

    private ObjectProperty<IsotopeType> isotopeType;
    public ObjectProperty<IsotopeType> isotopeTypeProperty() {
        if (isotopeType == null) {
            isotopeType = new SimpleObjectProperty<>();
            isotopeType.bind(isoComboBox.getSelectionModel().selectedItemProperty());
        }
        return isotopeType;
    }
    /**
     * Returns the selected {@code IsotopeType}, as indicated by the isotope type {@code ChoiceBox}.
     *
     * @return  selected IsotopeType
     */
    public IsotopeType getIsotopeType() {
        return isotopeTypeProperty().get() == null ? IsotopeType.GENERIC : isotopeTypeProperty().get();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public DataPreviewController(String[] hs, Double[][] d) {
        super();

        this.data = d;

        if (hs == null) {
            int maxRowSize = 0;
            for (Double[] row : data) {
                maxRowSize = Math.max(maxRowSize, row.length);
            }

            this.headers = new String[maxRowSize];
            for (int i = 1; i <= headers.length; i++) {
                headers[i - 1] = "[Column " + i + "]";
            }
        } else {
            this.headers = new String[hs.length];
	        System.arraycopy(hs, 0, headers, 0, hs.length);
        }

        try {
            FXMLLoader loader = new FXMLLoader(resourceExtractor.extractResourceAsPath(DATA_PREVIEW_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + DATA_PREVIEW_FXML, e);
        }
    }
    @FXML
    protected void initialize() {
        columnComboBoxes = new ArrayList<>();
        variableIndexMap = new LinkedHashMap<>(Variables.VARIABLE_LIST.size());

        ImageView warningIcon = new ImageView(resourceExtractor.extractResourceAsPath(WARNING_ICON_PATH).toUri().toString());
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

        unctComboBox.getItems().addAll(UncertaintyFormat.values());
        isoComboBox.getItems().addAll(IsotopeType.values());
        isoComboBox.getSelectionModel().select(IsotopeType.GENERIC);

        makeGrid();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Returns the header rows for the table.
     *
     * @return  List of String header rows
     */
    public String[] getHeaders() {
        return headers;
    }

    /**
     * Returns the data rows for the table.
     *
     * @return  List of Double data rows
     */
    public Double[][] getData() {
        return data;
    }

    public Map<Variable<Number>, Integer> getVariableIndexMap() {
        return variableIndexMap;
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    /**
     * Checks all {@code ComboBox}es other than the one that triggered this method to see if they contain the value
     * that the recently changed {@code ComboBox} now contains. If another {@code ComboBox} with the same value is
     * found, that {@code ComboBox}'s value is set to the empty option.
     *
     * @param   changed
     *          the ComboBox that was changed
     * @param   value
     *          the Variable value of the ComboBox's new selection
     */
    private void checkOtherComboBoxes(ComboBox<Variable<Number>> changed, Variable<Number> value) {
        for (ComboBox<Variable<Number>> cb : columnComboBoxes) {
            if (cb != changed) {
                if (cb.getValue() != null && cb.getValue().equals(value)) {
                    cb.getSelectionModel().select(null);
                }
            }
        }
    }

    private void makeGrid() {
        final int SAMPLE_SIZE = 5;

        grid.setMinSize(110.0 * headers.length, 202.0);
        grid.setMaxSize(125.0 * headers.length, 202.0);

        Double[][] sampleLines = Arrays.copyOfRange(data, 0, Math.min(data.length, SAMPLE_SIZE));

        // Create each column of the data preview.
        // ChoiceBox for the user to select which variable the column represents.
        for (int i = 0; i < headers.length; i++) {
            ComboBox<Variable<Number>> comboBox = new ComboBox<>();
            comboBox.getItems().addAll(Variables.VARIABLE_LIST);
            comboBox.setMinSize(100.0, 30.0);
            comboBox.setMaxSize(100.0, 30.0);
            comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    checkOtherComboBoxes(comboBox, newValue);
                    variableIndexMap.put(newValue, columnComboBoxes.indexOf(comboBox));
                } else {
                    variableIndexMap.remove(oldValue);
                }
            });

            grid.add(comboBox, i, 0);
            GridPane.setConstraints(comboBox, i, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority
                    .NEVER, new Insets(5.0, 5.0, 5.0, 5.0));
            columnComboBoxes.add(comboBox);
        }

        Label label;
        // Set header row
        for (int i = 0; i < headers.length; i++) {
            label = new Label(headers[i]);
            label.setStyle("-fx-font-weight: bold");
            label.setMinSize(100.0, 17.0);
            label.setMaxSize(100.0, 17.0);
            grid.add(label, i, 1);
            GridPane.setConstraints(label, i, 1, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER,
                                    new Insets(5.0, 5.0, 5.0, 5.0));
        }

        // Set data rows
        int rowIndex;
        for (int i = 0; i < sampleLines.length; i++) {
        	rowIndex = i + 2;
            for (int j = 0; j < headers.length; j++) {
                if (i >= sampleLines[i].length) {
                    label = new Label("0.0");
                } else {
                    label = new Label(sampleLines[i][j].toString());
                }
                label.setFont(Font.font("Monospaced"));
                label.setMinSize(100.0, 17.0);
                label.setMaxSize(100.0, 17.0);
                grid.add(label, j, rowIndex);
                GridPane.setConstraints(label, j, rowIndex, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS,
                                        Priority.NEVER, new Insets(5.0, 5.0, 5.0, 5.0));
            }
        }
    }
}
