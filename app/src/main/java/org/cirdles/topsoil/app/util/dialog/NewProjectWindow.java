package org.cirdles.topsoil.app.util.dialog;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.table.ObservableTableData;
import org.cirdles.topsoil.app.util.dialog.controller.ProjectPreviewController;
import org.cirdles.topsoil.app.util.dialog.controller.ProjectSourcesController;
import org.cirdles.topsoil.app.util.dialog.controller.ProjectTitleController;
import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;

import java.io.File;
import java.util.*;

/**
 * A custom {@code Stage} that handles the creation of a new project file.
 *
 * @author Jake Marotta
 */
public class NewProjectWindow extends Stage {

    private final double WIDTH = 600.0;
    private final double HEIGHT = 550.0;

    private NewProjectWindow() {
        super();

        this.setTitle("New Project");
        this.getIcons().add(MainWindow.getWindowIcon());
        this.initOwner(MainWindow.getPrimaryStage());
        this.setResizable(false);
    }

    /**
     * Creates a new project and returns a {@code List} of {@code TopsoilDataTable}s, if such data is specified.
     *
     * @return  List of ObservableTableData
     */
    public static List<ObservableTableData> newProject() {
        return new NewProjectWindow().createNewProject();
    }

    private List<ObservableTableData> createNewProject() {

        ProjectTitleController titleController = new ProjectTitleController();
        Scene titleScene = new Scene(titleController, WIDTH, HEIGHT);

        ProjectSourcesController sourcesController = new ProjectSourcesController();
        Scene sourcesScene = new Scene(sourcesController, WIDTH, HEIGHT);

        ProjectPreviewController previewController = new ProjectPreviewController();
        Scene previewScene = new Scene(previewController, WIDTH, HEIGHT);

        // Set order of scenes
        titleController.setNextScene(sourcesScene);

        sourcesController.setPreviousScene(titleScene);
        sourcesController.setNextScene(previewScene);

        previewController.setPreviousScene(sourcesScene);

        // Bind list of Paths in preview screen to list of Paths in source selection screen
        previewController.pathDelimiterListProperty().bind(sourcesController.pathDelimiterListProperty());

        // Set stage for project name controller
        this.setScene(titleScene);

        // Wait for the window to close
        this.showAndWait();

        List<ObservableTableData> tables = null;

        // New project from existing sources.
        if (previewController.didFinish()) {

            List<Map<ImportDataType, Object>> allSelections = previewController.getSelections();

            tables = new ArrayList<>();

            String[] headerRows;
            IsotopeType isotopeType;
            UncertaintyFormat format;
            Double[][] dataRows;

            // Creates a new TopsoilDataTable for each file.
            for (Map<ImportDataType, Object> selections : allSelections) {

                headerRows = (String[]) selections.get(ImportDataType.HEADERS);
                isotopeType = (IsotopeType) selections.get(ImportDataType.ISOTOPE_TYPE);
                format = (UncertaintyFormat) selections.get(ImportDataType.UNCERTAINTY);
                dataRows = (Double[][]) selections.get(ImportDataType.DATA);

                ObservableTableData data = new ObservableTableData(dataRows, true, headerRows, isotopeType, format);
                data.setTitle(String.valueOf(selections.get(ImportDataType.TITLE)));

                Map<Variable<Number>, Integer> varIndexMap = (Map<Variable<Number>, Integer>) selections.get(
                        ImportDataType.VARIABLE_INDEX_MAP);
                varIndexMap.entrySet().forEach((entry) -> {
                    data.setVariableForColumn(entry.getValue(), entry.getKey());
                });

                tables.add(data);
            }

            File projectFile = new File(titleController.getProjectLocation().toString() + File.separator +
                                        titleController.getTitle() + ".topsoil");

            // Sets the current project
            TopsoilSerializer.setCurrentProjectFile(projectFile);

        // New empty project
        } else if (sourcesController.didFinish()) {

            tables = new ArrayList<>();
            File projectFile = new File(titleController.getProjectLocation().toString() + File.separator +
                                        titleController.getTitle() + ".topsoil");

            // Sets the current project
            TopsoilSerializer.setCurrentProjectFile(projectFile);
        }
        return tables;
    }
}
