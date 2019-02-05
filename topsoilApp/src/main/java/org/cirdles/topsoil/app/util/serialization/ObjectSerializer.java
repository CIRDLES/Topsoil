package org.cirdles.topsoil.app.util.serialization;

import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;

import java.io.*;

/**
 * @author marottajb
 */
public class ObjectSerializer<T> {

    private File file;

    public ObjectSerializer(File file) {
        this.file = file;
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public void serialize(T object) {
        try (FileOutputStream out = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public T deserialize() {
        try (FileInputStream in = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(in)) {
            return (T) ois.readObject();
        } catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File getFile() {
        return file;
    }

}
