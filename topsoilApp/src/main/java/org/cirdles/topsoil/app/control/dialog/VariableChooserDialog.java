package org.cirdles.topsoil.app.control.dialog;

import com.google.common.collect.BiMap;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.Main;
import org.cirdles.topsoil.app.control.DeselectableRadioButton;
import org.cirdles.topsoil.app.data.column.DataColumn;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jake Marotta
 */
public class VariableChooserDialog extends Dialog<Map<Variable<?>, DataColumn<?>>> {

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    private VariableChooserDialog(DataTable table, List<Variable> required) {
        super();

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().add(Main.getController().getTopsoilLogo());
        stage.initOwner(Main.getController().getPrimaryStage());
        stage.setTitle("Variable Chooser");
        stage.setResizable(true);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label messageLabel = new Label("Choose which variables to associate with each column.");
        messageLabel.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        VariableChooser chooser = new VariableChooser(table);
        chooser.setPrefWidth(500.0);

        VBox container = new VBox(messageLabel, chooser);
        container.setAlignment(Pos.TOP_CENTER);

        this.getDialogPane().setContent(container);

        // The Scene doesn't seem to be completely done laying out its Nodes by the time this event is fired. Since
        // Platform.runLater() isn't being used extensively elsewhere, this works fine. If that changes, this may
        // have to be changed, as well.
//        this.setOnShown(event -> Platform.runLater(() ->  {
//            if (stage.getWidth() > 800.0) {
//                stage.setWidth(800.0);
//            }
//            if (stage.getHeight() > 600.0) {
//                stage.setHeight(600.0);
//            }
//        }));

        this.setResultConverter(result -> {
            if (result == ButtonType.OK) {
                return chooser.getSelections();
            } else {
                return null;
            }
        });
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    public static Map<Variable<?>, DataColumn<?>> showDialog(DataTable table, List<Variable> required) {
        return new VariableChooserDialog(table, required).showAndWait().orElse(null);
    }

	public static class VariableChooser extends HBox {

		private static final double ROW_HEIGHT = 32.0;
		private static final double COL_WIDTH = 100.0;

		private Map<Variable<?>, ToggleGroup> variableToggleGroups = new HashMap<>();
		private List<List<ColumnRadioButton>> buttonColumns = new ArrayList<>();
		private GridPane gridPane;
		private VBox variableLabelBox;

		public VariableChooser(DataTable table) {
			super();
			this.variableLabelBox = makeLabelBox();
			this.gridPane = makeGrid(table);
			this.setFillHeight(true);
			ScrollPane scrollPane = new ScrollPane(gridPane);
			scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
			scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			this.getChildren().addAll(
					variableLabelBox,
					scrollPane
			);
			HBox.setHgrow(variableLabelBox, Priority.NEVER);
		}

		public Map<Variable<?>, DataColumn<?>> getSelections() {
			Map<Variable<?>, DataColumn<?>> selections = new HashMap<>();
			ColumnRadioButton button;
			for (Map.Entry<Variable<?>, ToggleGroup> entry : variableToggleGroups.entrySet()) {
				button = (ColumnRadioButton) entry.getValue().getSelectedToggle();
				if (button != null) {
					selections.put(entry.getKey(), button.getColumn());
				}
			}
			return selections;
		}

		private GridPane makeGrid(DataTable table) {
			List<DataColumn<?>> columns = new ArrayList<>();
			for (DataColumn<?> column : table.getColumnTree().getLeafNodes()) {
				if (column.isSelected()) {
					columns.add(column);
				}
			}
			int colDepth = table.getColumnTree().getDepth();
			GridPane grid = new GridPane();
			Label label;
			for (int rowIndex = 0; rowIndex < colDepth - 1; rowIndex++) {
				for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
					// TODO Account for data categories properly
					label = new Label("-----");
					label.setPrefSize(COL_WIDTH, ROW_HEIGHT);
					grid.add(new Label("-----"),colIndex, rowIndex);
				}
			}
			for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
				label = new Label(columns.get(colIndex).getLabel());
				label.setPrefSize(COL_WIDTH, ROW_HEIGHT);
				label.setAlignment(Pos.BOTTOM_CENTER);
				label.setBorder(new Border(new BorderStroke(
						Paint.valueOf("#cccccc"),
						BorderStrokeStyle.SOLID,
						null,
						new BorderWidths(0.0, 0.0, 1.0, 0.0)
				)));
				grid.add(label, colIndex, colDepth - 1);
				buttonColumns.add(new ArrayList<>());
			}
			BiMap<Variable<?>, DataColumn<?>> varMap = table.getVariableColumnMap();
			ToggleGroup group;
			for (int rowIndex = colDepth; rowIndex < Variables.ALL.size() + colDepth; rowIndex++) {
				Variable<?> variable = Variables.ALL.get(rowIndex - colDepth);
				group = new ToggleGroup();
				for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
					DataColumn<?> column = columns.get(colIndex);
					ColumnRadioButton button = new ColumnRadioButton(column);
					button.setPrefHeight(ROW_HEIGHT);
					button.setGraphicTextGap(0.0);
					grid.add(button, colIndex, rowIndex);
					GridPane.setHalignment(button, HPos.CENTER);

					if (varMap.containsValue(column) && variable.equals(varMap.inverse().get(column))) {
						button.setSelected(true);
					}

					group.getToggles().add(button);
					buttonColumns.get(colIndex).add(button);

					int c = colIndex;
					button.selectedProperty().addListener(((observable, oldValue, newValue) -> {
						if (newValue) {
							for (RadioButton rB : buttonColumns.get(c)) {
								if (rB != button) {
									rB.setSelected(false);
								}
							}
						}
					}));
				}
				variableToggleGroups.put(variable, group);
			}
			return grid;
		}

		private VBox makeLabelBox() {
			VBox vBox = new VBox();
			vBox.setAlignment(Pos.BOTTOM_RIGHT);
			vBox.setPadding(new Insets(0.0, 5.0, 15.0, 5.0));
			Label label;
			for (Variable<?> variable : Variables.ALL) {
				label = new Label(variable.getName());
				label.setPrefSize(COL_WIDTH, ROW_HEIGHT);
				label.setMinSize(COL_WIDTH, ROW_HEIGHT);
				label.setAlignment(Pos.CENTER_RIGHT);
				vBox.getChildren().add(label);
			}
			return vBox;
		}

		private class ColumnRadioButton extends DeselectableRadioButton {
			private DataColumn<?> column;
			public ColumnRadioButton(DataColumn<?> column) {
				this.column = column;
			}
			public DataColumn<?> getColumn() {
				return column;
			}
		}

	}
}
