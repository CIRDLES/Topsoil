package org.cirdles.topsoil.app.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.constant.Lambda;

import java.io.IOException;

/**
 * @author marottajb
 */
public class ConstantsEditor extends VBox {

    private static String CONTROLLER_FXML = "constant-editor.fxml";

    @FXML private VBox lambdaBox;

    public ConstantsEditor() {
        try {
            final ResourceExtractor re = new ResourceExtractor(ConstantsEditor.class);
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
        for (Lambda l : Lambda.values()) {
            lambdaBox.getChildren().add(new LambdaRow(l));
        }
    }

}
