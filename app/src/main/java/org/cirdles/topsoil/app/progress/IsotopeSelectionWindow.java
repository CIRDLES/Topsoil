package org.cirdles.topsoil.app.progress;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.cirdles.topsoil.app.util.ErrorAlerter;

/**
 * Created by sbunce on 6/20/2016.
 */
public class IsotopeSelectionWindow {
    //TODO fix everything to work with the new enum IsotopeType
    //Creates the stage
    private Stage stage;

    public IsotopeSelectionWindow() {
        stage = new Stage();
        this.initialize();
    }

    public void initialize() {
        stage.setTitle("Select Your Isotope System");

        //Creates the layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 400, 150);
        stage.setScene(scene);

        Label iso = new Label("Isotope System:");
        grid.add(iso, 0, 1);
        //grid.add(items, 1, 1);

        Button okayButton = new Button("Okay");
        okayButton.setOnAction((EventHandler<ActionEvent>) e -> selectOkay());
        grid.add(okayButton, 0, 2);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction((EventHandler<ActionEvent>) e -> selectCancel());
        grid.add(cancelButton, 1, 2);

        stage.show();
    }

    private void selectOkay() {
        try {
            stage.close();
        } catch (Exception e) {
            ErrorAlerter alert = new ErrorAlerter();
            alert.alert("You must select an isotope.");
        }
    }

    private void selectCancel() {
        stage.close();
    }
}
