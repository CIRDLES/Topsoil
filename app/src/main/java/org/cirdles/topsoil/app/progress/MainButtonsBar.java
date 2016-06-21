package org.cirdles.topsoil.app.progress;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;



/**
 * Created by sbunce on 5/30/2016.
 */
public class MainButtonsBar extends HBox {
    private HBox buttonBar = new HBox();

    public MainButtonsBar() {
        super();
        this.initialize();
    }

    public void initialize() {
        buttonBar.setSpacing(10);
        buttonBar.setPadding(new Insets(15, 12, 15, 12));
        buttonBar.setStyle("-fx-background-color: #DCDCDC;");

        Button newTableButton = new Button("Create New Table");
        newTableButton.setPrefSize(150, 30);
        newTableButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                IsotopeSelectionWindow iso = new IsotopeSelectionWindow();
            }
        });

        Button clearButton = new Button("Clear Table");
        clearButton.setPrefSize(150, 30);

        buttonBar.getChildren()
                 .addAll(newTableButton,
                         clearButton);
    }

    //Returns compatible type to be added to main window
    public HBox getButtons() {
        return buttonBar;
    }
}
