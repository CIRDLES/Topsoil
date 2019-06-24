package org.cirdles.topsoil.app.control.dialog;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.data.ColumnTreeView;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.ResourceBundles;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.app.data.FXDataColumn;
import org.cirdles.topsoil.app.data.FXDataTable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author marottajb
 */
public class DataTableOptionsDialog extends Dialog<Map<DataTableOptionsDialog.Key, Object>> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private DataTableOptionsDialog(FXDataTable table, Stage owner) {
        this.setTitle(ResourceBundles.DIALOGS.getString("optionsTitle") + " " + table.getTitle());
        this.initOwner(owner);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().addAll(Topsoil.getLogo());
        stage.setOnShown(event -> stage.requestFocus());

        DataTableOptionsView controller = new DataTableOptionsView(table);
        this.getDialogPane().setContent(controller);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        this.setResultConverter(value -> {
            if (value == ButtonType.OK) {
                Map<Key, Object> settings = new HashMap<>();
                settings.put(Key.COLUMN_SELECTIONS, controller.getColumnSelections());
                settings.put(Key.FRACTION_DIGITS, controller.getFractionDigits());
                settings.put(Key.SCIENTIFIC_NOTATION, controller.getScientificNotation());
                settings.put(Key.UNCERTAINTY, controller.getUncertainty());
                return settings;
            }
            return null;
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Displays a dialog with controls for the user to modify current data table settings.
     *
     * @param table     DataTable
     * @param owner     Stage owner of this dialog
     *
     * @return          true if changes saved
     */
    public static Map<Key, Object> showDialog(FXDataTable table, Stage owner) {
        return new DataTableOptionsDialog(table, owner).showAndWait().orElse(null);
    }

    public static void applySettings(FXDataTable table, Map<Key, Object> settings) {
        // Column selections
        Map<FXDataColumn<?>, Boolean> columnSelections =
                (Map<FXDataColumn<?>, Boolean>) settings.get(Key.COLUMN_SELECTIONS);
        for (Map.Entry<FXDataColumn<?>, Boolean> entry : columnSelections.entrySet()) {
            entry.getKey().setSelected(entry.getValue());
        }

        // Fraction Digits
        int maxFractionDigits = (int) settings.get(Key.FRACTION_DIGITS);
        table.setMaxFractionDigits(maxFractionDigits);

        // Scientific Notation
        boolean scientificNotation = (boolean) settings.get(Key.SCIENTIFIC_NOTATION);
        table.setScientificNotation(scientificNotation);

        // Uncertainty
        Uncertainty uncertainty = (Uncertainty) settings.get(Key.UNCERTAINTY);
        table.setUncertainty(uncertainty);
    }

    /**
     * Controller for a screen that allows the user to preview their imported model, as well as choose an {@link
     * Uncertainty} and {@link IsotopeSystem} for each table.
     *
     * @author marottajb
     */
    public static class DataTableOptionsView extends GridPane {

        //**********************************************//
        //                  CONSTANTS                   //
        //**********************************************//

        private static final String CONTROLLER_FXML = "data-table-options.fxml";

        //**********************************************//
        //                   CONTROLS                   //
        //**********************************************//

        @FXML private Label columnTreeViewLabel;
        @FXML private VBox columnTreeViewPane;
        ColumnTreeView columnTreeView;

        @FXML private Label fractionDigitsLabel, uncertaintyLabel;
        @FXML CheckBox fractionDigitsCheckBox, scientificNotationCheckBox;
        @FXML ComboBox<Integer> fractionDigitsComboBox;
        @FXML ComboBox<Uncertainty> unctComboBox;

        //**********************************************//
        //                  ATTRIBUTES                  //
        //**********************************************//

        private FXDataTable table;

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        public DataTableOptionsView(FXDataTable table) {
            super();
            this.table = table;
            try {
                FXMLUtils.loadController(CONTROLLER_FXML, DataTableOptionsView.class, this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @FXML
        protected void initialize() {
            ResourceBundle resources = ResourceBundles.DIALOGS.getBundle();
            columnTreeViewLabel.setText(resources.getString("columnTreeLabel"));
            fractionDigitsLabel.setText(resources.getString("fractionDigitsLabel"));
            uncertaintyLabel.setText(resources.getString("uncertaintyLabel"));

            this.columnTreeView = new ColumnTreeView(table);
            columnTreeViewPane.getChildren().add(columnTreeView);

            // Configure other table options
            int maxFractionDigits = table.getMaxFractionDigits();
            for (int i = 0; i <= 12; i++) {
                fractionDigitsComboBox.getItems().add(i);
            }
            fractionDigitsComboBox.disableProperty().bind(Bindings.not(fractionDigitsCheckBox.selectedProperty()));
            if (maxFractionDigits != -1) {
                if (maxFractionDigits < 0) {
                    fractionDigitsComboBox.getSelectionModel().select(0);
                } else if (maxFractionDigits > 12) {
                    fractionDigitsComboBox.getSelectionModel().select(12);
                } else {
                    fractionDigitsComboBox.getSelectionModel().select(maxFractionDigits);
                }
                fractionDigitsCheckBox.setSelected(true);
            } else {
                fractionDigitsComboBox.getSelectionModel().select(9);
                fractionDigitsCheckBox.setSelected(false);
            }

            scientificNotationCheckBox.setSelected(table.isScientificNotation());

            unctComboBox.getItems().addAll(Uncertainty.values());
            unctComboBox.getSelectionModel().select(table.getUncertainty());
        }

        //**********************************************//
        //                PUBLIC METHODS                //
        //**********************************************//

        /**
         * Returns the column visibility selections in the variable chooser.
         *
         * @return  Map of DataComponent to Boolean values, true if column should be visible
         */
        public Map<FXDataColumn<?>, Boolean> getColumnSelections() {
            return columnTreeView.getColumnSelections();
        }

        public int getFractionDigits() {
            if (fractionDigitsCheckBox.isSelected()) {
                return fractionDigitsComboBox.getValue();
            } else {
                return -1;
            }
        }

        public boolean getScientificNotation() {
            return scientificNotationCheckBox.isSelected();
        }

        public Uncertainty getUncertainty() {
            return unctComboBox.getValue();
        }

    }

    public enum Key {
        COLUMN_SELECTIONS,
        FRACTION_DIGITS,
        SCIENTIFIC_NOTATION,
        UNCERTAINTY
    }

}
