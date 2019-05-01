package org.cirdles.topsoil.app.control.dialog;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.ProjectManager;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.tree.ColumnTreeView;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.ResourceBundles;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Uncertainty;
import org.cirdles.topsoil.variable.Variable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author marottajb
 */
public class DataTableOptionsDialog extends Dialog<Map<DataTableOptionsDialog.Key, Object>> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private DataTableOptionsDialog(DataTable table, Stage owner) {
        this.setTitle(ResourceBundles.DIALOGS.getString("optionsTitle") + " " + table.getLabel());
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
                settings.put(Key.VARIABLE_ASSOCIATIONS, controller.getVariableAssignments());
                settings.put(Key.COLUMN_SELECTIONS, controller.getColumnSelections());
                settings.put(Key.FRACTION_DIGITS, controller.getFractionDigits());
                settings.put(Key.ISOTOPE_SYSTEM, controller.getIsotopeSystem());
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
    public static Map<Key, Object> showDialog(DataTable table, Stage owner) {
        return new DataTableOptionsDialog(table, owner).showAndWait().orElse(null);
    }

    public static void applySettings(DataTable table, Map<Key, Object> settings) {
        // Variable assignments
        Map<Variable<?>, DataColumn<?>> variableAssignments =
                (Map<Variable<?>, DataColumn<?>>) settings.get(DataTableOptionsDialog.Key.VARIABLE_ASSOCIATIONS);
        table.setColumnsForAllVariables(variableAssignments);

        // Column selections
        Map<DataComponent, Boolean> columnSelections =
                (Map<DataComponent, Boolean>) settings.get(Key.COLUMN_SELECTIONS);
        for (Map.Entry<DataComponent, Boolean> entry : columnSelections.entrySet()) {
            entry.getKey().setSelected(entry.getValue());
        }

        // Fraction Digits
        int maxFractionDigits = (int) settings.get(Key.FRACTION_DIGITS);
        table.setMaxFractionDigits(maxFractionDigits);

        // Isotope system
        IsotopeSystem isotopeSystem = (IsotopeSystem) settings.get(Key.ISOTOPE_SYSTEM);
        table.setIsotopeSystem(isotopeSystem);

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

        @FXML private Label variableChooserLabel;
        @FXML private VBox variableChooserPane;
        VariableChooser variableChooser;

        @FXML private Label fractionDigitsLabel, uncertaintyLabel, isotopeSystemLabel;
        @FXML CheckBox fractionDigitsCheckBox;
        @FXML ComboBox<Integer> fractionDigitsComboBox;
        @FXML ComboBox<Uncertainty> unctComboBox;
        @FXML ComboBox<IsotopeSystem> isoComboBox;

        //**********************************************//
        //                  ATTRIBUTES                  //
        //**********************************************//

        private DataTable table;

        //**********************************************//
        //                 CONSTRUCTORS                 //
        //**********************************************//

        public DataTableOptionsView(DataTable table) {
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
            variableChooserLabel.setText(resources.getString("variableChooserLabel"));
            fractionDigitsLabel.setText(resources.getString("fractionDigitsLabel"));
            uncertaintyLabel.setText(resources.getString("uncertaintyLabel"));
            isotopeSystemLabel.setText(resources.getString("isotopeSystemLabel"));

            this.columnTreeView = new ColumnTreeView(table.getColumnRoot());
            columnTreeViewPane.getChildren().add(columnTreeView);

            this.variableChooser = new VariableChooser(table);
            variableChooserPane.getChildren().add(variableChooser);

            // Live edits between ColumnTreeView and VariableChooser
            listenToTreeItemChildren(columnTreeView.getRoot(), variableChooser);

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
            unctComboBox.getItems().addAll(Uncertainty.values());
            unctComboBox.getSelectionModel().select(table.getUncertainty());
            isoComboBox.getItems().addAll(IsotopeSystem.values());
            isoComboBox.getSelectionModel().select(table.getIsotopeSystem());
        }

        //**********************************************//
        //                PUBLIC METHODS                //
        //**********************************************//

        /**
         * Returns the column visibility selections in the variable chooser.
         *
         * @return  Map of DataComponent to Boolean values, true if column should be visible
         */
        public Map<DataComponent, Boolean> getColumnSelections() {
            return columnTreeView.getColumnSelections();
        }

        /**
         * Returns the variable/column associations in the variable chooser.
         *
         * @return  Map of Variable to DataColumn
         */
        public Map<Variable<?>, DataColumn<?>> getVariableAssignments() {
            return variableChooser.getSelections();
        }

        public int getFractionDigits() {
            if (fractionDigitsCheckBox.isSelected()) {
                return fractionDigitsComboBox.getValue();
            } else {
                return -1;
            }
        }

        public IsotopeSystem getIsotopeSystem() {
            return isoComboBox.getValue();
        }

        public Uncertainty getUncertainty() {
            return unctComboBox.getValue();
        }

        //**********************************************//
        //                PRIVATE METHODS               //
        //**********************************************//

        private void listenToTreeItemChildren(TreeItem<DataComponent> parent, VariableChooser chooser) {
            List<DataColumn<?>> columns = table.getDataColumns();
            for (TreeItem<DataComponent> treeItem : parent.getChildren()) {
                CheckBoxTreeItem<DataComponent> cBTreeItem = (CheckBoxTreeItem<DataComponent>) treeItem;
                if (cBTreeItem.getValue() instanceof DataColumn) {
                    DataColumn<?> dataColumn = (DataColumn<?>) cBTreeItem.getValue();
                    cBTreeItem.selectedProperty().bindBidirectional(
                            chooser.getLeafTableColumns().get(columns.indexOf(dataColumn)).visibleProperty()
                    );
                }
                if (cBTreeItem.getValue() instanceof DataCategory) {
                    listenToTreeItemChildren(cBTreeItem, chooser);
                }
            }
        }

    }

    public enum Key {
        VARIABLE_ASSOCIATIONS,
        COLUMN_SELECTIONS,
        FRACTION_DIGITS,
        ISOTOPE_SYSTEM,
        UNCERTAINTY
    }

}
