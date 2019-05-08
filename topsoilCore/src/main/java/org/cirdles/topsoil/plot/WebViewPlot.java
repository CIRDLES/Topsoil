package org.cirdles.topsoil.plot;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.plot.bridges.AxisExtentsBridge;
import org.cirdles.topsoil.plot.bridges.JavaScriptBridge;
import org.cirdles.topsoil.plot.bridges.Regression;
import org.cirdles.topsoil.plot.internal.BoundsToRectangle;
import org.cirdles.topsoil.plot.internal.IsBlankImage;
import org.cirdles.topsoil.plot.internal.SVGSaver;
import org.cirdles.topsoil.plot.PlotProperties.Property;
import org.cirdles.topsoil.variable.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static javafx.application.Platform.isFxApplicationThread;
import static javafx.concurrent.Worker.State.SUCCEEDED;

/**
 * A {@link Plot} that uses JavaScript and HTML to power its visualizations.
 *
 * @author John Zeringue
 */
public abstract class WebViewPlot extends AbstractPlot implements JavaFXDisplayable {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(WebViewPlot.class);

    private String HTML_TEMPLATE;

    public final ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(WebViewPlot.class);

    private final Path sourcePath;
    private final CompletableFuture<Void> loadFuture;

    private WebView webView;
    private JSObject topsoil;
    private final JavaScriptBridge bridge = new JavaScriptBridge();
    private final AxisExtentsBridge axisExtentsBridge = new AxisExtentsBridge();
    private final Regression regression = new Regression();

    /**
     * Creates a new {@link WebViewPlot} using the specified source file. No properties are set by default.
     *
     * @param plotType  PlotType
     */
    public WebViewPlot(PlotType plotType) {
        this(plotType, PlotProperties.defaultProperties());
    }

    /**
     * Creates a new {@link WebViewPlot} using the specified source file and properties.
     *
     * @param plotType  PlotType
     * @param properties a Map containing properties for the plot
     */
    public WebViewPlot(PlotType plotType, PlotProperties properties) {
        super(plotType, properties);
        setLocalURIs();

        this.sourcePath = RESOURCE_EXTRACTOR.extractResourceAsPath(plotType.getPlotFile());
        loadFuture = new CompletableFuture<>();
    }

    private void setLocalURIs() {
        // prepare the local URI for d3.js
        final URI D3_JS_URI = RESOURCE_EXTRACTOR
                .extractResourceAsPath("d3.min.js")
                .toUri();

        // prepare the local URI for numeric.js
        final URI NUMERIC_JS_URI = RESOURCE_EXTRACTOR
                .extractResourceAsPath("numeric.min.js")
                .toUri();

        // prepare the local URI for topsoil.js
        final URI TOPSOIL_JS_URI = RESOURCE_EXTRACTOR
                .extractResourceAsPath("topsoil.js")
                .toUri();

        // build the HTML template (comments show implicit elements/tags)
        HTML_TEMPLATE = (""
                + "<!DOCTYPE html>\n"
                // <html>
                // <head>
                + "<style>\n"
                + "body {\n"
                + "  margin: 0; padding: 0;\n"
                + "}\n"
                + "</style>\n"
                // </head>
                + "<body>"
                + "<script src=\"" + D3_JS_URI + "\"></script>\n"
                + "<script src=\"" + NUMERIC_JS_URI + "\"></script>\n"
                + "<script src=\"" + TOPSOIL_JS_URI + "\"></script>\n"
                + "<script src=\"%s\"></script>\n" // JS file for plot
                + "</body>"
                // </html>
                + "").replaceAll("%20", "%%20");
    }

    private static <T> T supplyOnFxApplicationThread(Supplier<T> supplier) {
        T result;

        if (isFxApplicationThread()) {
            result = supplier.get();
        } else {
            Task<T> supplierTask = new Task<T>() {
                @Override
                protected T call() throws Exception {
                    return supplier.get();
                }
            };

            Platform.runLater(supplierTask);
            result = supplier.get();
        }

        return result;
    }

