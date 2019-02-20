/*
 * Copyright 2017 CIRDLES.
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

plot.dataKeys = ['x', 'sigma_x', 'y', 'sigma_y', 'rho', 'selected', 'aliquot', 'label'];
plot.propertiesKeys = [
    'Title',

    'X Axis',
    'X Min',
    'X Max',

    'Y Axis',
    'Y Min',
    'Y Max',

    'Points',
    'Points Fill',
    'Points Opacity',

    'Ellipses',
    'Ellipses Fill',
    'Ellipses Opacity',

    'Unct Bars',
    'Unct Bars Fill',
    'Unct Bars Opacity',

    'Wetherill Line',
    'Wetherill Envelope',
    'Wetherill Line Fill',
    'Wetherill Envelope Fill',
    'Wasserburg Line',
    'Wasserburg Envelope',
    'Wasserburg Line Fill',
    'Wasserburg Envelope Fill',
    'Evolution Matrix',
    'McLean Regression',
    'McLean Regression Envelope',

    'Isotope System',
    'Uncertainty',
    'U234',
    'U235',
    'U238',
    'Th230',
    'R238_235S'
];

/*
    Creates an SVG group for model elements like points and ellipses. Inserting other groups below this one ensures that
    the model is always the top layer.
 */
plot.dataGroup = plot.area.clipped.append("g")
    .attr("class", "dataGroup");

if (plot.initialized == null) {
    plot.initialized = false;
}

/*
    Initializes required values. This function handles operations that only need to be done once, or need to be done at the
    beginning.
 */
