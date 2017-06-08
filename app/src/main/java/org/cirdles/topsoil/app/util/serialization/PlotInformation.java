package org.cirdles.topsoil.app.util.serialization;

import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import org.cirdles.topsoil.app.plot.TopsoilPlotType;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Plot;

/**
 * Stores information about an open {@link Plot}.
 *
 * @author Jake Marotta
 *
 * @see Plot
 * @see TopsoilPlotType
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
     * @param stage the Stage displaying the Plot
     */
    public PlotInformation(Plot plot, TopsoilPlotType plotType, ObservableMap<String, Object> plotProperties, Stage stage) {
        this.plot = plot;
        this.plotType = plotType;
        this.plotProperties = plotProperties;
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
     * Returns the {@code Stage} that is showing the {@code Plot}.
     *
     * @return  the Stage displaying the plot
     */
    public Stage getStage() {
        return this.stage;
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
