package org.cirdles.topsoil.app.menu.helpers;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.MainController;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.util.serialization.ProjectSerializer;
import org.cirdles.topsoil.app.view.TopsoilProjectView;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author marottajb
 */
public class FileMenuHelper {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static TopsoilProject newProject() {
        TopsoilProject project = new TopsoilProject();

        // TODO New project wizard

        return project;
    }

    public static TopsoilProject openProject(Path projectPath) {
        // TODO
        return null;
    }

    public static DataTable openSampleData(SampleData data) {
        return data.getDataTable();
    }

    public static boolean saveProject(TopsoilProject project) {
        boolean completed = false;
        // @TODO
        return completed;
    }

    public static boolean saveProjectAs(TopsoilProject project, Path path) {
        boolean completed = false;
        // @TODO
        return completed;
    }

    public static boolean closeProject(TopsoilProject project) {
        boolean completed = false;
        // @TODO
        return completed;
    }

    public static DataTable importTableFromFile(Path path) {
        // TODO
        return null;
    }

    public static DataTable importTableFromClipboard() {
        // TODO
        return null;
    }

    public static boolean exportTableAs(DataTable table, Path path) {
        boolean completed = false;
        // @TODO
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
                    if (ProjectSerializer.isProjectOpen()) {
                        saved = FileMenuHelper.saveProject(project);
                    } else {
                        File file = TopsoilFileChooser.saveTopsoilFile().showSaveDialog(Main.getPrimaryStage());
                        if (file != null) {
                            saved = FileMenuHelper.saveProjectAs(project, file.toPath());
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

    private static TopsoilProject getCurrentProject() {
        Node mainNode = Main.getController().getMainContent();
        if (mainNode instanceof TopsoilProjectView) {
            return ((TopsoilProjectView) mainNode).getProject();
        }
        return null;
    }

}
