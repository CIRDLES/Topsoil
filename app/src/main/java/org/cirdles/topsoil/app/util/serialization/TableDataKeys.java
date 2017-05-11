package org.cirdles.topsoil.app.util.serialization;

import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.plot.variable.format.VariableFormat;
import org.cirdles.topsoil.app.table.TopsoilDataTable;
import org.cirdles.topsoil.plot.Plot;

/**
 * A variety of keys used for storing {@link TopsoilDataTable} information in a {@link SerializableTopsoilSession}.
 *
 * @author Jake Marotta
 * @see SerializableTopsoilSession
 */
public class TableDataKeys {

    /**
     * A key for the title of a {@code TopsoilDataTable}.
     */
    public static final String TABLE_TITLE = "Title";

    /**
     * A key for the headers for a {@code TopsoilDataTable}.
     */
    public static final String TABLE_HEADERS = "Headers";

    /**
     * A key for the {@link IsotopeType} of a {@code TopsoilDataTable}.
     */
    public static final String TABLE_ISOTOPE_TYPE = "Isotope Type";

    /**
     * A key for the data from a {@code TopsoilDataTable}.
     */
    public static final String TABLE_DATA = "Data";

    /**
     * A key for the open {@link Plot}s of a {@code TopsoilDataTable}.
     */
    public static final String TABLE_PLOTS = "Plots";

    /**
     * A key for the plot properties for a {@code TopsoilDataTable}.
     */
    public static final String TABLE_PLOT_PROPERTIES = "Plot Properties";

    /**
     * A key for the {@link VariableFormat} of a {@code TopsoilDataTable}.
     */
    public static final String TABLE_UNCERTAINTY_FORMAT = "Uncertainty Format Name";

    private TableDataKeys() {
        // Prevents this class from being instantiated.
    }
}
