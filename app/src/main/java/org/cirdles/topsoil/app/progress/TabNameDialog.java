package org.cirdles.topsoil.app.progress;

import javafx.scene.control.TextInputDialog;
import org.controlsfx.dialog.Dialogs;

import java.util.Optional;

/**
 * Created by sbunce on 7/6/2016.
 */
public class TabNameDialog {
    private String newName;

    public TabNameDialog(String oldName) {
        //Sets default name
        newName = oldName;

        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setTitle("Tab Name Change");
        dialog.setContentText("Enter the new tab name:");
        dialog.showAndWait();

        if(!dialog.getResult().isEmpty()){
            newName = dialog.getResult();
        }
    }

    public String getName(){
        return newName;
    }


}
