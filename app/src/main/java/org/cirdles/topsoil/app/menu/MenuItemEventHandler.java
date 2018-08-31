package org.cirdles.topsoil.app.menu;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.browse.DesktopWebBrowser;
import org.cirdles.topsoil.app.metadata.TopsoilMetadata;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.table.ObservableTableData;
import org.cirdles.topsoil.app.util.dialog.*;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification.NotificationType;
import org.cirdles.topsoil.app.util.file.Delimiter;
import org.cirdles.topsoil.app.util.file.FileParser;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;
import org.cirdles.topsoil.app.util.issue.IssueCreator;
import org.cirdles.topsoil.app.util.issue.StandardGitHubIssueCreator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cirdles.topsoil.app.util.file.ExampleDataTable;
import java.awt.Desktop;

/**
 * A class containing a set of methods for handling actions for {@code MenuItem}s in the {@link MainMenuBar}.
 *
 * @author Benjamin Muldrow
 *
 * @see MainMenuBar
 */
public class MenuItemEventHandler {

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**
     * Handles the selection and importing of a table data file into an instance of {@code ObservableTableData}.
     *
     * @return  ObservableTableData
     */
    public static ObservableTableData importDataFromFile() {

    	File file = TopsoilFileChooser.openTableFile().showOpenDialog(MainWindow.getPrimaryStage());
        ObservableTableData table = null;

        if (file != null) {
	        Path path = Paths.get(file.toURI());
            if ( ! FileParser.isFileSupported(path) ) {
                TopsoilNotification.showNotification(
                        NotificationType.ERROR,
                        "Invalid File Type",
                        "Table file must be .csv, .tsv, or .txt.");
            } else {

                try {

                    if (FileParser.isFileEmpty(path)) {
                        TopsoilNotification.showNotification(
                                NotificationType.ERROR,
                                "Empty File",
                                "The file is empty. No data has been imported.");
                        throw new IOException("File is empty");
                    }

                    String delim = FileParser.getDelimiter(path);
                    if (delim == null) {
                        delim = DelimiterRequestDialog.showDialog("Data Separator",
                                                                  "How are the values in your data file separated? ",
                                                                  true);
                    }

                    if (delim != null) {
                        String[] headers = FileParser.parseHeaders(path, delim);
                        Double[][] data = FileParser.parseData(path, delim);

                        if (data != null) {
                            Map<ImportDataType, Object> selections = DataImportDialog
                                    .showImportDialog(headers, data);

                            if (selections != null) {
                                headers = (String[]) selections.get(ImportDataType.HEADERS);
                                data = (Double[][]) selections.get(ImportDataType.DATA);
                                UncertaintyFormat unctFormat = (UncertaintyFormat) selections
                                        .get(ImportDataType.UNCERTAINTY);

                                // Isotope system is Generic by default.
                                IsotopeType isotopeType;
                                if (selections.get(ImportDataType.ISOTOPE_TYPE) == null) {
                                    isotopeType = IsotopeType.GENERIC;
                                } else {
                                    isotopeType = (IsotopeType) selections.get(ImportDataType.ISOTOPE_TYPE);
                                }

                                table = new ObservableTableData(data, true, headers,
                                                                isotopeType, unctFormat);
                                table.setTitle(path.getFileName().toString()
                                                   .substring(0, path.getFileName().toString().indexOf(".")));

                                Map<Variable<Number>, Integer> varIndexMap = (Map<Variable<Number>, Integer>) selections
                                        .get(ImportDataType.VARIABLE_INDEX_MAP);
                                for (Map.Entry<Variable<Number>, Integer> entry : varIndexMap.entrySet()) {
                                    table.setVariableForColumn(entry.getValue(), entry.getKey());
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    String errorMessage = "Topsoil cannot read this file. Make sure the file contains"
                                          + " a complete data table. The import has been canceled.";
                    TopsoilNotification.showNotification(
                            NotificationType.ERROR,
                            "Unable to Import",
                            errorMessage
                    );
                    e.printStackTrace();
                }
            }
        }
        return table;
    }

    /**
     * Handles the importing of table data from the system clipboard into an instance of {@code ObservableTableData}.
     *
     * @return  ObservableTableData
     */
    public static ObservableTableData importDataFromClipboard() {
        String content = Clipboard.getSystemClipboard().getString();
        ObservableTableData table = null;

        try {
            String delim = FileParser.getDelimiter(content);

            if (delim == null) {
                delim = DelimiterRequestDialog.showDialog("Data Separator",
                                                          "What are the data values separated with?",
                                                          true);
            }

            if (delim != null) {
                String[] headers = FileParser.parseHeaders(content, delim);
                Double[][] data = FileParser.parseData(
                        Clipboard.getSystemClipboard().getString(), delim);

                if (data != null) {
                    Map<ImportDataType, Object> selections = DataImportDialog.showImportDialog(headers, data);

                    if (selections != null) {
                        headers = (String[]) selections.get(ImportDataType.HEADERS);
                        data = (Double[][]) selections.get(ImportDataType.DATA);
                        UncertaintyFormat unctFormat = (UncertaintyFormat) selections.get(ImportDataType.UNCERTAINTY);

                        // Isotope system is Generic by default.
                        IsotopeType isotopeType;
                        if (selections.get(ImportDataType.ISOTOPE_TYPE) == null) {
                            isotopeType = IsotopeType.GENERIC;
                        } else {
                            isotopeType = (IsotopeType) selections.get(ImportDataType.ISOTOPE_TYPE);
                        }

                        table = new ObservableTableData(data, true, headers, isotopeType, unctFormat);

                        // Apply variable selections
                        Map<Variable<Number>, Integer> varIndexMap = (Map<Variable<Number>, Integer>) selections
                                .get(ImportDataType.VARIABLE_INDEX_MAP);
                        for (Map.Entry<Variable<Number>, Integer> entry : varIndexMap.entrySet()) {
                            table.setVariableForColumn(entry.getValue(), entry.getKey());
                        }
                    }
                }
            }
        } catch (IOException e) {
            String errorMessage = "Topsoil cannot read the clipboard content. The import has been canceled.";
            TopsoilNotification.showNotification(
                    NotificationType.ERROR,
                    "Unable to Import",
                    errorMessage
            );
            e.printStackTrace();
        }
        return table;
    }

    /**
     * Handles the creation of a new {@code ObservableTableData}.
     *
     * @return  new ObservableTableData
     */
    public static ObservableTableData handleNewTable() {

        ObservableTableData table;

        // For now, the user shouldn’t have to select an isotope system; instead assume Generic.
        IsotopeType isotopeType = IsotopeType.GENERIC;

        // create empty table
        TableUncertaintyChoiceDialog uncertaintyChoiceDialog = new TableUncertaintyChoiceDialog();
        UncertaintyFormat unctFormat = uncertaintyChoiceDialog.selectUncertaintyFormat();

        if (unctFormat != null) {
            table = new ObservableTableData(null, true, null, isotopeType, unctFormat);
        } else {
            table = null;
        }

        return table;
    }

    /**
     * Handles the opening of sample data table for a given {@code IsotopeType}.
     *
     * @param   tabs
     *          the TopsoilTabPane to which to add tables
     * @param   isotopeType
     *          the IsotopeType of the example table to be opened
     */
    public static void handleOpenExampleTable(TopsoilTabPane tabs, IsotopeType isotopeType) {

        ObservableTableData table;
        UncertaintyFormat unctFormat;

        if (isotopeType != null) {
            String exampleContent = new ExampleDataTable().getSampleData(isotopeType);
            String exampleContentDelimiter = ",";

            try {
                String[] headerRows = FileParser.parseHeaders(exampleContent, exampleContentDelimiter);
                Double[][] dataRows = FileParser.parseData(exampleContent, exampleContentDelimiter);

                switch (isotopeType) {
                    case GENERIC:
                        unctFormat = UncertaintyFormat.TWO_SIGMA_PERCENT;
                        break;
                    case UPB:
                        unctFormat = UncertaintyFormat.TWO_SIGMA_PERCENT;
                        break;
                    case UTH:
                        unctFormat = UncertaintyFormat.TWO_SIGMA_ABSOLUTE;
                        break;
                    default:
                        unctFormat = UncertaintyFormat.TWO_SIGMA_PERCENT;
                }

                table = new ObservableTableData(dataRows, true, headerRows, isotopeType, unctFormat);

                // Set default variable associations.
                table.setVariableForColumn(0, Variables.X);
                table.setVariableForColumn(1, Variables.SIGMA_X);
                table.setVariableForColumn(2, Variables.Y);
                table.setVariableForColumn(3, Variables.SIGMA_Y);
                table.setVariableForColumn(4, Variables.RHO);

                tabs.add(table);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Open default browser and create a new GitHub issue with user specifications already supplied.
     * */
    public static void handleReportIssue() {
        IssueCreator issueCreator = new StandardGitHubIssueCreator(
                new TopsoilMetadata(),
                System.getProperties(),
                new DesktopWebBrowser(Desktop.getDesktop()),
                new StringBuilder()
        );
        issueCreator.create();
    }

    /**
     * Open default browser at the Topsoil Project Page on CIRLDES website.
     * */
    public static void handleOpenOnlineHelp() {

        String TOPSOIL_URL = "http://cirdles.org/projects/topsoil/";
        new DesktopWebBrowser(Desktop.getDesktop()).browse(TOPSOIL_URL);

    }

    /**
     * Handles exporting a data table to a delimited data file.
     *
     * @param   table
     *          ObservableTableData
     */
    public static void handleExportTable(ObservableTableData table) {
        try {
            String[] headers = table.getColumnHeaders();
            Double[][] data = new Double[table.rowCount()][table.colCount()];
            ObservableList<ObservableList<DoubleProperty>> observableRows = table.getObservableRows();

            for (int i = 0; i < observableRows.size(); i++) {
                for (int j = 0; j < observableRows.get(i).size(); j++) {
                    data[i][j] = observableRows.get(i).get(j).get();
                }
            }

            Path path = Paths.get(TopsoilFileChooser.exportTableFile().showSaveDialog(StageHelper.getStages().get(0))
                                                    .toURI());

            if (path != null) {
                String location = path.toString();
                String extension = location.substring(location.lastIndexOf('.'));

                String delim = Delimiter.valueOf(extension.toUpperCase()).toString();

                StringJoiner tableJoiner = new StringJoiner("\t");
                StringJoiner rowJoiner = new StringJoiner(delim);

                for (int i = 0; i < headers.length; i++) {
                    rowJoiner.add(headers[i]);
                }
                tableJoiner.add(rowJoiner.toString());

                for (int i = 0; i < data.length; i++) {
                    rowJoiner = new StringJoiner(delim);

                    for (int j = 0; j < data[i].length; j++) {
                        rowJoiner.add(data[i][j].toString());
                    }
                    tableJoiner.add(rowJoiner.toString());
                }
                BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
                writer.write(tableJoiner.toString());
                writer.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(MenuItemEventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Walks the user through creating a new Topsoil project.
     *
     * @param tabs  TopsoilTabPane
     */
    public static void handleNewProject(TopsoilTabPane tabs) {
        AtomicReference<List<ObservableTableData>> tablesReference = new AtomicReference<>(null);

        if (TopsoilSerializer.isProjectOpen()) {
            TopsoilNotification.showNotification(
                    NotificationType.YES_NO,
                    "Create New Project",
                    "Creating a new project will close your current project. Do you want to save your work?"
            ).ifPresent(response -> {
                if (response == ButtonType.YES) {
                    handleSaveProjectFile(tabs);
                    tablesReference.set(NewProjectWindow.newProject());
                } else if (response == ButtonType.NO) {
                    tablesReference.set(NewProjectWindow.newProject());
                }
            });
        } else if (!tabs.isEmpty()) {
            TopsoilNotification.showNotification(
                    NotificationType.VERIFICATION,
                    "Create New Project",
                    "Creating a new project will close your current work."
            ).ifPresent(response -> {
                if (response == ButtonType.OK) {
                    tablesReference.set(NewProjectWindow.newProject());
                }
            });
        } else {
            tablesReference.set(NewProjectWindow.newProject());
        }

        if (tablesReference.get() != null) {
            tabs.getTabs().clear();

            if (tablesReference.get().isEmpty()) {
                tabs.add(new ObservableTableData(
                        new Double[][]{ new Double[]{ 0.0, 0.0, 0.0, 0.0, 0.0 }},
                        true,
                        null,
                        IsotopeType.GENERIC,
                        UncertaintyFormat.TWO_SIGMA_ABSOLUTE));
                TopsoilNotification.showNotification(
                        NotificationType.INFORMATION,
                        "New Table Created",
                        "An empty table was placed in your project."
                );
            } else {
                for (ObservableTableData table : tablesReference.get()) {
                    tabs.add(table);
                }
            }

            TopsoilSerializer.serialize(TopsoilSerializer.getCurrentProjectFile(), tabs);
        }
    }

    /**
     * Opens a .topsoil project {@code File}. If any tabs or plots are open, they are closed and replaced with the
     * project's information.
     *
     * @param tabs  the TopsoilTabPane to which to add tables
     */
    public static void handleOpenProjectFile(TopsoilTabPane tabs) {
        if (!tabs.isEmpty()) {
            TopsoilNotification.showNotification(
                    NotificationType.VERIFICATION,
                    "Open Project",
                    "Opening a Topsoil project will replace your current tables. Continue?")
                               .ifPresent(response -> {
                                   if (response == ButtonType.OK) {
                                       File file = TopsoilFileChooser.openTopsoilFile().showOpenDialog(StageHelper.getStages().get(0));
                                       openProjectFile(tabs, file);
                                   }
                               });
        } else {
            File file = TopsoilFileChooser.openTopsoilFile().showOpenDialog(StageHelper.getStages().get(0));
            openProjectFile(tabs, file);
        }
    }

    /**
     * Opens a .topsoil {@code File}.
     *
     * @param tabs  the active TopsoilTabPane.
     * @param file  the project File
     */
    public static void openProjectFile(TopsoilTabPane tabs, File file) {
        if (file != null) {
            String fileName = file.getName();
            String extension = fileName.substring(
                    fileName.lastIndexOf(".") + 1,
                    fileName.length());
            if (!extension.equals("topsoil")) {
                TopsoilNotification.showNotification(
                        NotificationType.ERROR,
                        "Invalid File Type",
                        "Project must be a .topsoil file.");
            } else {
                closeAllTabsAndPlots(tabs);
                TopsoilSerializer.deserialize(file, tabs);
            }
        }
    }

    /**
     * Saves changes to an open .topsoil project.
     * <p>
     *     If the project is open, but the {@code File} can't be found (e.g. if the file was deleted externally while
     *     the project was open), then the user is forced to "Save As".
     * </p>
     *
     * @param tabs  the active TopsoilTabPane
     */
    public static void handleSaveProjectFile(TopsoilTabPane tabs) {
        if (TopsoilSerializer.isProjectOpen()) {
            if (TopsoilSerializer.projectFileExists()) {
                File file = TopsoilSerializer.getCurrentProjectFile();
                saveProjectFile(file, tabs);
            } else {
                handleSaveAsProjectFile(tabs);
            }
        }
    }

    /**
     * Creates a new .topsoil file for the current tabs and plots.
     *
     * @param tabs  the TopsoilTabPane from which to save tables
     * @return  true if the file was successfully saved
     */
    public static boolean handleSaveAsProjectFile(TopsoilTabPane tabs) {
        FileChooser fileChooser =  TopsoilFileChooser.saveTopsoilFile();
        File file = fileChooser.showSaveDialog(StageHelper.getStages().get(0));

        if (file != null) {
            saveProjectFile(file, tabs);
            TopsoilSerializer.setCurrentProjectFile(file);
            return true;
        }
        return false;
    }

    /**
     * Closes the project file, and closes all open tabs and plots.
     *
     * @param tabs  the active TopsoilTabPane
     * @return  true if the file is successfully closed
     */
    public static boolean handleCloseProjectFile(TopsoilTabPane tabs) {
        AtomicReference<Boolean> didClose = new AtomicReference<>(false);
        TopsoilNotification.showNotification(
                NotificationType.YES_NO,
                "Save Changes",
                "Do you want to save your changes?")
                           .ifPresent(response -> {
                               if (response != ButtonType.CANCEL) {
                                   if (response == ButtonType.YES) {
                                       MenuItemEventHandler.handleSaveProjectFile(tabs);
                                   }
                                   closeAllTabsAndPlots(tabs);
                                   TopsoilSerializer.closeProjectFile();
                                   didClose.set(true);
                               }
                           });

        return didClose.get();
    }

    /**
     * Asks the user if they really want to delete the open data table.
     *
     * @return true if delete is confirmed, false if not
     */
    public static Boolean confirmTableDeletion() {
        final AtomicReference<Boolean> reference = new AtomicReference<>(false);

        TopsoilNotification.showNotification(
                TopsoilNotification.NotificationType.VERIFICATION,
                "Delete Table",
                "Do you really want to delete this table?\n"
                + "This operation can not be undone."
        ).ifPresent(response -> {
            if (response == ButtonType.OK) {
                reference.set(true);
            }
        });

        return reference.get();
    }

    //**********************************************//
    //               PRIVATE METHODS                //
    //**********************************************//

    /**
     * Closes all open tabs in the {@code TopsoilTabPane}, as well as any open plots. Used when a project is loaded,
     * or when one is closed.
     *
     * @param tabs  the active TopsoilTabPane
     */
    private static void closeAllTabsAndPlots(TopsoilTabPane tabs) {
        tabs.getTabs().clear();
        List<Stage> stages = StageHelper.getStages();
        for (int index = stages.size() - 1; index > 0; index--) {
            stages.get(index).close();
        }
    }

    /**
     * Saves an open .topsoil project.
     *
     * @param file  the open .topsoil project File
     * @param tabs  the active TopsoilTabPane
     * @return  true if file is successfully saved
     */
    private static boolean saveProjectFile(File file, TopsoilTabPane tabs) {
        if (file != null) {
            String fileName = file.getName();
            String extension = fileName.substring(
                    fileName.lastIndexOf(".") + 1,
                    fileName.length());
            if (!extension.equals("topsoil")) {
                TopsoilNotification.showNotification(
                        NotificationType.ERROR,
                        "Invalid File Type",
                        "Project must be a .topsoil file.");
            } else {
                TopsoilSerializer.serialize(file, tabs);
                return true;
            }
        }
        return false;
    }
}