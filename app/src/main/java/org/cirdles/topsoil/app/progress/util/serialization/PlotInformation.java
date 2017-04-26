package org.cirdles.topsoil.app.progress.util.serialization;

import javafx.stage.Stage;
import org.cirdles.topsoil.app.plot.PlotContext;
import org.cirdles.topsoil.app.plot.VariableBinding;
import org.cirdles.topsoil.app.plot.VariableFormat;
import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;
import org.cirdles.topsoil.plot.BasePlot;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Plot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores information about an open <tt>Plot</tt>. Kept inside of
 * <tt>TopsoilTable</tt> so that each table knows which open plots use its
 * information.
 *
 * @author marottajb
 *
 * @see Plot
 * @see TopsoilPlotType
 * @see org.cirdles.topsoil.app.progress.table.TopsoilTable
 */
public class PlotInformation {

    private Plot plot;
    private TopsoilPlotType plotType;
    private Collection<VariableBinding> variableBindings;
    private Map<String, Object> plotProperties;
    private Stage stage;

    private VariableFormat<Number> xUncertainty;
    private VariableFormat<Number> yUncertainty;

    /**
     * Constructs an instance of <tt>PlotInformation</tt> for the specified
     * <tt>Plot</tt> object and <tt>TopsoilPlotType</tt>.
     *
     * @param plot  the Plot object
     * @param plotType  the TopsoilPlotType of the plot
     */
    public PlotInformation(Plot plot, TopsoilPlotType plotType, Map<String, Object> plotProperties, PlotContext
            plotContext, Stage stage) {
        this.plot = plot;
        this.plotType = plotType;
        this.plotProperties = plotProperties;
        this.variableBindings = plotContext.getBindings();
        this.stage = stage;
    }

    /**
     * Returns the <tt>Plot</tt> object.
     *
     * @return  the Plot
     */
    public Plot getPlot() {
        return this.plot;
    }

    /**
     * Returns the <tt>Plot</tt>'s <tt>TopsoilPlotType</tt>.
     *
     * @return  the plot's TopsoilPlotType
     */
    public TopsoilPlotType getTopsoilPlotType() {
        return this.plotType;
    }

    /**
     * Returns the <tt>HashMap</tt> containing the <tt>Plot</tt>'s properties.
     *
     * @return  the HashMap of plot properties
     */
    public Map<String, Object> getPlotProperties() {
        return this.plotProperties;
    }

    /**
     * Returns a Colletion of the plot's <tt>VariableBinding</tt>s.
     *
     * @return  a Collection of VariableBinding names
     */
    public Collection<VariableBinding> getVariableBindings() {
        return this.variableBindings;
    }

    /**
     * Converts the <tt>VariableBinding</tt>s of the <tt>Plot</tt> into a
     * <tt>HashMap</tt> for easy reference and stores them.
     *
     * @param bindings  a Collection of VariableBindings
     */
    public void setVariableBindings(Collection<VariableBinding> bindings) {
        this.variableBindings = bindings;
    }

    /**
     * Returns the <tt>Stage</tt> that is showing the <tt>Plot</tt>.
     *
     * @return  the Stage displaying the plot
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Define which open <tt>Stage</tt> is showing the <tt>Plot</tt>.
     *
     * @param stage the Stage displaying the plot
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Attempts to destroy the plot within its WebEngine, to prevent threading problems.
     */
    public void killPlot() {
        try {
            ((JavaScriptPlot) this.plot).killPlot();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
