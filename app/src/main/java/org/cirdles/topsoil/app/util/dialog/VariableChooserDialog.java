package org.cirdles.topsoil.app.util.dialog;

import javafx.application.Platform;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.data.ObservableDataColumn;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;
import org.cirdles.topsoil.app.tab.TopsoilDataView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Jake Marotta
 */
public class VariableChooserDialog extends Dialog<Map<Variable<Number>, ObservableDataColumn>> {

    private VariableChooserDialog(List<ObservableDataColumn> columns,
                                  List<Variable<Number>> variables,
                                  Map<Variable<Number>, ObservableDataColumn> selections,
                                  List<Variable<Number>> required) {
        super();

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().add(MainWindow.getWindowIcon());
        stage.initOwner(MainWindow.getPrimaryStage());
        stage.setTitle("Variable Chooser");
        stage.setResizable(true);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label messageLabel = new Label("Choose which variables to associate with each column.");
        messageLabel.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        VariableColumnChooser chooser = new VariableColumnChooser(columns, variables, selections, required);

        VBox container = new VBox(messageLabel, chooser);
        container.setAlignment(Pos.TOP_CENTER);

        // Disable OK button if not all required variables are assigned.
        this.getDialogPane().lookupButton(ButtonType.OK).setDisable(isOkDisabled(required, chooser));
        chooser.selectionsProperty().addListener((MapChangeListener<? super Variable<Number>, ? super ObservableDataColumn>) c -> {
            this.getDialogPane().lookupButton(ButtonType.OK).setDisable(isOkDisabled(required, chooser));
        });
        this.getDialogPane().setContent(container);

        // The Scene doesn't seem to be completely done laying out its Nodes by the time this event is fired. Since
        // Platform.runLater() isn't being used extensively elsewhere, this works fine. If that changes, this may
        // have to be changed, as well.
        this.setOnShown(event -> Platform.runLater(() ->  {
            chooser.callAfterVisible();
            if (stage.getWidth() > 800.0) {
                stage.setWidth(800.0);
            }
            if (stage.getHeight() > 600.0) {
                stage.setHeight(600.0);
            }
        }));

        this.setResultConverter(result -> {
            if (result == ButtonType.OK) {
                return chooser.getSelections();
            } else {
                return null;
            }
        });
    }

    public static Map<Variable<Number>, ObservableDataColumn> showDialog(TopsoilDataView dataView,
                                                                         List<Variable<Number>> requiredVariables) {

        List<ObservableDataColumn> columns = dataView.getData().getColumns();
        List<Variable<Number>> variables = Variables.VARIABLE_LIST;
        Map<Variable<Number>, ObservableDataColumn> currentSelections = dataView.getData().variableToColumnMap();

        return new VariableChooserDialog(columns, variables, currentSelections, requiredVariables).showAndWait().orElse(null);
    }

    //Set disabled = true when required variables are not assigned
    private boolean isOkDisabled(List<Variable<Number>> required, VariableColumnChooser chooser) {
        boolean areAssigned = false;

        if (!(required == null)) {
            for (Variable<Number> v : required) {
                if (!chooser.selectionsProperty().containsKey(v)) {
                    areAssigned = true;
                    break;
                }
            }
        }

        return areAssigned;
    }

	// @TODO Create FXML for controller

	public class VariableColumnChooser extends AnchorPane {

		private static final int ROW_HEIGHT = 30;

		private GridPane buttonGrid;

		private VBox variableLabels;  // Holds all of the {@code Label}s for each variable.
		private HBox columnLabels;    // Holds all of the {@code Label}s for each column.

		private ScrollPane columnLabelScroller;   // Controls scrolling for {@link #columnLabels}.
		private ScrollPane variableLabelScroller; // Controls scrolling for {@link #variableLabels}.
		private ScrollPane gridScroller;  // Controls scrolling for {@link #buttonGrid}.

		private List<ToggleGroup> toggleRows;
		private List<FakeToggleGroup> toggleColumns;    // RadioButtons can only belong to one ToggleGroup, this works around that

		//**********************************************//
		//                  PROPERTIES                  //
		//**********************************************//

		private MapProperty<Variable<Number>, ObservableDataColumn> selectionsProperty;
		public MapProperty<Variable<Number>, ObservableDataColumn> selectionsProperty() {
			if (selectionsProperty == null) {
				selectionsProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
			}
			return selectionsProperty;
		}
		public Map<Variable<Number>, ObservableDataColumn> getSelections() {
			return selectionsProperty.get();
		}

