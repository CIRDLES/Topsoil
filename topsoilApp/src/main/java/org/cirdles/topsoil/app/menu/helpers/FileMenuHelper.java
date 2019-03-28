package org.cirdles.topsoil.app.menu.helpers;

import com.sun.javafx.stage.StageHelper;
import javafx.scene.control.ButtonType;
import javafx.scene.input.Clipboard;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.cirdles.topsoil.app.ProjectManager;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.dialog.DataImportDialog;
import org.cirdles.topsoil.app.control.dialog.DataTableOptionsDialog;
import org.cirdles.topsoil.app.control.dialog.wizards.MultipleImportWizard;
import org.cirdles.topsoil.app.control.plot.PlotStage;
import org.cirdles.topsoil.app.control.plot.TopsoilPlotView;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.DataTemplate;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.file.RecentFiles;
import org.cirdles.topsoil.app.file.parser.DataParser;
import org.cirdles.topsoil.app.file.parser.Delimiter;
import org.cirdles.topsoil.app.util.ExampleData;
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
 * A utility class providing helper methods for the logic behind items in the menu bar.
 *
 * @author marottajb
 */
public class FileMenuHelper {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Handles the opening of a .topsoil file and returns the deserialized {@code TopsoilProject}.
     */
    public static void openProject() {
        Path path = Paths.get(TopsoilFileChooser.openTopsoilFile().showOpenDialog(Topsoil.getPrimaryStage()).toURI());
        openProject(path);
    }

