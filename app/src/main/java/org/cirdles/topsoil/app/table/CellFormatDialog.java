package org.cirdles.topsoil.app.table;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.util.TopsoilException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CellFormatDialog extends Dialog<String> {

	private CellFormatDialog(Map<CellFormatOption, Object> options) throws TopsoilException {
		super();

		CellFormatController controller = new CellFormatController();

		if (options != null) {
			controller.setScientificNotation((Boolean) options.get(CellFormatOption.SCI_NOTATION));
			controller.setPlacesAfterSeparator((Integer) options.get(CellFormatOption.NUM_PLACES_AFTER_SEPARATOR));
		}

		this.getDialogPane().setContent(controller);
		this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		stage.initOwner(MainWindow.getPrimaryStage());
		stage.getIcons().add(MainWindow.getWindowIcon());

		setResultConverter(value -> {
			if (value == ButtonType.OK) {
				return controller.getFormat();
			} else {
				return null;
			}
		});
	}

	public static String open(Map<CellFormatOption, Object> options) throws TopsoilException {
		CellFormatDialog dialog = new CellFormatDialog(options);
		dialog.setTitle("Set Cell Format");
		return dialog.showAndWait().orElse(null);
	}

	private class CellFormatController extends GridPane {

		private static final String CONTROLLER_FXML = "data-format-dialog.fxml";
		private final Double TEST_DOUBLE = 123.456789;

		private ResourceExtractor resourceExtractor = new ResourceExtractor(CellFormatController.class);
		private DecimalFormat df;

		@FXML private Label dataLabel, viewLabel;
		@FXML private HBox sciNotationDemoView;
		@FXML private TextField afterSeparatorTextField;
		@FXML private CheckBox sciNotationCheckBox;

		private CellFormatController() throws TopsoilException {
			try {
				FXMLLoader loader = new FXMLLoader(resourceExtractor.extractResourceAsPath(CONTROLLER_FXML)
						                                   .toUri().toURL());
				loader.setRoot(this);
				loader.setController(this);
				loader.load();
			} catch (IOException e) {
				throw new TopsoilException("Could not load " + CONTROLLER_FXML + ".", e);
			}
		}

		@FXML
		public void initialize() {
			// Defaults
			sciNotationCheckBox.setSelected(false);
			sciNotationDemoView.setVisible(false);
			sciNotationCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
				sciNotationDemoView.setVisible(newValue);
				update();
			});
			afterSeparatorTextField.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					Integer.parseInt(newValue);
					update();
				} catch (NumberFormatException e) {
					afterSeparatorTextField.setText(oldValue);
				}
			});
		}

		public String getFormat() {
			StringBuilder pattern = new StringBuilder();
			pattern.append("0");
			pattern.append(Separator.DECIMAL);
			pattern.append("0");

			try {
				int numPlaces = Integer.parseInt(afterSeparatorTextField.getText());
				if (numPlaces <= 1) {
					pattern.append("0########");
				} else {
					pattern.append(new String(new char[numPlaces - 1]).replace("\0", "#"));
				}
			} catch (NumberFormatException e) {
				pattern.append("0########");
			}

			if (sciNotationCheckBox.isSelected()) {
				pattern.append(Separator.EXP);
				pattern.append("0");
			}

			return pattern.toString();
		}

		void setScientificNotation(boolean b) {
			sciNotationCheckBox.setSelected(b);
		}

		void setPlacesAfterSeparator(int i) {
			afterSeparatorTextField.setText(Integer.toString(i));
		}

		private void update() {
			df = new DecimalFormat(getFormat());

			dataLabel.setText(Double.toString(TEST_DOUBLE));
			viewLabel.setText(df.format(TEST_DOUBLE));
		}
	}

	public enum CellFormatOption {
		SCI_NOTATION,
		RESTRICT_PLACES,
		NUM_PLACES_AFTER_SEPARATOR
	}

	public enum Separator {
		DECIMAL("."),
		COMMA(","),
		EXP("E");

		String value;

		Separator(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
