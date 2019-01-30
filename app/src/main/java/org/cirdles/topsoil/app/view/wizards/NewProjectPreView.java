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

    /**
     * Returns a {@code List} of {@code Map}s containing information from each of the {@link DataImportOptionsView}s
     * from the specified source files.
     *
     * @return  List of Maps of DataImportKeys to Objects
     */
    List<Map<ImportKey, Object>> getSelections() {
        // TODO
        List<Map<ImportKey, Object>> allSelections = new ArrayList<>();

        DataImportOptionsView controller;
        for (Tab tab : fileTabs.getTabs()) {
            controller = ((PreViewTab) tab).getController();
            Map<ImportKey, Object> selections = new HashMap<>();

            selections.put(ImportKey.LABEL, tab.getText());
            selections.put(ImportKey.COLUMN_TREE, controller.getColumnTree());
            selections.put(ImportKey.DATA_SEGMENTS, controller.getDataSegments());
            selections.put(ImportKey.UNCT_FORMAT, controller.getUncertaintyFormat());
            selections.put(ImportKey.ISO_SYSTEM, controller.getIsotopeType());
            selections.put(ImportKey.VARIABLE_MAP, controller.getVariableColumnMap());

            allSelections.add(selections);
        }

        return allSelections;
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    private void updateFinishButtonDisabledProperty(Wizard wizard) {
        boolean incomplete = false;
        for (Tab t : fileTabs.getTabs()) {
            if (((PreViewTab) t).getController().getUncertaintyFormat() == null) {
                incomplete = true;
                break;
            }
        }
        if (incomplete) {
            wizard.setInvalid(true);
        } else {
            wizard.setInvalid(false);
        }
    }

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
                tab.getController().uncertaintyFormatProperty().addListener(c -> updateFinishButtonDisabledProperty(wizard));
                fileTabs.getTabs().add(tab);
            }
        }
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    class PreViewTab extends Tab {

        private DataImportOptionsView controller;
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
            ColumnTree columnTree = dataParser.parseColumnTree();
            DataSegment[] dataSegments = dataParser.parseData();
            controller = new DataImportOptionsView(columnTree, Arrays.asList(dataSegments));

            this.setContent(controller);
            this.setText(path.getFileName().toString());

            ImageView warningIconCopy = new ImageView(warningIcon.getImage());
            warningIconCopy.setPreserveRatio(true);
            warningIconCopy.setFitHeight(20.0);
            this.setGraphic(warningIconCopy);

            controller.uncertaintyFormatProperty().addListener(c -> {
                if (controller.getUncertaintyFormat() == null) {
                    this.setGraphic(warningIconCopy);
                } else {
                    this.setGraphic(null);
                }
                /*
                The TabPane overflow menu doesn't automatically update the tab's graphic when it's changed,
                so the only way to get it to do so is to remove the tab, then add it again. The animations
                have been disabled to make this a smoother process.
                */
                TabPane tabPane = this.getTabPane();
                int index = tabPane.getTabs().indexOf(this);
                tabPane.getTabs().remove(this);
                tabPane.getTabs().add(index, this);
                tabPane.getSelectionModel().select(this);
            });
        }

        public ProjectSource getSource() {
            return source;
        }

        private DataImportOptionsView getController() {
            return controller;
        }
    }

}
