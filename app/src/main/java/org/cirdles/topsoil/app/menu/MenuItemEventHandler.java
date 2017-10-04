package org.cirdles.topsoil.app.menu;

import com.sun.javafx.stage.StageHelper;
import javafx.collections.FXCollections;
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
import org.cirdles.topsoil.app.table.TopsoilDataColumn;
import org.cirdles.topsoil.app.table.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.tab.TopsoilTabPane;
import org.cirdles.topsoil.app.dataset.entry.TopsoilDataEntry;
import org.cirdles.topsoil.app.table.TopsoilDataTable;
import org.cirdles.topsoil.app.util.TopsoilException;
import org.cirdles.topsoil.app.util.dialog.DataImportDialog;
import org.cirdles.topsoil.app.util.dialog.NewProjectWindow;
import org.cirdles.topsoil.app.util.dialog.TableUncertaintyChoiceDialog;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification;
import org.cirdles.topsoil.app.util.dialog.TopsoilNotification.NotificationType;
import org.cirdles.topsoil.app.util.dialog.DataImportKey;
import org.cirdles.topsoil.app.util.file.FileParser;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;
import org.cirdles.topsoil.app.util.issue.IssueCreator;
import org.cirdles.topsoil.app.util.issue.StandardGitHubIssueCreator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cirdles.topsoil.app.util.file.ExampleDataTable;
import java.awt.Desktop;
import java.io.File;
import java.io.UnsupportedEncodingException;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * A class containing a set of methods for handling actions for {@code MenuItem}s in the {@link MainMenuBar}.
 *
 * @author Benjamin Muldrow
 * @see MainMenuBar
 */
public class MenuItemEventHandler {

/**
     * Handles importing tables from CSV / TSV files
     *
     * @return Topsoil Table file
     * @throws IOException for invalid file selection
     */
    public static TopsoilDataTable handleTableFromFile() throws IOException {

        TopsoilDataTable table = null;

        // select file
        File file = TopsoilFileChooser.getTableFileChooser().showOpenDialog(MainWindow.getPrimaryStage());

        if (file != null) {

            if (!FileParser.isSupportedTableFile(file)) {
                TopsoilNotification.showNotification(
                        NotificationType.ERROR,
                        "Invalid File Type",
                        "Table file must be .csv, .tsv, or .txt."
                );
            } else {

                if (FileParser.isEmptyFile(file)) {
                    TopsoilNotification.showNotification(
                            NotificationType.ERROR,
                            "Empty File",
                            "The file is empty. No data has been imported."
                    );
                    throw new IOException("File is empty");
                }

                String delim = FileParser.getDelimiter(file);

                if (delim == null) {
                    delim = FileParser.requestDelimiter();
                }

                if (delim != null) {

                    // select headers
                    String[] headers = null;
                    Boolean hasHeaders = null;
                    hasHeaders = FileParser.detectHeader(file, delim);

                    try {
                        FileParser.parseHeaders(file, delim);
                    } catch (TopsoilException e) {
                        String errorMessage = "Topsoil cannot read this file. Make sure the file contains"
                                              + " a complete delimited data table. The import has been canceled.";
                        TopsoilNotification.showNotification(
                                NotificationType.ERROR,
                                "Could Not Read",
                                errorMessage
                        );
                        e.printStackTrace();
                    }

                    // hasHeaders would only be null if the user clicked "Cancel".
                    if (hasHeaders != null) {

                        List<TopsoilDataEntry> entries = null;

                        try {
                            if (hasHeaders) {
                                headers = FileParser.parseHeaders(file, delim);
                            }
                            entries = FileParser.parseFile(file, delim, hasHeaders);

                            if (entries.size() <= 0) {
                                entries = null;
                                throw new TopsoilException("Invalid delimiter.");
                            }
                        } catch (TopsoilException e) {
                            String errorMessage = "Topsoil can not read the imported content. Make sure it is"
                                                  + " a complete data table or try saving it as a .csv or .tsv. The import has " +
                                                  "been canceled.";
                            e.printStackTrace();
                            TopsoilNotification.showNotification(
                                    NotificationType.ERROR,
                                    "Could Not Read",
                                    errorMessage
                            );
                        }

                        if (entries != null) {
                            Map<DataImportKey, Object> selections = DataImportDialog.showImportDialog(headers, entries);

                            if (selections != null) {
                                headers = (String[]) selections.get(DataImportKey.HEADERS);
                                entries = (List<TopsoilDataEntry>) selections.get(DataImportKey.DATA);
                                UncertaintyFormat selectedFormat = (UncertaintyFormat) selections
                                        .get(DataImportKey.UNCERTAINTY);

                                // Isotope system is Generic by default.
                                IsotopeType isotopeType;
                                if (selections.get(DataImportKey.ISOTOPE_TYPE) == null) {
                                    isotopeType = IsotopeType.Generic;
                                } else {
                                    isotopeType = (IsotopeType) selections.get(DataImportKey.ISOTOPE_TYPE);
                                }

                                ObservableList<TopsoilDataEntry> data = FXCollections.observableList(entries);

                                table = new TopsoilDataTable(headers,
                                                             isotopeType,
                                                             selectedFormat,
                                                             data.toArray(new TopsoilDataEntry[data.size()]));
                                table.setTitle(file.getName().substring(0, file.getName().indexOf(".")));

                                List<TopsoilDataColumn> columns = table.getDataColumns();
                                Map<Variable<Number>, TopsoilDataColumn> variableAssignments = new HashMap<>();

                                // Apply variable selections
                                for (Map.Entry<Variable<Number>, Integer> entry :
                                        ((Map<Variable<Number>, Integer>) selections.get(DataImportKey.VARIABLE_INDEX_MAP))
                                                .entrySet()) {
                                    TopsoilDataColumn column = columns.get(entry.getValue());
                                    column.setVariable(entry.getKey());
                                    variableAssignments.put(entry.getKey(), column);
                                }

                                table.setVariableAssignments(variableAssignments);
                            }
                        }
                    }
                }
            }
        }

        return table;
    }

