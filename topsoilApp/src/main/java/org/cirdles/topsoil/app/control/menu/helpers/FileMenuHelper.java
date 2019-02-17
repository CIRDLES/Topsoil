package org.cirdles.topsoil.app.control.menu.helpers;

import javafx.scene.control.ButtonType;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.control.menu.MenuUtils;
import org.cirdles.topsoil.app.control.wizards.NewProjectWizard;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.util.file.parser.Delimiter;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.util.file.ProjectSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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
        if (MenuUtils.isDataOpen()) {
            if (! handleOverwrite()) {
                return null;
            } else {
                closeProject();
            }
        }
        TopsoilProject project = null;
        Map<String, Object> settings = NewProjectWizard.startWizard();
        if (settings != null) {
            String title = String.valueOf(settings.get(NewProjectWizard.Key.TITLE));
            Path location = (Path) settings.get(NewProjectWizard.Key.LOCATION);
            List<DataTable> tables = (List<DataTable>) settings.get(NewProjectWizard.Key.TABLES);
            try {
                project = new TopsoilProject(tables.toArray(new DataTable[]{}));
                File newFile = new File(location.toFile(), title + ".topsoil");
                ProjectSerializer.serialize(newFile.toPath(), project);
            } catch (IOException e) {
                e.printStackTrace();
                TopsoilNotification.showNotification(
                        TopsoilNotification.NotificationType.ERROR,
                        "Error",
                        "Unable to create project file: " + title + ".topsoil"
                );
            }
        }
        return project;
    }

    public static TopsoilProject openProject() {
        Path path = Paths.get(TopsoilFileChooser.openTopsoilFile().showOpenDialog(Main.getController().getPrimaryStage()).toURI());
        if (path != null && path.equals(ProjectSerializer.getCurrentProjectPath())) {
            return null;    // project already open
        }

        if (MenuUtils.isDataOpen()) {
            if (! handleOverwrite()) {
                return null;
            } else {
                closeProject();
            }
        }

        return openProject(path);
    }

    public static DataTable openSampleData(SampleData data) {
        return data.getDataTable();
    }

    public static boolean saveProject(TopsoilProject project) {
        boolean completed = false;
        Path path = ProjectSerializer.getCurrentProjectPath();
        // TODO Check that path is valid
        if (path != null) {
            try {
                completed = ProjectSerializer.serialize(path, project);
            } catch (IOException e) {
                e.printStackTrace();
                TopsoilNotification.showNotification(
                        TopsoilNotification.NotificationType.ERROR,
                        "Error",
                        "Unable to save project: " + path.getFileName().toString()
                );
            }
        } else {
            completed = saveProjectAs(project);
        }
        return completed;
    }

    public static boolean saveProjectAs(TopsoilProject project) {
        boolean completed = false;
        File file = TopsoilFileChooser.saveTopsoilFile().showSaveDialog(Main.getController().getPrimaryStage());
        try {
            completed = ProjectSerializer.serialize(file.toPath(), project);
        } catch (IOException e) {
            e.printStackTrace();
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Error",
                    "Unable to save project: " + file.getName()
            );
        }
        return completed;
    }

    public static boolean closeProject() {
        if (ProjectSerializer.getCurrentProjectPath() != null) {
            Main.getController().closeProjectView();
            ProjectSerializer.setCurrentProjectPath(null);
        }
        return true;
    }

    public static DataTable importTableFromFile(Path path, Delimiter delimiter, DataTemplate template) throws IOException {
        return template.getParser().parseDataTable(path, delimiter.getValue(), path.getFileName().toString());
    }

    public static DataTable importTableFromString(String content, Delimiter delimiter, DataTemplate template) {
        return template.getParser().parseDataTable(content, delimiter.getValue(), "clipboard-content");
    }

    public static boolean exportTableAs(DataTable table) {
        boolean completed = false;
        File file = TopsoilFileChooser.exportTableFile().showSaveDialog(Main.getController().getPrimaryStage());
        completed = exportTableAs(file.toPath(), table);
        return completed;
    }

    public static boolean exitTopsoilSafely() {
        Main.MainController mainController = Main.getController();
        // If something is open
        if (mainController.isDataShowing()) {
            TopsoilProject project = MenuUtils.getProjectView().getProject();
            ButtonType saveVerification = FileMenuHelper.verifyFinalSave();
            // If save verification was not cancelled
            if (saveVerification != ButtonType.CANCEL) {
                // If user wants to save
                if (saveVerification == ButtonType.YES) {
                    boolean saved = false;
                    // If a project is already defined
                    if (ProjectSerializer.getCurrentProjectPath() != null) {
                        saved = FileMenuHelper.saveProject(project);
                    } else {
                        File file = TopsoilFileChooser.saveTopsoilFile().showSaveDialog(Main.getController().getPrimaryStage());
                        if (file != null) {
                            try {
                                saved = ProjectSerializer.serialize(file.toPath(), project);
                            } catch (IOException e) {
                                e.printStackTrace();
                                TopsoilNotification.showNotification(
                                        TopsoilNotification.NotificationType.ERROR,
                                        "Error",
                                        "Unable to save project: " + file.getName()
                                );
                            }
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

    private static boolean handleOverwrite() {
        TopsoilProject currentProject = MenuUtils.getProjectView().getProject();
        ButtonType shouldSave = showOverwriteDialog();
        if (shouldSave.equals(ButtonType.YES)) {
            if (ProjectSerializer.getCurrentProjectPath() == null) {
                saveProjectAs(currentProject);
            } else {
                saveProject(currentProject);
            }
        }
        if (shouldSave.equals(ButtonType.CANCEL)) {
            return false;
        }
        return true;
    }


    private static ButtonType showOverwriteDialog() {
        return TopsoilNotification.showNotification(TopsoilNotification.NotificationType.YES_NO,
                                                    "Overwrite",
                                                    "This will overwrite your current data. Save?").orElse(null);
    }

    private static TopsoilProject openProject(Path projectPath) {
        try {
            return ProjectSerializer.deserialize(projectPath);
        } catch (IOException e) {
            e.printStackTrace();
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Error",
                    "Could not open project file: " + projectPath.getFileName().toString()
            );
        }
        return null;
    }

    private static boolean exportTableAs(Path path, DataTable table) {
        try {
            return table.getTemplate().getWriter().writeTableToFile(path, table);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
