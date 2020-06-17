package org.cirdles.topsoil.utils;

import java.io.File;
import java.nio.file.Path;

public class TopsoilPersistentState {

    public static final String TOPSOIL_USER_DATA_FOLDER_NAME = "TopsoilUserData";

    public static Path getTopsoilUserData() {
        // check if user data folder exists and create if it does not
        File dataFolder = new File(
                File.separator + System.getProperty("user.home") + File.separator + TOPSOIL_USER_DATA_FOLDER_NAME);
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        return dataFolder.toPath();
    }
}
