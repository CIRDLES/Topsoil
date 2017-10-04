package org.cirdles.topsoil.app.tab;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import org.cirdles.topsoil.app.table.TopsoilDataTable;
import org.cirdles.topsoil.app.table.TopsoilTableController;
import org.cirdles.topsoil.app.util.serialization.PlotInformation;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 * A custom {@code Tab} which displays data from a {@link TopsoilDataTable} in a {@link TopsoilTabContent}. The
 * TopsoilDataTable is accessed via a {@link TopsoilTableController}. The {@code TopsoilTabContent} is loaded from
 * FXML by the {@link TopsoilTabPane}.
 *
 * @author sbunce
 * @see Tab
 * @see TopsoilTabContent
 * @see TopsoilTabPane
 * @see TopsoilTableController
 */
public class TopsoilTab extends Tab {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code TopsoilTableController} that handles this tab's content.
     */
    private TopsoilTableController tableController;

    /**
     * An {@code UndoManager} that handles changes to the table view in this tab's content.
     */
    private UndoManager undoManager;

    /**
     * A {@code String} consisting of the abbreviation of the {@code IsotopeType} of this tab's corresponding {@code
     * TopsoilDataTable} and a hyphen. It serves as a prefix to the actual title of the tab, to quickly identify the
     * {@code IsotopeType} of the tab's data.
     */
    private String isotopePrefix;

    /**
     * A {@code StringProperty} containing the real title of the tab, which is also the title of the corresponding
     * {@code TopsoilDataTable}.
     */
    private SimpleStringProperty actualTitle;

    /**
     * The {@code Label} that conveys the tab's title.
     */
    private Label titleLabel;

    /**
     * A {@code TextField} for editing the tab's title.
     */
    private TextField textField;

    /**
     * The {@code TopsoilTabContent} containing all of the nodes within this tab, including the {@code TableView} and
     * the {@code PlotPropertiesPanelController}.
     */
    private TopsoilTabContent tabContent;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs a {@code TopsoilTab} using the specified {@code TopsoilTabContent}, with data from a {@code
     * TopsoilTableController}.
     *
     * @param tabContent the TopsoilTabContent for visualizing the data
     * @param tableController   the TopsoilTableController that manages tabContent and the data
     */
    public TopsoilTab(TopsoilTabContent tabContent, TopsoilTableController tableController) {
        this.tabContent = tabContent;
        this.tableController = tableController;
//        this.getTabPane().getTabs().remove(0);
        this.undoManager = new UndoManager(50);

        this.isotopePrefix = tableController.getTable().getIsotopeType().getAbbreviation() + " - ";

        this.actualTitle = new SimpleStringProperty(tableController.getTable().getTitle());
        this.actualTitle.bindBidirectional(tableController.getTable().titleProperty()); // bind to TopsoilDataTable nameProperty

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

        // On tab close, close any plots associated with the table.
        this.setOnClosed(event -> {
            if (!this.getTableController().getTable().getOpenPlots().isEmpty()) {
                for (PlotInformation plotInfo : this.getTableController().getTable().getOpenPlots()) {
                    plotInfo.getStage().close();
                }
            }
        });
    }

    //***********************
    // Methods
    //***********************

    /**
     * Begins editing the {@code TopsoilTab}'s title by providing a
     * {@code TextField}.
     */
    private void startEditingTitle() {
        this.textField = generateTitleTextField();
        this.textField.setText(this.actualTitle.get());
        this.textField.selectAll();

        this.titleLabel.setGraphic(this.textField);
        this.titleLabel.setText(null);
    }

    /**
     * Saves the text in the title {@code TextField} to the
     * {@code TopsoilTab}.
     */
    private void finishEditingTitle() {
        this.setTitle(this.textField.getText());
//        this.titleLabel.setGraphic(this.isotopeLabel);
        this.titleLabel.setGraphic(null);
        this.textField = null;
    }

    /**
     * Sets the title of the {@code TopsoilTab} and the
     * {@code TopsoilDataTable} to the provided {@code String}.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.titleLabel.setText(this.isotopePrefix + title);
        this.actualTitle.set(title);
    }

    /**
     * Generates a {@code TextField} for editing the tab's title.
     *
     * @return  the generated TextField
     */
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
     * Returns the {@code TopsoilTableController} that manages both the data and this tab's content.
     *
     * @return  TopsoilTableController
     */
    public TopsoilTableController getTableController() {
        return tableController;
    }

    /**
     * Returns the content of this tab.
     *
     * @return  TopsoilTabContent
     */
    public TopsoilTabContent getTabContent() {
        return tabContent;
    }

    /**
     * Adds a new {@code Command} to this {@code TopsoilTab}'s {@link UndoManager}.
     *
     * @param command   the Command to add
     */
    public void addUndo(Command command) {
        this.undoManager.add(command);
    }

    /**
     * Undoes the last executed {@link Command} in this {@code TopsoilTab}'s
     * {@link UndoManager}.
     */
    public void undo() {
        this.undoManager.undo();
    }

    /**
     * Gets a short message describing the last executed {@link Command} in
     * this {@code TopsoilTab}'s {@link UndoManager}.
     *
     * @return  a short description of the last executed command
     */
    public String getLastUndoMessage() {
        return this.undoManager.getUndoName();
    }

    /**
     * Re-executes the last undone {@link Command} in this {@code TopsoilTab}'s
     * {@link UndoManager}.
     */
    public void redo() {
        this.undoManager.redo();
    }

    /**
     * Gets a short message describing the last undone {@link Command} in
     * this {@code TopsoilTab}'s {@link UndoManager}.
     *
     * @return  a short description of the last undone command
     */
    public String getLastRedoMessage() {
        return this.undoManager.getRedoName();
    }
}
