package org.cirdles.topsoil.app.spreadsheet.cell;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

/**
 * Base class for Topsoil's custom {@link org.controlsfx.control.spreadsheet.SpreadsheetCell}s. Instead of providing
 * a value for the cell's {@code itemProperty}, a "source" {@link Property} is provided,
 *
 * @author marottajb
 */
public abstract class TopsoilSpreadsheetCellBase<T> extends SpreadsheetCellBase implements TopsoilSpreadsheetCell<T> {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    protected final TopsoilSpreadsheetView spreadsheet;
    private final ChangeListener<T> sourceListener = (
            (observable, oldValue, newValue) -> onSourceUpdated(oldValue, newValue)
    );

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private Property<Property<T>> source = new SimpleObjectProperty<>(new SimpleObjectProperty<>(null));
    private Property<Property<T>> sourceProperty() {
        return source;
    }
    /** {@inheritDoc} */
    public Property<T> getSource() {
        return source.getValue();
    }
    /** {@inheritDoc} */
    public boolean setSource(Property<T> p) {
        boolean changed = false;
        Property<T> src = source.getValue();
        if (p != null && (! p.equals(src))) {
            if (getCellType().match(p.getValue())) {
                src.removeListener(sourceListener);
                p.addListener(sourceListener);
                source.setValue(p);
                changed = true;
            }
        }
        return changed;
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    TopsoilSpreadsheetCellBase(TopsoilSpreadsheetView spreadsheet, final int row, final int col,
                                      final int rowSpan, final int colSpan, final SpreadsheetCellType<? extends T> type,
                                      final Property<T> source) {
        super(row, col, rowSpan, colSpan, type);
        this.spreadsheet = spreadsheet;

        setSource(source);
        setItem(getSource().getValue());
        // @TODO Check the type casts
        itemProperty().addListener((observable, oldValue, newValue) -> onItemUpdated((T) oldValue, (T) newValue));
    }

    //**********************************************//
    //               ABSTRACT METHODS               //
    //**********************************************//

    /**
     * This method is called internally when the cell's item is changed. It should not be called manually.
     */
    abstract void onItemUpdated(T oldValue, T newValue);

    /**
     * This method is called internally when the cell's source is changed. It should not be called manually.
     */
    abstract void onSourceUpdated(T oldValue, T newValue);

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /** {@inheritDoc} */
    public TopsoilSpreadsheetView getSpreadsheet() {
        return spreadsheet;
    }

    @Override
    public String toString() {
        return "cell[" + getRow() + "][" + getColumn() + "](" + getItem() + ")";
    }

}
