package org.cirdles.topsoil.app.progress.menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.metadata.TopsoilMetadata;
import org.cirdles.topsoil.app.progress.isotope.IsotopeSelectionDialog;
import org.cirdles.topsoil.app.progress.isotope.IsotopeType;
import org.cirdles.topsoil.app.progress.table.TopsoilDataEntry;
import org.cirdles.topsoil.app.progress.table.TopsoilTable;
import org.cirdles.topsoil.app.progress.util.FileParser;
import org.cirdles.topsoil.app.util.ErrorAlerter;
import org.cirdles.topsoil.app.util.IssueCreator;
import org.cirdles.topsoil.app.util.StandardGitHubIssueCreator;
import org.cirdles.topsoil.app.util.YesNoAlert;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by benjaminmuldrow on 6/21/16.
 */
public class MenuItemEventHandler {

    /**
     * Handles importing tables from CSV / TSV files
     * @return Topsoil Table file
     * @throws IOException for invalid file selection
     */
    public static TopsoilTable handleTableFromFile() throws IOException {

        TopsoilTable table;
        boolean valid = true;

        // select file
        File file = FileParser.openTableDialogue(new Stage());
        if (file == null) {
            valid = false;
        }

        // select headers
        String [] headers = null;
        Boolean hasHeaders = null;
        if (valid) {
            hasHeaders = FileParser.containsHeaderDialogue();
            if (hasHeaders == null) {
                valid = false;
            } else if (hasHeaders) {
                headers = FileParser.parseHeaders(file);
            } else {
                headers = null;
            }
        }

        // select isotope flavor
        IsotopeType isotopeType = null;
        ObservableList<TopsoilDataEntry> data = null;
        if (valid) {
            isotopeType = IsotopeSelectionDialog.selectIsotope(new IsotopeSelectionDialog());
            List<TopsoilDataEntry> entries = FileParser.parseFile(file, hasHeaders);
            data = FXCollections.observableList(entries);
        }

        // create table
        if (data == null ||  isotopeType == null) {
            table = null;
        } else {
            table = new TopsoilTable(headers, isotopeType, data.toArray(new TopsoilDataEntry[data.size()]));
        }

        return table;
    }

    public static TopsoilTable handleNewTable() {

        TopsoilTable table;

        // select isotope flavor
        IsotopeType isotopeType = IsotopeSelectionDialog.selectIsotope(new IsotopeSelectionDialog());

        // create empty table
        if (isotopeType == null) {
            table = null;
        } else {
            table = new TopsoilTable(null, isotopeType, new TopsoilDataEntry[]{});
        }

        return table;
    }

    public static void handleReportIssue() {
        IssueCreator issueCreator = new StandardGitHubIssueCreator(
                new TopsoilMetadata(),
                System.getProperties(),
                new DesktopWebBrowser(Desktop.getDesktop(), new ErrorAlerter()),
                new StringBuilder()
        );
        issueCreator.create();
    }

    public static TopsoilTable handleClearTable(TopsoilTable table) {

        // alert user for confirmation
        Alert confirmAlert = new YesNoAlert("Are you sure you want to clear the table?");
        Optional<ButtonType> response = confirmAlert.showAndWait();
        TopsoilTable resultingTable = table;

        // get user confirmation
        if (response.isPresent()
                && response.get() == ButtonType.YES) {
            resultingTable = new TopsoilTable(table.getHeaders(), table.getIsotopeType(), new TopsoilDataEntry[]{});
        }

        return resultingTable;
    }

}
