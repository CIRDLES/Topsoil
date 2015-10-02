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
package org.cirdles.topsoil.chart;

import javafx.concurrent.Worker;
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
import java.util.*;

import static javafx.concurrent.Worker.State.SUCCEEDED;
import static org.cirdles.topsoil.chart.Variables.*;
import static org.cirdles.topsoil.dataset.field.Fields.SELECTED;

/**
 * A {@link Chart} that uses JavaScript and HTML to power its visualizations.
 *
 * @author John Zeringue
 */
public class JavaScriptChart extends BaseChart implements JavaFXDisplayable {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(JavaScriptChart.class);

    private static final List<Variable> VARIABLES = Arrays.asList(
            X, SIGMA_X,
            Y, SIGMA_Y,
            RHO
    );

    private static final String HTML_TEMPLATE;

    static {
        final ResourceExtractor RESOURCE_EXTRACTOR
                = new ResourceExtractor(JavaScriptChart.class);

        // prepare the local URI for Firebug Lite
        final URI FIREBUG_LITE_URI = RESOURCE_EXTRACTOR
                .extractResourceAsPath("firebug-lite.js")
                .toUri();

        // prepare the local URI for d3.js
        final URI D3_JS_URI = RESOURCE_EXTRACTOR
                .extractResourceAsPath("d3.js")
                .toUri();

        // prepare the local URI for numeric.js
        final URI NUMERIC_JS_URI = RESOURCE_EXTRACTOR
                .extractResourceAsPath("numeric.js")
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
                + "<script src=\"" + FIREBUG_LITE_URI + "\"></script>\n"
                + "<script src=\"" + D3_JS_URI + "\"></script>\n"
                + "<script src=\"" + NUMERIC_JS_URI + "\"></script>\n"
                + "<script src=\"" + TOPSOIL_JS_URI + "\"></script>\n"
                + "<script src=\"%s\"></script>\n" // JS file for chart
                // </body>
                // </html>
                + "").replaceAll("%20", "%%20");
    }

    private final Collection<Runnable> afterLoadCallbacks = new ArrayList<>();

    private final Collection<Runnable> initializationCallbacks
            = new ArrayList<>();

    private final Path sourcePath;

    private WebView webView;
    private boolean initialized = false;
    /**
     * Creates a new {@link JavaScriptChart} using the specified source file.
     *
     * @param sourcePath the path to a valid JavaScript file
     */
    public JavaScriptChart(Path sourcePath) {
        if (Files.isDirectory(sourcePath)) {
            throw new IllegalArgumentException("sourcePath must be a file");
        }

        this.sourcePath = sourcePath;
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

    /**
     * A utility method for running code after this {@link JavaScriptChart} has
     * loaded it's HTML. This is necessary because
     * {@link WebEngine#load(java.lang.String)} and
     * {@link WebEngine#loadContent(java.lang.String)} are both asynchronous yet
     * provide no easy way to attach callback functions. If the generated HTML
     * for this chart has already been loaded, the {@link Runnable} is run
     * immediately.
     *
     * @param callback the runnable to be run after this
     * {@link JavaScriptChart}'s HTML has been loaded
     */
    private void afterLoad(Runnable callback) {
        boolean loadSucceeded = getWebEngine()
                .map(WebEngine::getLoadWorker)
                .map(Worker::getState)
                .map(state -> state == SUCCEEDED)
                .orElse(false);

        if (loadSucceeded) {
            callback.run();
        } else {
            afterLoadCallbacks.add(callback);
        }
    }

    /**
     * Motivated similarly to {@link #afterLoad(java.lang.Runnable)} but needed
     * to prevent data from being passed into the JavaScript environment before
     * everything is ready.
     *
     * @param callback the runnable to be run after this
     * {@link JavaScriptChart}'s {@link WebView} has been initialized
     */
    private void afterInitialization(Runnable callback) {
        if (initialized) {
            callback.run();
        } else {
            initializationCallbacks.add(callback);
        }
    }

    String buildContent() {
        return String.format(HTML_TEMPLATE, getSourcePath().toUri());
    }

    /**
     * Initializes this {@link JavaScriptChart}'s {@link WebView} and related
     * objects if it has not already been done.
     */
    private WebView initializeWebView() {
        // initialize webView and associated variables
        webView = new WebView();
        webView.setContextMenuEnabled(false);

        getWebEngine().get().getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == SUCCEEDED) {
                        afterLoadCallbacks.forEach(Runnable::run);
                        afterLoadCallbacks.clear();
                    }
                });

        // useful for debugging
        getWebEngine().get().setOnAlert(event -> {
            LOGGER.info(event.getData());
        });

        // used as a callback for webEngine.loadContent(HTML_TEMPLATE)
        afterLoad(() -> {
            // setup setting scope
            getTopsoil().get().call("setupSettingScope", getSettingScope());
            getTopsoil().get().call("showData");

            getSettingScope().addListener(settingNames -> {
                getWebEngine().get().executeScript("chart.update(ts.data);");
            });

            // initialization is over
            initialized = true;
            // so we need to trigger callbacks
            initializationCallbacks.forEach(Runnable::run);
            // and take that memory back
            initializationCallbacks.clear();
        });

        // asynchronous
        getWebEngine().get().loadContent(buildContent());

        return webView;
    }

    /**
     * Sets this {@link Chart}'s data by passing rows of length 5 with variables
     * in the following order: <code>x</code>, <code>σx</code>, <code>y</code>,
     * <code>σy</code>, <code>ρ</code>.
     *
     * @param variableContext
     */
    @Override
    public void setData(VariableContext variableContext) {
        super.setData(variableContext);

        EntryListener listener = (entry, field) -> {
            drawChart(variableContext);
        };

        List<Entry> entries = variableContext.getDataset().getEntries();
        //Listen to the entries (= value changes)
        entries.forEach(entry -> entry.addListener(listener));

        drawChart(variableContext);
    }

    public void drawChart(VariableContext variableContext) {
        // pass the data to JavaScript
        // this seems excessive but passing a double[][] creates a single array
        // of undefined objects on the other side of things
        afterInitialization(() -> {
            getTopsoil().get().call("clearData"); // old data must be cleared

            variableContext.getDataset().getEntries()
                    .stream()
                    .filter(entry -> entry.get(SELECTED).orElse(true))
                    .forEach(entry -> {
                JSObject row = (JSObject) getWebEngine().get()
                        .executeScript("new Object()");

                variableContext.getBindings().forEach(variableBinding -> {
                    row.setMember(
                            variableBinding.getVariable().getName(),
                            variableBinding.getValue(entry));
                });

                getTopsoil().get().call("addData", row);
            });

            getTopsoil().get().call("showData");
        });
    }

    @Override
    public Node displayAsNode() {
        return getWebView().orElseGet(this::initializeWebView);
    }

    /**
     * Returns the contents of the {@link Node} representation of this
     * {@link Chart} as a SVG document.
     *
     * @return a new {@link Document} with SVG contents if
     * {@link JavaScriptChart#displayAsNode()} has been called for this instance
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

            // the SVG element in the HTML should have the ID "chart"
            Element svgElement = getWebEngine().get()
                    .getDocument().getElementById("chart");
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

}
