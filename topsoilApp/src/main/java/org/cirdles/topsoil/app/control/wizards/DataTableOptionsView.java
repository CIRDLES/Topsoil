package org.cirdles.topsoil.app.control.wizards;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.control.tree.ColumnTreeView;
import org.cirdles.topsoil.app.data.DataTable;
import org.cirdles.topsoil.uncertainty.Uncertainty;
import org.cirdles.topsoil.isotope.IsotopeSystem;

import java.io.IOException;

/**
 * Controller for a screen that allows the user to preview their imported model, as well as choose an {@link
 * Uncertainty} and {@link IsotopeSystem} for each table.
 *
 * @author marottajb
 */
public class DataTableOptionsView extends VBox {

	//**********************************************//
	//                  CONSTANTS                   //
	//**********************************************//

	private static final String CONTROLLER_FXML = "data-table-options.fxml";

	//**********************************************//
	//                   CONTROLS                   //
	//**********************************************//

	@FXML private AnchorPane columnViewPane;
	@FXML private Label uncLabel;
	@FXML ComboBox<Uncertainty> unctComboBox;
	@FXML ComboBox<IsotopeSystem> isoComboBox;

	//**********************************************//
	//                  ATTRIBUTES                  //
	//**********************************************//

	private DataTable table;

	//**********************************************//
	//                 CONSTRUCTORS                 //
	//**********************************************//

	DataTableOptionsView(DataTable table) {
		super();
		this.table = table;
		final ResourceExtractor re = new ResourceExtractor(DataTableOptionsView.class);

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
		ColumnTreeView treeView = new ColumnTreeView(table.getColumnTree());
		columnViewPane.getChildren().add(treeView);
		AnchorPane.setTopAnchor(treeView, 0.0);
		AnchorPane.setRightAnchor(treeView, 0.0);
		AnchorPane.setBottomAnchor(treeView, 0.0);
		AnchorPane.setLeftAnchor(treeView, 0.0);

		unctComboBox.getItems().addAll(Uncertainty.values());
		unctComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			table.setUnctFormat(newValue);
		});
		isoComboBox.getItems().addAll(IsotopeSystem.values());
		isoComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->  {
			table.setIsotopeSystem(newValue);
		});
	}

	//**********************************************//
	//                PUBLIC METHODS                //
	//**********************************************//

    public DataTable getDataTable() {
		return table;
	}

}
