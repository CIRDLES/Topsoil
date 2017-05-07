package org.cirdles.topsoil.app.progress.plot;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.plot.Variable;
import org.cirdles.topsoil.app.plot.Variables;
import org.cirdles.topsoil.app.plot.standard.BasePlotPropertiesPanel;
import org.cirdles.topsoil.app.plot.standard.ScatterPlotPropertiesPanel;
import org.cirdles.topsoil.app.plot.standard.UncertaintyEllipsePlotPropertiesPanel;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.base.BasePlot;
import org.cirdles.topsoil.plot.scatter.ScatterPlot;
import org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlot;
import org.cirdles.topsoil.plot.uth.evolution.EvolutionPlot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

/**
 * Created by benjaminmuldrow on 8/9/16.
 */
public enum TopsoilPlotType {

    BASE_PLOT("Base Plot",
            asList(Variables.X, Variables.SIGMA_X, Variables.Y, Variables.SIGMA_Y, Variables.RHO),
            new BasePlot()),

    SCATTER_PLOT("Scatter Plot",
            asList(Variables.X, Variables.SIGMA_X, Variables.Y, Variables.SIGMA_Y, Variables.RHO),
            new ScatterPlot()),

    UNCERTAINTY_ELLIPSE_PLOT("Uncertainty Ellipse Plot",
            asList(Variables.X, Variables.SIGMA_X, Variables.Y, Variables.SIGMA_Y, Variables.RHO),
            new UncertaintyEllipsePlot()),

    EVOLUTION_PLOT("Evolution Plot",
            asList(Variables.X, Variables.SIGMA_X, Variables.Y, Variables.SIGMA_Y, Variables.RHO),
            new EvolutionPlot());

    private final String name;
    private final List<Variable> variables;
    private final Plot plot;

    public static final List<TopsoilPlotType> TOPSOIL_PLOT_TYPES;
    static {
        TOPSOIL_PLOT_TYPES = Collections.unmodifiableList(Arrays.asList(
                BASE_PLOT,
                SCATTER_PLOT,
                UNCERTAINTY_ELLIPSE_PLOT,
                EVOLUTION_PLOT
        ));
    }

    TopsoilPlotType(String name, List<Variable> variables, Plot plot) {
        this.name = name;
        this.variables = variables;
        this.plot = plot;
    }

    public String getName() {
        return name;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public Plot getPlot() {
        return plot;
    }
}
