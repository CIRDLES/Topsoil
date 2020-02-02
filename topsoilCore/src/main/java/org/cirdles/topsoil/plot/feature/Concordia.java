package org.cirdles.topsoil.plot.feature;

public enum Concordia {

    WETHERILL("wetherill"),
    TERA_WASSERBURG("tera-wasserburg");

    private String title;

    Concordia(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