    /**
     * Pareses table data from the system {@code Clipboard} into a new {@code TopsoilDataTable}.
     *
     * @return  a new TopsoilDataTable
     */
    public static TopsoilDataTable handleTableFromClipboard() {

        TopsoilDataTable table = null;
        String content = Clipboard.getSystemClipboard().getString();

        String delim = FileParser.getDelimiter(content);

        if (delim == null) {
            delim = FileParser.requestDelimiter();
        }

        if (delim != null) {

            String[] headers = null;
            Boolean hasHeaders = FileParser.detectHeader(content, delim);
            List<TopsoilDataEntry> entries = null;

            if (hasHeaders != null) {
                try {
                    if (hasHeaders) {
                        headers = FileParser.parseHeaders(content, delim);
                    }

                    entries = FileParser.parseClipboard(hasHeaders, delim);
                } catch (TopsoilException e) {
                    String errorMessage = "Topsoil can not read the imported content. Make sure it is"
                                          + " a complete data table or try saving it as a .csv or .tsv. The import has " +
                                          "been canceled.";
                    e.printStackTrace();
                    TopsoilNotification.showNotification(
                            NotificationType.ERROR,
                            "Could Not Read",
                            errorMessage
                    );
                }

                if (entries != null) {

                    Map<DataImportKey, Object> selections = DataImportDialog.showImportDialog(headers, entries);

                    if (selections != null) {
                        headers = (String[]) selections.get(DataImportKey.HEADERS);
                        entries = (List<TopsoilDataEntry>) selections.get(DataImportKey.DATA);
                        UncertaintyFormat selectedFormat = (UncertaintyFormat) selections.get(DataImportKey.UNCERTAINTY);

                        // Isotope system is Generic by default.
                        IsotopeType isotopeType;
                        if (selections.get(DataImportKey.ISOTOPE_TYPE) == null) {
                            isotopeType = IsotopeType.Generic;
                        } else {
                            isotopeType = (IsotopeType) selections.get(DataImportKey.ISOTOPE_TYPE);
                        }

                        ObservableList<TopsoilDataEntry> data = FXCollections.observableList(entries);

                        table = new TopsoilDataTable(headers,
                                                     isotopeType,
                                                     selectedFormat,
                                                     data.toArray(new TopsoilDataEntry[data.size()]));

                        List<TopsoilDataColumn> columns = table.getDataColumns();
                        Map<Variable<Number>, TopsoilDataColumn> variableAssignments = new HashMap<>();

                        // Apply variable selections
                        for (Map.Entry<Variable<Number>, Integer> entry :
                                ((Map<Variable<Number>, Integer>) selections.get(DataImportKey.VARIABLE_INDEX_MAP))
                                        .entrySet()) {
                            TopsoilDataColumn column = columns.get(entry.getValue());
                            column.setVariable(entry.getKey());
                            variableAssignments.put(entry.getKey(), column);
                        }

                        table.setVariableAssignments(variableAssignments);
                    }
                }
            }
        }
        return table;
    }