plot.initialize = function (data) {

    //create title
    plot.area.append("text")
        .attr("class", "titleText")
        .attr("font-family", "sans-serif")
        .attr("font-size", "20px")
        .attr("x", plot.width / 2)
        .attr("y", -60);

    //create x axis label
    plot.area.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + plot.height + ")")
        .append("text")
        .attr("class", "label")
        .style("font-size", "16px")
        .attr("x", plot.width / 2)
        .attr("y", -10);

    //create y axis label
    plot.area.append("g")
        .attr("class", "y axis")
        .append("text")
        .attr("class", "label")
        .attr("transform", "rotate(-90)")
        .style("font-size", "16px")
        .attr("x", -plot.height / 2)
        .attr("y", 15)
        .attr("dy", ".1em");

    // defaults if no model is provided
    plot.xDataMin = 0;
    plot.xDataMax = 1;
    plot.yDataMin = 0;
    plot.yDataMax = 1;

    // Initialize axis scales
    plot.xAxisScale = d3.scale.linear();
    plot.yAxisScale = d3.scale.linear();

    plot.t = d3.scale.linear();

    //draw the axes
    plot.xAxis = d3.svg.axis().orient("bottom");
    plot.yAxis = d3.svg.axis().orient("left");

    plot.data = data;

    // Updates plot.xDataMin, plot.xDataMax, etc. based on the model.
    plot.updateDataExtent();

    // Updates the scales for the x and y axes
    plot.xAxisScale
        .domain([plot.xDataMin, plot.xDataMax])
        .range([0, plot.width]);
    plot.yAxisScale
        .domain([plot.yDataMin, plot.yDataMax])
        .range([plot.height, 0]);

    // Applies the scales to the x and y axes.
    plot.xAxis.scale(plot.xAxisScale);
    plot.yAxis.scale(plot.yAxisScale);

    // call the axes
    plot.area.selectAll(".x.axis").call(plot.xAxis);
    plot.area.selectAll(".y.axis").call(plot.yAxis);

    plot.area.selectAll(".axis text")
        .attr("font-family", "sans-serif")
        .attr("font-size", "12px");
    plot.area.selectAll(".axis path, .axis line")
        .attr("fill", "none")
        .attr("stroke", "black")
        .attr("shape-rendering", "geometricPrecision");

    // add pan/zoom
    var zoom = plot.zoom = d3.behavior.zoom()
        .x(plot.xAxisScale)
        .y(plot.yAxisScale);
    function zoomed() {
        plot.zoomed();
    }
    zoom.on("zoom", zoomed);
    plot.area.clipped.call(zoom);

    // function to recenter the plot to its original control
    topsoil.recenter = function() {
        changeAxes(plot.xDataMin, plot.xDataMax, plot.yDataMin, plot.yDataMax);
    };

    // function to manually the x and y axes' extents
    topsoil.setAxes = function(xMin, xMax, yMin, yMax) {

        // if the user hasn't set a new extent for a field, leave it as-is
        if (xMin.length === 0) xMin = plot.xAxisScale.domain()[0];
        if (xMax.length === 0) xMax = plot.xAxisScale.domain()[1];
        if (yMin.length === 0) yMin = plot.yAxisScale.domain()[0];
        if (yMax.length === 0) yMax = plot.yAxisScale.domain()[1];

        // if the user input a min greater than the max, arbitrarily set the max to a larger value
        if(xMin >= xMax) { xMax = parseFloat(xMin) + .1; }
        if(yMin >= yMax) { yMax = parseFloat(yMin) + .1; }

        changeAxes(xMin, xMax, yMin, yMax);
    };

    // function to change the X and Y extents of the plot
    var changeAxes = function(xMin, xMax, yMin, yMax) {
        d3.transition().duration(750).tween("zoom", function() {
            var ix = d3.interpolate(plot.xAxisScale.domain(), [xMin, xMax]);
            var iy = d3.interpolate(plot.yAxisScale.domain(), [yMin, yMax]);
            return function(t) {
                zoom.x(plot.xAxisScale.domain(ix(t))).y(plot.yAxisScale.domain(iy(t)));
                zoomed();
            };
        });
    };
    
    // function to bring concordia to corners of plot 
    topsoil.snapToCorners = function () {
        
        // get x axis min and find coordinate on y axis 
        
        //this should be the currrent axis extent 
        var xAxisMin = plot.xAxisScale.domain()[0];
        var lamda235 = 0.00000000098485000000;
        var lamda238 = 0.00000000015512500000;
        var age207_235 = 0;
        var age206_238 = 0;
        
        var concordiaXMin = xAxisMin;
        var concordiaYMin = 0;
        
        //calculate y value passing through x axis 
        age207_235 = ( 1 / lamda235 ) * Math.log( xAxisMin + 1);
        age206_238 = age207_235;
        concordiaYMin = exp ( age206_238 * lamda238 ) - 1;
        
        // get x axis max and find coordinate on y axis 
        var xAxisMax = plot.xAxisScale.domain()[1];
        
        var concordiaXMax = xAxisMax;
        var concordiaYMax = 0;
        
        age207_235 = ( 1 / lamda235 ) * Math.log( xAxisMax + 1 ); 
        age206_238 = age207_235;
        concordiaYMax = exp ( age206_238 * lamda238 ) - 1;
        
        //change axes to snap the concordia to corners 
        changeAxes( concordiaXMin, concordiaXMax, concordiaYMin, concordiaYMax );
        
    };

    //Helper function that will bring the concordia line to the front

    plot.initialized = true;
    plot.manageAxisExtents();
    plot.update(plot.data);
};

/*
    Draws and sets plot elements, defines special behaviors like topsoil.recenter() and d3.behavior.zoom(). This function
    handles operations that need to be re-performed whenever new model is entered.
 */
plot.setData = function (data) {

    // Makes sure that the plot has been initialized.
    if (!plot.initialized) {
        plot.initialize(data);
        return;
    }

    plot.data = data;
    plot.ellipseData = plot.calcEllipses(plot.data);
    if (plot.regressionVisible == true) {
        plot.drawRegressionLine();
    }

    // Updates plot.xDataMin, plot.xDataMax, etc. based on the model.
    plot.updateDataExtent();

    plot.update(plot.data);
};

/*
    Updates plot elements. This function handles operations that need to be re-performed every time there is a change made
    to the plot.
 */
