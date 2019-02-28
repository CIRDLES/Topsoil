package org.cirdles.topsoil.app.control.menu.helpers;

import javafx.scene.control.ButtonType;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.control.menu.MenuUtils;
import org.cirdles.topsoil.app.control.wizards.NewProjectWizard;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.file.parser.Delimiter;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.file.ProjectSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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

    /**
     * Creates and returns a new {@code TopsoilProject}. Performs all necessary checks, handles data overwriting, and
     * calls the {@link NewProjectWizard}.
     *
     * @return  new TopsoilProject
     */
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

    /**
     * Handles the opening of a .topsoil file and returns the deserialized {@code TopsoilProject}. Performs all
     * necessary checks and handles data overwriting;.
     *
     * @return  deserialized TopsoilProject
     */
    public static TopsoilProject openProject() {
        Path path = Paths.get(TopsoilFileChooser.openTopsoilFile().showOpenDialog(Main.getController().getPrimaryStage()).toURI());
        if (path != null && path.equals(ProjectSerializer.getCurrentProjectPath())) {
            return null;    // project already open
        }

        return openProject(path);
    }

    public static TopsoilProject openProject(Path path) {
        if (MenuUtils.isDataOpen()) {
            if (! handleOverwrite()) {
                return null;
            } else {
                closeProject();
            }
        }

        return openProjectPrivate(path);
    }

    /**
     * Returns a {@code DataTable} for the provided {@code SampleData}.
     *
     * @param data  SampleData
     * @return      DataTable from data
     */
    public static DataTable openSampleData(SampleData data) {
        return data.getDataTable();
    }

    /**
     * Attempts to save the project that is currently open. If no project is loaded, it delegates to {@code
     * saveProjectAs()}.
     *
     * @param project   TopsoilProject to save
     * @return          true if successful
     */
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

    /**
     * Saves the provided {@code TopsoilProject} as a .topsoil file. Handles the path selection of the new file and
     * serialization of the project.
     *
     * @param project   TopsoilProject to save
     * @return          true if successful
     */
    public static boolean saveProjectAs(TopsoilProject project) {
        boolean completed = false;
        File file = TopsoilFileChooser.saveTopsoilFile().showSaveDialog(Main.getController().getPrimaryStage());
        if (file != null) {
            try {
                completed = ProjectSerializer.serialize(file.toPath(), project);
                Main.getController().addRecentFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                TopsoilNotification.showNotification(
                        TopsoilNotification.NotificationType.ERROR,
                        "Error",
                        "Unable to save project: " + file.getName()
                );
            }
        }
        return completed;
    }

    /**
     * Closes the current project, and resets the main content to the initial screen.
     *
     * @return  true if successful
     */
    public static boolean closeProject() {
        if (ProjectSerializer.getCurrentProjectPath() != null) {
            Main.getController().closeProjectView();
            ProjectSerializer.setCurrentProjectPath(null);
        }
        return true;
    }

    /**
     * Handles the parsing of a {@code DataTable} from some file.
     *
     * @param path          target file Path
     * @param delimiter     Delimiter for the file data
     * @param template      DataTemplate of the file data
     * @return              parsed DataTable
     *
     * @throws IOException  if unable to read the file
     */
    public static DataTable importTableFromFile(Path path, Delimiter delimiter, DataTemplate template) throws IOException {
        return template.getParser().parseDataTable(path, delimiter.getValue(), path.getFileName().toString());
    }

    /**
     * Handles the parsing of a {@code DataTable} from some {@code String} value.
     *
     * @param content       the String to parse
     * @param delimiter     Delimiter for the data
     * @param template      DataTemplate of the data
     *
     * @return              parsed DataTable
     */
    public static DataTable importTableFromString(String content, Delimiter delimiter, DataTemplate template) {
        return template.getParser().parseDataTable(content, delimiter.getValue(), "clipboard-content");
    }

    /**
     * Exports the provided {@code DataTable} to some file.
     *
     * @param table DataTable
     * @return      true if successful
     */
    public static boolean exportTableAs(DataTable table) {
        boolean completed = false;
        if (table.getTemplate() != DataTemplate.DEFAULT) {
            File file = TopsoilFileChooser.exportTableFile().showSaveDialog(Main.getController().getPrimaryStage());
            completed = exportTableAs(file.toPath(), table);
        } else {
            TopsoilNotification.showNotification(TopsoilNotification.NotificationType.INFORMATION,
                    "Unsupported Operation",
                    "Custom table exporting is currently unsupported."
            );
        }
        return completed;
    }

    /**
     * Exits the application, but not before checking for unsaved data.
     *
     * @return  true if successful
     */
    public static boolean exitTopsoilSafely() {
        Main.MainController mainController = Main.getController();
        // If something is open
        if (mainController.isDataShowing()) {
            TopsoilProject project = MenuUtils.getProjectView().getProject();
            ButtonType saveVerification = FileMenuHelper.verifyFinalSave();
            // If save verification was not cancelled
            if (saveVerification != ButtonType.CANCEL || saveVerification != null) {
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

    /**
     * Presents a {@link javafx.scene.control.Dialog} asking the user if they'd like to save their work.
     *
     * @return  ButtonType YES, NO, or CANCEL (or null)
     */
    public static ButtonType verifyFinalSave() {
        return TopsoilNotification.showNotification(
                TopsoilNotification.NotificationType.YES_NO,
                "Save Changes",
                "Would you like to save your work?"
        ).orElse(null);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Saves whatever data is loaded to a project, if the user so indicates.
     *
     * @return  true if data is saved; else false
     */
    private static boolean handleOverwrite() {
        TopsoilProject currentProject = MenuUtils.getProjectView().getProject();
        ButtonType shouldSave = showOverwriteDialog();
        if (shouldSave.equals(ButtonType.YES)) {
            if (ProjectSerializer.getCurrentProjectPath() == null) {
                saveProjectAs(currentProject);
            } else {
                saveProject(currentProject);
            }
            return true;
        }
        return false;
    }

    private static TopsoilProject openProjectPrivate(Path projectPath) {
        try {
            TopsoilProject project = ProjectSerializer.deserialize(projectPath);
            Main.getController().addRecentFile(projectPath);
            return project;
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

    /**
     * Presents the user with a {@link javafx.scene.control.Dialog} warning the user that their current data may be
     * overwritten, and asking whether to save said data.
     *
     * @return  ButtonType YES, NO, or CANCEL (or null)
     */
    private static ButtonType showOverwriteDialog() {
        return TopsoilNotification.showNotification(TopsoilNotification.NotificationType.YES_NO,
                                                    "Overwrite",
                                                    "This will overwrite your current data. Save?").orElse(null);
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
