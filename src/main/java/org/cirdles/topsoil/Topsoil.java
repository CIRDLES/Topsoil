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
package org.cirdles.topsoil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author John Zeringue
 */
public class Topsoil extends Application {

    public static final String APP_NAME = "Topsoil";

    public static final Path USER_HOME = Paths.get(System.getProperty("user.home"));

    public static final Path TOPSOIL_PATH = Paths.get(USER_HOME.toString(), APP_NAME);
    public static final Path LAST_TABLE_PATH = Paths.get(TOPSOIL_PATH.toString(), "last_table.tsv");

    /**
     * Text of the error shown if there aren't enough columns to fill all the charts' fields
     */
    public static final String NOT_ENOUGH_COLUMNS_MESSAGE = "Careful, you don't have enough columns to create an ErrorEllipse Chart";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create the Topsoil folder if it doesn't exist.
        Files.createDirectories(TOPSOIL_PATH);

        ResourceBundle bundle = ResourceBundle.getBundle("org.cirdles.topsoil.Resources");

        // TopsoilMainWindow root = new TopsoilMainWindow(primaryStage);
        Pane root = (Pane) FXMLLoader.load(getClass().getResource("topsoil.fxml"), bundle);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(String.format("%s [%s]",
                                            bundle.getString("applicationName"),
                                            bundle.getString("applicationVersion")));
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
