package org.cirdles.topsoil.app.view.wizards;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.data.ColumnTree;
import org.cirdles.topsoil.app.data.DataColumn;
import org.cirdles.topsoil.app.data.DataRow;
import org.cirdles.topsoil.app.data.DataSegment;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.variable.Variables;

import java.io.IOException;
import java.util.*;

/**
 * Controller for a screen that allows the user to preview their imported data, as well as choose an {@link
 * UncertaintyFormat} and {@link IsotopeSystem} for each table.
 *
 * @author marottajb
 */
public class DataImportOptionsView extends VBox {

	//**********************************************//
	//                  CONSTANTS                   //
	//**********************************************//

	private static final String CONTROLLER_FXML = "data-preview.fxml";
	private static final String WARNING_ICON_PATH = "warning.png";
	private static final int SAMPLE_SIZE = 5;

	//**********************************************//
	//                   CONTROLS                   //
	//**********************************************//

	@FXML private GridPane grid;
	@FXML private Label uncLabel;
	@FXML private ComboBox<UncertaintyFormat> unctComboBox;
	@FXML private ComboBox<IsotopeSystem> isoComboBox;

	//**********************************************//
	//                  ATTRIBUTES                  //
	//**********************************************//

	private ImageView warningIcon;
	private List<ComboBox<Variable>> columnComboBoxes;
	private Map<Variable, DataColumn> variableColumnMap;
	private ColumnTree columnTree;
	private List<DataSegment> dataSegments;

	//**********************************************//
	//                  PROPERTIES                  //
	//**********************************************//

	private ObjectProperty<UncertaintyFormat> uncertaintyFormat;
	public ObjectProperty<UncertaintyFormat> uncertaintyFormatProperty() {
		if (uncertaintyFormat == null) {
			uncertaintyFormat = new SimpleObjectProperty<>();
			uncertaintyFormat.bind(unctComboBox.getSelectionModel().selectedItemProperty());
		}
		return uncertaintyFormat;
	}
	/**
	 * Returns the selected {@code UncertaintyFormat}, as indicated by the uncertainty format {@code ChoiceBox}.
	 *
	 * @return  selected UncertaintyFormat
	 */
	public UncertaintyFormat getUncertaintyFormat() {
		return uncertaintyFormatProperty().get();
	}

	private ObjectProperty<IsotopeSystem> isotopeType;
	public ObjectProperty<IsotopeSystem> isotopeTypeProperty() {
		if (isotopeType == null) {
			isotopeType = new SimpleObjectProperty<>();
			isotopeType.bind(isoComboBox.getSelectionModel().selectedItemProperty());
		}
		return isotopeType;
	}
	/**
	 * Returns the selected {@code IsotopeSystem}, as indicated by the isotope type {@code ChoiceBox}.
	 *
	 * @return  selected IsotopeSystem
	 */
	public IsotopeSystem getIsotopeType() {
		return isotopeTypeProperty().get() == null ? IsotopeSystem.GENERIC : isotopeTypeProperty().get();
	}

	//**********************************************//
	//                 CONSTRUCTORS                 //
	//**********************************************//

