package org.cirdles.topsoil.app.progress;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

/**
 * Created by sbunce on 6/20/2016.
 */
public class IsotopeSelectionWindow {

    //All possible isotopes, and stores selected isotope
   // private IsotopeSystems selectedIso;
    //Creates a menu of the isotopes
    private ComboBox items;
    //Creates the stage
    private Stage stage;


    public IsotopeSelectionWindow(){
        //selectedIso = new IsotopeSystems();
        //items = new ComboBox(selectedIso.getList());
        stage = new Stage();
        this.initialize();
    }

    public void initialize(){
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
        grid.add(items, 1, 1);

        Button okayButton = new Button("Okay");
        okayButton.setOnAction((EventHandler<ActionEvent>) e -> selectOkay());
        grid.add(okayButton, 0, 2);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction((EventHandler<ActionEvent>) e -> selectCancel());
        grid.add(cancelButton, 1, 2);

        stage.show();
    }

    public void selectOkay(){
        try {
            String iso = (String) items.getSelectionModel().getSelectedItem();
            //selectedIso.setIsoSystem(iso);
            stage.close();
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You must select an isotope type.");
            alert.showAndWait();
        }
    }

    public void selectCancel(){
        stage.close();
    }
}
