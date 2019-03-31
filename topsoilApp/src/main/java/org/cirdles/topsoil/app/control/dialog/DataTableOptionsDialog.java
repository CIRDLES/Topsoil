package org.cirdles.topsoil.app.control.dialog;

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
import org.cirdles.topsoil.app.data.column.DataCategory;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.cirdles.topsoil.app.util.ResourceBundles;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.variable.Variable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author marottajb
 */
public class DataTableOptionsDialog extends Dialog<Boolean> {

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
                for (Map.Entry<DataComponent, Boolean> entry : controller.getColumnSelections().entrySet()) {
                    entry.getKey().setSelected(entry.getValue());
                }
                table.setColumnsForAllVariables(controller.getVariableAssignments());
                table.setIsotopeSystem(controller.getIsotopeSystem());
                table.setUncertainty(controller.getUncertainty());
                ProjectManager.updatePlotsForTable(table);
                return true;
            }
            return false;
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
    public static Boolean showDialog(DataTable table, Stage owner) {
        return new DataTableOptionsDialog(table, owner).showAndWait().orElse(null);
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

        @FXML private Label uncertaintyLabel, isotopeSystemLabel;
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
            uncertaintyLabel.setText(resources.getString("uncertaintyLabel"));
            isotopeSystemLabel.setText(resources.getString("isotopeSystemLabel"));

            this.columnTreeView = new ColumnTreeView(table.getColumnRoot());
            columnTreeViewPane.getChildren().add(columnTreeView);

            this.variableChooser = new VariableChooser(table);
            variableChooserPane.getChildren().add(variableChooser);

            listenToTreeItemChildren(columnTreeView.getRoot(), variableChooser);

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

}
