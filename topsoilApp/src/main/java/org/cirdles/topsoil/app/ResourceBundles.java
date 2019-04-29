package org.cirdles.topsoil.app;

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

    /**
     * A shortcut for ResourceBundles.BUNDLE.getBundle().getString(String s).
     *
     * @param key   String key
     *
     * @return      String value
     */
    public String getString(String key) {
        return bundle.getString(key);
    }

}
