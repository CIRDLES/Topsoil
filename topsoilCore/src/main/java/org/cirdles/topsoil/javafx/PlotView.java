package org.cirdles.topsoil.javafx;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Worker;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.Validate;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.data.DataColumn;
import org.cirdles.topsoil.data.DataTable;
import org.cirdles.topsoil.data.TableUtils;
import org.cirdles.topsoil.plot.DataEntry;
import org.cirdles.topsoil.plot.HTMLTemplate;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotFunction;
import org.cirdles.topsoil.plot.PlotOption;
import org.cirdles.topsoil.plot.PlotOptions;
import org.cirdles.topsoil.plot.PlotType;
import org.cirdles.topsoil.javafx.bridges.AxisExtentsBridge;
import org.cirdles.topsoil.javafx.bridges.JavaScriptBridge;
import org.cirdles.topsoil.javafx.bridges.Regression;
import org.cirdles.topsoil.symbols.SymbolMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.Map;

/**
 * A {@link Plot} that uses JavaScript and HTML to power its visualizations.
 *
 * @author John Zeringue
 */
public class PlotView extends Region implements Plot {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(PlotView.class);

    private PlotType plotType;
    private String htmlString;
    private DataTable table;

    private WebView webView;
    private JSObject topsoil;
    private final JavaScriptBridge bridge = new JavaScriptBridge();
    private final AxisExtentsBridge axisExtentsBridge;
    private final Regression regression = new Regression();

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private ReadOnlyListWrapper<DataEntry> plotData = new ReadOnlyListWrapper<>();
    public final ReadOnlyListProperty<DataEntry> plotDataProperty() {
        return plotData.getReadOnlyProperty();
    }
    @Override
    public final ObservableList<DataEntry> getPlotData() {
        return plotData.get();
    }

    private ReadOnlyMapWrapper<PlotOption<?>, Object> plotOptions = new ReadOnlyMapWrapper<>();
    public final ReadOnlyMapProperty<PlotOption<?>, Object> plotOptionsProperty() {
        return plotOptions;
    }
    @Override
    public final ObservableMap<PlotOption<?>, Object> getOptions() {
        return plotOptions.get();
    }
    @Override
    public final void setOptions(Map<PlotOption<?>, Object> options) {
        if (options instanceof PlotOptions) {
            plotOptions.set(FXCollections.observableMap(options));
        } else {
            plotOptions.set(FXCollections.observableMap(new PlotOptions(options)));
        }
    }

