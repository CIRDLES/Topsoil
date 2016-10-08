package org.cirdles.topsoil.app.progress;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.progress.menu.MainButtonsBar;
import org.cirdles.topsoil.app.progress.menu.MainMenuBar;
import org.cirdles.topsoil.app.progress.menu.MenuItemEventHandler;
import org.cirdles.topsoil.app.progress.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.util.Alerter;
import org.cirdles.topsoil.app.util.ErrorAlerter;

import java.io.IOException;

public class MainWindow extends Application {

    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(new VBox(), 750, 750);

        TopsoilTabPane tabs = new TopsoilTabPane();
        tabs.setId("TopsoilTabPane");
        MainMenuBar menuBar = new MainMenuBar(tabs);
        MenuBar mBar = menuBar.getMenuBar();
        mBar.setId("MenuBar");
        MainButtonsBar buttonBar = new MainButtonsBar(tabs);
        HBox buttons = buttonBar.getButtons();
        buttons.setId("HBox");

        // Create Scene
        ((VBox) scene.getRoot()).getChildren().addAll(
                mBar,
                buttons,
                tabs
        );

        // Display Scene
        primaryStage.setTitle("Topsoil Test");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Handle Keyboard Shortcuts
        scene.setOnKeyPressed(keyEvent -> {
            setTableKeyboardShortcuts(keyEvent, tabs);
            setUndoKeyboardShortcuts(keyEvent, tabs);
            keyEvent.consume();
        });

    }

    private static void setTableKeyboardShortcuts(KeyEvent keyEvent, TopsoilTabPane tabs) {
        // shortcut + T creates a new tab containing an empty table
        if (keyEvent.getCode() == KeyCode.T &&
                keyEvent.isShortcutDown()) {
            TopsoilTable table = MenuItemEventHandler.handleNewTable();
            tabs.add(table);
        }
        // shortcut + I imports a new table from a file
        if (keyEvent.getCode() == KeyCode.I &&
                keyEvent.isShortcutDown()) {
            try {
                TopsoilTable table = MenuItemEventHandler.handleTableFromFile();
                tabs.add(table);
            } catch (IOException e) {
                Alerter alerter = new ErrorAlerter();
                alerter.alert("File I/O Error.");
                e.printStackTrace();
            }
        }
    }

    private static void setUndoKeyboardShortcuts(KeyEvent keyEvent, TopsoilTabPane tabs) {
        // shortcut + Z undoes the last undoable action
        if (keyEvent.getCode() == KeyCode.Z &&
                keyEvent.isShortcutDown() &&
                !tabs.isEmpty()) {
            tabs.getSelectedTab().undo();
        }
        // shortcut + Y redoes the last undone action
        if (keyEvent.getCode() == KeyCode.Y &&
                keyEvent.isShortcutDown() &&
                !tabs.isEmpty()) {
            tabs.getSelectedTab().redo();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
