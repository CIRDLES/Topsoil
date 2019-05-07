package org.cirdles.topsoil.app.control.tree;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import org.cirdles.topsoil.app.data.composite.DataComponent;

public class MultilineHeaderTreeTableColumn<T> extends TreeTableColumn<DataComponent, T> {

    public static final int LABEL_PADDING = 5;

    protected Label label;
    private StackPane stackPaneGraphic;

    public MultilineHeaderTreeTableColumn(String labelText) {
        super();
        label = new Label(labelText);
        label.setStyle("-fx-padding: " + LABEL_PADDING + "px;");
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        stackPaneGraphic = new StackPane();
        stackPaneGraphic.getChildren().add(label);
        stackPaneGraphic.prefWidthProperty().bind(this.widthProperty().subtract(2));
        label.prefWidthProperty().bind(stackPaneGraphic.prefWidthProperty());
        this.setGraphic(stackPaneGraphic);  // Replaces the column's text; allows for multi-line column headers
    }

    public String getHeaderText() {
        return label.getText();
    }

    public void setHeaderText(String text) {
        if (text != null) {
            label.setText(text);
        }
    }
}
