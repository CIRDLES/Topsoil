package org.cirdles.topsoil.app.util.serialization;

import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.plot.PlotContext;
import org.cirdles.topsoil.app.plot.VariableBinding;
import org.cirdles.topsoil.app.plot.TopsoilPlotType;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Plot;

import java.util.Collection;

/**
 * Stores information about an open {@link Plot}.
 *
 * @author Jake Marotta
 *
 * @see Plot
 * @see TopsoilPlotType
 * @see VariableBinding
 */
public class PlotInformation {

    //***********************
    // Attributes
    //***********************

    /**
     * The {@code Plot} instance itself.
     */
    private Plot plot;

    /**
     * The {@code TopsoilPlotType} of the {@code Plot}.
     */
    private TopsoilPlotType plotType;

    /**
     * A {@code Collection} of {@code VariableBinding}s for the {@code Plot}.
     */
    private Collection<VariableBinding> variableBindings;

    /**
     * An {@code ObservableMap} containing plot property values.
     */
    private ObservableMap<String, Object> plotProperties;

    /**
     * The {@code Stage} that is displaying the {@code Plot}.
     */
    private Stage stage;

    //***********************
    // Constructors
    //***********************

    /**
     * Constructs an instance of {@code PlotInformation} for the specified {@code Plot} object and
     * {@code TopsoilPlotType}.
     *
     * @param plot  the Plot object
     * @param plotProperties    an ObservableMap of plot settings
     * @param plotType  the TopsoilPlotType of the plot
     * @param plotContext a PlotContext
     * @param stage the Stage displaying the Plot
     */
    public PlotInformation(Plot plot, TopsoilPlotType plotType, ObservableMap<String, Object> plotProperties,
                           PlotContext plotContext, Stage stage) {
        this.plot = plot;
        this.plotType = plotType;
        this.plotProperties = plotProperties;
        this.variableBindings = plotContext.getBindings();
        this.stage = stage;
    }

    //***********************
    // Methods
    //***********************

    /**
     * Returns the {@code Plot} instance.
     *
     * @return  the Plot
     */
    public Plot getPlot() {
        return this.plot;
    }

    /**
     * Returns the {@code Plot}'s {@code TopsoilPlotType}.
     *
     * @return  the plot's TopsoilPlotType
     */
    public TopsoilPlotType getTopsoilPlotType() {
        return this.plotType;
    }

    /**
     * Returns the {@code HashMap} containing the {@code Plot}'s properties.
     *
     * @return  the HashMap of plot properties
     */
    public ObservableMap<String, Object> getPlotProperties() {
        return this.plotProperties;
    }

    /**
     * Returns a {@code Collection} of the plot's {@code VariableBinding}s.
     *
     * @return  a Collection of VariableBinding names
     */
    public Collection<VariableBinding> getVariableBindings() {
        return this.variableBindings;
    }

    /**
     * Converts the {@code VariableBinding}s of the {@code Plot} into a
     * {@code HashMap} for easy reference and stores them.
     *
     * @param bindings  a Collection of VariableBindings
     */
    public void setVariableBindings(Collection<VariableBinding> bindings) {
        this.variableBindings = bindings;
    }

    /**
     * Returns the {@code Stage} that is showing the {@code Plot}.
     *
     * @return  the Stage displaying the plot
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Define which {@code Stage} displays the {@code Plot}.
     *
     * @param stage the Stage displaying the plot
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Attempts to destroy the {@code Plot} within the {@code WebEngine}, to prevent threading problems.
     */
    public void killPlot() {
        try {
            ((JavaScriptPlot) this.plot).killPlot();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
