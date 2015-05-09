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

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import netscape.javascript.JSObject;
import org.cirdles.topsoil.data.Entry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A {@link Chart} that uses JavaScript and HTML to power its visualizations.
 *
 * @author John Zeringue
 */
public class JavaScriptChart extends BaseChart implements JavaFXDisplayable {

    private static final VariableFormat<Number> ONE_SIGMA_ABSOLUTE
            = new BaseVariableFormat<Number>("1σ (Abs)") {

                @Override
                public Number normalize(VariableBinding<Number> binding,
                        Entry entry) {
                    return entry.get(binding.getField()).get().doubleValue();
                }

            };

    private static final VariableFormat<Number> ONE_SIGMA_PERCENT
            = new DependentVariableFormat<Number>("1σ (%)") {

                @Override
                public Number normalize(Number variableValue,
                        Number dependencyValue) {
                    return variableValue.doubleValue()
                    * dependencyValue.doubleValue() / 100;
                }

            };

    private static final VariableFormat<Number> TWO_SIGMA_ABSOLUTE
            = new BaseVariableFormat<Number>("2σ (Abs)") {

                @Override
                public Number normalize(VariableBinding<Number> binding,
                        Entry entry) {
                    return entry.get(binding.getField()).get().doubleValue() / 2;
                }

            };

    private static final VariableFormat<Number> TWO_SIGMA_PERCENT
            = new DependentVariableFormat<Number>("2σ (%)") {

                @Override
                public Number normalize(Number variableValue,
                        Number dependencyValue) {
                    return variableValue.doubleValue()
                    * dependencyValue.doubleValue() / 200;
                }

            };

    private static final List<VariableFormat> UNCERTAINTY_FORMATS
            = Arrays.asList(
                    ONE_SIGMA_ABSOLUTE,
                    ONE_SIGMA_PERCENT,
                    TWO_SIGMA_ABSOLUTE,
                    TWO_SIGMA_PERCENT
            );

    private static final Variable X = new IndependentVariable("x");
    private static final Variable SIGMA_X = new DependentVariable("sigma_x", X, UNCERTAINTY_FORMATS);
    private static final Variable Y = new IndependentVariable("y");
    private static final Variable SIGMA_Y = new DependentVariable("sigma_y", Y, UNCERTAINTY_FORMATS);
    private static final Variable RHO = new IndependentVariable("rho");

    private static final List<Variable> VARIABLES
            = Arrays.asList(X, SIGMA_X, Y, SIGMA_Y, RHO);

    private static final String HTML_TEMPLATE;

    static {
        // prepare the local URL for Firebug Lite
        final String FIREBUG_LITE_URL
                = JavaScriptChart.class.getResource("firebug-lite.js").toExternalForm();

        // prepare the local URL for d3.js
        final String D3_JS_URL
                = JavaScriptChart.class.getResource("d3.js").toExternalForm();

        // prepare the local URL for numeric.js
        final String NUMERIC_JS_URL
                = JavaScriptChart.class.getResource("numeric.js").toExternalForm();

        // prepare the local URL for topsoil.js
        final String TOPSOIL_JS_URL
                = JavaScriptChart.class.getResource("topsoil.js").toExternalForm();

        // build the HTML template (comments show implicit elements/tags)
        HTML_TEMPLATE
                = ("<!DOCTYPE html>\n"
                // <html>
                // <head>
                + "<style>\n"
                + "body {\n"
                + "  margin: 0; padding: 0;\n"
                + "}\n"
                + "</style>\n"
                // </head>
                + "<body>"
                + "<script src=\"" + FIREBUG_LITE_URL + "\"></script>\n"
                + "<script src=\"" + D3_JS_URL + "\"></script>\n"
                + "<script src=\"" + NUMERIC_JS_URL + "\"></script>\n"
                + "<script src=\"" + TOPSOIL_JS_URL + "\"></script>\n"
                + "<script src=\"%s\"></script>\n" // JS file for chart
                // </body>
                // </html>
                + "").replaceAll("%20", "%%20"); // excape appropriate percents
    }

    private final Collection<Runnable> initializationCallbacks = new ArrayList<>();

    private final Path sourcePath;

    private WebView webView;
    private WebEngine webEngine;
    private JSObject topsoil;

    private boolean loaded = false;
    private boolean initialized = false;

