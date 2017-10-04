package org.cirdles.topsoil.app.plot;

import org.cirdles.topsoil.app.plot.variable.Variable;
import org.cirdles.topsoil.app.plot.variable.Variables;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.base.BasePlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * An enum with information for types of {@link Plot}s, including a {@code List} of the {@link Variable}s that that
 * plot uses, and an instance of the {@code Plot} itself.
 *
 * @author Benjamin Muldrow
 */
public enum TopsoilPlotType {

    BASE_PLOT("Base Plot",
            asList(Variables.X, Variables.SIGMA_X, Variables.Y, Variables.SIGMA_Y, Variables.RHO),
            BasePlot.class);

    /**
     * The name of the plot type.
     */
    private final String name;

    /**
     * A {@code List} of the supported {@code Variable}s for the plot type.
     */
    private final List<Variable> variables;

    /**
     * The {@code Class} of the plot.
     */
    private final Class<? extends Plot> plot;

    public static final List<TopsoilPlotType> TOPSOIL_PLOT_TYPES;
    static {
        TOPSOIL_PLOT_TYPES = Collections.unmodifiableList(Arrays.asList(
                BASE_PLOT
        ));
    }

    /**
     * Constructs a {@code TopsoilPlotType} with the given plot type name, supported {@code Variable}s, and plot
     * {@code Class}.
     *
     * @param name  String name of plot type
     * @param variables List of supported Variables
     * @param plot  plot Class
     */
    TopsoilPlotType(String name, List<Variable> variables, Class<? extends Plot> plot) {
        this.name = name;
        this.variables = variables;
        this.plot = plot;
    }

    /**
     * Returns the name of the plot type.
     *
     * @return  String name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a {@code List} of the plot type's supported variables.
     *
     * @return  List of Variables
     */
    public List<Variable> getVariables() {
        return variables;
    }

    /**
     * Returns a new instance of the plot {@code Class} using its nullary constructor.
     *
     * @return  new instance of class Class
     */
    public Plot getPlot() {
        try {
            return plot.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
