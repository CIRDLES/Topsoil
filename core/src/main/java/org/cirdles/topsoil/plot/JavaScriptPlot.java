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
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.entry.EntryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.concurrent.Worker.State.SUCCEEDED;
import static org.cirdles.topsoil.plot.Variables.X;
import static org.cirdles.topsoil.plot.Variables.SIGMA_X;
import static org.cirdles.topsoil.plot.Variables.Y;
import static org.cirdles.topsoil.plot.Variables.SIGMA_Y;
import static org.cirdles.topsoil.plot.Variables.RHO;
import static org.cirdles.topsoil.dataset.field.Fields.SELECTED;

/**
 * A {@link Plot} that uses JavaScript and HTML to power its visualizations.
 *
 * @author John Zeringue
 */
public abstract class JavaScriptPlot extends BasePlot implements JavaFXDisplayable {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(JavaScriptPlot.class);

    private static final List<Variable> VARIABLES = Arrays.asList(
            X, SIGMA_X,
            Y, SIGMA_Y,
            RHO
    );

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

    private final CompletableFuture<Void> loadFuture;
    protected final CompletableFuture<Void> initializeFuture;

    private final Path sourcePath;

    private WebView webView;

    /**
     * Creates a new {@link JavaScriptPlot} using the specified source file.
     *
     * @param sourcePath the path to a valid JavaScript file
     */
    public JavaScriptPlot(Path sourcePath) {
        if (Files.isDirectory(sourcePath)) {
            throw new IllegalArgumentException("sourcePath must be a file");
        }

        this.sourcePath = sourcePath;
        loadFuture = new CompletableFuture<>();

        initializeFuture = loadFuture.thenRunAsync(() -> {
            getTopsoil().get().call("showData");
        }, Platform::runLater);
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    Optional<WebView> getWebView() {
        return Optional.ofNullable(webView);
    }

    Optional<WebEngine> getWebEngine() {
        return getWebView().map(WebView::getEngine);
    }

    Optional<JSObject> getTopsoil() {
        return getWebEngine().map(webEngine -> {
            return (JSObject) webEngine.executeScript("topsoil");
        });
    }

    public void fitData() {
        getTopsoil().get().call("showData");
    }

    @Override
    public List<Variable> getVariables() {
        return VARIABLES;
    }

    String buildContent() {
        return String.format(HTML_TEMPLATE, getSourcePath().toUri());
    }

    /**
     * Initializes this {@link JavaScriptPlot}'s {@link WebView} and related
     * objects if it has not already been done.
     */
    private WebView initializeWebView() {
        // initialize webView and associated variables
        webView = new WebView();
        webView.setContextMenuEnabled(false);

        // useful for debugging
        getWebEngine().get().setOnAlert(event -> {
            LOGGER.info(event.getData());
        });

        getWebEngine().get().getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == SUCCEEDED) {
                        loadFuture.complete(null);
                    }
                });

        // asynchronous
        getWebEngine().get().loadContent(buildContent());

        return webView;
    }

    /**
     * Sets this {@link Plot}'s data by passing rows of length 5 with variables
     * in the following order: <code>x</code>, <code>σx</code>, <code>y</code>,
     * <code>σy</code>, <code>ρ</code>.
     *
     * @param plotContext
     */
    @Override
    public void setData(PlotContext plotContext) {
        super.setData(plotContext);

        EntryListener listener = (entry, field) -> {
            drawPlot(plotContext);
        };

        List<Entry> entries = plotContext.getDataset().getEntries();
        //Listen to the entries (= value changes)
        entries.forEach(entry -> entry.addListener(listener));

        drawPlot(plotContext);
    }

    public void drawPlot(PlotContext plotContext) {
        // pass the data to JavaScript
        // this seems excessive but passing a double[][] creates a single array
        // of undefined objects on the other side of things
        initializeFuture.thenRunAsync(() -> {
            getTopsoil().get().call("clearData"); // old data must be cleared

            plotContext.getDataset().getEntries()
                    .stream()
                    .filter(entry -> entry.get(SELECTED).orElse(true))
                    .forEach(entry -> {
                        JSObject row = (JSObject) getWebEngine().get()
                                .executeScript("new Object()");

                        plotContext.getBindings().forEach(variableBinding -> {
                            row.setMember(
                                    variableBinding.getVariable().getName(),
                                    variableBinding.getValue(entry));
                        });

                        getTopsoil().get().call("addData", row);
                    });

            getTopsoil().get().call("showData");
        }, Platform::runLater);
    }

    @Override
    public Node displayAsNode() {
        return getWebView().orElseGet(this::initializeWebView);
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
            Element svgElement = getWebEngine().get()
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

    @Override
    public Object getProperty(String key) {
        try {
            return initializeFuture.thenApplyAsync(aVoid -> getTopsoil()
                    .orElseThrow(IllegalStateException::new)
                    .call("getProperty", key), Platform::runLater).get();
        } catch (ExecutionException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setProperty(String key, Object value) {
        try {
            initializeFuture.thenApplyAsync(aVoid -> getTopsoil()
                    .orElseThrow(IllegalStateException::new)
                    .call("setProperty", key, value), Platform::runLater).get();
        } catch (ExecutionException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
