package org.cirdles.topsoil.app.progress.tab;

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
            String temp = parseSpaces(dialog.getResult());
            if (!temp.isEmpty()) {
                newName = temp;
            }
        } catch (Exception e) {
            //Do nothing
        }
    }

    public String getName() {
        return newName;
    }

    private static String parseSpaces(String s) {
        String ret = s;
        while (ret.substring(ret.length() - 1).equals(" ")) {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }
}
