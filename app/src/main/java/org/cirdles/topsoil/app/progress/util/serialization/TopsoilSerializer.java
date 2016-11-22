package org.cirdles.topsoil.app.progress.util.serialization;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;

import java.io.*;

public class TopsoilSerializer {

    public static void serialize(File file, TopsoilTabPane tabs) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(out);

            oos.writeObject(new SerializableTopsoilSession(tabs));

            out.flush();
            oos.close();
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "Unable to save project to file.", ButtonType.OK);
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
        } catch (InvalidClassException|ClassNotFoundException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "This .topsoil file may be outdated.", ButtonType.OK);
            errorAlert.showAndWait();
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "The specified file does not exist.", ButtonType.OK);
            errorAlert.showAndWait();
            e.printStackTrace();
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                    "An unknown error has occurred.", ButtonType.OK);
            errorAlert.showAndWait();
            e.printStackTrace();
        }

    }
}
