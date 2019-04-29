package org.cirdles.topsoil.app;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.cirdles.topsoil.app.data.TopsoilProject;

import java.nio.file.Path;

/**
 * Utility class for managing the current {@link TopsoilProject}, as well as its associated file {@code Path} and open
 * plots.
 */
public final class ProjectManager {

    private static ObjectProperty<TopsoilProject> project = new SimpleObjectProperty<>();
    public static ObjectProperty<TopsoilProject> projectProperty() {
        return project;
    }
    /**
     * Returns the {@code TopsoilProject} representing the current working state of the application, if one exists.
     *
     * @return  current TopsoilProject
     */
    public static TopsoilProject getProject() {
        return project.get();
    }
    /**
     * Sets the {@code TopsoilProject} representing the current working state of the application.
     *
     * @param project   TopsoilProject
     */
    public static void setProject(TopsoilProject project) {
        ProjectManager.project.set(project);
    }


    private static ObjectProperty<Path> projectPath = new SimpleObjectProperty<>();
    public static ObjectProperty<Path> projectPathProperty() {
        return projectPath;
    }
    /**
     * Returns the {@code Path} associated with the current working state of the application, if one exists.
     *
     * @return  current project Path
     */
    public static Path getProjectPath() {
        return projectPath.get();
    }
    /**
     * Sets the {@code Path} associated with the current working state of the application.
     *
     * @param path  project Path
     */
    public static void setProjectPath(Path path) {
        ProjectManager.projectPath.set(path);
    }

    /**
     * Closes all open plots for the project and sets the project to null.
     */
    public static void closeProject() {
        ProjectManager.setProject(null);
        ProjectManager.setProjectPath(null);

        // Close all stages except for the primary stage
        Stage[] stages = StageHelper.getStages().toArray(new Stage[]{});
        for (Stage stage : stages) {
            if (stage != Topsoil.getPrimaryStage()) {
                // Fires an event so attached event handlers will still run
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        }
    }

    private ProjectManager() {}
}
