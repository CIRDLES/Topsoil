package org.cirdles.topsoil.app.util.serialization;

import org.cirdles.topsoil.app.plot.TopsoilPlotType;
import org.cirdles.topsoil.plot.Plot;

/**
 * A variety of keys used for storing {@link Plot} information in a {@link SerializableTopsoilSession}.
 *
 * @author Jake Marotta
 * @see SerializableTopsoilSession
 */
public class PlotDataKeys {

    /**
     * A key for the {@link TopsoilPlotType} of a {@code Plot}.
     */
    public static final String PLOT_TYPE = "Topsoil Plot Type";
    public static final String PLOT_PROPERTIES = "Plot Properties";

    private PlotDataKeys() {
        // Prevents this class from being instantiated.
    }

}
