package org.cirdles.topsoil.app.view.wizards;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.ColumnTree;
import org.cirdles.topsoil.app.data.DataSegment;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.util.file.*;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.cirdles.topsoil.app.view.wizards.NewProjectWizard.Key.SOURCES;

/**
 * @author marottajb
 */
class NewProjectPreView extends WizardPane {

    private static final String CONTROLLER_FXML = "controller/project-preview.fxml";
    private static final String WARNING_ICON_PATH = "warning.png";

    @FXML private TabPane fileTabs;
    
    private ImageView warningIcon;
    private List<ProjectSource> sources;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    NewProjectPreView() {
        final ResourceExtractor re = new ResourceExtractor(NewProjectPreView.class);

        warningIcon = new ImageView(new Image(re.extractResourceAsPath(WARNING_ICON_PATH).toString()));
        warningIcon.setPreserveRatio(true);
        warningIcon.setFitHeight(20.0);

        FXMLLoader loader;
        try {
            loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
        }
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
        removeOldTabs(wizard);
        createNewTabs(wizard);
    }

    @Override
    public void onExitingPage(Wizard wizard) {

    }

    List<DataTable> getSelections() {
        // TODO
        List<DataTable> tables = new ArrayList<>();
        DataTableOptionsView controller;
        for (Tab tab : fileTabs.getTabs()) {
            controller = ((PreViewTab) tab).getController();
            tables.add(controller.getDataTable());
        }
        return tables;
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private void removeOldTabs(Wizard wizard) {
        List<ProjectSource> settingsSources = (List<ProjectSource>) wizard.getSettings().get(SOURCES);
        for (Tab tab : fileTabs.getTabs()) {
            ProjectSource source = ((PreViewTab) tab).getSource();
            if (! settingsSources.contains(source)) {
                fileTabs.getTabs().remove(tab);
            }
        }
    }

    private void createNewTabs(Wizard wizard) {
        List<ProjectSource> settingsSources = (List<ProjectSource>) wizard.getSettings().get(SOURCES);
        for (ProjectSource source : settingsSources) {
            if (! sources.contains(source)) {
                PreViewTab tab = new PreViewTab(source);
                fileTabs.getTabs().add(tab);
            }
        }
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    class PreViewTab extends Tab {

        private DataTableOptionsView controller;
        private ProjectSource source;

        PreViewTab(ProjectSource src) {
            super();
            this.source = src;
            Path path = source.getPath();
            DataParser dataParser;
            switch (source.getTemplate()) {
                case DEFAULT:
                    dataParser = new DefaultDataParser(path);
                    break;
                case SQUID_3:
                    dataParser = new Squid3DataParser(path);
                    break;
                default:
                    dataParser = new DefaultDataParser(path);
                    break;
            }
            controller = new DataTableOptionsView(dataParser.parseDataTable(path.getFileName().toString()));

            this.setContent(controller);
            this.setText(path.getFileName().toString());
        }

        public ProjectSource getSource() {
            return source;
        }

        private DataTableOptionsView getController() {
            return controller;
        }
    }

}
