package org.cirdles.topsoil.app.progress.util.serialization;

import javafx.stage.Stage;
import org.cirdles.topsoil.app.plot.VariableBinding;
import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;
import org.cirdles.topsoil.plot.Plot;

import java.util.Collection;
import java.util.HashMap;

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
    // HashMap uses the variables name as a key, and the name of the column as the value.
    private HashMap<String, String> variableBindings;
    private HashMap<String, Object> plotProperties;
    private Stage stage;

    /**
     * Constructs an instance of <tt>PlotInformation</tt> for the specified
     * <tt>Plot</tt> object and <tt>TopsoilPlotType</tt>.
     *
     * @param plot  the Plot object
     * @param plotType  the TopsoilPlotType of the plot
     */
    public PlotInformation(Plot plot, TopsoilPlotType plotType) {
        this.plot = plot;
        this.plotType = plotType;
        this.plotProperties = (HashMap) plot.getProperties();
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
    public HashMap<String, Object> getPlotProperties() {
        return this.plotProperties;
    }

    //TODO Associate variable bindings with a table instead of a plot.
    /**
     * Converts the <tt>VariableBinding</tt>s of the <tt>Plot</tt> into a
     * <tt>HashMap</tt> for easy reference and stores them.
     *
     * @param bindings  a Collection of VariableBindings
     */
    public void setVariableBindings(Collection<VariableBinding> bindings) {

        // Store each Variable name with its field name.
        this.variableBindings = new HashMap<>();
        for (VariableBinding binding : bindings) {
            variableBindings.put(binding.getVariable().getName(), binding.getField().getName());
        }
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
     * Returns the <tt>Stage</tt> that is showing the <tt>Plot</tt>.
     *
     * @return  the Stage displaying the plot
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Returns the <tt>HashMap</tt> associated with the <tt>Plot</tt>'s
     * <tt>VariableBinding</tt>s.
     *
     * @return  a HashMap of variable binding names
     */
    public HashMap<String, String> getVariableBindingNames() {
        return this.variableBindings;
    }
}
