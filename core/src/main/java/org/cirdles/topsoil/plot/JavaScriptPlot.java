/*
 * Copyright 2014 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.topsoil.plot;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.plot.internal.BoundsToRectangle;
import org.cirdles.topsoil.plot.internal.IsBlankImage;
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
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
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
public abstract class JavaScriptPlot extends BasePlot implements JavaFXDisplayable {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(JavaScriptPlot.class);

    private static final String HTML_TEMPLATE;

    static {
        final ResourceExtractor RESOURCE_EXTRACTOR
                = new ResourceExtractor(JavaScriptPlot.class);

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
                // </body>
                // </html>
                + "").replaceAll("%20", "%%20");
    }

    private final Path sourcePath;
    private final CompletableFuture<Void> loadFuture;

    private WebView webView;
    private JSObject topsoil;
    private final Bridge bridge = new Bridge();

    /**
     * Creates a new {@link JavaScriptPlot} using the specified source file.
     *
     * @param sourcePath the path to a valid JavaScript file
     */
    public JavaScriptPlot(Path sourcePath) {
        this(sourcePath, new HashMap<>());
    }

    public JavaScriptPlot(Path sourcePath, Map<String, Object> defaultProperties) {
        super(defaultProperties);

        if (Files.isDirectory(sourcePath)) {
            throw new IllegalArgumentException("sourcePath must be a file");
        }

        this.sourcePath = sourcePath;
        loadFuture = new CompletableFuture<>();
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

    String buildContent() {

        ResourceExtractor RESOURCE_EXTRACTOR = new ResourceExtractor(JavaScriptPlot.class);

        final URI CONCORDIA_URI = RESOURCE_EXTRACTOR.extractResourceAsPath("base/Concordia.js").toUri();

        return String.format(HTML_TEMPLATE, sourcePath.toUri()).concat(
                "<script src=\"" + CONCORDIA_URI.toString() + "\"></script>\n"
        );
    }

    public WebEngine getWebEngine() {
        return webView.getEngine();
    }

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
     * Initializes this {@link JavaScriptPlot}'s {@link WebView} and related
     * objects if it has not already been done.
     */
    private void initializeWebView() {
        runOnFxApplicationThread(() -> {
            // initialize webView and associated variables
            webView = new WebView();
            webView.setContextMenuEnabled(false);

            WebEngine webEngine = webView.getEngine();
            webEngine.setJavaScriptEnabled(true);

            // useful for debugging
            webEngine.setOnAlert(event -> {
                LOGGER.info(event.getData());
            });

            webEngine.getLoadWorker().stateProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue == SUCCEEDED) {
                            if (new IsBlankImage().test(screenCapture())) {
                                webEngine.loadContent(buildContent());
                            }

                            topsoil = (JSObject) webEngine.executeScript("topsoil");

                            topsoil.setMember("bridge", bridge);

                            if (getProperties() != null) {
                                topsoil.call("setProperties", getProperties());
                            }

                            if (getData() != null) {
                                topsoil.call("setData", getData());
                            }

                            loadFuture.complete(null);
                        }
                    });

            // asynchronous
            webEngine.loadContent(buildContent());
        });
    }

    @Override
    public Node displayAsNode() {
        if (webView == null) {
            initializeWebView();
        }

        return webView;
    }

    //called when BasePlot changes isotope type
    public class Bridge {
        public void println(String s) {
            System.out.println(s);
        }
    }

    /**
     * Returns the contents of the {@link Node} representation of this
     * {@link Plot} as a SVG document.
     *
     * @return a new {@link Document} with SVG contents if
     * {@link JavaScriptPlot#displayAsNode()} has been called for this instance
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

    public void reset() {
        if (topsoil != null) {
            runOnFxApplicationThread(() -> topsoil.call("reset"));
        }
    }

    @Override
    public void setData(List<Map<String, Object>> data) {
        super.setData(data);

        if (topsoil != null) {
            runOnFxApplicationThread(() -> topsoil.call("setData", data));
        }
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        super.setProperties(properties);

        if (topsoil != null) {
            runOnFxApplicationThread(() -> {
                topsoil.call("setProperties", properties);
            });
        }
    }
}