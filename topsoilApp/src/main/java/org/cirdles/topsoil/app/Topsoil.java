package org.cirdles.topsoil.app;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.stage.StageHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.*;
import org.cirdles.commons.util.ResourceExtractor;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.StringJoiner;

/**
 * @see Application
 * @see MainController
 */
public class Topsoil extends Application {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String STYLESHEET = "topsoil.css";

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private static MainController controller;

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static void main(String[] args) {
        // arg[0] : -v[erbose]
        boolean verbose = false;
        if (args.length > 0) {
            verbose = args[0].startsWith("-v");
        }

        StringJoiner logo = new StringJoiner("\n");
        logo.add("                                                                                                           ");
        logo.add("                                                                                                           ");
        logo.add("TTTTTTTTTTTTTTTTTTTTTTT                                                                      iiii  lllllll ");
        logo.add("T:::::::::::::::::::::T                                                                     i::::i l:::::l ");
        logo.add("T:::::::::::::::::::::T                                                                      iiii  l:::::l ");
        logo.add("T:::::TT:::::::TT:::::T                                                                            l:::::l ");
        logo.add("TTTTTT  T:::::T  TTTTTTooooooooooo   ppppp   ppppppppp       ssssssssss      ooooooooooo   iiiiiii  l::::l ");
        logo.add("        T:::::T      oo:::::::::::oo p::::ppp:::::::::p    ss::::::::::s   oo:::::::::::oo i:::::i  l::::l ");
        logo.add("        T:::::T     o:::::::::::::::op:::::::::::::::::p ss:::::::::::::s o:::::::::::::::o i::::i  l::::l ");
        logo.add("        T:::::T     o:::::ooooo:::::opp::::::ppppp::::::ps::::::ssss:::::so:::::ooooo:::::o i::::i  l::::l ");
        logo.add("        T:::::T     o::::o     o::::o p:::::p     p:::::p s:::::s  ssssss o::::o     o::::o i::::i  l::::l ");
        logo.add("        T:::::T     o::::o     o::::o p:::::p     p:::::p   s::::::s      o::::o     o::::o i::::i  l::::l ");
        logo.add("        T:::::T     o::::o     o::::o p:::::p     p:::::p      s::::::s   o::::o     o::::o i::::i  l::::l ");
        logo.add("        T:::::T     o::::o     o::::o p:::::p    p::::::pssssss   s:::::s o::::o     o::::o i::::i  l::::l ");
        logo.add("      TT:::::::TT   o:::::ooooo:::::o p:::::ppppp:::::::ps:::::ssss::::::so:::::ooooo:::::oi::::::il::::::l");
        logo.add("      T:::::::::T   o:::::::::::::::o p::::::::::::::::p s::::::::::::::s o:::::::::::::::oi::::::il::::::l");
        logo.add("      T:::::::::T    oo:::::::::::oo  p::::::::::::::pp   s:::::::::::ss   oo:::::::::::oo i::::::il::::::l");
        logo.add("      TTTTTTTTTTT      ooooooooooo    p::::::pppppppp      sssssssssss       ooooooooooo   iiiiiiiillllllll");
        logo.add("                                      p:::::p                                                              ");
        logo.add("                                      p:::::p                                                              ");
        logo.add("                                     p:::::::p                                                             ");
        logo.add("                                     p:::::::p                                                             ");
        logo.add("                                     p:::::::p                                                             ");
        logo.add("                                     ppppppppp                                                             ");
        logo.add("                                                                                                           ");
        System.out.println(logo);

        // detect if running from jar file
        if (!verbose && (ClassLoader.getSystemResource("org/cirdles/topsoil/app/Topsoil.class").toExternalForm().startsWith("jar"))) {
            System.out.println(
                    "Running Topsoil from Jar file ... suppressing terminal output.\n"
                            + "\t use '-verbose' argument after jar file name to enable terminal output.");
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // NO-OP
                }
            }));
            System.setErr(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // NO-OP
                }
            }));
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Topsoil.controller = new MainController(primaryStage);
        Topsoil.controller.closeProjectView();

        Scene scene = new Scene(controller, 1200, 750);

        try {
            ResourceExtractor re = new ResourceExtractor(Topsoil.class);
            String stylesheet = re.extractResourceAsPath(STYLESHEET).toUri().toURL().toExternalForm();
            scene.getStylesheets().add(stylesheet);
            StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot load stylesheet: " + STYLESHEET, e);
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static MainController getController() {
        return controller;
    }

    /**
     * Exits the application.
     */
    public static void shutdown() {
        List<Stage> stages = StageHelper.getStages();
        for (int index = stages.size() - 1; index > 0; index--) {
            stages.get(index).close();
        }
        Platform.exit();
    }

}
