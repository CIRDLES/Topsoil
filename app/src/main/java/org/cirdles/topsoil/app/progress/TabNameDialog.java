package org.cirdles.topsoil.app.progress;

import javafx.scene.control.TextInputDialog;

/**
 * Created by sbunce on 7/6/2016.
 */
public class TabNameDialog {
    private String newName;

    public TabNameDialog(String oldName) {
        newName = oldName;
        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setTitle("Tab Name Change");
        dialog.setContentText("Enter the new tab name:");
        dialog.showAndWait();

        try {
            if (!dialog.getResult().isEmpty()) {
                newName = dialog.getResult();
            }
        } catch (Exception e) {
            //Do nothing
        }
    }

    public String getName() {
        return newName;
    }
}
