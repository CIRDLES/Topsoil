package org.cirdles.topsoil.app.util.serialization;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.serialization.objects.SerializableTopsoilProject;
import org.cirdles.topsoil.app.view.TopsoilProjectView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.InvalidClassException;
import java.io.FileNotFoundException;

/**
 * A class for reading and writing .topsoil project files.
 *
 * @author marottajb
 * @see SerializableTopsoilProject
 */
public class Serializer {

    /**
     * An {@code ObjectProperty} containing the open .topsoil project {@code File}, if it exists.
     */
    private static ObjectProperty<File> currentProjectFile = new SimpleObjectProperty<>(null);
    public static ObjectProperty<File> currentProjectFileProperty() {
        return currentProjectFile;
    }
    public static File getCurrentProjectFile() {
        return currentProjectFileProperty().get();
    }
    public static void setCurrentProjectFile(File file) {
        currentProjectFileProperty().set(file);
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static void serialize(File file, TopsoilProjectView projectView) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(out);

            oos.writeObject(new SerializableTopsoilProject(projectView));

            out.flush();
            out.close();
            oos.close();
        } catch (IOException e) {
            TopsoilNotification.showNotification(
                    TopsoilNotification.NotificationType.ERROR,
                    "Error",
                    "Unable to save project to file."
            );
            e.printStackTrace();
        }
    }

    public static void deserialize(File file, TopsoilProjectView projectView) {
        try {
            FileInputStream in = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(in);

            SerializableTopsoilProject project = (SerializableTopsoilProject) ois.readObject();
            project.reloadProjectToDataView(projectView);

            setCurrentProjectFile(file);

            in.close();
            ois.close();
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

    }

    /**
     * Sets the working .topsoil {@code File} to null.
     */
    public static void closeProjectFile() {
        currentProjectFileProperty().set(null);
    }

    /**
     * Checks whether a .topsoil {@code File} is open.
     *
     * @return  true if currentProjectFile != null
     */
    public static boolean isProjectOpen() {
        return currentProjectFileProperty().get() != null;
    }

    /**
     * Checks whether the current .topsoil {@code File} exists. Important if the file was deleted externally while
     * open in Topsoil.
     *
     * @return  true if the current .topsoil {@code File}.exists()
     */
    public static boolean projectFileExists() {
        return (isProjectOpen() && currentProjectFileProperty().get().exists());
    }

}