    private static void runOnFxApplicationThread(Runnable runnable) {
        supplyOnFxApplicationThread(() -> {
            runnable.run();
            return null;
        });
    }

    public CompletableFuture<Void> getLoadFuture() {
        return loadFuture;
    }

    private String buildContent() {

        ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(WebViewPlot.class);

        final URI POINTS_URI = RESOURCE_EXTRACTOR.extractResourceAsPath("impl/data/Points.js").toUri();
        final URI ELLIPSES_URI = RESOURCE_EXTRACTOR.extractResourceAsPath("impl/data/Ellipses.js").toUri();
        final URI CROSSES_URI = RESOURCE_EXTRACTOR.extractResourceAsPath("impl/data/UncertaintyBars.js").toUri();
        final URI CONCORDIA_URI = RESOURCE_EXTRACTOR.extractResourceAsPath("impl/feature/Concordia.js").toUri();
        final URI TWCONCORDIA_URI = RESOURCE_EXTRACTOR.extractResourceAsPath("impl/feature/TWConcordia.js").toUri();
        final URI REGRESSION_URI = RESOURCE_EXTRACTOR.extractResourceAsPath("impl/feature/Regression.js").toUri();
        final URI EVOLUTION_URI = RESOURCE_EXTRACTOR.extractResourceAsPath("impl/feature/Evolution.js").toUri();
        final URI LAMBDA_URI = RESOURCE_EXTRACTOR.extractResourceAsPath("impl/DefaultLambda.js").toUri();
        final URI UTILS_URI = RESOURCE_EXTRACTOR.extractResourceAsPath("impl/Utils.js").toUri();

        return String.format(HTML_TEMPLATE, sourcePath.toUri()).concat(
                "<script src=\"" + POINTS_URI + "\"></script>\n" +
                        "<script src=\"" + ELLIPSES_URI + "\"></script>\n" +
                        "<script src=\"" + CROSSES_URI + "\"></script>\n" +
                        "<script src=\"" + CONCORDIA_URI + "\"></script>\n" +
                        "<script src=\"" + TWCONCORDIA_URI + "\"></script>\n" +
                        "<script src=\"" + REGRESSION_URI + "\"></script>\n" +
                        "<script src=\"" + EVOLUTION_URI + "\"></script>\n" +
                        "<script src=\"" + LAMBDA_URI + "\"></script>\n" +
                        "<script src=\"" + UTILS_URI + "\"></script>\n"
        );
    }

    /**
     * Returns the {@code WebEngine} for this {@code Plot}.
     *
     * @return  WebEngine
     */
    public WebEngine getWebEngine() {
        return webView.getEngine();
    }