    private ReadOnlyMapWrapper<Variable<?>, DataColumn<?>> variableMap = new ReadOnlyMapWrapper<>();
    public final ReadOnlyMapProperty<Variable<?>, DataColumn<?>> variableMapProperty() {
        return variableMap.getReadOnlyProperty();
    }
    @Override
    public Map<Variable<?>, DataColumn<?>> getVariableMap() {
        return variableMap.get();
    }
    @Override
    public void setVariableMap(Map<Variable<?>, DataColumn<?>> variableMap) {
        if (variableMap instanceof ObservableMap) {
            this.variableMap.set((ObservableMap<Variable<?>, DataColumn<?>>) variableMap);
        } else {
            this.variableMap.set(FXCollections.observableMap(variableMap));
        }
        updateDataEntries();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public PlotView(PlotType plotType, DataTable table, Map<Variable<?>, DataColumn<?>> variableMap, PlotOptions options) {
        Validate.notNull(plotType, "Plot type cannot be null.");
        Validate.notNull(table, "Data table cannot be null.");
        Validate.notNull(variableMap, "Variable map cannot be null.");

        if (options == null) options = PlotOptions.defaultOptions();

        this.plotType = plotType;
        this.htmlString = HTMLTemplate.forPlotType(plotType);

        this.axisExtentsBridge = new AxisExtentsBridge(this);

        plotData.addListener((ListChangeListener<DataEntry>) c -> updateJSData());
        setDataTable(table, variableMap);

        plotOptions.addListener((MapChangeListener<PlotOption<?>, Object>) c -> updateJSOptions());
        setOptions(options);

        webView = new WebView();
        webView.setContextMenuEnabled(false);
        getChildren().add(webView);
        initializeWebEngine();
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    @Override
    public PlotType getPlotType() {
        return plotType;
    }

    @Override
    public DataTable getDataTable() {
        return table;
    }

    @Override
    public void setDataTable(DataTable table, Map<Variable<?>, DataColumn<?>> variableMap) {
        this.table = table;
        setVariableMap(variableMap);
    }

    @Override
    public String getJSONData() {
        return new JSONArray(getPlotData()).toString();
    }

    @Override
    public String getJSONOptions() {
        return SymbolMap.getJSONString(getOptions());
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("data", getPlotData());
        json.put("options", getOptions());
        return json.toString();
    }

    @Override
    public Object call(PlotFunction function, Object... args) {
        if (! plotType.equals(function.getPlotType())) {
            throw new IllegalArgumentException("Function \"" + function.getName() +
                    "\" is not supported by plot type \"" + plotType.getName() + "\".");
        }

        if (topsoil != null) {
            return topsoil.call(function.getName(), args);
        }
        return null;
    }

    @Override
    public Document toSVGDocument() {
        Document svgDocument = null;

        try {
            // create a new document that will be the SVG
            svgDocument = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
            // ugly but acceptable since we control SVG creation
            svgDocument.setStrictErrorChecking(false);

            // the SVG element in the HTML should have the ID "plot"
            Element svgElement = webView.getEngine()
                    .getDocument().getElementById("plot");

            // additional configuration to make the SVG standalone
            svgElement.setAttribute("xmlns", "http://www.w3.org/2000/svg");
            svgElement.setAttribute("version", "1.1");

            // set the svg element as the document root (must be imported first)
            svgDocument.appendChild(svgDocument.importNode(svgElement, true));
        } catch (ParserConfigurationException ex) {
            LOGGER.error(null, ex);
        }

        return svgDocument;
    }

    //**********************************************//
    //               PROTECTED METHODS              //
    //**********************************************//

    protected void updateDataEntries() {
        List<DataEntry> entries = TableUtils.getPlotData(table, variableMap);
        plotData.set(FXCollections.observableList(entries));
    }

    @Override
    protected double computeMinWidth(double height) {
        return webView.minWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        return webView.minHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height) {
        return computePrefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width) {
        return computePrefHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return webView.prefWidth(height) +
                snappedLeftInset() +
                snappedRightInset();
    }

    @Override
    protected double computePrefHeight(double width) {
        return webView.prefHeight(width) +
                snappedTopInset() +
                snappedBottomInset();
    }

    @Override
    protected void layoutChildren() {
        final double x = snappedLeftInset();
        final double y = snappedTopInset();

        final double width = getWidth() - (snappedLeftInset() + snappedRightInset());
        final double height = getHeight() - (snappedTopInset() + snappedBottomInset());

        webView.resizeRelocate(x, y, width, height);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Initializes this {@link PlotView}'s {@link WebView} and related
     * objects if it has not already been done.
     */
    private void initializeWebEngine() {
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        webView.widthProperty().addListener(c -> resize());
        webView.heightProperty().addListener(c -> resize());

        // useful for debugging
        webEngine.setOnAlert(event -> LOGGER.info(event.getData()));

        webEngine.getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (webEngine.getDocument() != null &&
                            webEngine.getDocument().getDoctype() != null &&
                            newValue == Worker.State.SUCCEEDED) {

                        topsoil = (JSObject) webEngine.executeScript("topsoil");

                        topsoil.setMember("bridge", bridge);
                        topsoil.setMember("axisExtentsBridge", axisExtentsBridge);
                        topsoil.setMember("regression", regression);

                        updateJSData();
                        updateJSOptions();

                        resize();
                    }
                });

        // asynchronous
        webEngine.loadContent(htmlString);
    }

    private void resize() {
        if (topsoil != null) {
            topsoil.call("resize", webView.getWidth(), webView.getHeight());
        }
    }

    private void updateJSData() {
        if (topsoil != null) {
            topsoil.call("setData", getJSONData());
        }
    }

    private void updateJSOptions() {
        if (topsoil != null) {
            topsoil.call("setOptions", getJSONOptions());
        }
    }

}