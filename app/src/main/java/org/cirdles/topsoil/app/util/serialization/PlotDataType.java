package org.cirdles.topsoil.app.util.serialization;

/**
 * A variety of keys used for storing plots in a {@link SerializableTopsoilSession}.
 *
 * @author marottajb
 */
public enum PlotDataType {

    PLOT_TYPE("Plot type"),
	PLOT_PROPERTIES("Plot properties");

    private String key;

    PlotDataType(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
