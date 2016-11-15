package org.cirdles.topsoil.app.progress.util.serialization;

import javafx.stage.Stage;
import org.cirdles.topsoil.app.plot.VariableBinding;
import org.cirdles.topsoil.app.progress.plot.TopsoilPlotType;
import org.cirdles.topsoil.plot.Plot;

import java.util.Collection;
import java.util.HashMap;

public class PlotInformation {

    private Plot plot;
    private TopsoilPlotType plotType;
    // HashMap uses the variables name as a key, and the name of the column as the value.
    private HashMap<String, String> variableBindings;
    private HashMap<String, Object> plotProperties;
    private Stage stage;

    public PlotInformation(Plot plot, TopsoilPlotType plotType) {
        this.plot = plot;
        this.plotType = plotType;
        this.plotProperties = (HashMap) plot.getProperties();
    }

    public Plot getPlot() {
        return this.plot;
    }

    public TopsoilPlotType getTopsoilPlotType() {
        return this.plotType;
    }

    public HashMap<String, Object> getPlotProperties() {
        return this.plotProperties;
    }

    public void setVariableBindings(Collection<VariableBinding> bindings) {

        // Store each Variable name with its field name.
        this.variableBindings = new HashMap<>();
        for (VariableBinding binding : bindings) {
            variableBindings.put(binding.getVariable().getName(), binding.getField().getName());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return this.stage;
    }

    public HashMap<String, String> getVariableBindingNames() {
        return this.variableBindings;
    }
}