	DataImportOptionsView(ColumnTree columnTree, List<DataSegment> dataSegments) {
		super();

		this.columnTree = columnTree;
		this.dataSegments = dataSegments;

		final ResourceExtractor re = new ResourceExtractor(DataImportOptionsView.class);

		warningIcon = new ImageView(new Image(re.extractResourceAsPath(WARNING_ICON_PATH).toString()));
		warningIcon.setPreserveRatio(true);
		warningIcon.setFitHeight(20.0);

		FXMLLoader loader;
		try {
			loader = new FXMLLoader(re.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
			loader.setRoot(this);
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
		}
	}

	@FXML
	protected void initialize() {
		columnComboBoxes = new ArrayList<>();
		variableColumnMap = new LinkedHashMap<>(Variables.ALL.size());

		if (getUncertaintyFormat() == null) {
			uncLabel.setGraphic(warningIcon);
		}
		uncertaintyFormatProperty().addListener(c -> {
			if (getUncertaintyFormat() == null) {
				uncLabel.setGraphic(warningIcon);
			} else {
				uncLabel.setGraphic(null);
			}
		});

		unctComboBox.getItems().addAll(UncertaintyFormat.values());
		isoComboBox.getItems().addAll(IsotopeSystem.values());
		isoComboBox.getSelectionModel().select(IsotopeSystem.GENERIC);

		makeGrid();
	}

	//**********************************************//
	//                PUBLIC METHODS                //
	//**********************************************//

	public Map<Variable, DataColumn> getVariableColumnMap() {
		return variableColumnMap;
	}

	public List<DataSegment> getDataSegments() {
	    return dataSegments;
    }

    public ColumnTree getColumnTree() {
	    return columnTree;
    }

	//**********************************************//
	//               PRIVATE METHODS                //
	//**********************************************//

	/**
	 * Checks all {@code ComboBox}es other than the one that triggered this method to see if they contain the value
	 * that the recently changed {@code ComboBox} now contains. If another {@code ComboBox} with the same value is
	 * found, that {@code ComboBox}'s value is setValue to the empty option.
	 *
	 * @param   changed
	 *          the ComboBox that was changed
	 * @param   value
	 *          the Variable value of the ComboBox's new selection
	 */
	private void checkOtherComboBoxes(ComboBox<Variable> changed, Variable value) {
		for (ComboBox<Variable> cb : columnComboBoxes) {
			if (cb != changed) {
				if (cb.getValue() != null && cb.getValue().equals(value)) {
					cb.getSelectionModel().select(null);
				}
			}
		}
	}

	private void makeGrid() {
		List<DataColumn> columns = columnTree.getLeafNodes();
		List<DataRow> firstSegmentRows = dataSegments.get(0).getChildren();
		DataRow[] sampleRows = Arrays.copyOfRange(firstSegmentRows.toArray(new DataRow[]{}), 0,
												  Math.min(firstSegmentRows.size(), SAMPLE_SIZE));

		grid.setMinSize(110.0 * columns.size(), 202.0);
		grid.setMaxSize(125.0 * columns.size(), 202.0);

		// Create each column of the data preview.
		// ChoiceBox for the user to select which variable the column represents.
		for (int i = 0; i < columns.size(); i++) {
			ComboBox<Variable> comboBox = new ComboBox<>();
			comboBox.getItems().addAll(Variables.ALL);
			comboBox.setMinSize(100.0, 30.0);
			comboBox.setMaxSize(100.0, 30.0);
			comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					checkOtherComboBoxes(comboBox, newValue);
					variableColumnMap.put(newValue, columns.get(columnComboBoxes.indexOf(comboBox)));
				} else {
					variableColumnMap.remove(oldValue);
				}
			});

			grid.add(comboBox, i, 0);
			GridPane.setConstraints(comboBox, i, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority
					.NEVER, new Insets(5.0, 5.0, 5.0, 5.0));
			columnComboBoxes.add(comboBox);
		}

		Label label;
		// Set header row
		for (int i = 0; i < columns.size(); i++) {
			label = new Label(columns.get(i).getLabel());
			label.setStyle("-fx-font-weight: bold");
			label.setMinSize(100.0, 17.0);
			label.setMaxSize(100.0, 17.0);
			grid.add(label, i, 1);
			GridPane.setConstraints(label, i, 1, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER,
			                        new Insets(5.0, 5.0, 5.0, 5.0));
		}

		// Set data rows
		int rowIndex;
		for (int i = 0; i < sampleRows.length; i++) {
			rowIndex = i + 2;
			for (int j = 0; j < columns.size(); j++) {
				if (i >= sampleRows[i].size()) {
					label = new Label("0.0");
				} else {
					label = new Label(sampleRows[i].getValuePropertyForColumn(columns.get(j)).toString());
				}
				label.setFont(Font.font("Monospaced"));
				label.setMinSize(100.0, 17.0);
				label.setMaxSize(100.0, 17.0);
				grid.add(label, j, rowIndex);
				GridPane.setConstraints(label, j, rowIndex, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS,
				                        Priority.NEVER, new Insets(5.0, 5.0, 5.0, 5.0));
			}
		}
	}

}