    /**
     * Gets a {@code BufferedImage} representing the space inside of the {@link WebView}.
     *
     * @return  contents of WebView as BufferedImage
     */
    private BufferedImage screenCapture() {
        try {
            Bounds bounds = webView.getBoundsInLocal();
            Bounds screenBounds = webView.localToScreen(bounds);

            Rectangle screenRect = new BoundsToRectangle().apply(screenBounds);
            return new Robot().createScreenCapture(screenRect);
        } catch (AWTException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Initializes this {@link WebViewPlot}'s {@link WebView} and related
     * objects if it has not already been done.
     */
    private void initializeWebView() {
        runOnFxApplicationThread(() -> {
            // initialize webView and associated variables
            webView = new WebView();
            webView.setContextMenuEnabled(false);

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
                                newValue == SUCCEEDED) {

                            if (new IsBlankImage().test(screenCapture())) {
                                webEngine.loadContent(buildContent());
                            }
                            topsoil = (JSObject) webEngine.executeScript("topsoil");

                            topsoil.setMember("bridge", bridge);
                            topsoil.setMember("axisExtentsBridge", axisExtentsBridge);
                            topsoil.setMember("regression", regression);

                            if (getData() != null) {
                                topsoil.call("setData", convertData(data));
                            }

                            if (getProperties() != null) {
                                topsoil.call("setProperties", convertProperties(getProperties()));
                            }

                            resize();
                            loadFuture.complete(null);
                        }
                    });

            // asynchronous
            webEngine.loadContent(buildContent());
        });
    }

    /**{@inheritDoc}*/
    @Override
    public Node displayAsNode() {
        if (webView == null) {
            initializeWebView();
        }

        return webView;
    }

    /**
     * Returns the contents of the {@link Node} representation of this
     * {@link Plot} as a SVG document.
     *
     * @return a new {@link Document} with SVG contents if
     * {@link WebViewPlot#displayAsNode()} has been called for this instance
     */
    @Override
    public Document displayAsSVGDocument() {
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

    /**{@inheritDoc}*/
    public void saveAsSVGDocument(File file) {
        new SVGSaver().save(displayAsSVGDocument(), file);
    }

    /**{@inheritDoc}*/
    @Override
    public void recenter() {
        if (topsoil != null) {
            runOnFxApplicationThread(() -> topsoil.call("recenter"));
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void setAxes(Double xMin, Double xMax, Double yMin, Double yMax) {
        if (topsoil != null) {
            runOnFxApplicationThread(() -> topsoil.call("setAxes", xMin, xMax, yMin, yMax));
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void snapToCorners() {
        if (topsoil != null) {
            runOnFxApplicationThread(() -> topsoil.call("snapToCorners"));
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void setData(List<PlotDataEntry> data) {
        super.setData(data);

        if (topsoil != null) {
            runOnFxApplicationThread(() -> topsoil.call("setData", convertData(data)));
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void setProperties(PlotProperties properties) {
        super.setProperties(properties);

        if (topsoil != null) {
            runOnFxApplicationThread(() -> topsoil.call("setProperties", convertProperties(properties)));
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void setProperty(Property<?> key, Object value) {
        super.setProperty(key, value);

        Map<String, Object> stringKeyProperties = convertProperties(this.getProperties());

        if (topsoil != null) {
            runOnFxApplicationThread(() -> topsoil.call("setProperties", stringKeyProperties));
        }
    }

    /**{@inheritDoc}*/
    @Override
    public boolean getIfUpdated() {
        return axisExtentsBridge.getIfUpdated();
    }

    /**{@inheritDoc}*/
    @Override
    public void setIfUpdated(boolean update) {
        axisExtentsBridge.setIfUpdated(update);
    }

    /**{@inheritDoc}*/
    @Override
    public void updateProperties() {

        Map<Property<?>, Object> properties = axisExtentsBridge.getProperties();

        for (Map.Entry<Property<?>, Object> entry : properties.entrySet()) {
            super.setProperty(entry.getKey(), entry.getValue());
        }
    }

    private void resize() {
        if (topsoil != null) {
            topsoil.call("resize", webView.getWidth(), webView.getHeight());
        }
    }

    private List<Map<String, Object>> convertData(List<PlotDataEntry> entries) {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map;
        for (PlotDataEntry dataEntry : entries) {
            map = new HashMap<>();
            for (Map.Entry<Variable<?>, Object> valueEntry : dataEntry.getMap().entrySet()) {
                map.put(valueEntry.getKey().getKeyString(), valueEntry.getValue());
            }
            data.add(map);
        }
        return data;
    }

    private Map<String, Object> convertProperties(PlotProperties properties) {
        Map<String, Object> newProperties = new HashMap<>();
        Property<?> property;
        for (Map.Entry<Property<?>, Object> entry : properties.getProperties().entrySet()) {
            property = entry.getKey();
            newProperties.put(entry.getKey().getKeyString(), property.toJSCompatibleValue(entry.getValue()));
        }
        return newProperties;
    }
}