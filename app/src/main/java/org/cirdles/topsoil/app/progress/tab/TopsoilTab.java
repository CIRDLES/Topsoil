package org.cirdles.topsoil.app.progress.tab;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.util.Command;
import org.cirdles.topsoil.app.progress.util.UndoManager;

/**
 * Created by sbunce on 6/30/2016.
 * Extends JavaFX Tab class
 */
public class TopsoilTab extends Tab {

    private TopsoilTable table;
//    private final Label isotopeLabel;
    private final String isotopeType;
    private String actualTitle;
    private final Label titleLabel;
    private TextField textField;
    private UndoManager undoManager;

    public TopsoilTab(TopsoilTable table) {
        this.undoManager = new UndoManager(50);

        this.isotopeType = table.getIsotopeType().getAbbreviation() + " - ";
//        this.isotopeLabel = new Label(isotopeType);
//        isotopeLabel.setId("Isotope");

        this.actualTitle = table.getTitle();
        this.titleLabel = new Label(this.isotopeType + table.getTitle());
        this.titleLabel.setId("Title");
        this.titleLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {
                this.startEditingTitle();
                this.textField.requestFocus();
            }
        });

//        this.titleLabel.setGraphic(isotopeLabel);
        this.setGraphic(this.titleLabel);

        this.table = table;
        this.setContent(this.table.getTable());
    }

    private void startEditingTitle() {
        this.textField = generateTitleTextField();
//        this.textField.setText(this.titleLabel.getText());
        this.textField.setText(this.actualTitle);
        this.titleLabel.setGraphic(this.textField);
        this.titleLabel.setText(null);
        this.textField.selectAll();
    }

    private void finishEditingTitle() {
        this.setTitle(this.textField.getText());
//        this.titleLabel.setGraphic(this.isotopeLabel);
        this.titleLabel.setGraphic(null);
        this.textField = null;
    }

    public void setTitle(String title) {
//        this.titleLabel.setText(title);
        this.titleLabel.setText(this.isotopeType + title);
        this.actualTitle = title;
//        this.table.setTitle(title);
        this.table.setTitle(this.actualTitle);
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
