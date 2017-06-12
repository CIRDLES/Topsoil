package org.cirdles.topsoil.app.util.serialization;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;

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
 * @author Jake Marotta
 * @see SerializableTopsoilSession
 */
public class TopsoilSerializer {

    //***********************
    // Attributes
    //***********************

    /**
     * An {@code ObjectProperty} containing the open .topsoil project {@code File}, if it exists.
     */
    private static ObjectProperty<File> currentProjectFile;

    //***********************
    // Methods
    //***********************

    /**
     * Creates a {@link SerializableTopsoilSession} and writes it to a
     * .topsoil file.
     *
     * @param file  the File to write to
     * @param tabs  the TopsoilTabPane containing the data to store
     */
    public static void serialize(File file, TopsoilTabPane tabs) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(out);

            oos.writeObject(new SerializableTopsoilSession(tabs));

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

    /**
     * Reads a .topsoil {@code File} into a {@link SerializableTopsoilSession}, and loads all of the data it contains
     * into the specified {@link TopsoilTabPane}.
     *
     * @param file  the File to read from
     * @param tabs  the TopsoilTabPane to add the data to
     */
    public static void deserialize(File file, TopsoilTabPane tabs) {
        try {
            FileInputStream in = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(in);

            SerializableTopsoilSession topsoilSession = (SerializableTopsoilSession) ois.readObject();
            topsoilSession.loadDataToTopsoilTabPane(tabs);

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
     * Returns the {@code ObjectProperty} which, if a project is open, contains the currently open project {@code File}.
     *
     * @return  an ObjectProperty of type File
     */
    public static ObjectProperty<File> currentProjectFileProperty() {
        if (currentProjectFile == null) {
            currentProjectFile = new SimpleObjectProperty<>(null);
        }
        return currentProjectFile;
    }

    /**
     * Gets the .topsoil {@code File} that is currently open.
     *
     * @return  the loaded .topsoil File
     */
    public static File getCurrentProjectFile() {
        if (!isProjectOpen()) {
            return null;
        }
        return currentProjectFileProperty().get();
    }

    /**
     * Sets the working .topsoil {@code File}.
     *
     * @param file  the open .topsoil File
     */
    public static void setCurrentProjectFile(File file) {
        currentProjectFileProperty().set(file);
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