		//**********************************************//
		//                 CONSTRUCTORS                 //
		//**********************************************//

		public VariableColumnChooser(List<ObservableDataColumn> columns,
		                             List<Variable<Number>> variables,
		                             Map<Variable<Number>, ObservableDataColumn> currentSelections,
		                             List<Variable<Number>> requiredVariables) {
			super();

			initializeControls();

			// Configures each of the ToggleGroups so that no two horizontally-aligned RadioButtons can be selected at the
			// same time.
			this.toggleRows = new ArrayList<>();
			for (int i = 0; i < variables.size(); i++) {
				ToggleGroup group = new ToggleGroup();

				buttonGrid.addRow(i);
				for (int j = 0; j < columns.size(); j++) {
					RadioButton radioButton = new RadioButton("");
					radioButton.setToggleGroup(group);
					radioButton.setAlignment(Pos.CENTER);
					radioButton.setMinHeight(ROW_HEIGHT);
					radioButton.setMaxHeight(ROW_HEIGHT);
					buttonGrid.addColumn(j, radioButton);
					GridPane.setHalignment(radioButton, HPos.CENTER);
					GridPane.setValignment(radioButton, VPos.CENTER);
				}
				toggleRows.add(group);
			}

			// Configures each of the "fake" ToggleGroups so that no two vertically-aligned RadioButtons can be selected
			// at the same time.
			this.toggleColumns = new ArrayList<>();
			for (int j = 0; j < columns.size(); j++) {
				FakeToggleGroup group = new FakeToggleGroup();

				// Updates selectionsProperty. It's done with the fake toggle groups because the variables are arranged
				// vertically, and if a selection is made that de-selects a Toggle in the same column, there will still
				// exist a key in selectionsProperty for the old variable assignment.
				group.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
					int rowIndex;
					int columnIndex = toggleColumns.indexOf(group);
					if (oldValue != null || newValue == null) {         // Removes old key from selectionsProperty
						rowIndex = group.getToggles().indexOf(oldValue);
						selectionsProperty().remove(variables.get(rowIndex));
					}
					if (newValue != null) {                             // Puts new key into selectionsProperty
						rowIndex = group.getToggles().indexOf(newValue);
						selectionsProperty().put(variables.get(rowIndex), columns.get(columnIndex));
					}
				}));

				for (int i = 0; i < variables.size(); i++) {
					group.addToggle((RadioButton) getNodeByRowColumnIndex(i, j, buttonGrid));
				}

