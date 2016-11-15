package org.cirdles.topsoil.app.progress.util.serialization;

import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class TopsoilSerializer {

    public static void serialize(File file, TopsoilTabPane tabs) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(out);

            oos.writeObject(new SerializableTopsoilSession(tabs));

            out.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deserialize(File file, TopsoilTabPane tabs) {
        try {
            FileInputStream in = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(in);

            SerializableTopsoilSession topsoilSession = (SerializableTopsoilSession) ois.readObject();
            topsoilSession.loadDataToTopsoilTabPane(tabs);

            in.close();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
