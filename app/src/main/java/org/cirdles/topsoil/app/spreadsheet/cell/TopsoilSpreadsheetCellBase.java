package org.cirdles.topsoil.app.spreadsheet.cell;

import javafx.beans.binding.Binding;
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

    private Property<T> bindingProp = new SimpleObjectProperty<>(null);

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    protected final Property<Property<T>> source = new SimpleObjectProperty<>(new SimpleObjectProperty<>(null));
    public Property<Property<T>> sourceProperty() {
        return source;
    }
    /** {@inheritDoc} */
    public Property<T> getSource() {
        return source.getValue();
    }
    /** {@inheritDoc} */
    public boolean setSource(Property<T> p) {
        boolean changed = false;
        if (p != null) {
            Property<T> src = source.getValue();
            if (p != src && getCellType().match(p.getValue())) {
                src.removeListener(sourceListener);
                p.addListener(sourceListener);
                source.setValue(p);
                setItem(source.getValue().getValue());
                changed = true;
            }
        }
        return changed;
    }

    public boolean setSource(Binding<T> b) {
        boolean changed = false;
        bindingProp.unbind();
        bindingProp.bind(b);
        if (getSource() != bindingProp) {
            changed = setSource(bindingProp);
        }
        return changed;
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    TopsoilSpreadsheetCellBase(TopsoilSpreadsheetView spreadsheet, final int row, final int col,
                                       final SpreadsheetCellType type, Property<T> property) {
        super(row, col, 1, 1, type);
        this.spreadsheet = spreadsheet;

        setSource(property);
        // @TODO Check the type casts
        itemProperty().addListener((observable, oldValue, newValue) -> onItemUpdated(oldValue, newValue));
    }

    TopsoilSpreadsheetCellBase(TopsoilSpreadsheetView spreadsheet, final int row, final int col,
                               final SpreadsheetCellType type, Binding<T> binding) {
        super(row, col, 1, 1, type);
        this.spreadsheet = spreadsheet;

        setSource(binding);
        // @TODO Check the type casts
        itemProperty().addListener((observable, oldValue, newValue) -> onItemUpdated(oldValue, newValue));
    }

    //**********************************************//
    //               ABSTRACT METHODS               //
    //**********************************************//

    /**
     * This method is called internally when the cell's item is changed. It should not be called manually.
     */
    abstract void onItemUpdated(Object oldValue, Object newValue);

    /**
     * This method is called internally when the cell's source is changed. It should not be called manually.
     */
    abstract void onSourceUpdated(Object oldValue, Object newValue);

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
