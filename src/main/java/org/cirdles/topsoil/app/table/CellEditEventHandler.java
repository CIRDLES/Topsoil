/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.topsoil.app.table;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.field.Field;

/**
 * Manage the event of a cell edition
 * Used by the class EntryTableColumn
 *
 * @author parizotclement
 * @param <T>
 */
public class CellEditEventHandler<T> implements EventHandler<CellEditEvent<Entry, T>> {
    
    private final Field field;

    public CellEditEventHandler(Field field) {
        this.field = field;
    }
    
    public void checkValue(TableColumn.CellEditEvent<Entry, T> event, Field field) {
        
        if (!Double.isNaN((Double) event.getNewValue())) {
                event.getRowValue().set(field, event.getNewValue());
            } else {
                //If the input isn't a number
                //Display a Warning Dialog and reset the cell's value
                warningPrompt("Please enter a valid number only !");
                event.getRowValue().set(field, event.getOldValue());
            }
            
            //Workaround for refreshing the cell's value
            //Otherwise the value isn't updated
            event.getTableView().getColumns().get(0).setVisible(false);
            event.getTableView().getColumns().get(0).setVisible(true);
        
    }
      
    /**
     * Display a warning dialog to the user with a custom message.
     * THe user can close it with a OK button.
     * 
     * @param message the message to be displayed
     */
    private void warningPrompt(String message) {
        
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("WARNING");
        alert.setHeaderText(message);

        alert.showAndWait();
    }

    @Override
    public void handle(CellEditEvent<Entry, T> event) {
        checkValue(event, field);
    }    
}
