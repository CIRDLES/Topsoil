package org.cirdles.topsoil.app.control;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.cirdles.commons.util.ResourceExtractor;

import java.io.IOException;

/**
 * @author marottajb
 */
public class FXMLUtils {

    public static void setAnchorPaneConstraints(Node child, double top, double right, double bottom, double left) {
        AnchorPane.setTopAnchor(child, top);
        AnchorPane.setRightAnchor(child, right);
        AnchorPane.setBottomAnchor(child, bottom);
        AnchorPane.setLeftAnchor(child, left);
    }

    /**
     * A helper method that loads FXML defined controls into the provided controller.
     *
     * @param fileName      FXML file name
     * @param clazz         controller Class
     * @param controller    controller instance
     * @param <T>           type of controller
     *
     * @throws IOException  if file error
     */
    public static <T> void loadController(String fileName, Class<T> clazz, T controller) throws IOException {
        try {
            final ResourceExtractor re = new ResourceExtractor(clazz);
            final FXMLLoader loader = new FXMLLoader(re.extractResourceAsPath(fileName).toUri().toURL());
            loader.setRoot(controller);
            loader.setController(controller);
            loader.load();
        } catch (IOException e) {
            throw new IOException("Could not load " + fileName, e);
        }
    }

}