    /**
     * Handles the opening of a .topsoil file and returns the deserialized {@code TopsoilProject}.
     *
     * @param path  project Path
     */
    public static void openProject(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Cannot open a project at path \"null\".");
        }

        if (ProjectManager.getProject() != null) {
            if (path.equals(ProjectManager.getProjectPath())) {
                return;    // project already open
            }
            handleOverwrite();
        }

        openProjectPrivate(path);
    }

    /**
     * Returns a {@code DataTable} for the provided {@code ExampleData}.
     *
     * @param data  ExampleData
     */
    public static void openExampleData(ExampleData data) {
        TopsoilProject project = ProjectManager.getProject();
        if (project != null) {
            project.addDataTable(data.getDataTable());
        } else {
            project = new TopsoilProject(data.getDataTable());
            ProjectManager.setProject(project);
        }
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
        Path path = ProjectManager.getProjectPath();
        if (path != null) {
            try {
                completed = ProjectSerializer.serialize(path, project);
            } catch (IOException e) {
                e.printStackTrace();
                TopsoilNotification.error(
                        "Error",
                        "Unable to save project: " + path.toString()
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
        File file = TopsoilFileChooser.saveTopsoilFile().showSaveDialog(Topsoil.getPrimaryStage());
        if (file != null) {
            try {
                Path path = file.toPath();
                completed = ProjectSerializer.serialize(path, project);
                ProjectManager.setProjectPath(path);
                RecentFiles.addPath(path);
            } catch (IOException e) {
                e.printStackTrace();
                TopsoilNotification.error(
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
        if (ProjectManager.getProject() != null) {
            if (handleDataBeforeClose()) {
                Stage[] stages = StageHelper.getStages().toArray(new Stage[]{});
                for (Stage stage : stages) {
                    if (stage instanceof PlotStage) {
                        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                    }
                }
                ProjectManager.setProjectPath(null);
                ProjectManager.setProject(null);
            }
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
        DataParser parser = template.getParser();
        if (parser != null) {
            String label = path.getFileName() != null ? path.getFileName().toString() : path.toString();
            return template.getParser().parseDataTable(path, delimiter.getValue(), label);
        }
        // @TODO Throw exception
        return null;
    }

    /**
     * Handles the parsing of a {@code DataTable} from some {@code String} value.
     */
    public static void importTableFromClipboard() {
        String content = Clipboard.getSystemClipboard().getString();
        Delimiter delimiter = DataParser.guessDelimiter(content);
        Map<DataImportDialog.Key, Object> settings = DataImportDialog.showDialog("Clipboard", delimiter);
        if (settings != null) {
            delimiter = (Delimiter) settings.get(DataImportDialog.Key.DELIMITER);
            DataTemplate template = (DataTemplate) settings.get(DataImportDialog.Key.TEMPLATE);
            if (delimiter != null && template != null) {
                DataParser parser = template.getParser();
                if (parser != null) {
                    DataTable table = parser.parseDataTable(content, delimiter.getValue(), "clipboard-content");
                    if (DataTableOptionsDialog.showDialog(table, Topsoil.getPrimaryStage())) {
                        if (ProjectManager.getProject() == null) {
                            ProjectManager.setProject(new TopsoilProject(table));
                        } else {
                            ProjectManager.getProject().addDataTable(table);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles the parsing of multiple files into tables.
     */
    public static void importMultipleFiles() {
        Map<String, Object> settings = MultipleImportWizard.startWizard();
        if (settings != null) {
            List<DataTable> tables = (List<DataTable>) settings.get(MultipleImportWizard.Key.TABLES);
            TopsoilProject project = ProjectManager.getProject();
            if (project != null) {
                project.addDataTables(tables.toArray(new DataTable[]{}));
            } else {
                ProjectManager.setProject(new TopsoilProject(tables.toArray(new DataTable[]{})));
            }
        }
    }

    /**
     * Exports the provided {@code DataTable} to some file.
     *
     * @param table DataTable
     * @return      true if successful
     */
    public static boolean exportTableAs(DataTable table) {
        boolean completed = false;
        if (table.getTemplate() == DataTemplate.SQUID_3) {
            TopsoilNotification.info(
                    "Unsupported Operation",
                    "Squid 3 table exporting is currently unsupported."
            );
        } else {
            File file = TopsoilFileChooser.exportTableFile().showSaveDialog(Topsoil.getPrimaryStage());
            if (file != null) {
                completed = exportTableAs(file.toPath(), table);
            }
        }
        return completed;
    }

    /**
     * Exits the application, but not before checking for unsaved data.
     *
     * @return  true if should close
     */
    public static boolean handleDataBeforeClose() {
        boolean completed = false;
        TopsoilProject project = ProjectManager.getProject();
        // If something is open
        if (project != null) {
            ButtonType saveVerification = FileMenuHelper.verifyFinalSave();
            // If save verification was not cancelled
            if (saveVerification != null && ! saveVerification.equals(ButtonType.CANCEL)) {
                // If user wants to save
                if (saveVerification == ButtonType.YES) {
                    // If a project path is already defined
                    if (ProjectManager.getProjectPath() != null) {
                        completed = FileMenuHelper.saveProject(project);
                    } else {
                        File file = TopsoilFileChooser.saveTopsoilFile().showSaveDialog(Topsoil.getPrimaryStage());
                        if (file != null) {
                            try {
                                completed = ProjectSerializer.serialize(file.toPath(), project);
                            } catch (IOException e) {
                                e.printStackTrace();
                                TopsoilNotification.error("Error", "Unable to save project: " + file.getName());
                            }
                        }
                    }
                } else {
                    completed = true;
                }
            }
        // If nothing is open
        } else {
            completed = true;
        }
        return completed;
    }

    /**
     * Presents a {@link javafx.scene.control.Dialog} asking the user if they'd like to save their work.
     *
     * @return  ButtonType YES, NO, or CANCEL (or null)
     */
    public static ButtonType verifyFinalSave() {
        return TopsoilNotification.yesNo("Save Changes", "Would you like to save your work?").orElse(null);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Saves whatever data is loaded to a project, if the user so indicates.
     *
     * @return  true if data is saved; else false
     */
    private static void handleOverwrite() {
        TopsoilProject project = ProjectManager.getProject();
        if (project != null) {
            ButtonType buttonType = showOverwriteDialog();
            if (buttonType != null && ! buttonType.equals(ButtonType.CANCEL)) {
                if (buttonType.equals(ButtonType.YES)) {
                    if (ProjectManager.getProjectPath() == null) {
                        saveProjectAs(project);
                    } else {
                        saveProject(project);
                    }
                }
                closeProject();
            }
        }
    }

    private static void openProjectPrivate(Path projectPath) {
        try {
            TopsoilProject project = ProjectSerializer.deserialize(projectPath);
            if (project != null) {
                ProjectManager.setProject(project);
                ProjectManager.setProjectPath(projectPath);
                RecentFiles.addPath(projectPath);
                ProjectManager.setProject(project);
            }
        } catch (IOException e) {
            e.printStackTrace();
            TopsoilNotification.error(
                    "Error",
                    "Could not open project file: " + projectPath.toString()
            );
        }
    }

    /**
     * Presents the user with a {@link javafx.scene.control.Dialog} warning the user that their current data may be
     * overwritten, and asking whether to save said data.
     *
     * @return  ButtonType YES, NO, or CANCEL (or null)
     */
    private static ButtonType showOverwriteDialog() {
        return TopsoilNotification.yesNo(
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
