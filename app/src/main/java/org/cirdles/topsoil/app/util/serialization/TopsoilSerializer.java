package org.cirdles.topsoil.app.util.serialization;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;

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
 * @see SerializableTopsoilSession
 */
public class TopsoilSerializer {

    private static File currentProjectFile;

    /**
     * Creates a <tt>SerializableTopsoilSession</tt> and writes it to a
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
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "Unable to save project to file.", ButtonType.OK);
            errorAlert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Reads a .topsoil file into a <tt>SerializableTopsoilSession</tt>, and
     * loads all of the data it contains into the specified
     * <tt>TopsoilTabPane</tt>.
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
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "Unable to load .topsoil file. This may be outdated.", ButtonType.OK);
            errorAlert.showAndWait();
        } catch (FileNotFoundException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "The specified file does not exist.", ButtonType.OK);
            errorAlert.showAndWait();
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "An unknown error has occurred.", ButtonType.OK);
            errorAlert.showAndWait();
            e.printStackTrace();
        }

    }

    /**
     * Gets the .topsoil <tt>File</tt> that is currently open.
     *
     * @return  the loaded .topsoil File
     */
    public static File getCurrentProjectFile() {
        if (!isProjectOpen()) {
            return null;
        }
        return currentProjectFile;
    }

    /**
     * Sets the working .topsoil <tt>File</tt>.
     *
     * @param file  the open .topsoil File
     */
    public static void setCurrentProjectFile(File file) {
        currentProjectFile = file;
    }

    /**
     * Sets the working .topsoil <tt>File</tt> to null.
     */
    public static void closeProjectFile() {
        currentProjectFile = null;
    }

    /**
     * Checks whether a .topsoil <tt>File</tt> is open.
     *
     * @return  true if currentProjectFile != null
     */
    public static boolean isProjectOpen() {
        return currentProjectFile != null;
    }

    /**
     * Checks whether the current .topsoil <tt>File</tt> exists. Important if
     * the file was deleted externally while open in Topsoil.
     *
     * @return  true if the current .topsoil <tt>File</tt>.exists()
     */
    public static boolean projectFileExists() {
        return (isProjectOpen() && currentProjectFile.exists());
    }

}
