package org.cirdles.topsoil.app.tab;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import org.cirdles.topsoil.plot.TopsoilPlotType;
import org.cirdles.topsoil.app.table.ObservableTableData;
import org.cirdles.topsoil.app.util.undo.Command;
import org.cirdles.topsoil.app.util.undo.UndoManager;

/**
 *
 * @author sbunce
 * @see Tab
 * @see TopsoilDataView
 * @see TopsoilTabPane
 */
public class TopsoilTab extends Tab {

    private static int UNDO_HISTORY_MAX_SIZE = 50;

    /**
     * The {@code TopsoilDataView} containing the content for this tab.
     */
    private TopsoilDataView dataView;
    /**
     * An {@code UndoManager} that handles changes to the table in this tab's content.
     */
    private UndoManager undoManager;

    /**
     * A {@code String} consisting of the abbreviation of the {@code IsotopeSystem} of this tab's table and a hyphen. It
     * serves as a prefix to the actual title of the tab, to quickly identify the {@code IsotopeSystem} of the tab's data.
     */
    private String isotopePrefix;

    /**
     * A {@code StringProperty} containing the real title of the tab, which is also the title of the corresponding
     * {@code TopsoilDataTable}.
     */
    private SimpleStringProperty title;

    /**
     * The {@code Label} that conveys the tab's title.
     */
    private Label titleLabel;

    /**
     * A {@code TextField} for editing the tab's title.
     */
    private TextField textField;

    //***********************
    // Constructors
    //***********************

    public TopsoilTab(TopsoilDataView view) {
        this.dataView = view;
        ObservableTableData data = view.getData();

        setContent(view);

        this.undoManager = new UndoManager(UNDO_HISTORY_MAX_SIZE);

        this.isotopePrefix = data.getIsotopeType().getAbbreviation() + " - ";

        this.title = new SimpleStringProperty(data.getTitle());
        this.title.bindBidirectional(data.titleProperty()); // bind to TopsoilDataTable nameProperty

        data.isotopeTypeProperty().addListener(c -> {
            isotopePrefix = data.getIsotopeType().getAbbreviation() + " - ";
            setTitle(this.title.get());
        });

        this.titleLabel = new Label(isotopePrefix + title.get());
        this.titleLabel.setId("Title");
        this.titleLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {
                startEditingTitle();
                textField.requestFocus();
            }
        });

        this.setGraphic(titleLabel);

        // On tab close, close any plots associated with the table.
        this.setOnClosed(event -> closeTabPlots() );
    }

    public void closeTabPlots() {
        if (! dataView.getData().getOpenPlots().isEmpty()) {
            for (TopsoilPlotType type : dataView.getData().getOpenPlots().keySet()) {
                dataView.getData().removePlot(type);
            }
        }
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
        this.textField.setText(this.title.get());
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
        this.title.set(title);
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
     * Returns the content of this tab.
     *
     * @return  TopsoilDataView
     */
    public TopsoilDataView getDataView() {
        return dataView;
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
