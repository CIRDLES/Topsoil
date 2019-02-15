package org.cirdles.topsoil.app.control.menu.helpers;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.MainController;
import org.cirdles.topsoil.app.control.wizards.NewProjectWizard;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.util.file.parser.FileParser;
import org.cirdles.topsoil.app.util.file.DataWriter;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.util.serialization.ProjectSerializer;
import org.cirdles.topsoil.app.control.ProjectView;
import org.cirdles.topsoil.isotope.IsotopeSystem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A utility class providing helper methods for the logic behind items in
 * {@link org.cirdles.topsoil.app.control.menu.TopsoilMenuBar}.
 *
 * @author marottajb
 */
public class FileMenuHelper {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static TopsoilProject newProject() {
        TopsoilProject project = null;
        if (! isDataOpen() || shouldOverwriteData("New Project")) {
            Map<String, Object> settings = NewProjectWizard.startWizard();
            if (settings != null) {
                String title = String.valueOf(settings.get(NewProjectWizard.Key.TITLE));
                Path location = (Path) settings.get(NewProjectWizard.Key.LOCATION);
                List<DataTable> tables = (List<DataTable>) settings.get(NewProjectWizard.Key.TABLES);
                project = new TopsoilProject(tables.toArray(new DataTable[]{}));

                File newFile = new File(location.toFile(), title + ".topsoil");
                ProjectSerializer.serialize(newFile.toPath(), project);
            }
        }
        return project;
    }

    public static TopsoilProject openProject() {
        Path path = Paths.get(TopsoilFileChooser.openTopsoilFile().showOpenDialog(Main.getController().getPrimaryStage()).toURI());
        if (path != null && path.equals(ProjectSerializer.getCurrentProjectPath())) {
            return null;    // project already open
        }

        // handle overwriting project
        if (isDataOpen()) {
            TopsoilProject currentProject = getCurrentProject();
            ButtonType shouldSave = showOverwriteDialog();
            if (shouldSave.equals(ButtonType.YES)) {
                if (ProjectSerializer.getCurrentProjectPath() == null) {
                    saveProjectAs(currentProject);
                } else {
                    serializeProject(currentProject, ProjectSerializer.getCurrentProjectPath());
                }
            }
            if (shouldSave.equals(ButtonType.CANCEL)) {
                return null;
            }
        }

        return openProject(path);
    }

    public static DataTable openSampleData(SampleData data) {
        return data.getDataTable();
    }

    public static boolean saveProject(TopsoilProject project) {
        boolean completed;
        if (ProjectSerializer.getCurrentProjectPath() != null) {
            completed = serializeProject(project, ProjectSerializer.getCurrentProjectPath());
        } else {
            completed = saveProjectAs(project);
        }
        return completed;
    }

    public static boolean saveProjectAs(TopsoilProject project) {
        boolean completed = false;
        File file = TopsoilFileChooser.saveTopsoilFile().showSaveDialog(Main.getController().getPrimaryStage());
        if (file.exists()) {
            completed = serializeProject(project, Paths.get(file.toURI()));
        }
        return completed;
    }

    public static boolean closeProject() {
        boolean completed = false;
        if (ProjectSerializer.getCurrentProjectPath() != null) {
            completed = true;
        } else {
            Main.getController().closeProjectView();
            ProjectSerializer.setCurrentProjectPath(null);
            completed = true;
        }

        return completed;
    }

    public static DataTable importTableFromFile(Path path, DataTemplate template,
                                                IsotopeSystem isotopeSystem, Uncertainty unctFormat) {
//        DataParserBase parser = template.getDataParser(path);
//        if (parser == null) {
//            return null;
//        }
//        return importTable(path.getFileName().toString(), parser, isotopeSystem, unctFormat);
        return null;
    }

    public static DataTable importTableFromString(String content, DataTemplate template, IsotopeSystem isotopeSystem,
                                                     Uncertainty unctFormat) {
//        DataParserBase parser = template.getDataParser(content);
//        if (parser == null) {
//            return null;
//        }
//        return importTable("clipboard-content", parser, isotopeSystem, unctFormat);
        return null;
    }

    public static boolean exportTableAs(DataTable table) {
        boolean completed = false;
        File file = TopsoilFileChooser.exportTableFile().showSaveDialog(Main.getController().getPrimaryStage());
        completed = exportTableAs(table, Paths.get(file.toURI()));
        return completed;
    }

    public static boolean exitTopsoilSafely() {
        MainController mainController = Main.getController();
        // If something is open
        if (mainController.isDataShowing()) {
            TopsoilProject project = getCurrentProject();
            ButtonType saveVerification = FileMenuHelper.verifyFinalSave();
            // If save verification was not cancelled
            if (saveVerification != ButtonType.CANCEL) {
                // If user wants to save
                if (saveVerification == ButtonType.YES) {
                    boolean saved = false;
                    // If a project is already defined
                    if (isDataOpen()) {
                        saved = FileMenuHelper.saveProject(project);
                    } else {
                        File file = TopsoilFileChooser.saveTopsoilFile().showSaveDialog(Main.getController().getPrimaryStage());
                        if (file != null) {
                            saved = FileMenuHelper.serializeProject(project, file.toPath());
                        }
                    }
                    // If file was successfully saved
                    if (saved) {
                        Main.shutdown();
                    }
                // If user doesn't want to save
                } else {
                    Main.shutdown();
                }
            }
        // If nothing is open
        } else {
            Main.shutdown();
        }
        return false;
    }

    public static ButtonType verifyFinalSave() {
        final AtomicReference<ButtonType> reference = new AtomicReference<>(null);

        TopsoilNotification.showNotification(
                TopsoilNotification.NotificationType.YES_NO,
                "Save Changes",
                "Would you like to save your work?"
        ).ifPresent(response -> {
            reference.set(response);
        });

        return reference.get();
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private static TopsoilProject getCurrentProject() {
        Node mainNode = Main.getController().getMainContent();
        if (mainNode instanceof ProjectView) {
            return ((ProjectView) mainNode).getProject();
        }
        return null;
    }

    private static boolean isDataOpen() {
        return Main.getController().getMainContent() instanceof ProjectView;
    }

    private static boolean shouldOverwriteData(String windowTitle) {
        Optional<ButtonType> response = TopsoilNotification.showNotification(
                TopsoilNotification.NotificationType.YES_NO,
                windowTitle,
                "This will close your current model tables. Do you want to continue?"
        );
        return (response.isPresent() && response.get().equals(ButtonType.YES));
    }

    private static ButtonType showOverwriteDialog() {
        return TopsoilNotification.showNotification(TopsoilNotification.NotificationType.YES_NO,
                                                    "Overwrite",
                                                    "This will overwrite your current data. Save?").orElse(null);
    }

    private static TopsoilProject openProject(Path projectPath) {
        return ProjectSerializer.deserialize(projectPath).getTopsoilProject();
    }

    private static boolean serializeProject(TopsoilProject project, Path path) {
        boolean completed;
        ProjectSerializer.serialize(path, project);
        completed = true;
        return completed;
    }

    private static DataTable importTable(String title, FileParser parser, IsotopeSystem isotopeSystem, Uncertainty unctFormat) {
//        DataTable table = parser.parseDataTable(title);
//        if (table != null) {
//            table.setIsotopeSystem(isotopeSystem);
//            table.setUnctFormat(unctFormat);
//        }
        return null;
    }

    private static boolean exportTableAs(DataTable table, Path path) {
        return DataWriter.writeTableToFile(table, path);
    }

}
