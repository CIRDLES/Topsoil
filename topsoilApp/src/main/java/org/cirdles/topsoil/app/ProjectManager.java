package org.cirdles.topsoil.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.data.TopsoilProject;

import java.nio.file.Path;

public class ProjectManager {

    private static ObjectProperty<TopsoilProject> project = new SimpleObjectProperty<>();
    public static ObjectProperty<TopsoilProject> projectProperty() {
        return project;
    }
    public static TopsoilProject getProject() {
        return project.get();
    }
    public static void setProject(TopsoilProject project) {
        ProjectManager.project.set(project);
    }

    private static ObjectProperty<Path> projectPath = new SimpleObjectProperty<>();
    public static ObjectProperty<Path> projectPathProperty() {
        return projectPath;
    }
    public static Path getProjectPath() {
        return projectPath.get();
    }
    public static void setProjectPath(Path path) {
        ProjectManager.projectPath.set(path);
    }

}
