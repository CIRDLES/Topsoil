package org.cirdles.topsoil.plot;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.JSArray;
import com.teamdev.jxbrowser.chromium.JSFunction;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMDocument;
import com.teamdev.jxbrowser.chromium.dom.DOMElement;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.scene.Node;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.plot.PlotProperties.Property;
import org.cirdles.topsoil.plot.bridges.AxisExtentsBridge;
import org.cirdles.topsoil.plot.bridges.Regression;
import org.cirdles.topsoil.plot.internal.SVGSaver;
import org.cirdles.topsoil.variable.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Base implementation for {@link Plot} subclasses. Configures the JxBrowser {@code BrowserView} used to display JS files.
 */
public abstract class JxBrowserPlot extends AbstractPlot implements JavaFXDisplayable {

    private static final List<String> PLOT_RESOURCE_FILES = new ArrayList<>();
    static {
        Collections.addAll(PLOT_RESOURCE_FILES,
                "impl/data/Points.js",
                "impl/data/Ellipses.js",
                "impl/data/UncertaintyBars.js",
                "impl/feature/Concordia.js",
                "impl/feature/TWConcordia.js",
                "impl/feature/Regression.js",
                "impl/feature/Evolution.js",
                "impl/DefaultLambda.js",
                "impl/Utils.js"
        );
    }

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private static final Logger LOGGER = LoggerFactory.getLogger(JxBrowserPlot.class);

    private final ResourceExtractor resourceExtractor = new ResourceExtractor(JxBrowserPlot.class);
    private final AxisExtentsBridge axisExtentsBridge = new AxisExtentsBridge();
    private final Regression regression = new Regression();
    private Browser browser;
    private BrowserView browserView;
    private JSObject topsoil;

    private Map<String, Thread> updateThreads = new HashMap<>();

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /**
     * Constructs a new instance of {@code SimplePlot} of the specified {@code PlotType}.
     *
     * @param plotType      plot type
     */
    public JxBrowserPlot(PlotType plotType) {
        super(plotType, PlotProperties.defaultProperties());
    }

    //**********************************************//
    //                PUBLIC METHODS                //
    //**********************************************//

