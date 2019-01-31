package org.cirdles.topsoil.app.menu.helpers;

import javafx.scene.control.ButtonType;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.MainController;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.app.util.SampleData;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.util.serialization.Serializer;
import org.cirdles.topsoil.app.view.TopsoilProjectView;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author marottajb
 */
public class FileMenuHelper {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static TopsoilProjectView newProject() {
        TopsoilProjectView projectView = new TopsoilProjectView();

        // TODO New project wizard

        return projectView;
    }
    public static TopsoilProjectView newProject(TopsoilProjectView projectView) {
        // TODO Close old project view
        return newProject();
    }

    public static TopsoilProjectView openProject() {
        TopsoilProjectView projectView = new TopsoilProjectView();
        if (openProject(projectView)) {
            return projectView;
        } else {
            // TODO Show some error notification
            return null;
        }
    }
    public static boolean openProject(TopsoilProjectView projectView) {
        boolean completed = false;
        // @TODO
        return completed;
    }

    public static TopsoilProjectView openSampleData(SampleData data) {
        TopsoilProjectView projectView = new TopsoilProjectView();
        if (openSampleData(data, projectView)) {
            Main.getController().replaceMainContent(projectView);
            return projectView;
        } else {
            return null;
        }
    }
    public static boolean openSampleData(SampleData data, TopsoilProjectView projectView) {
//        projectView.addDataTable(data.getDataTable());
        projectView.addDataTable(data.parseDataTable());
//        DataTable table = data.parseDataTable();
        return true;
    }

    public static boolean saveProject(TopsoilProjectView projectView) {
        boolean completed = false;
        // @TODO
        return completed;
    }

    public static boolean saveProjectAs(TopsoilProjectView projectView, Path path) {
        boolean completed = false;
        // @TODO
        return completed;
    }

    public static boolean closeProject(TopsoilProjectView projectView) {
        boolean completed = false;
        // @TODO
        return completed;
    }

    public static TopsoilProjectView importTableFromFile() {
        TopsoilProjectView projectView = new TopsoilProjectView();
        if (importTableFromFile(projectView)) {
            return projectView;
        } else {
            // TODO Show some error notification
            return null;
        }
    }
    public static boolean importTableFromFile(TopsoilProjectView projectView) {
        boolean completed = false;
        // @TODO
        return completed;
    }

    public static TopsoilProjectView importTableFromClipboard() {
        TopsoilProjectView projectView = new TopsoilProjectView();
        if (importTableFromClipboard(projectView)) {
            return projectView;
        } else {
            // TODO Show some error notification
            return null;
        }
    }
    public static boolean importTableFromClipboard(TopsoilProjectView projectView) {
        boolean completed = false;
        // @TODO
        return completed;
    }

    public static boolean exportTable(TopsoilProjectView projectView) {
        boolean completed = false;
        // @TODO
        return completed;
    }

    public static boolean exitTopsoilSafely() {
        MainController mainController = Main.getController();
        // If something is open
        if (mainController.isDataShowing()) {
            TopsoilProjectView dataView = (TopsoilProjectView) mainController.getMainContent();
            ButtonType saveVerification = FileMenuHelper.verifyFinalSave();
            // If save verification was not cancelled
            if (saveVerification != ButtonType.CANCEL) {
                // If user wants to save
                if (saveVerification == ButtonType.YES) {
                    boolean saved = false;
                    // If a project is already defined
                    if (Serializer.isProjectOpen()) {
                        saved = FileMenuHelper.saveProject(dataView);
                    } else {
                        File file = TopsoilFileChooser.saveTopsoilFile().showSaveDialog(Main.getPrimaryStage());
                        if (file != null) {
                            saved = FileMenuHelper.saveProjectAs(dataView, file.toPath());
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

}
