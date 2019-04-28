/**
 * @author Emily Coleman
 */

if (plot.regressionVisible == null) {
    plot.regressionVisible = false;
}

if (plot.regressionEnvelopeVisible == null) {
    plot.regressionEnvelopeVisible = false;
}

/**
 *The y-intercept of the McLean Regression line.
 * @type {number}
 */
plot.regressionYIntercept = 0;

/**
 * The slope of the McLean Regression line.
 * @type {number}
 */
plot.regressionSlope = 0;

/**
 * Uses the RegressionLine.java class to calculate the McLean Regression.
 *
 */
plot.calculateRegressionLine = function () {
    var data = plot.data;

    var x_list = [];
    var y_list = [];
    var sigma_x_list = [];
    var sigma_y_list = [];
    var rho_list = [];

    var fillLists = data.map(function (d) {
        if(d['selected']) {
            x_list.push(d.x);
            y_list.push(d.y);
            sigma_x_list.push(d.sigma_x);
            sigma_y_list.push(d.sigma_y);
            rho_list.push(d.rho);
        }
    });

    topsoil.regression.fitLineToDataFor2D(x_list.toString(), y_list.toString(), sigma_x_list.toString(), sigma_y_list.toString(), rho_list.toString());

    plot.regressionSlope = topsoil.regression.getSlope();
    plot.regressionYIntercept = topsoil.regression.getIntercept();
};

/**
 * Creates the SVG elements required to display the regression line.
 */
plot.drawRegressionLine = function () {
    // Remove line before redrawing it.
    if (plot.regressionVisible) {
        plot.removeRegressionLine();
    }

    // Create a separate SVG group for the regression line.
    plot.regressionGroup = plot.area.clipped.insert("g", ".dataGroup")
        .attr("class", "regressionGroup");

    plot.regressionGroup.append("line")
        .attr("class", "regression")
        .attr("stroke", "black")
        .attr("stroke-width", 1);

    plot.regressionVisible = true;

    plot.drawInfoBox();

    plot.calculateRegressionLine();
    plot.drawRegressionEnvelope();
    plot.updateRegressionLine();
};

/**
 * Updates the regression line as the plot zooms and resizes.
 */
plot.updateRegressionLine = function () {

    if (plot.regressionVisible) {

        // Draw a line from point x1, y1 to point x2, y2.
        var x1 = 0;
        var y1 = plot.regressionYIntercept;
        var x2 = plot.xAxisScale.domain()[1];
        var y2 = (plot.regressionSlope * x2) + plot.regressionYIntercept; //y = mx + b

        plot.regressionGroup.selectAll(".regression")
            .attr("x1", plot.xAxisScale(x1))
            .attr("y1", plot.yAxisScale(y1))
            .attr("x2", plot.xAxisScale(x2))
            .attr("y2", plot.yAxisScale(y2));
    }

    plot.updateInfoBox();
    plot.updateRegressionEnvelope();
};

/**
 * Removes elements associated with the regression line.
 */
plot.removeRegressionLine = function () {
    if (plot.regressionVisible) {
        plot.regressionGroup.remove();
        plot.removeInfoBox();
        plot.regressionVisible = false;
    }
};

/**
 * Displays regression slope above plot.
 */
plot.drawInfoBox = function () {
    plot.area.append("text")
        .attr("class", "regressionInfo")
        .attr("font-family", "sans-serif")
        .attr("font-size", "12px")
        .attr("x", plot.innerWidth)
        .attr("y", -20)
        .attr("fill", "black");
};

/**
 * Gets current slope value and adjusts text position on plot resize
 */
plot.updateInfoBox = function () {
    plot.area.select(".regressionInfo")
        .text("Regression slope: " + plot.regressionSlope)
        .attr("x", (plot.innerWidth - 30) - (plot.area.select(".regressionInfo").node().getBBox().width));
};

/**
 * Removes slope value from above plot.
 */
plot.removeInfoBox = function () {
    plot.area.select(".regressionInfo")
        .remove();
};

/**
 * Performs calculations necessary for the regression uncertainty envelope to draw.
 */
