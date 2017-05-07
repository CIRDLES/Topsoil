package org.cirdles.topsoil.app.progress.table.command;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTableCell;
import org.cirdles.topsoil.app.progress.util.Command;

import java.util.ArrayDeque;

/**
 * An undoable <tt>Command</tt> instance that can be added to a TopsoilTab's
 * <tt>UndoManager</tt> when a <tt>TableColumn</tt> in the <tt>TableView</tt>
 * is cleared. This class stores the data from the cleared column, and its
 * index in <tt>TableView</tt>.<i>getColumns()</i>.
 *
 * @author marottajb
 * @see Command
 * @see org.cirdles.topsoil.app.progress.util.UndoManager
 */
public class ClearColumnCommand implements Command {

    private TableColumn<TopsoilDataEntry, Double> column;
    private int index;
    private ArrayDeque<SimpleDoubleProperty> columnData;

    /**
     * Constructs a new clear column command from the specified cell.
     *
     * @param cell  the TopsoilTableCell that the command came from
     */
    public ClearColumnCommand(TopsoilTableCell cell) {

        this.column = cell.getTableColumn();
        this.index = cell.getColumnIndex();
        this.columnData = new ArrayDeque<>();
    }

    /**
     * Called to execute the column deletion.
     */
    public void execute() {

        this.column.setCellValueFactory(param -> {
            this.columnData.add((SimpleDoubleProperty)
                                        param.getValue().getProperties().get(index));
            return (ObservableValue) new SimpleDoubleProperty(0.0);
        });
        this.column.setVisible(false);
        this.column.setVisible(true);
    }

    /**
     * Called to undo the column deletion.
     */
    public void undo() {

        this.column.setCellValueFactory(param -> (ObservableValue)
                this.columnData.poll());
        this.column.setVisible(false);
        this.column.setVisible(true);
    }

    /**
     * Called from the <tt>UndoManager</tt> to get a short description of the
     * command.
     *
     * @return the name of the command
     */
    public String getActionName() {
        return "Clear column";
    }
}