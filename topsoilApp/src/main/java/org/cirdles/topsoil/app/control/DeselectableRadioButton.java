package org.cirdles.topsoil.app.control;

import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;

public class DeselectableRadioButton extends RadioButton {

    public DeselectableRadioButton() {
        SelectionHandler selectionHandler = new SelectionHandler(this);
        this.setOnMousePressed(selectionHandler.getMousePressed());
        this.setOnMouseReleased(selectionHandler.getMouseReleased());
    }

    private class SelectionHandler {

        private RadioButton button;
        private Boolean selected = false;

        private EventHandler<MouseEvent> mousePressed = event -> {
            if (button.isSelected()) {
                selected = true;
            }
        };

        private EventHandler<MouseEvent> mouseReleased = event -> {
            if (selected) {
                button.setSelected(false);
            }
            selected = false;
        };

        public SelectionHandler(RadioButton button) {
            this.button = button;
        }

        public EventHandler<MouseEvent> getMousePressed() {
            return mousePressed;
        }

        public EventHandler<MouseEvent> getMouseReleased() {
            return mouseReleased;
        }

    }
}
