package org.cirdles.topsoil.app.control.wizards;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.cirdles.topsoil.app.control.dialog.DataTableOptionsDialog;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.composite.DataComponent;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;
import java.util.*;

import static org.cirdles.topsoil.app.control.wizards.NewProjectWizard.INIT_HEIGHT;
import static org.cirdles.topsoil.app.control.wizards.NewProjectWizard.INIT_WIDTH;
import static org.cirdles.topsoil.app.control.wizards.NewProjectWizard.Key.TABLES;

/**
 * @author marottajb
 */
class NewProjectPreView extends WizardPane {

    private static final String CONTROLLER_FXML = "project-preview.fxml";
    private static final String WARNING_ICON = "warning.png";

    @FXML private TabPane fileTabs;
    
    private List<DataTable> tables;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    NewProjectPreView() {
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, NewProjectPreView.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setPrefHeight(INIT_HEIGHT);
//        this.setPrefSize(INIT_WIDTH, INIT_HEIGHT);
    }

    @FXML
    protected void initialize() {
        fileTabs.setStyle("-fx-open-tab-animation: NONE; -fx-close-tab-animation: NONE;");
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public void onEnteringPage(Wizard wizard) {
        wizard.setTitle("New Project: Preview");
        List<DataTable> newTables = (List<DataTable>) wizard.getSettings().get(TABLES);
        updateTabs(newTables);
    }

    @Override
    public void onExitingPage(Wizard wizard) {
        wizard.invalidProperty().unbind();
        PreViewTab preViewTab;
        List<DataTable> tables = new ArrayList<>();
        for (Tab tab : fileTabs.getTabs()) {
            preViewTab = (PreViewTab) tab;
            Map<DataComponent, Boolean> selections = ((PreViewTab) tab).controller.getColumnSelections();
            for (Map.Entry<DataComponent, Boolean> entry : selections.entrySet()) {
                entry.getKey().setSelected(entry.getValue());
            }
            preViewTab.getTable().setIsotopeSystem(preViewTab.getController().getIsotopeSystem());
            preViewTab.getTable().setUnctFormat(preViewTab.getController().getUncertainty());
            tables.add(preViewTab.getTable());
        }
        wizard.getSettings().put(TABLES, tables);
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private void updateTabs(List<DataTable> tableList) {
        for (Tab tab : fileTabs.getTabs()) {
            DataTable table = ((PreViewTab) tab).getTable();
            if (! tableList.contains(table)) {
                fileTabs.getTabs().remove(tab);
            }
        }
        for (DataTable table : tableList) {
            if (! hasTabForTable(table)) {
                PreViewTab tab = new PreViewTab(table);
                fileTabs.getTabs().add(tab);
            }
        }
    }

    private boolean hasTabForTable(DataTable table) {
        for (Tab tab : fileTabs.getTabs()) {
            if (((PreViewTab) tab).getTable().equals(table)) {
                return true;
            }
        }
        return false;
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    class PreViewTab extends Tab {

        private DataTableOptionsDialog.DataTableOptionsView controller;
        private DataTable table;

        PreViewTab(DataTable table) {
            super(table.getLabel());
            this.table = table;
            // TODO if table == null
            controller = new DataTableOptionsDialog.DataTableOptionsView(table);

            this.setContent(controller);
        }

        public DataTable getTable() {
            return table;
        }

        private DataTableOptionsDialog.DataTableOptionsView getController() {
            return controller;
        }
    }

}