plot.update = function (data) {
    // Makes sure that the plot has been initialized.
    if (!plot.initialized) {
        plot.initialize(data);
        return;
    }

    //if the isotope type has changed, alert Java
    if (plot.currentIsotope != plot.getProperty('Isotope System')) {
        plot.currentIsotope = plot.getProperty('Isotope System');
    }

    // If the uncertainty has changed, the plot extent and ellipse model have to be re-calculated, and the ellipses
    // redrawn. Removes ellipses to be later re-drawn by plot.manageEllipses().
    if (plot.uncertainty != plot.getProperty("Uncertainty")) {
        plot.uncertainty = plot.getProperty("Uncertainty");
        plot.updateDataExtent();
        plot.ellipseData = plot.calcEllipses(plot.data);
        plot.removeEllipses();
    }

    var redrawEvolution = false;
    var redrawConcordia = false;
    var redrawTWConcordia = false;

    if (plot.getProperty('U234') != null && !isNaN(plot.getProperty('U234'))) {
        if (plot.lambda.U234 != plot.getProperty("U234")) {
            plot.lambda.U234 = plot.getProperty("U234");
            redrawConcordia = true;
            redrawEvolution = true;
            redrawTWConcordia = true;
        }
    } else if (plot.lambda.U234 === null) {
        plot.lambda.U234 = topsoil.defaultLambda.U234;
    }
    if (plot.getProperty('U235') != null && !isNaN(plot.getProperty('U235'))) {
        if (plot.lambda.U235 != plot.getProperty("U235")) {
            plot.lambda.U235 = plot.getProperty("U235");
            redrawConcordia = true;
            redrawTWConcordia = true;
        }
    } else if (plot.lambda.U235 === null) {
        plot.lambda.U235 = topsoil.defaultLambda.U235;
    }
    if (plot.getProperty('U238') != null && !isNaN(plot.getProperty('U238'))) {
        if (plot.lambda.U238 != plot.getProperty("U238")) {
            plot.lambda.U238 = plot.getProperty("U238");
            redrawEvolution = true
        }
    } else if (plot.lambda.U238 === null) {
        plot.lambda.U238 = topsoil.defaultLambda.U238;
    }
    if (plot.getProperty('Th230') != null && !isNaN(plot.getProperty('Th230'))) {
        if (plot.lambda.Th230 != plot.getProperty("Th230")) {
            plot.lambda.Th230 = plot.getProperty("Th230");
            redrawEvolution = true
        }
    } else if (plot.lambda.Th230 === null) {
        plot.lambda.Th230 = topsoil.defaultLambda.Th230;
    }
    if (plot.getProperty('R238_235S') != null && !isNaN(plot.getProperty('R238_235S'))) {
        if (plot.lambda.R238_235S != plot.getProperty("R238_235S")) {
            plot.lambda.R238_235S = plot.getProperty("R238_235S");
            redrawTWConcordia = true;
        }
    } else if (plot.lambda.R238_235S === null) {
        plot.lambda.R238_235S = topsoil.defaultLambda.R238_235S;
    }

    if (redrawEvolution) {
        plot.removeEvolutionMatrix();
        plot.calculateIsochrons();
    }

    if (redrawConcordia) {
        plot.removeConcordia();
    }

    if (redrawTWConcordia) {
        plot.removeTWConcordia();
    }

    //draw title and axis labels
    d3.select(".titleText")
        .text(plot.getProperty("Title"))
        .attr("x", (plot.width / 2) - (d3.select(".titleText").node().getBBox().width) / 2);
    d3.select(".x.axis .label")
        .text(plot.getProperty("X Axis"))
        .attr("x", (plot.width) - (d3.select(".x.axis .label").node().getBBox().width));
    d3.select(".y.axis .label")
        .text(plot.getProperty("Y Axis"))
        .attr("x",  -(d3.select(".y.axis .label").node().getBBox().width));

    // axis styling
    plot.area.selectAll(".axis text")
        .attr("font-family", "sans-serif")
        .attr("font-size", "10px");
    plot.area.selectAll(".axis path, .axis line")
        .attr("fill", "none")
        .attr("stroke", "black")
        .attr("shape-rendering", "geometricPrecision"); // see SVG docs

    // Manage the plot elements
    plot.managePoints();
    plot.manageEllipses();
    plot.manageRegressionLine();
    plot.manageUncertaintyBars();
    plot.managePlotFeatures();
};

