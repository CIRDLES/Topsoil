/*
 * Copyright 2014 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.topsoil.app;

import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.util.BuilderFactory;
import org.cirdles.topsoil.app.browse.WebBrowser;
import org.cirdles.topsoil.app.dataset.DatasetMapper;
import org.cirdles.topsoil.app.flyway.FlywayMigrateTask;
import org.cirdles.topsoil.app.metadata.ApplicationMetadata;
import org.cirdles.topsoil.app.metadata.TestApplicationMetadata;
import org.cirdles.topsoil.app.util.AboutDialog;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.testfx.framework.junit.ApplicationTest;

import javax.inject.Provider;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.testfx.api.FxAssert.verifyThat;

/**
 *
 * @author John Zeringue
 */
public class TopsoilMainWindowTest extends ApplicationTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private AboutDialog aboutDialog;

    @Mock
    private Provider<AboutDialog> aboutDialogProvider;

    @Mock
    private DatasetMapper datasetMapper;

    @Mock
    private FlywayMigrateTask flywayMigrateTask;

    @Mock
    private WebBrowser webBrowser;

    private TopsoilMainWindow topsoilMainWindow;

    @Override
    public void start(Stage stage) throws Exception {
        ApplicationMetadata metadata = new TestApplicationMetadata();
        BuilderFactory builderFactory = new JavaFXBuilderFactory();

        topsoilMainWindow = new TopsoilMainWindow(
                aboutDialogProvider,
                metadata,
                builderFactory,
                datasetMapper,
                flywayMigrateTask,
                webBrowser);

        Scene scene = new Scene(topsoilMainWindow);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testFocusOnNewTab() {
        // ensure there is at least one tab already open
        clickOn("Create Data Table");

        // create another tab
        // should be focused
        clickOn("Create Data Table");

        verifyThat(topsoilMainWindow.getDataTableTabPane(), (TabPane tabPane) -> {
            int numberOfTabs = tabPane.getTabs().size();
            int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();

            return selectedIndex == numberOfTabs - 1;
        });
    }

    @Test
    public void testSaveDataButtonInitiallyDisabled() {
        verifyThat("#saveDataTableButton", button -> button.isDisabled());
    }

    @Test
    public void testSaveDataButtonEnablesAfterNewDataTable() {
        clickOn("Create Data Table");
        verifyThat("#saveDataTableButton", button -> !button.isDisabled());
    }

    @Test
    @Ignore
    public void testSaveDataButtonDisablesAfterTabsAreClosed() {
        clickOn("Create Data Table");
        clickOn("Create Data Table");

        Tab tab1 = topsoilMainWindow.getDataTableTabPane().getTabs().remove(0);
        tab1.getOnClosed().handle(null);

        // shouldn't be disabled yet
        // one tab is still open
        verifyThat("#saveDataTableButton", button -> !button.isDisabled());

        Tab tab2 = topsoilMainWindow.getDataTableTabPane().getTabs().remove(0);
        tab2.getOnClosed().handle(null);

        verifyThat("#saveDataTableButton", button -> button.isDisabled());
    }

    @Test
    @Ignore
    public void testAboutDialogOpens() {
        when(aboutDialogProvider.get()).thenReturn(aboutDialog);

        clickOn("Help").clickOn("About");

        verify(aboutDialog).run();
    }

}
