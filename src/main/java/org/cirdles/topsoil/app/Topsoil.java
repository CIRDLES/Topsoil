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

import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author John Zeringue
 */
public class Topsoil extends Application {

    public static final String APP_NAME = "Topsoil";

    public static final Path USER_HOME
            = Paths.get(System.getProperty("user.home"));

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new TopsoilMainWindow());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     * <p>
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