plot.calculateRegressionUncertaintyEnvelope = function () {
    var xMin = plot.xAxisScale.domain()[0];
    var xMax = plot.xAxisScale.domain()[1];

    var aXvar = topsoil.regression.getAX();
    var aYvar = topsoil.regression.getIntercept();
    var vXvar = topsoil.regression.getVectorX();
    var vYvar = topsoil.regression.getSlope();

    var subCov = plot.getSav();

    //Split visible plot into an arbitrary number of chunks
    var tIncrement = (xMax - xMin) / 50;

    plot.uncertaintyEnvelopeLowerBound = [];
    plot.uncertaintyEnvelopeUpperBound = [];

    var path = [];

    //For each chunk in tIncrement
    if (tIncrement > 0) {
        //Make the edges of the envelope just beyond the edges of the visible plot
        for (var tStep = (0.9 * xMin); tStep <= (1.1 * xMax); tStep += tIncrement) {
            var vperp = [[-vYvar, vXvar]];
            var Jxyab = [[0, 0], [1, tStep]];

            var term1 = dot(vperp, Jxyab);
            var thing2 = dot(term1, subCov);
            var thing3 = dot(thing2, numeric.transpose(Jxyab));
            var thing4 = dot(thing3, numeric.transpose(vperp));
            var thing5 = thing4[0][0];
            var thing6 = dot(vperp, numeric.transpose(vperp));
            var s2perp = thing5 / thing6[0][0];

            var xv = 2 * Math.cos(Math.atan(-vXvar / vYvar)) * Math.sqrt(s2perp);
            var yv = 2 * Math.sin(Math.atan(-vXvar / vYvar)) * Math.sqrt(s2perp);

            var xplus = plot.xAxisScale(aXvar + vXvar * tStep + xv);
            var yplus = plot.yAxisScale(aYvar + vYvar * tStep + yv);
            var xminus = plot.xAxisScale(aXvar + vXvar * tStep - xv);
            var yminus = plot.yAxisScale(aYvar + vYvar * tStep - yv);

            plot.uncertaintyEnvelopeLowerBound.push({x:xminus, y:yminus});
            plot.uncertaintyEnvelopeUpperBound.push({x:xplus, y:yplus});
        }
    }
};

/**
 * Creates the SVG elements required to display the regression uncertainty envelope.
 */
plot.drawRegressionEnvelope = function () {
    // Remove envelope before redrawing it.
    if (plot.regressionEnvelopeVisible) {
        plot.removeRegressionEnvelope();
    }

    plot.lowerEnvelope = plot.regressionGroup.append("path")
        .attr("stroke", "blue")
        .attr("stroke-width", 2)
        .attr("fill", "none")
        .style("stroke-dasharray", ("5, 5"));

    plot.upperEnvelope = plot.regressionGroup.append("path")
        .attr("stroke", "blue")
        .attr("stroke-width", 2)
        .attr("fill", "none")
        .style("stroke-dasharray", ("5, 5"));

    plot.regressionEnvelopeVisible = true;
};

/**
 * Updates the regression uncertainty envelope on zoom, resize, or model update.
 */
plot.updateRegressionEnvelope = function () {

    if(plot.regressionVisible && plot.getProperty(Property.MCLEAN_REGRESSION_ENVELOPE)) {

        //Draws the regression envelope if it's not present
        if(!plot.regressionEnvelopeVisible) {
            plot.drawRegressionEnvelope();
        }

        var lineGenerator = d3.svg.line()
            .interpolate("cardinal")
            .x(function (d) {
                return d.x;
            })
            .y(function (d) {
                return d.y;
            });

        plot.calculateRegressionUncertaintyEnvelope();

        plot.lowerEnvelope.attr("d", lineGenerator(plot.uncertaintyEnvelopeUpperBound));
        plot.upperEnvelope.attr("d", lineGenerator(plot.uncertaintyEnvelopeLowerBound));
    } else if (plot.regressionVisible && !plot.getProperty(Property.MCLEAN_REGRESSION_ENVELOPE)) {
        plot.removeRegressionEnvelope();
    }
};

/**
 * Removes regression uncertainty envelope.
 */
plot.removeRegressionEnvelope = function () {
    if(plot.regressionEnvelopeVisible) {
        plot.lowerEnvelope.remove();
        plot.upperEnvelope.remove();
        plot.regressionEnvelopeVisible = false;
    }
};

/**
 * If the Sav matrix has already been retrieved from the Java, return it.
 * Else, retrieve Sav from Java and return it
 */
plot.getSav = function () {
    if (plot.regressionSav != null) {
        return plot.regressionSav;
    } else {
        plot.calcSav();
        return plot.regressionSav;
    }
};

/**
 * Convert Sav from delimited string to a matrix
 */
plot.calcSav = function () {
    //Sav comes in as a string because matrices cannot be passed between Java and Javascript
    var savString = topsoil.regression.getSav();
    var savMatrix = [[], []];
    var savList = savString.split(";");

    for (var i = 0; i < savList.length - 1; i++) {
        var savValues = savList[i].split(",");
        for (var j = 0; j < savValues.length - 1; j++) {
            savMatrix[i][j] = savValues[j];
        }
    }

    plot.regressionSav = savMatrix;

};

