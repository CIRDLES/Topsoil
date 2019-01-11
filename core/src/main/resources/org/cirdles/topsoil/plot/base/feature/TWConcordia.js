
if (plot.twconcordiaVisible == null) {
    plot.twconcordiaVisible = false;
}

plot.drawTWConcordia = function() {

    // Removes the concordia before redrawing it
    if (plot.twconcordiaVisible) {
        plot.removeTWConcordia();
    }

    plot.twconcordiaGroup = plot.area.clipped.insert("g", ".dataGroup")
        .attr("class", "twconcordiaGroup");

    // initialize the concordia envelope
    plot.twconcordiaGroup.append("path")
        .attr("class", "twuncertaintyEnvelope")
        .attr("stroke", "none")
        .attr("shape-rendering", "geometricPrecision");

    // initialize the concordia
    plot.twconcordiaGroup.append("path")
        .attr("class", "twconcordia")
        .attr("fill", "none")
        .attr("shape-rendering", "geometricPrecision");

    plot.twconcordiaVisible = true;
    plot.updateTWConcordia();
};

plot.updateTWConcordia = function() {
    if (plot.twconcordiaVisible) {

        if (plot.lambda.U235 == null) {
            if (plot.getProperty('U235') != null && !isNaN(plot.getProperty('U235'))) {
                plot.lambda.U235 = plot.getProperty('U235');
            } else {
                plot.lambda.U235 = topsoil.defaultLambda.U235;
            }
        } else if (plot.getProperty('U235') != null && !isNaN(plot.getProperty('U235')) ||
            plot.lambda.U235 != plot.getProperty("U235")) {

            plot.lambda.U235 = plot.getProperty("U235");
        }

        if (plot.lambda.U238 == null) {
            if (plot.getProperty('U238') != null && !isNaN(plot.getProperty('U238'))) {
                plot.lambda.U238 = plot.getProperty('U238');
            } else {
                plot.lambda.U238 = topsoil.defaultLambda.U238;
            }
        } else if (plot.getProperty('U238') != null && !isNaN(plot.getProperty('U238')) ||
            plot.lambda.U238 != plot.getProperty("U238")) {

            plot.lambda.U238 = plot.getProperty("U238");
        }

        if (plot.lambda.R238_235S == null) {
            if (plot.getProperty('R238_235S') != null && !isNaN(plot.getProperty('R238_235S'))) {
                plot.lambda.R238_235S = plot.getProperty('R238_235S');
            } else {
                plot.lambda.R238_235S = topsoil.defaultLambda.R238_235S;
            }
        } else if (plot.getProperty('R238_235S') != null && !isNaN(plot.getProperty('R238_235S')) ||
            plot.lambda.R238_235S != plot.getProperty("R238_235S")) {

            plot.lambda.R238_235S = plot.getProperty("R238_235S");
        }

        initializeWasserburg({
            LAMBDA_235: plot.lambda.U235,
            LAMBDA_238: plot.lambda.U238,
            R238_235S: plot.lambda.R238_235S
        });


        var minX_T = (Math.log1p(plot.xAxisScale.domain()[1]) - Math.log(plot.xAxisScale.domain()[1]))
            / plot.lambda.U238;
        var minY_T = calculateDate(plot.yAxisScale.domain()[0], 0.0);
        var minT = Math.max(minX_T, minY_T);


        var maxX_T = (Math.log1p(plot.xAxisScale.domain()[0]) - Math.log(plot.xAxisScale.domain()[0]))
            / plot.lambda.U238;
        var maxY_T = calculateDate(plot.yAxisScale.domain()[1], 0.0);
        var maxT = Math.min(maxX_T, maxY_T);


        // build the concordia line
        plot.twconcordia = plot.twconcordiaGroup.select(".twconcordia")
            .attr("d", function () {
                var approximateSegment = function (path, minT, maxT) {
                    var p1 = wasserburg(minT).plus(
                        wasserburg.prime(minT).times((maxT - minT) / 3))
                        .scaleBy(plot.xAxisScale, plot.yAxisScale);
                    var p2 = wasserburg(maxT).minus(
                        wasserburg.prime(maxT).times((maxT - minT) / 3))
                        .scaleBy(plot.xAxisScale, plot.yAxisScale);
                    var p3 = wasserburg(maxT).scaleBy(plot.xAxisScale, plot.yAxisScale);

                    // append a cubic bezier to the path
                    plot.cubicBezier(path, p1, p2, p3);
                };

                // initialize path
                var path = [];
                moveTo(path, wasserburg(minT).scaleBy(plot.xAxisScale, plot.yAxisScale));

                // determine the step size using the number of pieces
                var pieces = 60;
                var stepSize = (maxT - minT) / pieces;

                // build the pieces
                for (var i = 0; i < pieces; i++) {
                    approximateSegment(path, minT + stepSize * i, minT + stepSize * (i + 1));
                }

                return path.join("");
            })
            .attr("stroke", plot.getProperty('Wasserburg Line Fill'))
            .attr("stroke-width", 2);

        if (plot.getProperty("Wasserburg Envelope")) {
            plot.twconcordiaGroup.select(".twuncertaintyEnvelope")
                .attr("d", function () {
                    var approximateUpperSegment = function (path, minT, maxT) {
                        var p1 = wasserburg.upperEnvelope(minT).plus(
                            wasserburg.prime(minT).times((maxT - minT) / 3))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p2 = wasserburg.upperEnvelope(maxT).minus(
                            wasserburg.prime(maxT).times((maxT - minT) / 3))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p3 = wasserburg.upperEnvelope(maxT).scaleBy(plot.xAxisScale, plot.yAxisScale);

                        // append a cubic bezier to the path
                        plot.cubicBezier(path, p1, p2, p3);
                    };

                    var approximateLowerSegment = function (path, minT, maxT) {
                        var p1 = wasserburg.lowerEnvelope(minT).plus(
                            wasserburg.prime(minT).times((maxT - minT) / 3))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p2 = wasserburg.lowerEnvelope(maxT).minus(
                            wasserburg.prime(maxT).times((maxT - minT) / 3))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p3 = wasserburg.lowerEnvelope(maxT).scaleBy(plot.xAxisScale, plot.yAxisScale);

                        // append a cubic bezier to the path
                        plot.cubicBezier(path, p1, p2, p3);
                    };

                    var minT = Math.min(
                        newtonMethod(wasserburg.upperEnvelope.x, plot.xAxisScale.domain()[0]),
                        newtonMethod(wasserburg.upperEnvelope.y, plot.yAxisScale.domain()[0]));

                    var maxT = Math.min(
                        newtonMethod(wasserburg.upperEnvelope.x, plot.xAxisScale.domain()[1]),
                        newtonMethod(wasserburg.upperEnvelope.y, plot.yAxisScale.domain()[1]));


                    // initialize path
                    var path = [];
                    moveTo(path, wasserburg.upperEnvelope(minT).scaleBy(plot.xAxisScale, plot.yAxisScale));

                    // determine the step size using the number of pieces
                    var pieces = 30;
                    var stepSize = (maxT - minT) / pieces;

                    // build the pieces
                    for (var i = 0; i < pieces; i++) {
                        approximateUpperSegment(path, minT + stepSize * i, minT + stepSize * (i + 1));
                    }

                    lineTo(path, [plot.xAxisScale.range()[1], plot.yAxisScale.range()[1]]);

                    minT = Math.min(
                        newtonMethod(wasserburg.lowerEnvelope.x, plot.xAxisScale.domain()[0]),
                        newtonMethod(wasserburg.lowerEnvelope.y, plot.yAxisScale.domain()[0]));

                    maxT = Math.min(
                        newtonMethod(wasserburg.lowerEnvelope.x, plot.xAxisScale.domain()[1]),
                        newtonMethod(wasserburg.lowerEnvelope.y, plot.yAxisScale.domain()[1]));

                    lineTo(path, wasserburg.lowerEnvelope(maxT).scaleBy(plot.xAxisScale, plot.yAxisScale));

                    stepSize = (maxT - minT) / pieces;

                    // build the pieces
                    for (i = 0; i < pieces; i++) {
                        approximateLowerSegment(path, maxT - stepSize * i, maxT - stepSize * (i + 1));
                    }

                    lineTo(path, [plot.xAxisScale.range()[0], plot.yAxisScale.range()[0]]);
                    close(path);

                    return path.join("");
                })
                .attr("fill", plot.getProperty('Wasserburg Envelope Fill'));
        }

        plot.t.domain([minT, maxT]);

        var concordiaTicks;
        (concordiaTicks = plot.concordiaTicks = plot.twconcordiaGroup.selectAll(".concordiaTicks")
            .data(plot.t.ticks()))
            .enter()
            .append("circle")
            .attr("class", "concordiaTicks")
            .attr("r", 5)
            .style('stroke-width', 2)
            .style("stroke", "black")
            .style("fill", "white");;

        concordiaTicks
            .attr("cx", function (t) {
                return plot.xAxisScale(wasserburg.x(t));
            })
            .attr("cy", function (t) {
                return plot.yAxisScale(wasserburg.y(t));
            });

        var tickLabels;
        (tickLabels = plot.tickLabels = plot.twconcordiaGroup.selectAll(".tickLabel")
            .data(plot.t.ticks()))
            .enter()
            .append("text")
            .attr("font-family", "sans-serif")
            .attr("class", "tickLabel");

        tickLabels
            .attr("x", function (t) {
                return plot.xAxisScale(wasserburg.x(t)) + 12;
            })
            .attr("y", function (t) {
                return plot.yAxisScale(wasserburg.y(t)) + 5;
            })
            .text(function (t) {
                return t / 1000000;
            });

        plot.concordiaTicks.exit().remove();
        plot.tickLabels.exit().remove();
    }
};

plot.removeTWConcordia = function() {
    if (plot.twconcordiaVisible) {
        plot.twconcordiaGroup.remove();
        plot.twconcordiaVisible = false;
    }
};