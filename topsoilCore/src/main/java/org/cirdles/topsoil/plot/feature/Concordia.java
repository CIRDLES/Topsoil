package org.cirdles.topsoil.plot.feature;

public enum Concordia {

    WETHERILL("Wetherill"),
    TERA_WASSERBURG("Tera-Wasserburg");

    private String title;

    Concordia(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