    /**{@inheritDoc}*/
    @Override
    public final void recenter() {
        JSFunction recenter = getTopsoilFunction("recenter");
        if (recenter != null) {
            recenter.invoke(topsoil);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public final void setAxes(Double xMin, Double xMax, Double yMin, Double yMax) {
        JSFunction setAxes = getTopsoilFunction("setAxes");
        if (setAxes != null) {
            setAxes.invoke(topsoil, xMin, xMax, yMin, yMax);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public final void snapToCorners() {
        JSFunction snapToCorners = getTopsoilFunction("snapToCorners");
        if (snapToCorners != null) {
            snapToCorners.invoke(topsoil);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public final Node displayAsNode() {
        if (browserView == null) {
            initializeBrowserView();
        }
        return browserView;
    }

    /**{@inheritDoc}*/
    @Override
    public final void setData(List<PlotDataEntry> data) {
        super.setData(data);

        updateJSOnDelayedThread("setData", () -> updateJSData());
    }

    /**{@inheritDoc}*/
    @Override
    public final void setProperties(PlotProperties properties) {
        super.setProperties(properties);

        updateJSOnDelayedThread("setProperties", () -> updateJSProperties());
    }

    /**{@inheritDoc}*/
    @Override
    public final void setProperty(Property<?> key, Object value) {
        super.setProperty(key, value);
        if (topsoil != null) {
            JSValue setProperties = topsoil.getProperty("setProperty");
            if (setProperties != null && setProperties.isFunction()) {
                setProperties.asFunction().invoke(topsoil, convertProperties(this.properties));
            }
        }
    }

    /**{@inheritDoc}*/
    @Override
    public final boolean getIfUpdated() {
        return axisExtentsBridge.getIfUpdated();
    }

    /**{@inheritDoc}*/
    @Override
    public final void setIfUpdated(boolean update) {
        axisExtentsBridge.setIfUpdated(update);
    }

    /**{@inheritDoc}*/
    @Override
    public final void updateProperties() {
        properties.setAll(axisExtentsBridge.getProperties());
        setProperties(properties);
    }

    /**
     * Returns the contents of the {@link Node} representation of this
     * {@link Plot} as a SVG document.
     *
     * @return a new {@link Document} with SVG contents
     */
    @Override
    public final Document displayAsSVGDocument() {
        DOMDocument document = browser.getDocument();
        Document svg = null;
        if (document != null) {
            try {
                DOMElement domElement = document.getDocumentElement().findElement(By.id("svgContainer"));

                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                svg = builder.parse(new InputSource(new StringReader(domElement.getInnerHTML())));

                Element svgElement = svg.getDocumentElement();
                svgElement.setAttribute("xmlns", "http://www.w3.org/2000/svg");
                svgElement.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
                svgElement.setAttribute("version", "1.1");
            } catch (ParserConfigurationException | IOException | SAXException ex) {
                LOGGER.error(null, ex);
            }
        }
        return svg;
    }

    /**{@inheritDoc}*/
    public final void saveAsSVGDocument(File file) {
        new SVGSaver().save(displayAsSVGDocument(), file);
    }

    //**********************************************//
    //                PRIVATE METHODS               //
    //**********************************************//

    /**
     * Constructs and configures the {@code BrowserView} in this plot.
     */
    private void initializeBrowserView() {
        this.browser = new Browser(BrowserType.LIGHTWEIGHT);
        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    Browser browser = event.getBrowser();

                    // Get JSObject for Java-to-JS calls
                    topsoil = browser.executeJavaScriptAndReturnValue("topsoil").asObject();

                    // Add bridges for JS-to-Java calls
                    topsoil.setProperty("axisExtentsBridge", axisExtentsBridge);
                    topsoil.setProperty("regression", regression);

                    if (data != null) {
                        updateJSData();
                    }
                    if (properties != null) {
                        setProperties(properties);
                    }
                    resize();
                    recenter();
                }
            }
        });
        // Listen for logs from JS
        browser.addConsoleListener(event -> {
            if (Objects.equals(event.getLevel().toString(), "ERROR")) {
                LOGGER.error(event.getMessage());
            } else {
                LOGGER.info(event.getMessage());
            }
        });

        browser.loadHTML(buildHTML());

        browserView = new BrowserView(browser);
        // Listen for size changes to Node
        browserView.widthProperty().addListener(c -> resize());
        browserView.heightProperty().addListener(c -> resize());
    }

    /**
     * Returns a {@code String} containing the HTML document to be displayed in the {@link Browser}.
     *
     * @return  String HTML content
     */
    private String buildHTML() {
        final URI D3_JS_URI = resourceExtractor
                .extractResourceAsPath("d3.min.js")
                .toUri();

        // prepare the local URI for numeric.js
        final URI NUMERIC_JS_URI = resourceExtractor
                .extractResourceAsPath("numeric.min.js")
                .toUri();

        // prepare the local URI for topsoil.js
        final URI TOPSOIL_JS_URI = resourceExtractor
                .extractResourceAsPath("topsoil.js")
                .toUri();

       String htmlTemplate = (""
               + "<!DOCTYPE html>\n"
               // <html>
               // <head>
               + "<style>\n"
               + "body {\n"
               + "  margin: 0; padding: 0;\n"
               + "  overflow: hidden;\n"
               + "}\n"
               + "</style>\n"
               // </head>
               + "<body>\n"
               + "<script src=\"" + D3_JS_URI + "\"></script>\n"
               + "<script src=\"" + NUMERIC_JS_URI + "\"></script>\n"
               + "<script src=\"" + TOPSOIL_JS_URI + "\"></script>\n"
               + "<script src=\"%s\"></script>\n" // add plot file here
               + "</body>\n"
               // </html>
               + "").replaceAll("%20", "%%20");

        String plotFile = resourceExtractor.extractResourceAsPath(plotType.getPlotFile()).toUri().toString();

        // Append plotType-specific scripts to HTML
        StringBuilder script = new StringBuilder();
        Path path;
        for (String resource : PLOT_RESOURCE_FILES) {
            path = resourceExtractor.extractResourceAsPath(resource);
            if (path == null) {
                throw new RuntimeException(plotType.getName() + " resource not found: " + resource);
            }
            script.append("<script src=\"");
            script.append(resourceExtractor.extractResourceAsPath(resource).toUri().toString());
            script.append("\"></script>\n");
        }
        return String.format(htmlTemplate, plotFile).concat(script.toString());
    }

    /**
     * Converts plot properties into a {@link JSObject} that can be passed into the {@link Browser}.
     *
     * @param properties    properties as Java Map
     *
     * @return              JSObject
     */
    private JSObject convertProperties(PlotProperties properties) {
        JSObject jsProperties = browser.getJSContext().createObject();
        Property<?> property;
        for (Map.Entry<Property<?>, Object> entry : properties.getProperties().entrySet()) {
            property = entry.getKey();
            jsProperties.setProperty(property.getKeyString(), property.toJSCompatibleValue(entry.getValue()));
        }
        return jsProperties;
    }

    /**
     * Puts plot data into a {@link JSArray} that can be passed into the {@link Browser}.
     *
     * @param javaData      data as Java List
     * @param jsData        empty JSArray to write data to
     */
    private void putJavaDataInJSArray(List<PlotDataEntry> javaData, JSArray jsData) {
        JSObject row;
        for (int i = 0; i < Math.max(javaData.size(), jsData.length()); i++) {
            if (i >= javaData.size()) {
                jsData.set(i, null);
            } else {
                row = browser.getJSContext().createObject();
                for (Map.Entry<Variable<?>, Object> entry : javaData.get(i).getMap().entrySet()) {
                    row.setProperty(entry.getKey().getName(), entry.getValue());
                }
                jsData.set(i, row);
            }
        }
    }

    private void updateJSData() {
        JSFunction setData = getTopsoilFunction("setData");
        if (setData != null) {
            JSArray jsData = emptyJSArray();
            putJavaDataInJSArray(this.data, jsData);
            setData.invoke(topsoil, jsData);
        }
    }

    private void updateJSProperties() {
        JSFunction setProperties = getTopsoilFunction("setProperties");
        if (setProperties != null) {
            setProperties.invoke(topsoil, convertProperties(this.properties));
        }
    }

    /**
     * Forces a short delay before the update is applied. Meant to avoid freezing in situations like the (de)selection
     * of an entire data segment/aliquot, where many small data events are fired and "setData" may be called many times.
     *
     * @param jsFunctionName    String name of JS function to be invoked
     * @param runnable          Runnable code to execute the update
     */
    private void updateJSOnDelayedThread(String jsFunctionName, Runnable runnable) {
        if (topsoil != null) {
            if (updateThreads.get(jsFunctionName) == null) {
                Thread updateThread = new Thread(() -> {
                    try {
                        Thread.sleep(10);   // Forces a wait in case of multiple data changes at the same time
                        runnable.run();           // Runs the update action
                        updateThreads.remove(jsFunctionName);   // Removes the current update thread from
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
                });
                updateThreads.put(jsFunctionName, updateThread);    // Put the update thread in the map so we know that
                                                                    // there is already an update being performed.
                updateThread.start();
            }
        }
    }

    /**
     * Obtains a {@link JSFunction} object for the specified function of "topsoil".
     *
     * @param functionName      String function name
     * @return                  JSFunction
     */
    private JSFunction getTopsoilFunction(String functionName) {
        if (topsoil != null) {
            JSValue function = topsoil.getProperty(functionName);
            if (function != null && function.isFunction()) {
                return function.asFunction();
            }
        }
        return null;
    }

    /**
     * Obtains an empty array from the {@link Browser}'s JS context.
     *
     * @return  empty JSArray
     */
    private JSArray emptyJSArray() {
        return browser.executeJavaScriptAndReturnValue("topsoil.emptyArray()").asArray();
    }

    /**
     * Manually adjusts the size of the {@link Browser} and the size of the JS execution of topsoil to the size of the
     * containing {@link BrowserView}.
     */
    private void resize() {
        JSFunction resize = getTopsoilFunction("resize");
        double width = browserView.getWidth();
        double height = browserView.getHeight();
        if (resize != null) {
            browser.setSize((int) width, (int) height);
            resize.asFunction().invoke(topsoil, width, height);
        }
    }

}
