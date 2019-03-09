package org.cirdles.topsoil.app.file;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.data.SerializableProject;
import org.cirdles.topsoil.app.data.TopsoilProject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A class for reading and writing .topsoil project files.
 *
 * @author marottajb
 */
public class ProjectSerializer {

    private static ObjectProperty<TopsoilProject> currentProject = new SimpleObjectProperty<>(null);
    public static ObjectProperty<TopsoilProject> currentProjectProperty() {
        return currentProject;
    }
    public static TopsoilProject getCurrentProject() {
        return currentProject.get();
    }
    public static void setCurrentProject(TopsoilProject project) {
        currentProject.set(project);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static boolean serialize(Path projectPath, TopsoilProject project) throws IOException {
        OutputStream out = Files.newOutputStream(projectPath);
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(new SerializableProject(project));
        project.setPath(projectPath);
        oos.close();
        out.close();
        return true;
    }

    public static TopsoilProject deserialize(Path projectPath) throws IOException {
        try (InputStream in = Files.newInputStream(projectPath); ObjectInputStream ois = new ObjectInputStream(in)) {
            TopsoilProject project = ((SerializableProject) ois.readObject()).reconstruct();
            project.setPath(projectPath);
            return project;
        } catch (InvalidClassException | ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

}
