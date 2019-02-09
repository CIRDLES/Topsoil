package org.cirdles.topsoil.app.util.serialization;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.model.TopsoilProject;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A class for reading and writing .topsoil project files.
 *
 * @author marottajb
 * @see SerializableTopsoilProject
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

    public static void serialize(Path projectPath, TopsoilProject project) {
        try (OutputStream out = Files.newOutputStream(projectPath); ObjectOutputStream oos =
                new ObjectOutputStream(out)) {
            oos.writeObject(new SerializableTopsoilProject(project));
            currentProjectPath.set(projectPath);
        } catch (IOException e) {
            e.printStackTrace();
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Error",
                    "Unable to save project to file."
            );
        }
    }

    public static SerializableTopsoilProject deserialize(Path projectPath) {
        try (InputStream in = Files.newInputStream(projectPath); ObjectInputStream ois = new ObjectInputStream(in)) {
            SerializableTopsoilProject project = (SerializableTopsoilProject) ois.readObject();
            currentProjectPath.set(projectPath);
            return project;
        } catch (InvalidClassException | ClassNotFoundException e) {
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Outdated File",
                    "Unable to load .topsoil file. This may be outdated."
            );
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Invalid File",
                    "The specified file does not exist."
            );
            e.printStackTrace();
        } catch (IOException e) {
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Error",
                    "An unknown error has occurred."
            );
            e.printStackTrace();
        }
        return null;
    }
}
