package org.cirdles.topsoil.app.control.dialog;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.control.tree.ColumnTreeView;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.uncertainty.Uncertainty;

import java.io.IOException;

import static org.cirdles.topsoil.app.control.wizards.NewProjectWizard.INIT_HEIGHT;
import static org.cirdles.topsoil.app.control.wizards.NewProjectWizard.INIT_WIDTH;

/**
 * @author marottajb
 */
public class DataTableOptionsDialog extends Dialog<Boolean> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    public static final double INIT_WIDTH = 600.0;
    public static final double INIT_HEIGHT = 550.0;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private DataTableOptionsDialog(DataTable table, Stage owner) {
        this.setTitle("Options: " + table.getLabel());
        this.initOwner(owner);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().addAll(Main.getController().getTopsoilLogo());

        DataTableOptionsView controller = new DataTableOptionsView(table);
        this.getDialogPane().setContent(controller);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        this.setResultConverter(value -> {
            if (value == ButtonType.OK) {
                table.setIsotopeSystem(controller.isoComboBox.getValue());
                table.setUnctFormat(controller.unctComboBox.getValue());
                return true;
            }
            return false;
        });


    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static Boolean showDialog(DataTable table, Stage owner) {
        return new DataTableOptionsDialog(table, owner).showAndWait().orElse(null);
    }

    /**
     * Controller for a screen that allows the user to preview their imported model, as well as choose an {@link
     * Uncertainty} and {@link IsotopeSystem} for each table.
     *
     * @author marottajb
     */
    public static class DataTableOptionsView extends VBox {

        //**********************************************//
        //                  CONSTANTS                   //
        //**********************************************//

        private static final String CONTROLLER_FXML = "data-table-options.fxml";

        //**********************************************//
        //                   CONTROLS                   //
        //**********************************************//

        @FXML
        private AnchorPane columnViewPane;
        @FXML private Label uncLabel;
        @FXML
        ComboBox<Uncertainty> unctComboBox;
        @FXML ComboBox<IsotopeSystem> isoComboBox;

        private BooleanProperty invalid;
        public BooleanProperty invalidProperty() {
            if (invalid == null) {
                invalid = new SimpleBooleanProperty(unctComboBox.getValue() == null || isoComboBox.getValue() == null);
            }
            return invalid;
        }
        public final Boolean isInvalid() {
            return invalidProperty().get();
        }

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
            this.setPrefSize(INIT_WIDTH, INIT_HEIGHT);
        }

        @FXML
        protected void initialize() {
            ColumnTreeView treeView = new ColumnTreeView(table.getColumnTree());
            columnViewPane.getChildren().add(treeView);
            FXMLUtils.setAnchorPaneBounds(treeView, 0.0, 0.0, 0.0, 0.0);

            unctComboBox.getItems().addAll(Uncertainty.values());
            unctComboBox.getSelectionModel().select(table.getUnctFormat());
            isoComboBox.getItems().addAll(IsotopeSystem.values());
            isoComboBox.getSelectionModel().select(table.getIsotopeSystem());
        }

        //**********************************************//
        //                PUBLIC METHODS                //
        //**********************************************//

        public IsotopeSystem getIsotopeSystem() {
            return isoComboBox.getValue();
        }

        public Uncertainty getUncertainty() {
            return unctComboBox.getValue();
        }
    }
}
