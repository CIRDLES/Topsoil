package org.cirdles.topsoil.app.progress.tab;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.util.Command;
import org.cirdles.topsoil.app.progress.util.UndoManager;

/**
 * Created by sbunce on 6/30/2016.
 * Extends JavaFX Tab class
 */
public class TopsoilTab extends Tab {

    private TopsoilTable table;
    private HBox graphic;
    private final Label titleLabel;
    private TextField textField;
    private UndoManager undoManager;

    public TopsoilTab(TopsoilTable table) {
        this.undoManager = new UndoManager(50);

        this.graphic = new HBox();
        Label isotope = new Label(table.getIsotopeType().getAbbreviation() + " - ");
        isotope.setId("Isotope");
        titleLabel = new Label(table.getTitle());
        this.titleLabel.setId("Title");
        this.graphic.getChildren().addAll(isotope, this.titleLabel);
        this.graphic.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2 && this.graphic.getChildren().contains(this.titleLabel)) {
                this.startEditingTitle();
            }
        });

        this.setGraphic(graphic);

        this.table = table;
        this.setContent(this.table.getTable());
    }

   private void startEditingTitle() {
       this.textField = generateTitleTextField();
       this.textField.setText(this.titleLabel.getText());
       this.titleLabel.setGraphic(this.textField);
       this.titleLabel.setText(null);
       this.textField.selectAll();
    }

    private void finishEditingTitle() {
        this.titleLabel.setText(this.textField.getText());
        this.titleLabel.setGraphic(null);
        this.table.setTitle(this.textField.getText());
        this.textField = null;
    }

    private TextField generateTitleTextField() {
        TextField textField = new TextField();
        textField.setMinWidth(this.titleLabel.getWidth() - this.titleLabel.getGraphicTextGap() * 2);
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                finishEditingTitle();
            }
        });

        return textField;
    }

    /**
     * get the TableView object from the tab
     * @return tableview
     */
    public TableView getTable() {
        return table.getTable();
    }

    /**
     * get the Topsoil Table object from the tab
     * @return TopsoilTable
     */
    public TopsoilTable getTopsoilTable() {
        return table;
    }

    public void addUndo(Command command) {
        this.undoManager.add(command);
    }

    public void undo() {
        this.undoManager.undo();
    }

    public void redo() {
        this.undoManager.redo();
    }
}