    /**
     * Creates a new {@link JavaScriptChart} using the specified source file.
     *
     * @param sourcePath the path to a valid JavaScript file
     */
    public JavaScriptChart(Path sourcePath) {
        if (Files.isDirectory(sourcePath)) {
            throw new IllegalArgumentException("sourcePath cannot be a directory");
        }

        this.sourcePath = sourcePath;
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
        // if already initialized, just run the callback
        if (initialized) {
            callback.run();
            return;
        }

        // otherwise add the runnable to the collection of callbacks
        initializationCallbacks.add(callback);
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
        // if already loaded, just run the callback
        if (loaded) {
            callback.run();
            return;
        }

        // otherwise add a new listener that triggers the callback
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {

            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                if (newValue == Worker.State.SUCCEEDED) {
                    loaded = true;

                    // run the callback
                    callback.run();

                    // remove this listener since the callback should only be run once
                    webEngine.getLoadWorker().stateProperty().removeListener(this);
                }
            }

        });
    }

    @Override
    public Node displayAsNode() {
        // this chart's node (a WebView) is lazily instantiated
        initializeWebViewIfNeeded();

        return webView;
    }

    /**
     * Initializes this {@link JavaScriptChart}'s {@link WebView} and related
     * objects if it has not already been done.
     */
    private void initializeWebViewIfNeeded() {
        // if webView is not null, initialization is not needed
        if (webView != null) {
            return;
        }

        // initialize webView and associated variables
        webView = new WebView();
        webEngine = webView.getEngine();

        // useful for debugging
        webEngine.setOnAlert(event -> {
            System.out.println(event.getData());
        });
        webEngine.setOnError(event -> {
            System.err.println(event.getMessage());
        });

        // used as a callback for webEngine.loadContent(HTML_TEMPLATE)
        afterLoad(() -> {
            topsoil = (JSObject) webEngine.executeScript("topsoil");

            // setup setting scope
            topsoil.call("setupSettingScope", getSettingScope());
            topsoil.call("showData");
            getSettingScope().addListener(settingNames -> {
                webEngine.executeScript("chart.update(ts.data);");
            });

            // initialization is over
            initialized = true;
            // so we need to trigger callbacks
            initializationCallbacks.stream().forEach(Runnable::run);
            // and take that memory back
            initializationCallbacks.clear();
        });

        String chartURL = null;
        try {
            chartURL = sourcePath.toUri().toURL().toExternalForm();
        } catch (MalformedURLException ex) {
            Logger.getLogger(JavaScriptChart.class.getName()).log(Level.SEVERE, null, ex);
        }
        webEngine.loadContent(String.format(HTML_TEMPLATE, chartURL)); // asynchronous
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

        // this chart's node (a WebView) is lazily instantiated
        initializeWebViewIfNeeded();

        // pass the data to JavaScript
        // this seems excessive but passing a double[][] creates a single array
        // of undefined objects on the other side of things
        afterInitialization(() -> {
            topsoil.call("clearData"); // old data must be cleared away

            variableContext.getDataset().getEntries().forEach(entry -> {
                JSObject row = (JSObject) webEngine.executeScript("new Object()");

                variableContext.getBindings().forEach(variableBinding -> {
                    row.setMember(
                            variableBinding.getVariable().getName(),
                            variableBinding.getValue(entry));
                });

                topsoil.call("addData", row);
            });

            topsoil.call("showData");
        });
    }

    @Override
    public List<Variable> getVariables() {
        return VARIABLES;
    }

    /**
     * Returns the contents of the {@link Node} representation of this
     * {@link Chart} as a SVG document.
     *
     * @return a new {@link Document} with SVG contents if
     * {@link JavaScriptChart#asNode()} has been called for this instance
     */
    public Document toSVG() {
        Document svgDocument = null;

        try {
            // create a new document that will be the SVG
            svgDocument = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
            // ugly but acceptable since we control SVG creation
            svgDocument.setStrictErrorChecking(false);

            // the SVG element in the HTML should have the ID "chart"
            Element svgElement = webEngine.getDocument().getElementById("chart");
            // additional configuration to make the SVG standalone
            svgElement.setAttribute("xmlns", "http://www.w3.org/2000/svg");
            svgElement.setAttribute("version", "1.1");

            // set the svg element as the document root (must be imported first)
            svgDocument.appendChild(svgDocument.importNode(svgElement, true));

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(JavaScriptChart.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return svgDocument;
    }

    public void fitData() {
        afterInitialization(() -> {
            topsoil.call("showData");
        });
    }

}
