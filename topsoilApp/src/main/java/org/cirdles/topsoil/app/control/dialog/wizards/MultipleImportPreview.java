package org.cirdles.topsoil.app.control.dialog.wizards;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.cirdles.topsoil.app.control.dialog.DataTableOptionsDialog;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.data.FXDataColumn;
import org.cirdles.topsoil.app.ResourceBundles;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;
import java.util.*;

import static org.cirdles.topsoil.app.control.dialog.wizards.MultipleImportWizard.INIT_HEIGHT;
import static org.cirdles.topsoil.app.control.dialog.wizards.MultipleImportWizard.Key.TABLES;

/**
 * @author marottajb
 */
class MultipleImportPreview extends WizardPane {

    private static final String CONTROLLER_FXML = "project-preview.fxml";
    private static final String WARNING_ICON = "warning.png";

    @FXML private TabPane fileTabs;
    
    private List<FXDataTable> tables;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    MultipleImportPreview() {
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, MultipleImportPreview.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setPrefHeight(INIT_HEIGHT);
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
        wizard.setTitle(ResourceBundles.DIALOGS.getString("importPreviewTitle"));
        List<FXDataTable> newTables = (List<FXDataTable>) wizard.getSettings().get(TABLES);
        updateTabs(newTables);
    }

    @Override
    public void onExitingPage(Wizard wizard) {
        wizard.invalidProperty().unbind();
        PreviewTab preViewTab;
        List<FXDataTable> tables = new ArrayList<>();
        for (Tab tab : fileTabs.getTabs()) {
            preViewTab = (PreviewTab) tab;
            Map<FXDataColumn<?>, Boolean> selections = ((PreviewTab) tab).controller.getColumnSelections();
            for (Map.Entry<FXDataColumn<?>, Boolean> entry : selections.entrySet()) {
                entry.getKey().setSelected(entry.getValue());
            }
//            preViewTab.getTable().setColumnsForAllVariables(((PreviewTab) tab).controller.getVariableAssignments());
//            preViewTab.getTable().setIsotopeSystem(preViewTab.getController().getIsotopeSystem());
            preViewTab.getTable().setUncertainty(preViewTab.getController().getUncertainty());
            tables.add(preViewTab.getTable());
        }
        wizard.getSettings().put(TABLES, tables);
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private void updateTabs(List<FXDataTable> tableList) {
        for (Tab tab : fileTabs.getTabs()) {
            FXDataTable table = ((PreviewTab) tab).getTable();
            if (! tableList.contains(table)) {
                fileTabs.getTabs().remove(tab);
            }
        }
        for (FXDataTable table : tableList) {
            if (! hasTabForTable(table)) {
                PreviewTab tab = new PreviewTab(table);
                fileTabs.getTabs().add(tab);
            }
        }
    }

    private boolean hasTabForTable(FXDataTable table) {
        for (Tab tab : fileTabs.getTabs()) {
            if (((PreviewTab) tab).getTable().equals(table)) {
                return true;
            }
        }
        return false;
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    static class PreviewTab extends Tab {

        private DataTableOptionsDialog.DataTableOptionsView controller;
        private FXDataTable table;

        PreviewTab(FXDataTable table) {
            super(table.getTitle());
            this.table = table;
            controller = new DataTableOptionsDialog.DataTableOptionsView(table);

            this.setContent(controller);
        }

        public FXDataTable getTable() {
            return table;
        }

        private DataTableOptionsDialog.DataTableOptionsView getController() {
            return controller;
        }
    }

}
