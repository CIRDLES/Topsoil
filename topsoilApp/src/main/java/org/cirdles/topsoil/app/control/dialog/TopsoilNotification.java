package org.cirdles.topsoil.app.control.dialog;

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
import org.cirdles.topsoil.app.Topsoil;

import java.util.Optional;

/**
 * @author marottajb
 */
public class TopsoilNotification extends Dialog<ButtonType> {

    //**********************************************//
    //                  CONSTANTS                   //
    //**********************************************//

    private static final Double STANDARD_WIDTH = 400.0;
    private static final Double STANDARD_HEIGHT = 150.0;

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private ImageView image;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private TopsoilNotification(String windowTitle, String message) {
        super();

        this.setTitle(windowTitle);
        Stage thisStage = (Stage) this.getDialogPane().getScene().getWindow();
        thisStage.setOnShown(event -> thisStage.requestFocus());

        // Set icon
        thisStage.getIcons().add(Topsoil.getLogo());

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
        this.initOwner(Topsoil.getPrimaryStage());
    }


    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Presents a simple information {@link Dialog} with one button, "OK".
     *
     * @param windowTitle   String title
     * @param message       String message
     *
     * @return              Optional ButtonType pressed
     */
    public static Optional<ButtonType> info(String windowTitle, String message) {
        return showNotification(NotificationType.INFORMATION, windowTitle, message);
    }

    /**
     * Presents a simple error {@link Dialog} with one button, "OK".
     *
     * @param windowTitle   String title
     * @param message       String message
     *
     * @return              Optional ButtonType pressed
     */
    public static Optional<ButtonType> error(String windowTitle, String message) {
        return showNotification(NotificationType.ERROR, windowTitle, message);
    }

    /**
     * Presents a simple verification {@link Dialog} with two buttons, "Cancel" and "OK".
     *
     * @param windowTitle   String title
     * @param message       String message
     *
     * @return              Optional ButtonType pressed
     */
    public static Optional<ButtonType> verify(String windowTitle, String message) {
        return showNotification(NotificationType.VERIFICATION, windowTitle, message);
    }

    /**
     * Presents a simple {@link Dialog} with three buttons, "Yes", "No", and "Cancel".
     *
     * @param windowTitle   String title
     * @param message       String message
     *
     * @return              Optional ButtonType pressed
     */
    public static Optional<ButtonType> yesNo(String windowTitle, String message) {
        return showNotification(NotificationType.YES_NO, windowTitle, message);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    public static Optional<ButtonType> showNotification(NotificationType type, String windowTitle, String message) {
        TopsoilNotification notification = new TopsoilNotification(windowTitle, message);

        notification.setImage(type.getImage());
        notification.getDialogPane().getButtonTypes().addAll(type.getButtonTypes());
        return notification.showAndWait();
    }

    private void setImage(Image image) {
        this.image.setImage(image);
    }

    //**********************************************//
    //                INNER CLASSES                 //
    //**********************************************//

    /**
     * A {@code NotificationType} contains pre-setValue {@code Image}s and {@code ButtonType}s for construction a
     * {@link TopsoilNotification}.
     */
    public enum NotificationType {

        INFORMATION("notify.png", ButtonType.OK),
        ERROR("error.png", ButtonType.OK),
        VERIFICATION("question.png", ButtonType.CANCEL, ButtonType.OK),
        YES_NO("question.png", ButtonType.CANCEL, ButtonType.NO, ButtonType.YES);

        private final Image image;
        private final ButtonType[] buttonTypes;
        private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(NotificationType.class);

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
            ButtonType[] copy = new ButtonType[buttonTypes.length];
            System.arraycopy(buttonTypes, 0, copy, 0, buttonTypes.length);
            return copy;
        }
    }

}
