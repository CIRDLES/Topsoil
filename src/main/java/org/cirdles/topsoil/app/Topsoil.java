/*
 * Copyright (C) 2014 John Zeringue
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cirdles.topsoil.app;

import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.utils.GetApplicationDirectoryOperation;

/**
 *
 * @author John Zeringue
 */
public class Topsoil extends Application {

    public static final String APP_NAME = "Topsoil";

    public static final Path USER_HOME = Paths.get(System.getProperty("user.home"));

    public static final Path OLD_TOPSOIL_PATH = USER_HOME.resolve(APP_NAME);
    public static final Path OLD_LAST_TABLE_PATH = OLD_TOPSOIL_PATH.resolve("last_table.tsv");

    public static final Path TOPSOIL_PATH = new GetApplicationDirectoryOperation().perform(APP_NAME);
    public static final Path LAST_TABLE_PATH = TOPSOIL_PATH.resolve("last_table.tsv");

    /**
     * Text of the error shown if there aren't enough columns to fill all the charts' fields
     */
    public static final String NOT_ENOUGH_COLUMNS_MESSAGE = "Careful, you don't have enough columns to create an ErrorEllipse Chart";
    public static final String NOT_ENOUGH_COLUMNS_MESSAGE_2 = "You are missing data columns, so Topsoil is supplying columns of zeroes to support an ErrorEllipse Chart";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // create the Topsoil folder if it doesn't exist
        // note that Files.createDirectory(TOPSOIL_PATH) throws an error if the folder already exists
        Files.createDirectories(TOPSOIL_PATH);

        // migrate from the old file structure to the new
        if (Files.exists(OLD_LAST_TABLE_PATH) && !Files.exists(LAST_TABLE_PATH)) {
            Files.move(OLD_LAST_TABLE_PATH, LAST_TABLE_PATH);
        }

        // delete the old data store if it's now empty
        try {
            Files.deleteIfExists(OLD_TOPSOIL_PATH);
        } catch (DirectoryNotEmptyException ex) {
            Logger.getLogger(Topsoil.class.getName()).log(Level.INFO, "Old Topsoil path not empty");
        }
        
        primaryStage.setScene(new Scene(new TopsoilMainWindow()));
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the
     * application can not be launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans
     * ignores main().
     * <p>
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
