package org.cirdles.topsoil.app.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.constant.Lambda;
import org.controlsfx.control.PopOver;

import java.io.IOException;

/**
 * A custom control for editing constant values.
 *
 * @author marottajb
 */
public class ConstantsEditor extends VBox {

    private static String CONTROLLER_FXML = "constants-editor.fxml";

    @FXML private VBox lambdaBox;

    public ConstantsEditor() {
        try {
            FXMLUtils.loadController(CONTROLLER_FXML, ConstantsEditor.class, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        for (Lambda l : Lambda.values()) {
            lambdaBox.getChildren().add(new LambdaRow(l));
        }
    }

    /**
     * @author marottajb
     */
    public static class LambdaRow extends HBox {

        private static String CONTROLLER_FXML = "lambda-row.fxml";

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
            try {
                final ResourceExtractor re = new ResourceExtractor(LambdaRow.class);
                final FXMLLoader loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
                loader.setRoot(this);
                loader.setController(this);
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
            }
        }

        @FXML
        protected void initialize() {
            constantLabel.textProperty().bind(lambda.titleProperty());
            textField.setText(lambda.getValue().toString());

            Button setButton = new Button("Set Value");
            setButton.setOnAction(event -> {
                try {
                    double value = Double.parseDouble(textField.getText());
                    lambda.setValue(value);
                } catch (NumberFormatException e) {
                    textField.setText(lambda.getValue().toString());
                }
            });

            Button resetButton = new Button("Reset to Default");
            resetButton.setOnAction(event -> {
                lambda.resetToDefault();
                textField.setText(lambda.getValue().toString());
            });

            VBox vBox = new VBox(setButton, resetButton);
            vBox.setSpacing(10.0);
            vBox.setPadding(new Insets(10.0));

            PopOver popOver = new PopOver(vBox);
            moreOptionsButton.setOnAction(event -> popOver.show(moreOptionsButton));
        }

    }
}
