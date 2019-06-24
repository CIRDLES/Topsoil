package org.cirdles.topsoil.javafx;

import javafx.application.Platform;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link Plot} that uses JavaScript and HTML to power its visualizations.
 *
 * @author John Zeringue
 */
public class PlotView extends SingleChildRegion<WebView> implements Plot {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(PlotView.class);

    private WebView webView;
    private WebEngine webEngine;
    private CompletableFuture<Void> loadFuture;

    private PlotType plotType;
    private String htmlString;
    private DataTable table;
    private final Map<String, Thread> updateThreads = new HashMap<>();

    private JSObject topsoil;
    private final JavaScriptBridge bridge = new JavaScriptBridge();
    private final AxisExtentsBridge axisExtentsBridge;
    private final Regression regression = new Regression();

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    private ReadOnlyListWrapper<DataEntry> plotData = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    public final ReadOnlyListProperty<DataEntry> plotDataProperty() {
        return plotData.getReadOnlyProperty();
    }
    @Override
    public final ObservableList<DataEntry> getData() {
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

    public PlotView(PlotType type) {
        super(new WebView());

        Validate.notNull(type, "Plot type cannot be null.");

        this.axisExtentsBridge = new AxisExtentsBridge(this);

        this.plotType = type;
        this.htmlString = HTMLTemplate.forPlotType(plotType);

        plotData.addListener((ListChangeListener<DataEntry>) c -> updateJSDelayed("setData", this::updateJSData));
        plotOptions.addListener((MapChangeListener<PlotOption<?>, Object>) c -> updateJSDelayed("setOptions", this::updateJSOptions));

        loadFuture = new CompletableFuture<>();

        this.webView = getChild();
        webView.setContextMenuEnabled(false);
        webView.widthProperty().addListener(c -> update());
        webView.heightProperty().addListener(c -> update());

        this.webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        // useful for debugging
        webEngine.setOnAlert(event -> LOGGER.info(event.getData()));
        webEngine.getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (webEngine.getDocument() != null &&
                            webEngine.getDocument().getDoctype() != null &&
                            newValue == Worker.State.SUCCEEDED) {

                        Number initXMin = (Number) plotOptions.get(PlotOption.X_MIN),
                                initXMax = (Number) plotOptions.get(PlotOption.X_MAX),
                                initYMin = (Number) plotOptions.get(PlotOption.Y_MIN),
                                initYMax = (Number) plotOptions.get(PlotOption.Y_MAX);
                        boolean isCustomViewport = ! (
                                PlotOption.X_MIN.getDefaultValue().equals(initXMin) &&
                                        PlotOption.X_MAX.getDefaultValue().equals(initXMax) &&
                                        PlotOption.Y_MIN.getDefaultValue().equals(initYMin) &&
                                        PlotOption.Y_MAX.getDefaultValue().equals(initYMax)
                        );

                        topsoil = (JSObject) webEngine.executeScript("topsoil");

                        topsoil.setMember("bridge", bridge);
                        topsoil.setMember("axisExtentsBridge", axisExtentsBridge);
                        topsoil.setMember("regression", regression);

                        topsoil.call("init", getJSONData(), getJSONOptions());

                        if (isCustomViewport) {
                            call(PlotFunction.Scatter.SET_AXIS_EXTENTS,
                                    initXMin,
                                    initXMax,
                                    initYMin,
                                    initYMax,
                                    false
                            );
                        }
                        loadFuture.complete(null);
                    }
                });
        reloadEngine();
    }

    public PlotView(PlotType type, PlotOptions options) {
        this(type);
        setOptions((options != null) ? options : PlotOptions.defaultOptions());
    }

    public PlotView(PlotType type, PlotOptions options, List<DataEntry> data) {
        this(type, options);
        setData(data);
    }

    public PlotView(PlotType type, PlotOptions options, DataTable table, Map<Variable<?>, DataColumn<?>> variableMap) {
        this(type, options);
        setData(table, variableMap);
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
    public void setData(DataTable table, Map<Variable<?>, DataColumn<?>> variableMap) {
        Validate.notNull(table, "Table cannot be null.");
        Validate.notNull(variableMap, "Variable map cannot be null.");

        this.table = table;
        setVariableMap(variableMap);
    }

    @Override
    public void setData(List<DataEntry> data) {
        if (data != null) {
            plotData.setAll(data);
            this.table = null;
            variableMap.clear();
        }
    }

    @Override
    public String getJSONData() {
        return new JSONArray(getData()).toString();
    }

    @Override
    public String getJSONOptions() {
        return SymbolMap.getJSONString(getOptions());
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("data", getData());
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

    public void update() {
        if (topsoil != null) {
            topsoil.call("update");
        }
    }

    /**
     * Loads a webpage with the Topsoil JS plot files.
     */
    public CompletableFuture<Void> reloadEngine() {
        loadFuture = new CompletableFuture<>();
        // asynchronous
        webEngine.loadContent(htmlString);
        return loadFuture;
    }

    public CompletableFuture<Void> getLoadFuture() {
        return loadFuture;
    }

    //**********************************************//
    //               PROTECTED METHODS              //
    //**********************************************//

    protected void updateDataEntries() {
        List<DataEntry> entries = TableUtils.getPlotData(table, variableMap);
        plotData.setAll(entries);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    private void updateJSData() {
        if (topsoil != null) {
            Platform.runLater(() -> topsoil.call("setData", getJSONData()));
        }
    }

    private void updateJSOptions() {
        if (topsoil != null) {
            Platform.runLater(() -> topsoil.call("setOptions", getJSONOptions()));
        }
    }

    private void updateJSDelayed(String name, Runnable runnable) {
        if (updateThreads.containsKey(name)) {
            return; // an update has already been called
        }
        Thread updateThread = new Thread(() -> {
            try {
                Thread.sleep(10);
                runnable.run();
            } catch (InterruptedException e) {
                // Do nothing
            } finally {
                updateThreads.remove(name);
            }
        });
        updateThreads.put(name, updateThread);
        updateThread.start();
    }

}