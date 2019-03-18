package org.cirdles.topsoil.app.util;

import java.util.ResourceBundle;

public enum ResourceBundles {

    MAIN("org/cirdles/topsoil/app/Topsoil"),
    MENU_BAR("org/cirdles/topsoil/app/control/menu/TopsoilMenuBar"),
    DIALOGS("org/cirdles/topsoil/app/control/dialog/Dialogs");

    private ResourceBundle bundle;

    ResourceBundles(String bundleName) {
        this.bundle = ResourceBundle.getBundle(bundleName);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public String getString(String key) {
        return bundle.getString(key);
    }

}
