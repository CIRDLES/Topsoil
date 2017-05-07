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
            String temp = filterSpaces(dialog.getResult());
            if (!temp.isEmpty()) {
                newName = temp;
            }
        } catch (Exception e) {
            //Do nothing
        }
    }

    /**
     * Get the new name entered by the user
     * @return new name
     */
    public String getName() {
        return newName;
    }

    /**
     * Filter extraneous spaces out of a string
     * @param entry original string
     * @return original string with filtered whitespaces
     */
    private static String filterSpaces(String entry) {
        String result = entry;
        while (result.substring(result.length() - 1).equals(" ")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
