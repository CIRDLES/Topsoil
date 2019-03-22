package org.cirdles.topsoil.app.util;

import java.util.ResourceBundle;

public enum ResourceBundles {

    MAIN("Main"),
    MENU_BAR("TopsoilMenuBar"),
    DIALOGS("Dialogs");

    private static final String BUNDLES_DIR = "org/cirdles/topsoil/app/bundles/";

    private ResourceBundle bundle;

    ResourceBundles(String bundleName) {
        this.bundle = ResourceBundle.getBundle(BUNDLES_DIR + bundleName);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public String getString(String key) {
        return bundle.getString(key);
    }

}
