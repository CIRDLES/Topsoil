package org.cirdles.topsoil.app.control.tree;

import javafx.event.Event;
import javafx.event.EventType;
import org.cirdles.topsoil.app.data.row.DataRow;

/**
 * An event that is fired when a cell in a {@link TopsoilTreeTableView} is edited.
 */
public class CellEditEvent extends Event {

    public final static EventType<CellEditEvent> CELL_EDITED = new EventType<>("CELL_EDITED");

    private DataRow.DataValue<?> dataValue;
    private Object oldValue;
    private Object newValue;

    CellEditEvent(DataRow.DataValue<?> dataValue, Object oldValue, Object newValue) {
        super(CELL_EDITED);

        this.dataValue = dataValue;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public DataRow.DataValue<?> getDataValue() {
        return dataValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

}
