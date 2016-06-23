package org.cirdles.topsoil.app.progress;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by benjaminmuldrow on 6/21/16.
 */
public class MenuItemEventHandler {

    public static UPbTable handleTableFromFile() throws IOException {

        // select file
        File file = FileParser.openTableDialogue(new Stage());
        List<UPbDataEntry> entries = FileParser.parseFile(file);
        ObservableList<UPbDataEntry> data = FXCollections.observableList(entries);
        UPbTable table = new UPbTable(data);
        return table;
    }
}