    /**
     * Handle new {@code TopsoilDataTable} creation.
     *
     * @return new TopsoilDataTable
     */
    public static TopsoilDataTable handleNewTable() {

        TopsoilDataTable table;

//        // select isotope flavor -- For now, the user shouldn’t have to select an isotope system; instead assume Generic.
//        IsotopeType isotopeType = IsotopeSelectionDialog.selectIsotope(new IsotopeSelectionDialog());
        IsotopeType isotopeType = IsotopeType.Generic;

        // create empty table
        TableUncertaintyChoiceDialog uncertaintyChoiceDialog = new TableUncertaintyChoiceDialog();
        UncertaintyFormat selectedFormat = uncertaintyChoiceDialog.selectUncertaintyFormat();

        if (selectedFormat != null) {
            table = new TopsoilDataTable(null, isotopeType, selectedFormat, new TopsoilDataEntry[]{});
        } else {
            table = null;
        }

        return table;
    }

    /**
     * Handles the opening of sample data table for a given isotope type.
     *
     * @param tabs  the TopsoilTabPane to which to add tables
     * @param isotopeType the isotope type of the example table to be opened
     * @return  the resulting TopsoilDataTable
     */
    public static TopsoilDataTable handleOpenExampleTable(TopsoilTabPane tabs, IsotopeType isotopeType) {
        TopsoilDataTable table = null;
        UncertaintyFormat format;

        if (isotopeType != null) {

            List<TopsoilDataEntry> entries = null;
            String[] headers = null;
            String exampleContent = new ExampleDataTable().getSampleData(isotopeType);
            String exampleContentDelimiter = ",";

            try {
                headers = FileParser.parseHeaders(exampleContent, exampleContentDelimiter);
                entries = FileParser.parseTxt(FileParser.readLines(exampleContent), exampleContentDelimiter, true);

                switch (isotopeType) {
                    case Generic:
                        format = UncertaintyFormat.TWO_SIGMA_PERCENT;
                        break;
                    case UPb:
                        format = UncertaintyFormat.TWO_SIGMA_PERCENT;
                        break;
                    case UTh:
                        format = UncertaintyFormat.TWO_SIGMA_ABSOLUTE;
                        break;
                    default:
                        format = UncertaintyFormat.TWO_SIGMA_PERCENT;
                }

                ObservableList<TopsoilDataEntry> data = FXCollections.observableList(entries);

                table = new TopsoilDataTable(headers, isotopeType, format, data.toArray(new TopsoilDataEntry[data.size()]));
                table.setTitle(isotopeType.getName() + " Example Data");
            } catch (TopsoilException e) {
                e.printStackTrace();
            }
        }
        return table;
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
     * Clear a table of all data.
     *
     * @param table TopsoilDataTable to clear
     * @return resulting TopsoilDataTable
     */
    public static TopsoilDataTable handleClearTable(TopsoilDataTable table) {

        AtomicReference<TopsoilDataTable> resultingTable = new AtomicReference<>(table);

        TopsoilNotification.showNotification(
                NotificationType.VERIFICATION,
                "Clear Table",
                "Are you sure you want to clear the table?"
        ).ifPresent(response -> {
            if (response == ButtonType.OK) {
                resultingTable.set(new TopsoilDataTable(table.getColumnNames(), table.getIsotopeType(), table
                        .getUncertaintyFormat(), new TopsoilDataEntry[]{}));
            }
        });

        return resultingTable.get();
    }

    public static void handleExportTable(TopsoilDataTable table) {
        PrintWriter writer = null;
        try {
            String[] titles = table.getColumnNames();
            List<Double[]> data = table.getFormattedDataAsArrays();
            File file = TopsoilFileChooser.getExportTableFileChooser().showSaveDialog(StageHelper.getStages().get(0));

            if (file != null) {
                String location = file.toString();
                String type = location.substring(location.length() - 3);
                String delim;
                switch (type) {
                    case "csv":
                        delim = ", ";
                        break;
                    case "tsv":
                        delim = "\t";
                        break;
                    case "txt":
                        delim = requestDelimiter();
                        break;
                    default:
                        delim = "\t";
                        break;
                }
                writer = new PrintWriter(location, "UTF-8");
                for (int i = 0; i < titles.length; i++) {
                    writer.print(titles[i]);
                    if (i < titles.length - 1) {
                        writer.print(delim);
                    }
                }
                writer.print('\n');
                for (int i = 0; i < data.size(); i++) {
                    for (int j = 0; j < data.get(i).length; j++) {
                        writer.print(data.get(i)[j]);
                        if (j < data.get(i).length - 1) {
                            writer.print(delim);
                        }
                    }
                    writer.print('\n');
                }
                writer.close();
            }

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(MenuItemEventHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }

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
     * Walks the user through creating a new Topsoil project.
     *
     * @param tabs  TopsoilTabPane
     */
    public static void handleNewProject(TopsoilTabPane tabs) {
        AtomicReference<List<TopsoilDataTable>> tablesReference = new AtomicReference<>(null);
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
                tabs.add(new TopsoilDataTable(
                        IsotopeType.Generic.getHeaders(),
                        IsotopeType.Generic,
                        UncertaintyFormat.TWO_SIGMA_ABSOLUTE,
                        new TopsoilDataEntry[]{}
                ));
                TopsoilNotification.showNotification(
                        NotificationType.INFORMATION,
                        "New Table Created",
                        "An empty table was placed in your project."
                );
            } else {
                for (TopsoilDataTable table : tablesReference.get()) {
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
                                       File file = TopsoilFileChooser.getTopsoilOpenFileChooser().showOpenDialog(StageHelper.getStages().get(0));
                                       openProjectFile(tabs, file);
                                   }
                               });
        } else {
            File file = TopsoilFileChooser.getTopsoilOpenFileChooser().showOpenDialog(StageHelper.getStages().get(0));
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

    /**
     * Creates a new .topsoil file for the current tabs and plots.
     *
     * @param tabs  the TopsoilTabPane from which to save tables
     * @return  true if the file was successfully saved
     */
    public static boolean handleSaveAsProjectFile(TopsoilTabPane tabs) {
        FileChooser fileChooser =  TopsoilFileChooser.getTopsoilSaveFileChooser();
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

    //    /**
//     * Normalizes the supplied data using the value of the specified {@code UncertaintyFormat}.
//     *
//     * @param format    UncertaintyFormat
//     * @param data  data as a List of TopsoilDataEntries
//     */
//    public static void applyUncertaintyFormat(UncertaintyFormat format, List<TopsoilDataEntry> data) {
//        // If uncertainty uncertaintyFormat is not one sigma absolute, convert uncertainty data to one sigma absolute.
//        if (format != UncertaintyFormat.ONE_SIGMA_ABSOLUTE) {
//            double formatValue = format.getValue();
//
//            for (TopsoilDataEntry entry : data) {
//                entry.getProperties().get(2).set(entry.getProperties().get(2).get() / formatValue);
//                entry.getProperties().get(3).set(entry.getProperties().get(3).get() / formatValue);
//            }
//        }
//    }
    private static final String COMMA = "Commas";
    private static final String TAB = "Tabs";
    private static final String COLON = "Colons";
    private static final String SEMICOLON = "Semicolons";

    private static final HashMap<String, String> COMMON_DELIMITERS; // Checked against when guessing a delimiter
    static {
        COMMON_DELIMITERS = new LinkedHashMap<>();
        COMMON_DELIMITERS.put(COMMA, ",");
        COMMON_DELIMITERS.put(TAB, "\t");
        COMMON_DELIMITERS.put(COLON, ":");
        COMMON_DELIMITERS.put(SEMICOLON, ";");
    }

    private static String requestDelimiter() {
        String otherDelimiterOption = "Other";

        Dialog<String> delimiterRequestDialog = new Dialog<>();
        delimiterRequestDialog.setTitle("Delimiter Request");

        /*
            CONTENT NODES
         */
        VBox vBox = new VBox(10.0);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_RIGHT);

        Label requestLabel = new Label("How do you want the values in the data separated?  ");
        ChoiceBox<String> delimiterChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(COMMON_DELIMITERS
                                                                                                         .keySet()));
        delimiterChoiceBox.getItems().addAll(otherDelimiterOption);

        Label otherLabel = new Label("Other: ");
        TextField otherTextField = new TextField();
        otherTextField.setDisable(true);

        delimiterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(otherDelimiterOption)) {
                otherTextField.setDisable(false);
            } else {
                otherTextField.setDisable(true);
            }
        });

        grid.add(requestLabel, 0, 0);
        grid.add(delimiterChoiceBox, 1, 0);
        grid.add(otherLabel, 0, 1);
        grid.add(otherTextField, 1, 1);

        vBox.getChildren().addAll(grid);

        delimiterRequestDialog.getDialogPane().setContent(vBox);

        /*
            BUTTONS AND RETURN
         */
        delimiterRequestDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        delimiterRequestDialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        delimiterChoiceBox.getSelectionModel().selectedItemProperty().addListener(c -> {
            if (delimiterChoiceBox.getSelectionModel().getSelectedItem() == null) {
                delimiterRequestDialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            } else {
                delimiterRequestDialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
            }
        });

        delimiterRequestDialog.setResultConverter(value -> {
            if (value == ButtonType.OK) {
                if (delimiterChoiceBox.getSelectionModel().getSelectedItem().equals(otherDelimiterOption)) {
                    return otherTextField.getText().trim();
                } else {
                    return COMMON_DELIMITERS.get(delimiterChoiceBox.getValue());
                }
            } else {
                return null;
            }
        });

        Optional<String> result = delimiterRequestDialog.showAndWait();
        return result.orElse(null);
    }
}