/*
    Custom zoom behavior function, required for d3.behavior.zoom(). Re-calls the axes for the new translation and scale,
    then updates all plot elements.
 */
plot.zoomed = function() {

    // re-tick the axes
    plot.area.selectAll(".x.axis").call(plot.xAxis);
    plot.area.selectAll(".y.axis").call(plot.yAxis);

    //If necessary, update the regression line
    if(plot.regressionVisible) {
        plot.updateRegressionLine();
    }

    plot.manageAxisExtents();
    plot.update(topsoil.data);
};

/*
    Updates the global variables plot.xDataMin, plot.yDataMin, plot.xDataMax, and plot.yDataMax based on the model provided. If
    plot.uncertainty is unspecified, the default value 2 is used.
 */
plot.updateDataExtent = function () {
    //find the extent of the points
    if (plot.data.length > 0) {
        var dataXMin = d3.min(plot.data, function (d) {
            return d.x - (d.sigma_x * (plot.uncertainty != null ? plot.uncertainty : 2));
        });
        var dataYMin = d3.min(plot.data, function (d) {
            return d.y - (d.sigma_y * (plot.uncertainty != null ? plot.uncertainty : 2));
        });
        var dataXMax = d3.max(plot.data, function (d) {
            return d.x + (d.sigma_x * (plot.uncertainty != null ? plot.uncertainty : 2));
        });
        var dataYMax = d3.max(plot.data, function (d) {
            return d.y + (d.sigma_y * (plot.uncertainty != null ? plot.uncertainty : 2));
        });

        var xRange = dataXMax - dataXMin;
        var yRange = dataYMax - dataYMin;

        plot.xDataMin = dataXMin - 0.05 * xRange;
        plot.yDataMin =  dataYMin - 0.05 * yRange;
        plot.xDataMax = dataXMax + 0.05 * xRange;
        plot.yDataMax = dataYMax + 0.05 * yRange;
    }
};

plot.manageAxisExtents = function() {
    //Keep properties up-to-date with plot control
    //toFixed truncates the extents to 8 decimal places
    var xmin = plot.xAxisScale.domain()[0].toFixed(8),
        xmax = plot.xAxisScale.domain()[1].toFixed(8),
        ymin = plot.yAxisScale.domain()[0].toFixed(8),
        ymax = plot.yAxisScale.domain()[1].toFixed(8);

    topsoil.updateProperty("X Min", xmin);
    topsoil.updateProperty("X Max", xmax);
    topsoil.updateProperty("Y Min", ymin);
    topsoil.updateProperty("Y Max", ymax);
    topsoil.propertiesBridge.setAxesExtents(xmin, xmax, ymin, ymax);

    topsoil.propertiesBridge.setIfUpdated(true);
};

/*
    Manages point elements in the plot based on whether or not they should be visible, and whether or not they are visible.
 */
plot.managePoints = function () {

    // If points should be visible...
    if (plot.getProperty("Points")) {

        // If points should be visible, but aren't...
        if (!plot.pointsVisible) {
            plot.drawPoints(plot.data);
        }

        // If points should be visible, and already are...
        else {
            plot.updatePoints(plot.data);
        }
    }

    // If points should NOT be visible, but are...
    else if (plot.pointsVisible) {
        plot.removePoints();
    }
};

/*
 Manages ellipse elements in the plot based on whether or not they should be visible, and whether or not they are visible.
 */
plot.manageEllipses = function () {

    // If ellipses should be visible...
    if (plot.getProperty("Ellipses")) {

        // If the ellipses simply need to be updated...
        if (plot.ellipsesVisible) {
            plot.updateEllipses();
        }

        // If ellipses need to be drawn...
        else {
            plot.drawEllipses(plot.ellipseData);
        }
    }

    // If ellipses should NOT be visible, but are...
    else if (plot.ellipsesVisible) {
        plot.removeEllipses();
    }
};

