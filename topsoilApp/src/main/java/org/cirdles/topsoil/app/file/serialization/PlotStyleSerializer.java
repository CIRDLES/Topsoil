package org.cirdles.topsoil.app.file.serialization;

import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;

import java.io.*;

/**
 * A class for serializing SimpleSymbolMap objects in order to save the state of a Plot.
 *
 * @author Garrett Brenner
 */
public class PlotStyleSerializer {

    private static final String FILE_EXTENSION = ".ser";
    // NOTE: This used to throw a SquidException and I have modified it to throw a regular Exception.
    // I do not know if this was the best option
    /**
     *
     * @param serializableObject
     * @param fileName
     * @throws Exception
     */
    public static void serializeObjectToFile(Serializable serializableObject, String fileName) throws Exception {

        // https://dzone.com/articles/fast-java-file-serialization
        // Sept 2018 speedup per Rayner request
        ObjectOutputStream objectOutputStream = null;
        try {
            fileName += FILE_EXTENSION;
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            FileOutputStream fos = new FileOutputStream(raf.getFD());
            objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeObject(serializableObject);
        } /*catch (IOException ex) {
            throw new Exception("Cannot serialize object of " + serializableObject.getClass().getSimpleName() + " to: " + fileName);

        }*/ finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException iOException) {
                }
            }
        }
    }

    /**
     *
     * @param filename
     * @return
     */
    public static Object getSerializedObjectFromFile(String filename, boolean verbose) {
        //FileInputStream inputStream;
        ObjectInputStream deserializedInputStream;
        Object deserializedObject = null;
        filename += FILE_EXTENSION;

        try (FileInputStream inputStream = new FileInputStream(filename)) {
            deserializedInputStream = new ObjectInputStream(inputStream);
            deserializedObject = deserializedInputStream.readObject();
            inputStream.close();

        } catch (FileNotFoundException ex) {
            if (verbose) {
                String errorString = "The file you are attempting to open does not exist:\n" + " " + filename;
                TopsoilNotification.error("Error",errorString);
            }
        } catch (IOException ex) {
            if (verbose) {
                TopsoilNotification.error("Error", "The file you are attempting to open is not a valid '*.ser' file");
            }
        } catch (ClassNotFoundException | ClassCastException ex) {
            if (verbose) {
                TopsoilNotification.error("Error", "The file you are attempting to open is not a valid Plot Style file");
            }
        }

        return deserializedObject;
    }
}
