package org.cirdles.topsoil.app.controls;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.constants.Lambda;

/**
 * @author marottajb
 */
public class ConstantsEditorPane extends VBox {

    @FXML private VBox lambdaBox;

    public ConstantsEditorPane() {
        super();
    }

    @FXML
    protected void initialize() {
        for (Lambda l : Lambda.values()) {
            lambdaBox.getChildren().add(new LambdaRow(l));
        }
    }

}
