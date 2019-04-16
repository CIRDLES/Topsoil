package org.cirdles.topsoil.plot;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
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
import org.cirdles.topsoil.plot.bridges.PropertiesBridge;
import org.cirdles.topsoil.plot.bridges.Regression;
import org.cirdles.topsoil.plot.internal.SVGSaver;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Base implementation for {@link Plot} subclasses. Controls the JxBrowser {@code BrowserView} used to display JS files.
 */
public abstract class SimplePlot extends AbstractPlot implements JavaFXDisplayable {

    //**********************************************//
    //                  ATTRIBUTES                  //
    //**********************************************//

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePlot.class);

    private final ResourceExtractor resourceExtractor = new ResourceExtractor(SimplePlot.class);
    private final PropertiesBridge propertiesBridge = new PropertiesBridge();
    private final Regression regression = new Regression();
    private Browser browser;
    private BrowserView browserView;
    private JSObject topsoil;

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    public SimplePlot(PlotType plotType, Map<PlotProperty, Object> plotProperties) {
        super(plotType, plotProperties);

        this.browser = new Browser(BrowserType.LIGHTWEIGHT);
        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    Browser browser = event.getBrowser();

                    // Get JSObject for Java-to-JS calls
                    topsoil = browser.executeJavaScriptAndReturnValue("topsoil").asObject();

                    // Add bridges for JS-to-Java calls
                    topsoil.setProperty("propertiesBridge", propertiesBridge);
                    topsoil.setProperty("regression", regression);

                    if (data != null) {
                        setData(data);
                    }

                    if (properties != null) {
                        setProperties(properties);
                    }

                    resize();
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
    public final void setAxes(String xMin, String xMax, String yMin, String yMax) {
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
        return browserView;
    }

    /**{@inheritDoc}*/
    @Override
    public final void setData(List<Map<String, Object>> data) {
        super.setData(data);
        JSFunction setData = getTopsoilFunction("setData");
        if (setData != null) {
            JSArray jsData = emptyJSArray();    // Browser requires a JSArray instead of a Java array, so
                                                // we get one here, and write to it in convertData()
            setData.invoke(topsoil, convertData(data, jsData));
        }
    }

    /**{@inheritDoc}*/
    @Override
    public final void setProperties(Map<PlotProperty, Object> properties) {
        super.setProperties(properties);
        JSFunction setProperties = getTopsoilFunction("setProperties");
        if (setProperties != null) {
            setProperties.invoke(topsoil, convertProperties(this.properties));
        }
    }

    /**{@inheritDoc}*/
    @Override
    public final void setProperty(PlotProperty key, Object value) {
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
        return propertiesBridge.getIfUpdated();
    }

    /**{@inheritDoc}*/
    @Override
    public final void setIfUpdated(boolean update) {
        propertiesBridge.setIfUpdated(update);
    }

    /**{@inheritDoc}*/
    @Override
    public final void updateProperties() {
        Map<PlotProperty, Object> properties = propertiesBridge.getProperties();
        for (Map.Entry<PlotProperty, Object> entry : properties.entrySet()) {
            super.setProperty(entry.getKey(), entry.getValue());
        }
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
        for (String resource : plotType.getResources()) {
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
     * Converts plot properties into a JSObject that can be passed into the Browser.
     *
     * @param properties    properties as Java Map
     *
     * @return              JSObject
     */
    private JSObject convertProperties(Map<PlotProperty, Object> properties) {
        JSObject jsProperties = browser.getJSContext().createObject();
        for (Map.Entry<PlotProperty, Object> entry : properties.entrySet()) {
            jsProperties.setProperty(entry.getKey().toString(), entry.getValue());
        }
        return jsProperties;
    }

    /**
     * Converts plot data into a JSArray that can be passed into the Browser.
     *
     * @param javaData      data as Java List
     * @param jsData        empty JSArray to write data to
     *
     * @return              JSArray
     */
    private JSArray convertData(List<Map<String, Object>> javaData, JSArray jsData) {
        JSObject row;
        for (int i = 0; i < Math.max(javaData.size(), jsData.length()); i++) {
            if (i >= javaData.size()) {
                jsData.set(i, null);
            } else {
                row = browser.getJSContext().createObject();
                for (Map.Entry<String, Object> entry : javaData.get(i).entrySet()) {
                    row.setProperty(entry.getKey(), entry.getValue());
                }
                jsData.set(i, row);
            }
        }
        return jsData;
    }

    /**
     * Obtains a JSFunction object for the specified function of "topsoil".
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

    private JSArray emptyJSArray() {
        return browser.executeJavaScriptAndReturnValue("topsoil.emptyArray()").asArray();
    }

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
