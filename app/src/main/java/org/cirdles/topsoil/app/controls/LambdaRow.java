package org.cirdles.topsoil.app.controls;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.constants.Lambda;
import org.controlsfx.control.PopOver;

/**
 * @author marottajb
 */
public class LambdaRow extends VBox {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private Lambda lambda;

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private Label constantLabel;
    @FXML private TextField textField;
    @FXML private Button moreOptionsButton;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public LambdaRow(Lambda l) {
        this.lambda = l;
    }

    @FXML
    protected void initialize() {
        constantLabel.textProperty().bind(lambda.titleProperty());
//        lambda.valueProperty().bindBidirectional(textField.textProperty());

        Button resetButton = new Button("Reset to Default");
        resetButton.setOnAction(event -> lambda.resetToDefault());

        VBox vBox = new VBox(resetButton);
        vBox.setSpacing(10.0);
        vBox.setPadding(new Insets(10.0));

        PopOver popOver = new PopOver(vBox);
        moreOptionsButton.setOnAction(event -> popOver.show(moreOptionsButton));
    }

}
