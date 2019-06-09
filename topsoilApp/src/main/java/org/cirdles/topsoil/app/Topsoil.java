package org.cirdles.topsoil.app;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.stage.StageHelper;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import javafx.stage.*;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.control.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.app.file.FileChoosers;
import org.cirdles.topsoil.app.file.serialization.ProjectSerializer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

import static org.cirdles.topsoil.app.MenuItemHelper.saveProject;

/**
 * The main class of the Topsoil application.
 */
public class Topsoil extends Application {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final String TOPSOIL_LOGO = "topsoil-logo.png";
    private static final String STYLESHEET = "style/topsoil.css";
    private static final String ARIMO_REGULAR = "style/fonts/Arimo/Arimo-Regular.ttf";
    private static final String ARIMO_BOLD = "style/fonts/Arimo/Arimo-Bold.ttf";
    private static final String ARIMO_ITALIC = "style/fonts/Arimo/Arimo-Italic.ttf";
    private static final String ARIMO_BOLDITALIC = "style/fonts/Arimo/Arimo-BoldItalic.ttf";

    private static final double INIT_WIDTH = 1200.0;
    private static final double INIT_HEIGHT = 750.0;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private static Stage primaryStage;
    private static Image logo;

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public void init() {
        // JxBrowser; enables lightweight mode, local files, and logging
        BrowserPreferences.setChromiumSwitches(
                "--disable-gpu",
                "--disable-gpu-compositing",
                "--enable-begin-frame-scheduling",
                "--software-rendering-fps=60",
                "--disable-web-security",
                "-–allow-file-access-from-files",
                "--enable-logging --v=1"
        );
        if (Environment.isMac()) {
            //BrowserCore.initialize();   // must be initialized on non-UI thread on Mac
        }
    }

    @Override
    public void start(Stage primaryStage) {
        setPrimaryStage(primaryStage);
        MainController controller = MainController.getInstance();

        // Set the stage to appear at the center of the screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - INIT_WIDTH) / 2);
        primaryStage.setY((screenBounds.getHeight() - INIT_HEIGHT) / 2);

        Scene scene = new Scene(controller, INIT_WIDTH, INIT_HEIGHT);

        // Bind the title of the stage to show current project title, if applicable
        primaryStage.titleProperty().bind(Bindings.createStringBinding(() -> {
            String appName = ResourceBundles.MAIN.getString("appName");
            Path path = ProjectManager.getProjectPath();
            if (path != null) {
                if (path.getFileName() != null) {
                    appName += (" - " + path.getFileName().toString());
                }
            }
            return appName;
        }, ProjectManager.projectPathProperty()));

        try {
            // Load logo image
            ResourceExtractor re = new ResourceExtractor(Topsoil.class);
            setLogo(new Image(re.extractResourceAsPath(TOPSOIL_LOGO).toUri().toString()));
            primaryStage.getIcons().add(logo);

            // Load custom fonts
            Font.loadFont(re.extractResourceAsFile(ARIMO_REGULAR).toURI().toURL().toExternalForm(), 12.0);
            Font.loadFont(re.extractResourceAsFile(ARIMO_BOLD).toURI().toURL().toExternalForm(), 12.0);
            Font.loadFont(re.extractResourceAsFile(ARIMO_ITALIC).toURI().toURL().toExternalForm(), 12.0);
            Font.loadFont(re.extractResourceAsFile(ARIMO_BOLDITALIC).toURI().toURL().toExternalForm(), 12.0);

            String stylesheet = re.extractResourceAsPath(STYLESHEET).toUri().toURL().toExternalForm();
            scene.getStylesheets().add(stylesheet);
            StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
        } catch (MalformedURLException e) {
            throw new RuntimeException(ResourceBundles.MAIN.getString("stylesheetError") + " " + STYLESHEET, e);
        }

        // Exit platform on window close
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            safeShutdown();
        });

        setupKeyboardShortcuts(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Returns the primary {@code Stage} of the application.
     *
     * @return  primary Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Returns the {@code Image} for the Topsoil logo.
     *
     * @return  logo Image
     */
    public static Image getLogo() {
        return logo;
    }

    public static void safeShutdown() {
        TopsoilProject project = ProjectManager.getProject();
        if (project == null) {
            shutdown();     // No data loaded, safe to shut down
            return;
        }

        // Ask the user whether or not to save their work before exiting
        ButtonType saveVerification = TopsoilNotification.yesNo(
                "Save Changes",
                "Would you like to save your work?"
        ).orElse(null);
        if (saveVerification == null || saveVerification.equals(ButtonType.CANCEL)) {
            return;   // Dialog cancelled; don't exit the application
        }

        // If the user wants to save their work
        if (saveVerification == ButtonType.YES) {
            if (ProjectManager.getProjectPath() != null) {
                MenuItemHelper.saveProject(project);
            } else {
                File file = FileChoosers.saveTopsoilFile().showSaveDialog(Topsoil.getPrimaryStage());

                if (file != null) {
                    try {
                        ProjectSerializer.serialize(file.toPath(), project);
                        shutdown();
                    } catch (IOException e) {
                        e.printStackTrace();
                        TopsoilNotification.error("Error", "Unable to save project: " + file.getName());
                    }
                }
            }
        } else {
            shutdown();
        }
    }

    /**
     * Exits the application without saving.
     */
    public static void shutdown() {
        List<Stage> stages = StageHelper.getStages();
        for (int index = stages.size() - 1; index > 0; index--) {
            stages.get(index).close();
        }
        Platform.exit();
    }

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
                            + "\t Use '-verbose' argument after jar file name to enable terminal output.");
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

    private void setPrimaryStage(Stage stage) {
        Topsoil.primaryStage = stage;
    }

    private void setLogo(Image logo) {
        Topsoil.logo = logo;
    }

    private static void setupKeyboardShortcuts(Scene scene) {
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN),
                () -> MenuUtils.undoLastAction()
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN),
                () -> MenuUtils.redoLastAction()
        );
    }

}