				toggleColumns.add(group);
			}

			// Selects the current variable-column assignments
			for (Map.Entry<Variable<Number>, ObservableDataColumn> entry : currentSelections.entrySet()) {
				RadioButton toggle = (RadioButton) getNodeByRowColumnIndex(variables.indexOf(entry.getKey()),
				                                                           columns.indexOf(entry.getValue()),
				                                                           buttonGrid);
				toggle.setSelected(true);
			}

			// Configures each variable name label
			for (int i = 0; i < variables.size(); i++) {
				Label label = new Label(variables.get(i).getName() + ": ");
				label.setMinHeight(ROW_HEIGHT);
				label.setMaxHeight(ROW_HEIGHT);
				label.setMinWidth(Region.USE_PREF_SIZE);
				label.setPadding(new Insets(0.0, 5.0, 0.0, 5.0));
				label.setAlignment(Pos.CENTER_RIGHT);
				variableLabels.getChildren().add(label);
				VBox.setVgrow(label, Priority.NEVER);
				buttonGrid.getRowConstraints().add(new RowConstraints());
				buttonGrid.getRowConstraints().get(i).minHeightProperty().bind(label.heightProperty());
			}

			// Configures each column name label
			for (int i = 0; i < columns.size(); i++) {
				Label label = new Label(columns.get(i).getHeader());
				label.setMinHeight(ROW_HEIGHT);
				label.setMaxHeight(ROW_HEIGHT);
				label.setMinWidth(Region.USE_PREF_SIZE);
				label.setPadding(new Insets(0.0, 10.0, 0.0, 10.0));
				columnLabels.getChildren().add(label);
				HBox.setHgrow(label, Priority.NEVER);
				buttonGrid.getColumnConstraints().add(new ColumnConstraints());
				buttonGrid.getColumnConstraints().get(i).minWidthProperty().bind(label.widthProperty());
			}

			if (requiredVariables != null) {
				// Manages color-changing for required variables
				for (Variable<Number> req : requiredVariables) {
					variableLabels.getChildren().get(variables.indexOf(req)).setStyle("-fx-text-fill: red");
				}
				selectionsProperty().addListener((MapChangeListener<? super Variable<Number>, ? super ObservableDataColumn>) c -> {
					if (c.wasAdded()) {
						Label label = (Label) variableLabels.getChildren().get(variables.indexOf(c.getKey()));
						if (requiredVariables.contains(c.getKey())) {
							label.setStyle("-fx-text-fill: black");
						}
					} else if (c.wasRemoved()) {
						Label label = (Label) variableLabels.getChildren().get(variables.indexOf(c.getKey()));
						if (requiredVariables.contains(c.getKey())) {
							label.setStyle("-fx-text-fill: red");
						}
					}
				});
			}
		}

		//**********************************************//
		//                PUBLIC METHODS                //
		//**********************************************//

		/**
		 * This method needs to be run after the Scene has been displayed so that the scrolling label boxes will
		 * appropriately adjust whenever the selector's {@link ScrollBar}s appear. As-is, it will not work if called
		 * beforehand, because the pixel width and height of these properties will not have been calculated.
		 b     */
		public void callAfterVisible() {

			AtomicReference<ScrollBar> vBarReference = new AtomicReference<>(null);
			AtomicReference<ScrollBar> hBarReference = new AtomicReference<>(null);
			for (Node n : gridScroller.lookupAll(".scroll-bar")) {
				if (n instanceof ScrollBar) {
					ScrollBar bar = (ScrollBar) n;
					if (bar.getOrientation().equals(Orientation.VERTICAL)) {
						vBarReference.set(bar);
					}
					if (bar.getOrientation().equals(Orientation.HORIZONTAL)) {
						hBarReference.set(bar);
					}
				}
			}

			// Adjust padding to account for offset of vertical scrollbar
			if (vBarReference.get().isVisible()) {
				columnLabels.setPadding(new Insets(0.0, vBarReference.get().getWidth(), 0.0, 0.0));
			} else {
				columnLabels.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));
			}
			vBarReference.get().visibleProperty().addListener(((observable, oldValue, newValue) -> {
				if (newValue) {
					columnLabels.setPadding(new Insets(0.0, vBarReference.get().getWidth(), 0.0, 0.0));
				} else {
					columnLabels.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));
				}
			}));

			// Adjust padding to account for offset of horizontal scrollbar
			if (hBarReference.get().isVisible()) {
				variableLabels.setPadding(new Insets(0.0, 0.0, hBarReference.get().getHeight(), 0.0));
			} else {
				variableLabels.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));
			}
			hBarReference.get().visibleProperty().addListener(((observable, oldValue, newValue) -> {
				if (newValue) {
					variableLabels.setPadding(new Insets(0.0, 0.0, hBarReference.get().getHeight(), 0.0));
				} else {
					variableLabels.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));
				}
			}));
		}

		//**********************************************//
		//               PRIVATE METHODS                //
		//**********************************************//

		/**
		 * Retrieves a {@code Node} from the specified {@code GridPane} by its row and column indices, if it exists.
		 * <p>
		 * Adapted from invariant's Stack Overflow answer (https://stackoverflow.com/questions/20825935/javafx-get-node-by-row-and-column)
		 *
		 * @param row   the row index of the desired node
		 * @param column    the column index of the desired node
		 * @param gridPane  the GridPane containing the desired node
		 * @return  hopefully, the desired node; otherwise null
		 */
		private Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
			Node result = null;
			ObservableList<Node> children = gridPane.getChildren();

			Integer nodeRow;
			Integer nodeCol;
			for (Node node : children) {

				nodeRow = GridPane.getRowIndex(node);
				nodeCol = GridPane.getColumnIndex(node);

				if (nodeRow != null && nodeCol != null) {
					if (nodeRow == row && nodeCol == column) {
						result = node;
						break;
					}
				}
			}

			return result;
		}

		private void initializeControls() {

			GridPane outerGrid = new GridPane();

			variableLabels = new VBox();
			variableLabels.setAlignment(Pos.CENTER_RIGHT);
			variableLabelScroller = new ScrollPane(variableLabels);
			variableLabelScroller.setMinWidth(USE_PREF_SIZE);
			variableLabelScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			variableLabelScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			outerGrid.add(variableLabelScroller, 0, 1);
			GridPane.setValignment(variableLabelScroller, VPos.TOP);

			columnLabels = new HBox();
			columnLabelScroller = new ScrollPane(columnLabels);
			columnLabelScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			columnLabelScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			outerGrid.add(columnLabelScroller, 1, 0);
			GridPane.setHalignment(columnLabelScroller, HPos.LEFT);

			buttonGrid = new GridPane();
			buttonGrid.setGridLinesVisible(true);
			AnchorPane gridAnchor = new AnchorPane(buttonGrid);
			AnchorPane.setTopAnchor(buttonGrid, 0.0);
			AnchorPane.setLeftAnchor(buttonGrid, 0.0);
			gridScroller = new ScrollPane(gridAnchor);
			outerGrid.add(gridScroller, 1, 1);
			GridPane.setHalignment(gridScroller, HPos.LEFT);
			GridPane.setValignment(gridScroller, VPos.TOP);

			ColumnConstraints column1 = new ColumnConstraints();
			column1.setHgrow(Priority.NEVER);
			ColumnConstraints column2 = new ColumnConstraints();
			column2.setHgrow(Priority.NEVER);
			outerGrid.getColumnConstraints().addAll(column1, column2);

			RowConstraints row1 = new RowConstraints();
			row1.setVgrow(Priority.NEVER);
			RowConstraints row2 = new RowConstraints();
			row2.setVgrow(Priority.NEVER);
			outerGrid.getRowConstraints().addAll(row1, row2);

			this.getChildren().add(outerGrid);
			AnchorPane.setBottomAnchor(outerGrid, 0.0);
			AnchorPane.setLeftAnchor(outerGrid, 0.0);
			AnchorPane.setTopAnchor(outerGrid, 0.0);
			AnchorPane.setRightAnchor(outerGrid, 0.0);

			columnLabelScroller.hvalueProperty().bindBidirectional(gridScroller.hvalueProperty());
			variableLabelScroller.vvalueProperty().bindBidirectional(gridScroller.vvalueProperty());
		}
	}

	/**
	 * A quickly-written class that acts as a {@link ToggleGroup}. Once created, {@code Toggle}s can be added to it.
	 * If a {@link Toggle} within the {@code FakeToggleGroup} is selected, it deselects the other {@code Toggle}s in
	 * the group. This is needed because {@code Toggle}s can't belong to more than one {@code ToggleGroup} at time of
	 * writing.
	 */
	private class FakeToggleGroup {

		private ObservableList<Toggle> toggles;

		/**
		 * Holds the currently selected {@code Toggle}.
		 */
		private ObjectProperty<Toggle> selectedToggleProperty;
		ObjectProperty<Toggle> selectedToggleProperty() {
			if (selectedToggleProperty == null) {
				selectedToggleProperty = new SimpleObjectProperty<>(null);
			}
			return selectedToggleProperty;
		}
		private Toggle getSelectedToggle() {
			return selectedToggleProperty().get();
		}
		private void setSelectedToggle(Toggle t) {
			selectedToggleProperty().set(t);
		}

		/**
		 * Constructs a new {@code FakeToggleGroup}.
		 */
		FakeToggleGroup() {
			this.toggles = FXCollections.observableArrayList();
			selectedToggleProperty(); // Initialize selected property
		}

		/**
		 * Adds a {@code Toggle} to the {@code FakeToggleGroup}.
		 *
		 * @param toggle    Toggle
		 */
		void addToggle(Toggle toggle) {

			toggles.add(toggle);

			// If the new Toggle is selected already, set it as the selectedToggle
			if (toggle.isSelected()) {
				for (Toggle t : toggles) {
					if (!t.equals(toggle)) {
						t.setSelected(false);
					}
				}
				setSelectedToggle(toggle);
			}

			toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					if (getSelectedToggle() != null) {
						getSelectedToggle().setSelected(false);
					}
					selectedToggleProperty().set(toggle);
				} else {
					if (getSelectedToggle() == toggle) {
						setSelectedToggle(null);
					}
				}
			});
		}

		/**
		 * Returns the {@code Toggle}s in this {@code FakeToggleGroup}.
		 *
		 * @return
		 */
		ObservableList<Toggle> getToggles() {
			return toggles;
		}
	}

}
