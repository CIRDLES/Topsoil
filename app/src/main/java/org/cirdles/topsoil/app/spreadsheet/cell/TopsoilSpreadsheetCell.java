package org.cirdles.topsoil.app.spreadsheet.cell;

import javafx.beans.property.Property;
import org.cirdles.topsoil.app.spreadsheet.TopsoilSpreadsheetView;

/**
 * @author marottajb
 */
public interface TopsoilSpreadsheetCell<T> {

    /**
     * Returns the {@code TopsoilSpreadsheetView} that this cell is associated with.
     *
     * @return  TopsoilSpreadsheetView
     */
    TopsoilSpreadsheetView getSpreadsheet();

    /**
     * Returns the {@code Property} acting as the data model for the cell's item.
     *
     * @return  Property containing source data
     */
    Property<T> getSource();

    /**
     * Sets the data source for the cell to the specified {@code Property}.
     * <p>
     * This method will return null if it is unsuccessful in setting the new source property.
     *
     * @param p     new source Property
     * @return      true if the source was changed
     */
    boolean setSource(Property<T> p);

}
