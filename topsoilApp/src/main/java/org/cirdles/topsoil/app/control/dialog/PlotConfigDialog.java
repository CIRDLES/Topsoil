package org.cirdles.topsoil.app.control.dialog;

import javafx.beans.binding.Bindings;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.app.ProjectManager;
import org.cirdles.topsoil.app.Topsoil;
import org.cirdles.topsoil.app.control.FXMLUtils;
import org.cirdles.topsoil.app.data.FXDataTable;
import org.cirdles.topsoil.app.data.TopsoilProject;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.javafx.SingleChildRegion;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotOption;

import java.io.IOException;
import java.util.*;

public class PlotConfigDialog extends Dialog<Map<PlotConfigDialog.Key, Object>> {

    public PlotConfigDialog(FXDataTable table) {
        this(table, null);
    }

    public PlotConfigDialog(FXDataTable table, Map<Key, Object> settings) {
        PlotConfigDialogPane dialogPane = new PlotConfigDialogPane(table, settings);
        this.setDialogPane(dialogPane);
        this.setTitle("Plot Configuration");

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().addAll(Topsoil.getLogo());
        stage.setOnShown(event -> stage.requestFocus());
        stage.setResizable(true);

        this.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.APPLY) {
                Map<Key, Object> results = new LinkedHashMap<>();
                results.put(Key.VARIABLE_MAP, dialogPane.getVariableSelections());
                results.put(Key.ISOTOPE_SYSTEM, dialogPane.getIsotopeSystem());
                return results;
            }
            return null;
        });
    }

    public enum Key {
        VARIABLE_MAP,
        ISOTOPE_SYSTEM,
    }

    private class PlotConfigDialogPane extends DialogPane {

        private static final String CONTROLLER_FXML = "plot-config-dialog-pane.fxml";

        private FXDataTable table;
        private Map<Key, Object> preSettings;
        private MapProperty<Variable<?>, DataColumn<?>> selections = new SimpleMapProperty<>(FXCollections.observableHashMap());
        private Map<Variable<?>, SelectionEntry> selectionEntries = new LinkedHashMap<>();
        private Map<DataColumn<?>, LeafColumnControl> leafColumnGraphics = new LinkedHashMap<>();

        @FXML private TreeView<DataColumn<?>> columnTreeView;
        @FXML private ListView<SelectionEntry> variableListView;
        @FXML private Button removeButton, useExistingButton, classicButton; //setAllButton;
        @FXML private ComboBox<IsotopeSystem> isotopeSystemComboBox;

        public PlotConfigDialogPane(FXDataTable table, Map<Key, Object> settings) {
            this.table = table;
            this.preSettings = (settings != null) ? settings : new LinkedHashMap<>(0 );
            try {
                FXMLUtils.loadController(CONTROLLER_FXML, PlotConfigDialogPane.class, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        public void initialize() {
            TreeItem<DataColumn<?>> rootItem = new TreeItem<>();
            columnTreeView.setRoot(rootItem);
            columnTreeView.setShowRoot(false);
            columnTreeView.setCellFactory(param -> new ColumnTreeViewCell());

            //regular for loop
            for (DataColumn<?> column : table.getColumns()) {
                TreeItem<DataColumn<?>> item = createColumnItem(column);
                if (item != null) {
                    //if has dependent{
                    // skip[ next}
                    rootItem.getChildren().add(item);
                }
            }

            variableListView.setCellFactory(param -> new ListCell<SelectionEntry>() {
                    @Override
                    protected void updateItem(SelectionEntry item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(String.format(
                                    "%s => %s",
                                    item.variable.getAbbreviation(),
                                    item.column.getTitle()
                            ));
                        } else {
                            setText("");
                        }
                    }
            });

            // When variable selections are updated, update the entries for the variableListView
            selections.addListener((MapChangeListener<Variable<?>, DataColumn<?>>) c -> {
                SelectionEntry selectionEntry = selectionEntries.get(c.getKey());
                Variable<?> variable = c.getKey();
                DataColumn<?> column;
                if (c.wasAdded()) {
                    column = c.getValueAdded();
                    if (selectionEntry == null) {
                        selectionEntry = new SelectionEntry(variable, column);
                        selectionEntries.put(variable, selectionEntry);
                        variableListView.getItems().add(selectionEntry);
                    }
                    if (column == null) {
                        variableListView.getItems().remove(selectionEntry);
                    } else {
                        selectionEntry.column = column;
                    }
                }
                if (c.wasRemoved()) {
                    if (! c.getMap().containsKey(variable)) {
                        selectionEntries.remove(variable);
                        variableListView.getItems().remove(selectionEntry);
                    }
                }

                // Sort selections and refresh cells
                variableListView.getItems().sort(
                        Comparator.comparingInt(entry -> Variable.CLASSIC.indexOf(entry.variable))
                );
                variableListView.refresh();
            });
            if (preSettings.get(Key.VARIABLE_MAP) != null) {
                Map<Variable<?>, DataColumn<?>> map = (Map<Variable<?>, DataColumn<?>>) preSettings.get(Key.VARIABLE_MAP);
                for (Variable<?> variable : Variable.CLASSIC) {
                    select(variable, map.get(variable));
                }
            }

            removeButton.disableProperty().bind(
                    variableListView.getSelectionModel().selectedItemProperty().isNull()
            );

            useExistingButton.disableProperty().bind(ProjectManager.getProject().getPlotMap().get(table).emptyProperty());

            isotopeSystemComboBox.getItems().addAll(IsotopeSystem.values());
            if (preSettings.get(Key.ISOTOPE_SYSTEM) != null) {
                isotopeSystemComboBox.getSelectionModel().select((IsotopeSystem) preSettings.get(Key.ISOTOPE_SYSTEM));
            } else {
                isotopeSystemComboBox.getSelectionModel().select(IsotopeSystem.GENERIC);
            }
        }

        private Map<Variable<?>, DataColumn<?>> getVariableSelections() {
            return selections;
        }

        private IsotopeSystem getIsotopeSystem() {
            return isotopeSystemComboBox.getSelectionModel().getSelectedItem();
        }

        //**********************************************//
        //                PRIVATE METHODS               //
        //**********************************************//

        private void select(Variable<?> variable, DataColumn<?> column) {
            selections.put(variable, column);
            if (leafColumnGraphics.get(column) != null) {
                leafColumnGraphics.get(column).variableComboBox.setValue(variable);
            }
            if (column.getDependentColumn() != null) {
                if (variable == Variable.X) {
                    select(Variable.X, column.getDependentColumn());
                }
            }
        }

        private void deselect(Variable<?> variable) {
            selections.remove(variable);
        }

        private void setVariableSelections(Map<Variable<?>, DataColumn<?>> variableMap) {
            selections.clear();
            selections.putAll(variableMap);
        }

        private void setIsotopeSystem(IsotopeSystem isotopeSystem) {
            if (isotopeSystem != null) {
                isotopeSystemComboBox.getSelectionModel().select(isotopeSystem);
            }
        }

        private TreeItem<DataColumn<?>> createColumnItem(DataColumn<?> column) {
            TreeItem<DataColumn<?>> treeItem = null;
            if (column.countChildren() > 0) {
                // column group
                List<TreeItem<DataColumn<?>>> children = new ArrayList<>(column.countChildren());
                TreeItem<DataColumn<?>> childItem;

                //same as 104
                for (DataColumn<?> child : column.getChildren()) {
                    childItem = createColumnItem(child);
                    if (childItem != null) {
                        children.add(childItem);
                    }
                }
                // only create the tree item if it has children
                if (children.size() > 0) {
                    treeItem = new TreeItem<>(column);
                    treeItem.getChildren().addAll(children);
                }
            } else if (column.isSelected()) {
                // leaf column
                treeItem = new TreeItem<>(column);
                leafColumnGraphics.put(column, new LeafColumnControl(column));
            }
            return treeItem;
        }

        @FXML
        private void removeButtonAction() {
            SelectionEntry entry = variableListView.getSelectionModel().getSelectedItem();
            deselect(entry.variable);
        }

        @FXML
        private void useExistingButtonAction() {
            Plot existingPlot =
                    new ExistingPlotDialog(ProjectManager.getProject(), table).showAndWait().orElse(null);
            if (existingPlot != null) {
                setVariableSelections(existingPlot.getVariableMap());
                setIsotopeSystem((IsotopeSystem) existingPlot.getOptions().get(PlotOption.ISOTOPE_SYSTEM));
            }
        }

        /**
         * Selects the first available leaf columns to correspond to X, SIGMA_X, Y, SIGMA_Y, and RHO variables,
         * respectively.
         */
        @FXML
        private void classicButtonAction() {
            selections.clear();
            List<? extends DataColumn<?>> leafColumns = table.getLeafColumns();
            DataColumn<?> column;
            int index = 0, numSelected = 0;
            while (numSelected < Math.min(Variable.CLASSIC.size(), leafColumns.size())) {
                column = leafColumns.get(index);
                if (column.isSelected()) {
                    select(Variable.CLASSIC.get(numSelected), column);
                    numSelected++;
                }
                index++;
            }
        }

//        @FXML
//        private void setAllButtonAction() {
//            Variable<?> variable;
//            for (Map.Entry<DataColumn<?>, LeafColumnControl> entry : leafColumnGraphics.entrySet()) {
//                variable = entry.getValue().variableComboBox.getValue();
//                if (variable != null) {
//                    select(variable, entry.getKey());
//                }
//            }
//        }

        private class SelectionEntry {
            private final Variable<?> variable;
            private DataColumn<?> column;

            SelectionEntry(Variable<?> variable, DataColumn<?> column) {
                Validate.notNull(variable, "Variable cannot be null.");
                Validate.notNull(column, "Column cannot be null.");

                this.variable = variable;
                this.column = column;
            }
        }

        private class ColumnTreeViewCell extends TreeCell<DataColumn<?>> {

            private ColumnTreeViewCell() {
                super();
                this.setContentDisplay(ContentDisplay.RIGHT);
            }

            @Override
            protected void updateItem(DataColumn<?> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                    setGraphic(null);
                    return;
                }

                if (item.countChildren() == 0) {
                    setText("");
                    LeafColumnControl graphic = leafColumnGraphics.get(item);
                    if (graphic == null) {
                        //graphic = new LeafColumnControl(item);  // Only create a new graphic if necessary
                        graphic = new HBox(new LeafColumnControl(item), new LeafColumnControl(item.getDependentColumn();
                        leafColumnGraphics.put(item, graphic);
                    }
                    setGraphic(graphic);
                } else {
                    setText(item.getTitle());
                    setGraphic(null);
                }
            }
        }

        private class LeafColumnControl extends SingleChildRegion<HBox> {

            private static final double LABEL_WIDTH = 120.0;

            private Label label;
            private ComboBox<Variable<?>> variableComboBox;

            LeafColumnControl(DataColumn<?> column) {
                super(new HBox());

                label = new Label(column.getTitle());
                label.setMinWidth(LABEL_WIDTH);
                label.setMaxWidth(LABEL_WIDTH);
                label.setWrapText(true);

                variableComboBox = new ComboBox<>(FXCollections.observableList(Variable.nullIncludedCLASSIC)); // put null list in then classic
                variableComboBox.setCellFactory(param ->  new ListCell<Variable<?>>() {
                    @Override
                    protected void updateItem(Variable<?> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getAbbreviation());
                        } else {
                            setText("");
                        }
                    }
                });
                variableComboBox.setConverter(new StringConverter<Variable<?>>() {
                    @Override
                    public String toString(Variable<?> object) {
                        return object.getAbbreviation();
                    }

                    @Override
                    public Variable<?> fromString(String string) {
                        return Variable.variableForAbbreviation(string);
                    }
                });
                variableComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        return;
                    }
                    ComboBox<Variable<?>> otherBox;
                    for (LeafColumnControl graphic : leafColumnGraphics.values()) {
                        if (graphic == null) {
                            continue;
                        }
                        otherBox = graphic.variableComboBox;
                        if (this != graphic && newValue.equals(otherBox.getValue())) {
                            // graphic is another instance with the same variable selected, set other value to null
                            otherBox.setValue(null);
                        }
                    }
                });

                variableComboBox.setOnAction((event) -> {
                    Variable<?> variable = variableComboBox.getValue();
                    select(variable, column);
                });
                HBox container = getChild();
                container.setSpacing(5.0);
                container.getChildren().addAll(label, variableComboBox);
            }
        }

        private class ExistingPlotDialog extends Dialog<Plot> {

            private ExistingPlotDialog(TopsoilProject project, FXDataTable table) {

                ListView<Plot> plotListView = new ListView<>();
                plotListView.setCellFactory(param -> new ListCell<Plot>() {
                    @Override
                    protected void updateItem(Plot item, boolean empty) {
                        super.updateItem(item, empty);
                        setText((item != null) ? String.valueOf(item.getOptions().get(PlotOption.TITLE)) : "");
                    }
                });
                plotListView.getItems().addAll(project.getPlotMap().get(table));
                plotListView.setPrefSize(300.0, 200.0);

                DialogPane dialogPane = this.getDialogPane();
                dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.APPLY);
                dialogPane.lookupButton(ButtonType.APPLY).disableProperty().bind(
                        Bindings.createBooleanBinding(
                                () -> plotListView.getSelectionModel().getSelectedItems().isEmpty(),
                                plotListView.getSelectionModel().selectedItemProperty()
                        )
                );
                dialogPane.setContent(plotListView);

                Stage stage = (Stage) dialogPane.getScene().getWindow();
                stage.getIcons().add(Topsoil.getLogo());
                stage.setTitle("Use Existing Plot Config");
                this.initOwner(PlotConfigDialog.this.getDialogPane().getScene().getWindow());

                this.setResultConverter(buttonType -> {
                    if (buttonType == ButtonType.APPLY) {
                        return plotListView.getSelectionModel().getSelectedItem();
                    }
                    return null;
                });
            }

        }

    }
}
