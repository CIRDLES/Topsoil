package org.cirdles.topsoil.app.progress.tab;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.util.Command;
import org.cirdles.topsoil.app.progress.util.UndoManager;

/**
 * A custom <tt>Tab</tt> which stores a <tt>TopsoilTable</tt>.
 *
 * @author sbunce
 * @see Tab
 * @see TopsoilTabPane
 * @see TopsoilTable
 */
public class TopsoilTab extends Tab {

    private TopsoilTable table;
    //    private final Label isotopeLabel;
    private final String isotopeType;
    private String actualTitle;
    private final Label titleLabel;
    private TextField textField;
    private UndoManager undoManager;

    /**
     * Constructs a <tt>TopsoilTab</tt> for the specified <tt>TopsoilTable</tt>.
     *
     * @param table the TopsoilTable to store
     */
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


    /**
     * Begins editing the <tt>TopsoilTab</tt>'s title by providing a
     * <tt>TextField</tt>.
     */
    private void startEditingTitle() {
        this.textField = generateTitleTextField();
//        this.textField.setText(this.titleLabel.getText());
        this.textField.setText(this.actualTitle);
        this.titleLabel.setGraphic(this.textField);
        this.titleLabel.setText(null);
        this.textField.selectAll();
    }

    /**
     * Saves the text in the title <tt>TextField</tt> to the
     * <tt>TopsoilTab</tt>.
     */
    private void finishEditingTitle() {
        this.setTitle(this.textField.getText());
//        this.titleLabel.setGraphic(this.isotopeLabel);
        this.titleLabel.setGraphic(null);
        this.textField = null;
    }

    /**
     * Sets the title of the <tt>TopsoilTab</tt> and the
     * <tt>TopsoilTable</tt> to the provided <tt>String</tt>.
     *
     * @param title the new title
     */
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

    /**
     * Adds a new <tt>Command</tt> to this <tt>TopsoilTab</tt>'s <tt>UndoManager</tt>.
     *
     * @param command   the Command to add
     */
    public void addUndo(Command command) {
        this.undoManager.add(command);
    }

    /**
     * Undoes the last executed <tt>Command</tt> in this <tt>TopsoilTab</tt>'s
     * <tt>UndoManager</tt>.
     */
    public void undo() {
        this.undoManager.undo();
    }

    /**
     * Gets a short message describing the last executed <tt>Command</tt> in
     * this <tt>TopsoilTab</tt>'s<tt>UndoManager</tt>.
     *
     * @return  a short description of the last executed command
     */
    public String getLastUndoMessage() {
        return this.undoManager.getUndoName();
    }

    /**
     * Re-executes the last undone <tt>Command</tt> in this <tt>TopsoilTab</tt>'s
     * <tt>UndoManager</tt>.
     */
    public void redo() {
        this.undoManager.redo();
    }

    /**
     * Gets a short message describing the last undone <tt>Command</tt> in
     * this <tt>TopsoilTab</tt>'s <tt>UndoManager</tt>.
     *
     * @return  a short description of the last undone command
     */
    public String getLastRedoMessage() {
        return this.undoManager.getRedoName();
    }
}
