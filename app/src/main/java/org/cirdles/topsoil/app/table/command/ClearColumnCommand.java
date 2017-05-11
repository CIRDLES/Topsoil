package org.cirdles.topsoil.app.table.command;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilTableCell;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

import java.util.ArrayDeque;

/**
 * An undoable {@link Command} instance that can be added to a TopsoilTab's {@link UndoManager} when a
 * {@link TableColumn} in the {@code TableView} is cleared. This {@code Command} stores the data from the cleared
 * column, and its index in {@link TableView#getColumns()}.
 *
 * @author Jake Marotta
 * @see Command
 * @see UndoManager
 */
public class ClearColumnCommand implements Command {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TableColumn} that was cleared.
     */
    private TableColumn<TopsoilDataEntry, Double> column;

    /**
     * The index of the {@code TableColumn} in {@code TableView.getColumns()}.
     */
    private int index;

    /**
     * An {@code ArrayDeque} that stores the properties from the cleared {@code TableColumn}.
     */
    private ArrayDeque<DoubleProperty> columnData;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a new {@code ClearColumnCommand} for the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    public ClearColumnCommand(TopsoilTableCell cell) {

        this.column = cell.getTableColumn();
        this.index = cell.getColumnIndex();
        this.columnData = new ArrayDeque<>();
    }

    //***********************
    // Methods
    //***********************

    /**
     * Called to execute the column deletion.
     */
    public void execute() {

        this.column.setCellValueFactory(param -> {
            this.columnData.add(param.getValue().getProperties().get(index));
            return (ObservableValue) new SimpleDoubleProperty(0.0);
        });
        this.column.setVisible(false);
        this.column.setVisible(true);
    }

    /**
     * Called to undo the column deletion.
     */
    public void undo() {

        this.column.setCellValueFactory(param -> (ObservableValue) this.columnData.poll());

        // This is a workaround to force the TableView to update the visible Node
        this.column.setVisible(false);
        this.column.setVisible(true);
    }

    /** {@inheritDoc}
     */
    public String getActionName() {
        return "Clear column";
    }
}