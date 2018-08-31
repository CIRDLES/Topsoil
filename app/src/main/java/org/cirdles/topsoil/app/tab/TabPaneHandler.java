package org.cirdles.topsoil.app.tab;

/**
 * This class provides utility methods for accessing the application's {@code TopsoilTabPane}. There should only be
 * one, containing tabs for each open data table. The value of {@code tabs} is set once when {@code MainWindow} starts.
 */
public class TabPaneHandler {

	private static TopsoilTabPane tabs;

	public static TopsoilTabPane getTabPane() {
		return tabs;
	}

	public static void setTabPane(TopsoilTabPane tabPane) {
		tabs = tabPane;
	}
}
