package org.cirdles.topsoil.app.util.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.MainWindow;

import java.util.Optional;

/**
 * A custom dialog class that essentially acts the same way that the Java {@link Alert} class does. Having this class
 * allows us to customize different types of notifications (that is, different {@code ButtonType} arrangements) without
 * having to do so wherever they are called. It also makes adding new types of notifications relatively simple.
 *
 * @author Jake Marotta
 */
public class TopsoilNotification extends Dialog<ButtonType> {

    private ImageView image;

    private final Double STANDARD_WIDTH = 400.0;
    private final Double STANDARD_HEIGHT = 150.0;

    private TopsoilNotification(String windowTitle, String message) {
        super();

        this.setTitle(windowTitle);
        Stage thisStage = (Stage) this.getDialogPane().getScene().getWindow();

        // Set icon
        thisStage.getIcons().add(MainWindow.getWindowIcon());

        // Build layout
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);

        image = new ImageView();
        image.setPreserveRatio(true);
        image.setFitWidth(75.0);
        hBox.getChildren().add(image);
        HBox.setMargin(image, new Insets(10.0, 10.0, 10.0, 10.0));

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        hBox.getChildren().add(messageLabel);
        HBox.setMargin(messageLabel, new Insets(20.0, 10.0, 20.0, 10.0));

        hBox.setStyle("-fx-background-color: transparent");

        this.getDialogPane().setMinSize(STANDARD_WIDTH, STANDARD_HEIGHT);

        // Set window to the center of the screen.
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        thisStage.setX((screenBounds.getWidth() - STANDARD_WIDTH) / 2);
        thisStage.setY((screenBounds.getHeight() - STANDARD_HEIGHT) / 2);

        this.getDialogPane().setContent(hBox);

        // Sets owner (main window)
        this.initOwner(MainWindow.getPrimaryStage());
    }

    private void setImage(Image image) {
        this.image.setImage(image);
    }

    /**
     * Shows a small notification window with the specified title and message. Other attributes, such as the
     * available {@code ButtonType}s and the graphic are based on the supplied {@code NotificationType}.
     *
     * @param type  NotificationType
     * @param windowTitle   the String title of the Stage
     * @param message   String message
     * @return  the ButtonType of the button the user pressed in response
     */
    public static ButtonType showNotification(NotificationType type, String windowTitle, String message) {
        TopsoilNotification notification = new TopsoilNotification(windowTitle, message);

        notification.setImage(type.getImage());
        notification.getDialogPane().getButtonTypes().addAll(type.getButtonTypes());

        Optional<ButtonType> result = notification.showAndWait();

        return result.orElse(null);
    }

    /**
     * A {@code NotificationType} contains pre-set {@code Image}s and {@code ButtonType}s for construction a
     * {@link TopsoilNotification}.
     */
    public enum NotificationType {

        INFORMATION("notify.png", ButtonType.OK),
        ERROR("error.png", ButtonType.OK),
        VERIFICATION("question.png", ButtonType.CANCEL, ButtonType.OK),
        YES_NO("question.png", ButtonType.CANCEL, ButtonType.NO, ButtonType.YES);

        private Image image;
        private ButtonType[] buttonTypes;
        private ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(NotificationType.class);

        NotificationType(String imageName, ButtonType... buttonTypes) {
            this.image = new Image(RESOURCE_EXTRACTOR.extractResourceAsPath(imageName).toUri().toString());
            this.buttonTypes = buttonTypes;
        }

        /**
         * Returns the {@code Image} associated with this {@code NotificationType}.
         *
         * @return  JavaFX Image
         */
        public Image getImage() {
            return image;
        }

        /**
         * Returns the {@code ButtonType}s associatied with this {@code NotificationType}.
         *
         * @return  array of ButtonTypes
         */
        public ButtonType[] getButtonTypes() {
            return buttonTypes;
        }
    }

}
