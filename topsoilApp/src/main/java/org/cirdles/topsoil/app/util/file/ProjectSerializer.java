package org.cirdles.topsoil.app.util.file;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A class for reading and writing .topsoil project files.
 *
 * @author marottajb
 */
public class ProjectSerializer {

    /**
     * An {@code ObjectProperty} containing the open .topsoil project {@code File}, if it exists.
     */
    private static ObjectProperty<Path> currentProjectPath = new SimpleObjectProperty<>(null);
    public static ObjectProperty<Path> currentProjectPathProperty() {
        return currentProjectPath;
    }
    public static Path getCurrentProjectPath() {
        return currentProjectPathProperty().get();
    }
    public static void setCurrentProjectPath(Path path) {
        currentProjectPathProperty().set(path);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static boolean serialize(Path projectPath, TopsoilProject project) throws IOException {
        OutputStream out = Files.newOutputStream(projectPath);
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(project);
        oos.close();
        out.close();
        currentProjectPath.set(projectPath);
        return true;
    }

    public static TopsoilProject deserialize(Path projectPath) throws IOException {
        try (InputStream in = Files.newInputStream(projectPath); ObjectInputStream ois = new ObjectInputStream(in)) {
            TopsoilProject project = (TopsoilProject) ois.readObject();
            currentProjectPath.set(projectPath);
            return project;
        } catch (InvalidClassException | ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}
