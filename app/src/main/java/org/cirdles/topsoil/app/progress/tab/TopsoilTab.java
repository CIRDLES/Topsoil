package org.cirdles.topsoil.app.progress.tab;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.table.TopsoilTableController;
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

    private TopsoilTableController tableController;
    private UndoManager undoManager;

    private String isotopePrefix;
    private SimpleStringProperty actualTitle;
    private Label titleLabel;
    private TextField textField;

    private TopsoilTabContent tabContent;

    /**
     * Constructs a <tt>TopsoilTab</tt> for the specified <tt>TopsoilTable</tt>.
     *
     * @param tabContent the TopsoilTabContent for visualizing the data
     */
    public TopsoilTab(TopsoilTabContent tabContent, TopsoilTableController tableController) {
        this.tabContent = tabContent;
        this.tableController = tableController;
        this.undoManager = new UndoManager(50);

        this.isotopePrefix = tableController.getTable().getIsotopeType().getAbbreviation() + " - ";

        this.actualTitle = new SimpleStringProperty(tableController.getTable().getTitle());
        this.actualTitle.bindBidirectional(tableController.getTable().titleProperty()); // bind to TopsoilTable titleProperty

        tableController.getTable().isotopeTypeObjectProperty().addListener(c -> {
            isotopePrefix = tableController.getTable().getIsotopeType().getAbbreviation() + " - ";
            setTitle(this.actualTitle.get());
        });


        this.titleLabel = new Label(isotopePrefix + actualTitle.get());
        this.titleLabel.setId("Title");
        this.titleLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {
                startEditingTitle();
                textField.requestFocus();
            }
        });

        this.setGraphic(titleLabel);
        this.setContent(tableController.getTabContent().getTableView());
    }


    /**
     * Begins editing the <tt>TopsoilTab</tt>'s title by providing a
     * <tt>TextField</tt>.
     */
    private void startEditingTitle() {
        this.textField = generateTitleTextField();
        this.textField.setText(this.actualTitle.get());
        this.textField.selectAll();

        this.titleLabel.setGraphic(this.textField);
        this.titleLabel.setText(null);
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
        this.titleLabel.setText(this.isotopePrefix + title);
        this.actualTitle.set(title);
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

    public TopsoilTableController getTableController() {
        return tableController;
    }

    /**
     * get the Topsoil Table object from the tab
     * @return TopsoilTable
     */
    public TopsoilTable getTopsoilTable() {
        return tableController.getTable();
    }

    public TopsoilTabContent getTabContent() {
        return tabContent;
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
