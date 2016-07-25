package org.cirdles.topsoil.app.progress;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.metadata.TopsoilMetadata;
import org.cirdles.topsoil.app.plot.PlotType;
import org.cirdles.topsoil.app.util.ErrorAlerter;
import org.cirdles.topsoil.app.util.IssueCreator;
import org.cirdles.topsoil.app.util.StandardGitHubIssueCreator;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        // select file
        File file = FileParser.openTableDialogue(new Stage());

        // select headers
        boolean hasHeaders = FileParser.containsHeaderDialogue();
        String [] headers;
        if (hasHeaders) {
            headers = FileParser.parseHeaders(file);
        } else {
            headers = null;
        }

        // select isotope flavor
        IsotopeType isotopeType = IsotopeSelectionDialog.selectIsotope(new IsotopeSelectionDialog());
        List<TopsoilDataEntry> entries = FileParser.parseFile(file, hasHeaders);
        ObservableList<TopsoilDataEntry> data = FXCollections.observableList(entries);

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

        // create empty dataset
        List<TopsoilDataEntry> entries = new ArrayList<>();
        ObservableList<TopsoilDataEntry> data = FXCollections.observableList(entries);

        // create empty table
        table = new TopsoilTable(null, isotopeType, data.toArray(new TopsoilDataEntry[data.size()]));

        return table;
    }

    public static PlotType handlePlotType(IsotopeType iso) {
        PlotType plotType;
        String name = iso.getName();
        //TODO add a better way to identify the isotope type
        if (name.equals("Uranium Lead")) {
            // Selects plot type
            plotType = UPbPlotSelectionDialog.selectPlot(new UPbPlotSelectionDialog());
        } else {
            // Selects plot type
            plotType = UThPlotSelectionDialog.selectPlot((new UThPlotSelectionDialog()));
        }

        return plotType;
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

}