plot.manageRegressionLine = function() {
    // If RegressionLine shouldn't be visible
    if(plot.getProperty("McLean Regression")) {

        // If the RegressionLine needs to be updated
        if (plot.regressionVisible) {
            plot.updateRegressionLine();
            plot.updateRegressionEnvelope();
        }

        //If RegressionLine needs to be drawn
        else {
            plot.drawRegressionLine();
        }
    }

    //If RegressionLine should not be visible, but is
    else if (plot.regressionVisible) {
        plot.removeRegressionLine();
    }
};

plot.manageUncertaintyBars = function () {

    // If UncertaintyBars should be visible...
    if (plot.getProperty("Unct Bars")) {

        // If the UncertaintyBars simply need to be updated...
        if (plot.uncertaintyBarsVisible) {
            plot.updateUncertaintyBars(plot.data);
        }

        // If Uncertainty Bars need to be drawn...
        else {
            plot.drawUncertaintyBars(plot.data);
        }
    }

    // If Uncertainty Bars should NOT be visible, but are...
    else if (plot.uncertaintyBarsVisible) {
        plot.removeUncertaintyBars();
    }

};

/*
 Manages plot feature elements in the plot based on whether or not they should be visible, and whether or not they are visible.
 */
plot.managePlotFeatures = function () {

    // If the isotope system is UPb...
    if (plot.currentIsotope == "Uranium Lead") {

        // If the concordia line should be visible...
        if (plot.getProperty("Wetherill Line")) {

            // If the concordia line should be visible, but isn't...
            if (!plot.concordiaVisible) {
                plot.drawConcordia();
            }
            // If the concordia line should be visible, and already is...
            else {
                plot.drawConcordia();
            }
        }

        // If the concordia line should NOT be visible, and is...
        else if (plot.concordiaVisible) {
            plot.removeConcordia();
        }

        if (plot.getProperty("Wasserburg Line")) {
            if (!plot.twconcordiaVisible) {
                plot.drawTWConcordia();
            }
            else {
                plot.drawTWConcordia();
            }
        }
        else if (plot.twconcordiaVisible) {
            plot.removeTWConcordia();
        }
    }

    // If the isotope system is not UPb, but the concordia line is visible...
    else if (plot.concordiaVisible || plot.twconcordiaVisible) {
        if(plot.concordiaVisible){
            plot.removeConcordia();
        }
        if(plot.twconcordiaVisible){
            plot.removeTWConcordia();
        }
    }

    // If the isotope system is UTh...
    if (plot.currentIsotope = "Uranium Thorium" ) {

        // If the evolution matrix should be visible...
        if (plot.getProperty("Evolution Matrix")) {

            // If the evolution matrix should be visible, but isn't...
            if (!plot.evolutionMatrixVisible) {
                plot.drawEvolutionMatrix();
            }

            // If the evolution matrix should be visible, and already is...
            else {
                plot.updateEvolutionMatrix();
            }
        }

        // If the evolution matrix should NOT be visible, but is...
        else if (plot.evolutionMatrixVisible) {
            plot.removeEvolutionMatrix();
        }
    }

    // If the isotope system is not UTh, but the evolution matrix is visible...
    else if (plot.evolutionMatrixVisible) {
        plot.removeEvolutionMatrix();
    }
};

plot.removeDataFeatures = function () {
    plot.removeUncertaintyBars();
    plot.removeEllipses();
    plot.removePoints();
};

/*
    Removes all plot features from the plot.
 */
plot.removePlotFeatures = function () {
    plot.removeRegressionLine();
    plot.removeConcordia();
    plot.removeEvolutionMatrix();
    plot.removeTWConcordia();
};


/*
 Since initialize() is called on each resize, removes old axes on plot.resize().
 */
plot.removeAxes = function () {
    plot.area.selectAll(".x.axis").remove();
    plot.area.selectAll(".y.axis").remove();
};