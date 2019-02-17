package org.cirdles.topsoil.app.util;

import javafx.fxml.FXMLLoader;
import org.cirdles.commons.util.ResourceExtractor;

import java.io.IOException;

/**
 * @author marottajb
 */
public class FXMLUtils {

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
