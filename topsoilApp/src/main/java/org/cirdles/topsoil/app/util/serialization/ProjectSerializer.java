package org.cirdles.topsoil.app.util.serialization;

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
        try (OutputStream out = Files.newOutputStream(projectPath); ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(project);
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

    public static TopsoilProject deserialize(Path projectPath) {
        try (InputStream in = Files.newInputStream(projectPath); ObjectInputStream ois = new ObjectInputStream(in)) {
            TopsoilProject project = (TopsoilProject) ois.readObject();
            currentProjectPath.set(projectPath);
            return project;
        } catch (InvalidClassException | ClassNotFoundException e) {
            e.printStackTrace();
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Outdated File",
                    "Unable to load .topsoil file. This may be outdated."
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Invalid File",
                    "The specified file does not exist."
            );
        } catch (OptionalDataException e) {
            System.err.println("EOF? " + e.eof);
            System.err.println("length: " + e.length);
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Error",
                    "An unknown error has occurred."
            );
        }
        return null;
    }
}
