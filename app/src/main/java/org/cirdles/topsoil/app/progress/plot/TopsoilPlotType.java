package org.cirdles.topsoil.app.progress.plot;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.cirdles.topsoil.app.plot.Variable;
import org.cirdles.topsoil.app.plot.Variables;
import org.cirdles.topsoil.app.plot.standard.ScatterPlotPropertiesPanel;
import org.cirdles.topsoil.app.plot.standard.UncertaintyEllipsePlotPropertiesPanel;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.scatter.ScatterPlot;
import org.cirdles.topsoil.plot.upb.uncertainty.UncertaintyEllipsePlot;
import org.cirdles.topsoil.plot.uth.evolution.EvolutionPlot;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

/**
 * Created by benjaminmuldrow on 8/9/16.
 */
public enum TopsoilPlotType {

    SCATTER_PLOT("Scatter Plot",
            asList(Variables.X, Variables.SIGMA_X, Variables.Y, Variables.SIGMA_Y, Variables.RHO),
            ScatterPlot::new, ScatterPlotPropertiesPanel::new),

    UNCERTAINTY_ELLIPSE_PLOT("Uncertainty Ellipse Plot",
            asList(Variables.X, Variables.SIGMA_X, Variables.Y, Variables.SIGMA_Y, Variables.RHO),
            UncertaintyEllipsePlot::new, UncertaintyEllipsePlotPropertiesPanel::new),

    EVOLUTION_PLOT("Evolution Plot",
            asList(Variables.X, Variables.SIGMA_X, Variables.Y, Variables.SIGMA_Y, Variables.RHO),
            EvolutionPlot::new, TopsoilPlotType::emptyPropertyPanel);

    private final String name;
    private final List<Variable> variables;
    private final Plot plot;
    private final Node propertiesPanel;

    TopsoilPlotType(String name, List<Variable> variables, Supplier<? extends Plot> plot,
                           Function<Plot, ? extends Node> propertiesPanel) {
        this.name = name;
        this.variables = variables;
        this.plot = plot.get();
        this.propertiesPanel = propertiesPanel.apply(plot.get());
    }

    public static Node emptyPropertyPanel(Plot plot) {
        return new VBox();
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

    public Node getPropertiesPanel() {
        return propertiesPanel;
    }
}
