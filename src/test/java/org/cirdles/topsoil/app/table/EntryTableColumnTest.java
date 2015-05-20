/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.topsoil.app.table;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.TSVTable;
import org.cirdles.topsoil.app.TSVTableTest;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.dataset.reader.DatasetReader;
import org.cirdles.topsoil.app.dataset.writer.DatasetWriter;
import org.cirdles.topsoil.app.dataset.reader.TSVDatasetReader;
import org.cirdles.topsoil.app.dataset.writer.TSVDatasetWriter;
import org.cirdles.topsoil.dataset.Dataset;
import org.junit.Test;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 *
 * @author parizotclement
 */
public class EntryTableColumnTest extends ApplicationTest {
    

   private Parent getRootNode() {
     
       // generate path to the sample TSV file
       Path sampleTSVPath = null;
       try {
           URI sampleTSV_URI = getClass().getResource("../sample.tsv").toURI();
           sampleTSVPath = Paths.get(sampleTSV_URI);
       } catch (URISyntaxException ex) {
           Logger.getLogger(TSVTableTest.class.getName()).log(Level.SEVERE, null, ex);
       }

       //Create a TSVTable
       TSVTable testTSVTable = new TSVTable(sampleTSVPath);
       testTSVTable.setId("tsvTable");
       
       //Create a dataset
       Dataset dataset = Dataset.EMPTY_DATASET;
       DatasetReader tableReader = new TSVDatasetReader(true);

       try {
           //Extract the data from the sample file
           dataset = tableReader.read(sampleTSVPath);
           testTSVTable.setDataset(dataset);
       } catch (IOException ex) {
           Logger.getLogger(Topsoil.class.getName()).log(Level.SEVERE, null, ex);
       }

       DatasetWriter tableWriter = new TSVDatasetWriter();
       try {
           //Write the extracted data into the table
           tableWriter.write(testTSVTable.getDataset(), Topsoil.LAST_TABLE_PATH);
       } catch (IOException ex) {
           Logger.getLogger(TSVTableTest.class.getName()).log(Level.SEVERE, null, ex);
       }

       return testTSVTable;
               
    }
    
    @Override
    public void start(Stage stage) throws Exception {                    
        
        Scene scene = new Scene(getRootNode());
        stage.setScene(scene);
        stage.show();
    }    
    
    //Test if the filter is correctly working on a non-number input
    @Test
    public void testInputNotANumber() {
        
        //Get the value of the first cell, before any editing
        String value = cell("#tsvTable", 0, 0).getText();
        
        doubleClickOn(cell("#tsvTable", 0, 0), MouseButton.PRIMARY) //Double-click on the first cell
                .write("test") //Write "test"
                .push(KeyCode.ENTER) //Press ENTER for committing
                .push(KeyCode.ENTER); //Press ENTER to close the Warning Dialog
        
        //Check that the first cell content hasn't been modified
        verifyThat(cell("#tsvTable", 0, 0), hasText(value));
    }
    
    
    /*
     * ---TEMPORARY STORAGE---
     *
     * Methods taken from https://github.com/TestFX/TestFX/issues/27
     * Allows to retrieve a row or a cell from a table view, as there is no method yet in TestFX to get them
     *
     * These methods are stored here for the moment, until they are integrated to TestFX or a decision is made regarding their place   
     */
    
    
    /**
    * @param tableSelector Id of a TableView
    * @return the corresponding TableView, unless an exception is thrown
    */
   private TableView<?> getTableView(String tableSelector) {
       Node node = lookup(tableSelector).queryFirst();
       if (!(node instanceof TableView)) {
            throw new RuntimeException(tableSelector + " selected " + node + " which is not a TableView!");
       }
       return (TableView<?>) node;
   }

    /**
     * @param tableSelector Id of a TableView
     * @param row Index of the row
     * @return the corresponding row
     */
    protected TableRow<?> row(String tableSelector, int row) {

        TableView<?> tableView = getTableView(tableSelector);

        List<Node> current = tableView.getChildrenUnmodifiable();
        while (current.size() == 1) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        current = ((Parent) current.get(1)).getChildrenUnmodifiable();
        while (!(current.get(0) instanceof TableRow)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(row);
        if (node instanceof TableRow) {
            return (TableRow<?>) node;
        } else {
            throw new RuntimeException("Expected Group with only TableRows as children");
        }
    }

    /**
     * @param tableSelector Id of a TableView
     * @param row Index of the row
     * @param column Index of the column
     * @return the corresponding cell
     */
    protected TableCell<?, ?> cell(String tableSelector, int row, int column) {
        List<Node> current = row(tableSelector, row).getChildrenUnmodifiable();
        while (current.size() == 1 && !(current.get(0) instanceof TableCell)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(column);
        if (node instanceof TableCell) {
            return (TableCell<?, ?>) node;
        } else {
            throw new RuntimeException("Expected TableRowSkin with only TableCells as children");
        }
    }
}
