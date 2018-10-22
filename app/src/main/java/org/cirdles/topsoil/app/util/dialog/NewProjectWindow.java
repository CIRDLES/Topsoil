package org.cirdles.topsoil.app.util.dialog;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.app.MainWindow;
import org.cirdles.topsoil.app.data.ObservableDataTable;
import org.cirdles.topsoil.isotope.IsotopeSystem;
import org.cirdles.topsoil.variable.Variable;
import org.cirdles.topsoil.app.uncertainty.UncertaintyFormat;
import org.cirdles.topsoil.app.util.TopsoilException;
import org.cirdles.topsoil.app.util.file.FileParser;
import org.cirdles.topsoil.app.util.file.TopsoilFileChooser;
import org.cirdles.topsoil.app.util.serialization.TopsoilSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     * @return  List of ObservableDataTable
     */
    public static List<ObservableDataTable> newProject() {
        return new NewProjectWindow().createNewProject();
    }

    private List<ObservableDataTable> createNewProject() {

        NewProjectTitleView titleView = new NewProjectTitleView();
        Scene titleScene = new Scene(titleView, WIDTH, HEIGHT);

        NewProjectSourcesView sourcesView = new NewProjectSourcesView();
        Scene sourcesScene = new Scene(sourcesView, WIDTH, HEIGHT);

        ProjectSourcesOptionsView sourcesOptionsView = new ProjectSourcesOptionsView();
        Scene previewScene = new Scene(sourcesOptionsView, WIDTH, HEIGHT);

        // Set order of scenes
        titleView.setNextScene(sourcesScene);

        sourcesView.setPreviousScene(titleScene);
        sourcesView.setNextScene(previewScene);

        sourcesOptionsView.setPreviousScene(sourcesScene);

        // Bind list of Paths in preview screen to list of Paths in source selection screen
        sourcesOptionsView.pathDelimiterListProperty().bind(sourcesView.pathDelimiterListProperty());

        // Set stage for project name controller
        this.setScene(titleScene);

        // Wait for the window to close
        this.showAndWait();

        List<ObservableDataTable> tables = null;

        // New project from existing sources.
        if (sourcesOptionsView.didFinish()) {

            List<Map<ImportDataType, Object>> allSelections = sourcesOptionsView.getSelections();

            tables = new ArrayList<>();

            String[] headerRows;
            IsotopeSystem isotopeType;
            UncertaintyFormat format;
            Double[][] dataRows;

            // Creates a new TopsoilDataTable for each file.
            for (Map<ImportDataType, Object> selections : allSelections) {

                headerRows = (String[]) selections.get(ImportDataType.HEADERS);
                isotopeType = (IsotopeSystem) selections.get(ImportDataType.ISOTOPE_TYPE);
                format = (UncertaintyFormat) selections.get(ImportDataType.UNCERTAINTY);
                dataRows = (Double[][]) selections.get(ImportDataType.DATA);

                ObservableDataTable data = new ObservableDataTable(dataRows, true, headerRows, isotopeType, format);
                data.setTitle(String.valueOf(selections.get(ImportDataType.TITLE)));

                Map<Variable<Number>, Integer> varIndexMap = (Map<Variable<Number>, Integer>) selections.get(
                        ImportDataType.VARIABLE_INDEX_MAP);
                varIndexMap.entrySet().forEach((entry) -> {
                    data.setVariableForColumn(entry.getValue(), entry.getKey());
                });

                tables.add(data);
            }

            File projectFile = new File(titleView.getProjectLocation().toString() + File.separator +
                                        titleView.getTitle() + ".topsoil");

            // Sets the current project
            TopsoilSerializer.setCurrentProjectFile(projectFile);

        // New empty project
        } else if (sourcesView.didFinish()) {

            tables = new ArrayList<>();
            File projectFile = new File(titleView.getProjectLocation().toString() + File.separator +
                                        titleView.getTitle() + ".topsoil");

            // Sets the current project
            TopsoilSerializer.setCurrentProjectFile(projectFile);
        }
        return tables;
    }

	/**
	 * Controller that allows the user to specify the title and location of their new project.
	 *
	 * @author marottajb
	 */
	public class NewProjectTitleView extends AnchorPane {

		private static final String PROJECT_TITLE_FXML = "project-title.fxml";
		private final ResourceExtractor resourceExtractor = new ResourceExtractor(NewProjectTitleView.class);

		@FXML private TextField titleTextField, pathTextField;
		@FXML private Button cancelButton, nextButton;

		/**
		 * The next scene in the "New Project" sequence. As of typing, this is a Scene containing a
		 * {@link NewProjectSourcesView}.
		 */
		private Scene nextScene;
		private static final int MAX_FILE_NAME_LENGTH = 60;

		//**********************************************//
		//                  PROPERTIES                  //
		//**********************************************//

		private ObjectProperty<Path> projectLocation;
		private ObjectProperty<Path> projectLocationProperty() {
			if (projectLocation == null) {
				projectLocation = new SimpleObjectProperty<>(null);
			}
			return projectLocation;
		}
		/**
		 * Returns the selected project file location.
		 *
		 * @return  File location
		 */
		Path getProjectLocation() {
			return projectLocationProperty().get();
		}
		private void setProjectLocation(File file) {
			projectLocationProperty().set(file.toPath());
		}

		//**********************************************//
		//                 CONSTRUCTORS                 //
		//**********************************************//

		NewProjectTitleView() {
			super();

			try {
				FXMLLoader loader = new FXMLLoader(resourceExtractor.extractResourceAsPath(PROJECT_TITLE_FXML).toUri().toURL());
				loader.setRoot(this);
				loader.setController(this);
				loader.load();
			} catch (IOException e) {
				throw new RuntimeException("Could not load " + PROJECT_TITLE_FXML, e);
			}
		}

		@FXML
		protected void initialize() {
			nextButton.setDisable(true);
			projectLocationProperty().addListener(c -> updateNextButtonDisabledProperty());
			titleTextField.textProperty().addListener(c -> updateNextButtonDisabledProperty());
			//Make home directory the default file location
			setProjectFile(new File(System.getProperty("user.home")));
		}

		//**********************************************//
		//                PUBLIC METHODS                //
		//**********************************************//

		/**
		 * Returns the specified title of the new project.
		 *
		 * @return  String title
		 */
		public String getTitle() {
			return titleTextField.getText();
		}

		/**
		 * Sets the next {@code Scene} in the "New Project" sequence. This scene will replace the current one when the
		 * user clicks the "Next" button.
		 *
		 * @param   scene
		 *          next Scene
		 */
		void setNextScene(Scene scene) {
			nextScene = scene;
		}

		//**********************************************//
		//               PRIVATE METHODS                //
		//**********************************************//

		/**
		 * When the user presses the "..." button, a {@code DirectoryChooser} is opened where they can specify the
		 * desired location for their new project file.
		 */
		@FXML private void choosePathButtonAction() {
			DirectoryChooser dirChooser = new DirectoryChooser();
			dirChooser.setTitle("Choose Project Location");

			File file = dirChooser.showDialog(MainWindow.getPrimaryStage());
			setProjectFile(file);
		}

		private void setProjectFile(File file) {
			if (file != null) {
				String fileName = file.getPath();

				if (fileName.length() > MAX_FILE_NAME_LENGTH) {
					fileName = "..." + fileName.substring(fileName.length() - MAX_FILE_NAME_LENGTH);
				}

				setProjectLocation(file);
				pathTextField.setText(fileName);
			}
		}

		/**
		 * Closes the {@code Stage} without doing anything when the user clicks "Cancel".
		 */
		@FXML private void cancelButtonAction() {
			((Stage) cancelButton.getScene().getWindow()).close();
		}

		/**
		 * Checks that all required information is valid, then sets the {@code Stage} to display the next {@code Scene}
		 * in the "New Project" sequence.
		 */
		@FXML private void nextButtonAction() {
			File file = new File(getProjectLocation().toString() + File.separator + getTitle() + ".topsoil");
			System.out.println("Target: C:\\Users\\Jake\\Desktop\\test.topsoil");
			System.out.println("Actual: " + file.getAbsolutePath());

			if (file.exists()) {
				TopsoilNotification.showNotification(
						TopsoilNotification.NotificationType.YES_NO,
						"Existing Project",
						"There is already a project in that location with that name. Would you like to replace it?"
				                                    )
						.filter(response -> response == ButtonType.YES)
						.ifPresent(response -> {
							if (nextScene != null) {
								((Stage) nextButton.getScene().getWindow()).setScene(nextScene);
							} else {
								((Stage) nextButton.getScene().getWindow()).close();
							}
						});
			} else {
				if (nextScene != null) {
					((Stage) nextButton.getScene().getWindow()).setScene(nextScene);
				} else {
					((Stage) nextButton.getScene().getWindow()).close();
				}
			}
		}

		/**
		 * Updates the {@link Button#disableProperty()} of {@link #nextButton} based on whether all required information
		 * is present.
		 */
		private void updateNextButtonDisabledProperty() {
			if (projectLocation.get() == null) {
				nextButton.setDisable(true);
			} else if (titleTextField.getText().equals("")) {
				nextButton.setDisable(true);
			} else {
				nextButton.setDisable(false);
			}
		}
	}

	/**
	 * Controller for a window that allows the user to specify whether they want to import existing files into Topsoil,
	 * and which files they want to import.
	 *
	 * @author Jake Marotta
	 */
	public class NewProjectSourcesView extends AnchorPane {

		private static final String PROJECT_SOURCES_FXML = "project-sources.fxml";
		private final ResourceExtractor resourceExtractor = new ResourceExtractor(NewProjectSourcesView.class);

		private ToggleGroup toggle;
		@FXML private RadioButton emptyProjectButton, fromFilesButton;
		@FXML private Button addFilesButton, removeFileButton;
		@FXML private ListView<Label> sourceFileListView;
		@FXML private Button cancelButton, nextButton;

		/**
		 * The previous scene in the "New Project" sequence. As of typing, this should be a Scene containing a
		 * {@link NewProjectSourcesView}.
		 */
		private Scene previousScene;
		/**
		 * The next scene in the "New Project" sequence. As of typing, this should be a Scene containing a {@link
		 * ProjectSourcesOptionsView}.
		 */
		private Scene nextScene;

		private Boolean didFinish;

		private BiMap<Path, Label> pathLabelBiMap;

		//**********************************************//
		//                  PROPERTIES                  //
		//**********************************************//

		/**
		 * A {@code ListProperty} containing a list of type {@code PathDelimiterPair}, which keeps track of the {@code
		 * Path}s of source files as well as the appropriate {@code String} delimiter for each of them.
		 */
		private ListProperty<PathDelimiterPair> pathDelimiterList;

		ListProperty<PathDelimiterPair> pathDelimiterListProperty() {
			if ( pathDelimiterList == null ) {
				pathDelimiterList = new SimpleListProperty<>(FXCollections.observableArrayList());
			}
			return pathDelimiterList;
		}

		//**********************************************//
		//                 CONSTRUCTORS                 //
		//**********************************************//

		NewProjectSourcesView() {
			super();

			try {
				FXMLLoader loader = new FXMLLoader(resourceExtractor.extractResourceAsPath(PROJECT_SOURCES_FXML).toUri().toURL());
				loader.setRoot(this);
				loader.setController(this);
				loader.load();
			} catch ( IOException e ) {
				throw new RuntimeException("Could not load " + PROJECT_SOURCES_FXML, e);
			}
		}

		@FXML
		protected void initialize() {
			didFinish = false;
			pathLabelBiMap = HashBiMap.create();

			toggle = new ToggleGroup();
			toggle.getToggles().addAll(emptyProjectButton, fromFilesButton);

			// Disable file selection controls
			addFilesButton.setDisable(true);
			removeFileButton.setDisable(true);
			sourceFileListView.setDisable(true);

			nextButton.setDisable(true);
			toggle.selectedToggleProperty().addListener(c -> {
				if ( toggle.getSelectedToggle() != null ) {
					if ( toggle.getSelectedToggle() == emptyProjectButton ) {
						nextButton.setText("Finish");
						disableFileSelection();

					} else if ( toggle.getSelectedToggle() == fromFilesButton ) {
						nextButton.setText("Next");
						enableFileSelection();
					}
				} else {
					disableFileSelection();
				}
				updateNextButtonDisabledProperty();
			});
			toggle.selectToggle(emptyProjectButton);

			sourceFileListView.getSelectionModel().selectedItemProperty().addListener(c -> {
				if ( sourceFileListView.getSelectionModel().getSelectedItem() == null ) {
					removeFileButton.setDisable(true);
				} else {
					removeFileButton.setDisable(false);
				}
			});

		}

		//**********************************************//
		//                PUBLIC METHODS                //
		//**********************************************//

		/**
		 * Returns true if the {@code Stage} was closed on this {@code Scene}.
		 *
		 * @return true if Stage was closed here
		 */
		Boolean didFinish() {
			return didFinish;
		}

		/**
		 * Sets the previous {@code Scene} in the "New Project" sequence. This scene will replace the current one when
		 * the user clicks the "Previous" button.
		 *
		 * @param scene
		 * 		previous Scene
		 */
		void setPreviousScene( Scene scene ) {
			previousScene = scene;
		}

		/**
		 * Sets the next {@code Scene} in the "New Project" sequence. This scene will replace the current one when the
		 * user clicks the "Next" button.
		 *
		 * @param scene
		 * 		next Scene
		 */
		void setNextScene( Scene scene ) {
			nextScene = scene;
		}

		/**
		 * Upon pressing "Add Files...", the user is presented with a {@code FileChooser} where they can select multiple
		 * table files to import into their new project.
		 */
		@FXML
		private void addFilesButtonAction() {
			List<File> files = TopsoilFileChooser.openTableFile().showOpenMultipleDialog(MainWindow.getPrimaryStage());

			if ( files != null ) {

				List<File> selectedFiles = new ArrayList<>(files);
				List<File> rejectedFiles = new ArrayList<>();

				if ( !selectedFiles.isEmpty() ) {

					// Check all of the files for compatibility.
					Iterator<File> iterator = selectedFiles.iterator();
					Path path;
					while ( iterator.hasNext() ) {
						File file = iterator.next();
						path = Paths.get(file.toURI());
						String delim;

						try {
							delim = FileParser.getDelimiter(path);
						} catch ( IOException e ) {
							throw new RuntimeException(e);
						}

						if ( delim == null ) {
							delim = DelimiterRequestDialog.showDialog(
									"Delimiter Request",
									file.getName() + ": Please select the separator for this file.",
									false
							                                         );
						}

						boolean valid;
						try {
							valid = FileParser.isFileSupported(path) && !FileParser.isFileEmpty(path);
						} catch ( IOException e ) {
							throw new RuntimeException(e);
						}

						if ( !valid || delim == null ) {
							iterator.remove();
							rejectedFiles.add(file);
						} else {
							try {
								if ( FileParser.parseData(path, delim).length <= 0 ) {
									throw new TopsoilException("Invalid delimiter.");
								}
								if ( pathLabelBiMap.containsKey(file.toPath()) ) {
									iterator.remove();
								} else {
									Label pathLabel = new Label(file.getName());
									sourceFileListView.getItems().add(pathLabel);
									pathLabelBiMap.put(file.toPath(), pathLabel);
									pathDelimiterListProperty().add(new PathDelimiterPair(file.toPath(), delim));
								}
							} catch ( TopsoilException e ) {
								iterator.remove();
								rejectedFiles.add(file);
							} catch ( IOException e ) {
								throw new RuntimeException(e);
							}
						}
					}

					if ( rejectedFiles.size() > 0 ) {
						StringBuilder badFileNames = new StringBuilder("");
						for (File file : rejectedFiles) {
							badFileNames.append(file.getName());
							badFileNames.append("\n");
						}

						TopsoilNotification.showNotification(
								TopsoilNotification.NotificationType.ERROR,
								"Incompatible Files",
								"Topsoil could not read the following files: \n\n" + badFileNames.toString()
						                                    );
					}
					updateNextButtonDisabledProperty();
				}
			}
		}

		/**
		 * Upon pressing "Remove File", the currently selected item in {@link #sourceFileListView} is removed.
		 */
		@FXML
		private void removeFileButtonAction() {
			// Remove from paths
			for (PathDelimiterPair pair : pathDelimiterList) {
				if ( pair.getPath().equals(pathLabelBiMap.inverse().get(sourceFileListView.getSelectionModel().getSelectedItem())) ) {
					pathDelimiterListProperty().remove(pair);
					break;
				}
			}

			// Remove from map
			pathLabelBiMap.inverse().remove(sourceFileListView.getSelectionModel().getSelectedItem());

			// Remove from ListView
			sourceFileListView.getItems().remove(sourceFileListView.getSelectionModel().getSelectedItem());

			updateNextButtonDisabledProperty();
		}

		/**
		 * Upon pressing "Cancel", the {@code Stage} is closed without doing anything.
		 */
		@FXML
		private void cancelButtonAction() {
			((Stage) cancelButton.getScene().getWindow()).close();
		}

		/**
		 * Sets the {@code Stage} to display the previous {@code Scene} in the "New Project" sequence.
		 */
		@FXML
		private void previousButtonAction() {
			if ( previousScene != null ) {
				((Stage) nextButton.getScene().getWindow()).setScene(previousScene);
			} else {
				((Stage) nextButton.getScene().getWindow()).close();
			}
		}

		/**
		 * Sets the {@code Stage} to display the next {@code Scene} in the "New Project" sequence.
		 */
		@FXML
		private void nextButtonAction() {
			if ( toggle.getSelectedToggle() == fromFilesButton && nextScene != null ) {
				((Stage) nextButton.getScene().getWindow()).setScene(nextScene);
			} else {
				didFinish = true;
				((Stage) nextButton.getScene().getWindow()).close();
			}
		}

		/**
		 * Updates the {@link Button#disableProperty()} of {@link #nextButton} based on whether all required information
		 * is present.
		 */
		private void updateNextButtonDisabledProperty() {
			if ( toggle.getSelectedToggle() == null ) {
				nextButton.setDisable(true);
			} else if ( toggle.getSelectedToggle() == fromFilesButton && sourceFileListView.getItems().isEmpty() ) {
				nextButton.setDisable(true);
			} else {
				nextButton.setDisable(false);
			}
		}

		/**
		 * Enables controls that allow the user to select source files.
		 */
		private void enableFileSelection() {
			addFilesButton.setDisable(false);
			if ( sourceFileListView.getItems().size() > 0 ) {
				removeFileButton.setDisable(false);
			}
			sourceFileListView.setDisable(false);
		}

		/**
		 * Disables controls that allow the user to select source files.
		 */
		private void disableFileSelection() {
			addFilesButton.setDisable(true);
			removeFileButton.setDisable(true);
			sourceFileListView.setDisable(true);
		}
	}

	/**
	 * Controller that allows the user to preview their imported source files, as well as choose an
	 * {@link UncertaintyFormat} and {@link IsotopeSystem} for each data table.
	 *
	 * @author marottajb
	 */
	public class ProjectSourcesOptionsView extends AnchorPane {

		private static final String CONTROLLER_FXML = "project-preview.fxml";
		private static final String WARNING_ICON_PATH = "warning.png";

		private final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(ProjectSourcesOptionsView.class);
		private final ImageView WARNING_ICON = new ImageView(RESOURCE_EXTRACTOR.extractResourceAsPath(WARNING_ICON_PATH)
				                                                     .toUri().toString());

		@FXML private TabPane fileTabs;
		@FXML private Button cancelButton;
		@FXML private Button finishButton;

		private Boolean didFinish;

		/**
		 * The previous scene in the "New Project" sequence. As of typing, this should be a Scene containing a
		 * {@link NewProjectSourcesView}.
		 */
		private Scene previousScene;

		//**********************************************//
		//                  PROPERTIES                  //
		//**********************************************//

		/**
		 * A {@code ListProperty} containing a list of type {@code PathDelimiterPair}, which keeps track of the
		 * {@code Path}s of source files as well as the appropriate {@code String} delimiter for each of them.
		 */
		private ListProperty<PathDelimiterPair> pathDelimiterList;
		ListProperty<PathDelimiterPair> pathDelimiterListProperty() {
			if (pathDelimiterList == null) {
				pathDelimiterList = new SimpleListProperty<>(FXCollections.observableArrayList());
			}
			return pathDelimiterList;
		}

		//**********************************************//
		//                 CONSTRUCTORS                 //
		//**********************************************//

		ProjectSourcesOptionsView() {
			super();

			try {
				FXMLLoader loader = new FXMLLoader(RESOURCE_EXTRACTOR.extractResourceAsPath(CONTROLLER_FXML).toUri().toURL());
				loader.setRoot(this);
				loader.setController(this);
				loader.load();
			} catch (IOException e) {
				throw new RuntimeException("Could not load " + CONTROLLER_FXML, e);
			}
		}

		@FXML
		protected void initialize() {
			pathDelimiterListProperty();
			didFinish = false;
			finishButton.setDisable(true);

			fileTabs.setStyle("-fx-open-tab-animation: NONE; -fx-close-tab-animation: NONE;");

			WARNING_ICON.setPreserveRatio(true);
			WARNING_ICON.setFitHeight(20.0);

			for (Tab tab : fileTabs.getTabs()) {
				((ProjectSourcesOptionsView.DataPreviewControllerTab) tab).getController().uncertaintyFormatProperty().addListener(c -> {
					boolean incomplete = false;
					for (Tab t : fileTabs.getTabs()) {
						if ( ((ProjectSourcesOptionsView.DataPreviewControllerTab) t).getController().getUncertaintyFormat() == null) {
							incomplete = true;
							break;
						}
					}
					if (incomplete) {
						finishButton.setDisable(true);
					} else {
						finishButton.setDisable(false);
					}
				});
			}

			pathDelimiterListProperty().addListener((ListChangeListener<PathDelimiterPair>) c -> {
				c.next();

				if (c.wasAdded()) {
					PathDelimiterPair pair = pathDelimiterListProperty().get(c.getTo() - 1);

					finishButton.setDisable(true);
					ProjectSourcesOptionsView.DataPreviewControllerTab controllerTab = new ProjectSourcesOptionsView.DataPreviewControllerTab(pair.getPath(), pair.getDelimiter());
					controllerTab.getController().uncertaintyFormatProperty().addListener(ch -> {
						updateFinishButtonDisabledProperty();
					});

					fileTabs.getTabs().add(controllerTab);
				} else if (c.wasRemoved()) {
					fileTabs.getTabs().remove(c.getFrom());
				}

			});
		}

		//**********************************************//
		//                PUBLIC METHODS                //
		//**********************************************//

		/**
		 * Returns true if the {@code Stage} was closed on this {@code Scene}.
		 *
		 * @return  true if Stage was closed here
		 */
		Boolean didFinish() {
			return didFinish;
		}

		/**
		 * Returns a {@code List} of {@code Map}s containing information from each of the {@link DataImportOptionsView}s
		 * from the specified source files.
		 *
		 * @return  List of Maps of DataImportKeys to Objects
		 */
		List<Map<ImportDataType, Object>> getSelections() {
			// TODO
			List<Map<ImportDataType, Object>> allSelections = new ArrayList<>();

			DataImportOptionsView controller;
			for (Tab tab : fileTabs.getTabs()) {
				controller = ((ProjectSourcesOptionsView.DataPreviewControllerTab) tab).getController();
				Map<ImportDataType, Object> selections = new HashMap<>();

				selections.put(ImportDataType.TITLE, tab.getText());
				selections.put(ImportDataType.HEADERS, controller.getHeaders());
				selections.put(ImportDataType.DATA, controller.getData());
				selections.put(ImportDataType.UNCERTAINTY, controller.getUncertaintyFormat());
				selections.put(ImportDataType.ISOTOPE_TYPE, controller.getIsotopeType());
				selections.put(ImportDataType.VARIABLE_INDEX_MAP, controller.getVariableIndexMap());

				allSelections.add(selections);
			}

			return allSelections;
		}

		/**
		 * Sets the previous {@code Scene} in the "New Project" sequence. This scene will replace the current one when the
		 * user clicks the "Previous" button.
		 *
		 * @param scene previous Scene
		 */
		void setPreviousScene(Scene scene) {
			previousScene = scene;
		}

		//**********************************************//
		//               PRIVATE METHODS                //
		//**********************************************//

		/**
		 * Upon pressing "Cancel", the {@code Stage} is closed without doing anything.
		 */
		@FXML private void cancelButtonAction() {
			((Stage) cancelButton.getScene().getWindow()).close();
		}

		/**
		 * Sets the {@code Stage} to display the previous {@code Scene} in the "New Project" sequence.
		 */
		@FXML private void previousButtonAction() {
			if (previousScene != null) {
				((Stage) finishButton.getScene().getWindow()).setScene(previousScene);
			} else {
				((Stage) finishButton.getScene().getWindow()).close();
			}
		}

		/**
		 * Finishes the "New Project" sequence.
		 */
		@FXML private void finishButtonAction() {
			didFinish = true;
			((Stage) cancelButton.getScene().getWindow()).close();
		}

		/**
		 * Updates the {@link Button#disableProperty()} of {@link #finishButton} based on whether all required information
		 * is present.
		 */
		private void updateFinishButtonDisabledProperty() {
			boolean incomplete = false;
			for (Tab t : fileTabs.getTabs()) {
				if ( ((ProjectSourcesOptionsView.DataPreviewControllerTab) t).getController().getUncertaintyFormat() == null) {
					incomplete = true;
					break;
				}
			}
			if (incomplete) {
				finishButton.setDisable(true);
			} else {
				finishButton.setDisable(false);
			}
		}

		//**********************************************//
		//                INNER CLASSES                 //
		//**********************************************//

		/**
		 * A custom {@code Tab} which contains a {@link DataImportOptionsView}.
		 */
		private class DataPreviewControllerTab extends Tab {

			private DataImportOptionsView controller;

			DataPreviewControllerTab(Path path, String delim) {
				super();
				try {
					String[] headers = FileParser.parseHeaders(path, delim);
					Double[][] data = FileParser.parseData(path, delim);

					controller = new DataImportOptionsView(headers, data);
					this.setContent(controller);
					this.setText(path.getFileName().toString());

					ImageView warningIconCopy = new ImageView(WARNING_ICON.getImage());
					warningIconCopy.setPreserveRatio(true);
					warningIconCopy.setFitHeight(20.0);
					this.setGraphic(warningIconCopy);

					controller.uncertaintyFormatProperty().addListener(c -> {
						if (controller.getUncertaintyFormat() == null) {
							this.setGraphic(warningIconCopy);
						} else {
							this.setGraphic(null);
						}
                    /*
                        The TabPane overflow menu doesn't automatically update the tab's graphic when it's changed,
                        so the only way to get it to do so is to remove the tab, then add it again. The animations
                        have been disabled to make this a smoother process.
                     */
						TabPane tabPane = this.getTabPane();
						int index = tabPane.getTabs().indexOf(this);
						tabPane.getTabs().remove(this);
						tabPane.getTabs().add(index, this);
						tabPane.getSelectionModel().select(this);
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private DataImportOptionsView getController() {
				return controller;
			}
		}
	}

	/**
	 * Keeps track of the {@code Path} of a source files as well as the appropriate {@code String} delimiter for
	 * parsing it.
	 */
	class PathDelimiterPair {

		private Path path;
		private String delim;

		private PathDelimiterPair( Path path, String delim ) {
			this.path = path;
			this.delim = delim;
		}

		/**
		 * Returns the associated {@code Path}.
		 *
		 * @return Path
		 */
		Path getPath() {
			return path;
		}

		/**
		 * Returns the {@code String} delimiter for the associated {@code Path}.
		 *
		 * @return String delimiter
		 */
		String getDelimiter() {
			return delim;
		}
	}
}
