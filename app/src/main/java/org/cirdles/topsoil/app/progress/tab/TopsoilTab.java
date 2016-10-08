package org.cirdles.topsoil.app.progress.tab;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.util.Command;
import org.cirdles.topsoil.app.progress.util.UndoManager;

/**
 * Created by sbunce on 6/30/2016.
 * Extends JavaFX Tab class
 */
public class TopsoilTab extends Tab {

    private TopsoilTable table;
    private final Label label;
    private UndoManager undoManager;

    public TopsoilTab(TopsoilTable table) {
        this.undoManager = new UndoManager(50);
        label = new Label(table.getTitle());
        this.setGraphic(label);
        this.table = table;
        this.setContent(this.table.getTable());
        label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TabNameDialog nameChange = new TabNameDialog(label.getText());
                table.setTitle(nameChange.getName());
                label.setText(table.getTitle());
            }
        });
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

    public String getLastUndoMessage() {
        return this.undoManager.getUndoName();
    }

    public String getLastRedoMessage() {
        return this.undoManager.getRedoName();
    }

    public void clearUndoHistory() {
        this.undoManager.clear();
    }

}
