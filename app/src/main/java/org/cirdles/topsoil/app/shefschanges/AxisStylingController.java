package org.cirdles.topsoil.app.shefschanges;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.cirdles.commons.util.ResourceExtractor;

import java.io.IOException;

public class AxisStylingController extends AnchorPane {

    @FXML
    TextField plotTitleTextField;

    @FXML
    TextField xMinTextField;

    @FXML
    TextField xMaxTextField;

    @FXML
    Button xSetExtentsButton;

    @FXML
    private void setXSetExtentsActionButton() {

    }


    @FXML
    TextField xTitleTextField;

    @FXML
    TextField yMinTextField;

    @FXML
    TextField yMaxTextField;

    @FXML
    Button ySetExtentsButton;

    @FXML
    private void setYSetExtentsActionButton() {

    }

    @FXML
    TextField yTitleTextField;

    @FXML
    public void initialize() {

    }


    public AxisStylingController() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    new ResourceExtractor(AxisStylingController.class).extractResourceAsPath("axis-styling.fxml").toUri().toURL()
            );
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //************************************************//
    //                    METHODS                     //
    //************************************************//


}


//    public enum VerticalPosition {
//
//        TOP("Top"),
//        CENTER("Center"),
//        BOTTOM("Bottom");
//
//        String vertical;
//
//        VerticalPosition(String vertical) {
//            this.vertical = vertical;
//        }
//
//        public String getVertical() {
//            return vertical;
//        }
//
//        @Override
//        public String toString() {
//            return vertical;
//        }
//
//
//    }
//
//    public enum HorizontalPosition {
//
//        LEFT("left"),
//        CENTER("center"),
//        RIGHT("right");
//
//        String horizontal;
//
//        HorizontalPosition(String horizontal) {
//            this.horizontal = horizontal;
//        }
//
//        public String getHorizontal() {
//            return horizontal;
//        }
//
//        @Override
//        public String toString() {
//            return horizontal;
//        }
//    }
//}
