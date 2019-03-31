/**
 * @author Emily Coleman
 */

if (plot.concordiaVisible == null) {
    plot.concordiaVisible = false;
}

plot.drawConcordia = function() {

    // Removes the concordia before redrawing it
    if (plot.concordiaVisible) {
        plot.removeConcordia();
    }

    plot.concordiaGroup = plot.area.clipped.insert("g", ".dataGroup")
        .attr("class", "concordiaGroup");

    // initialize the concordia envelope
    plot.concordiaGroup.append("path")
        .attr("class", "uncertaintyEnvelope")
        .attr("stroke", "none")
        .attr("shape-rendering", "geometricPrecision");

    // initialize the concordia
    plot.concordiaGroup.append("path")
        .attr("class", "concordia")
        .attr("fill", "none")
        .attr("shape-rendering", "geometricPrecision");

    plot.concordiaVisible = true;
    plot.updateConcordia();
};

plot.updateConcordia = function() {
    if (plot.concordiaVisible) {

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

        initializeWetherill({
            LAMBDA_235: plot.lambda.U235,
            LAMBDA_238: plot.lambda.U238
        });

        var minT = Math.max(
            newtonMethod(wetherill.x, plot.xAxisScale.domain()[0]),
            newtonMethod(wetherill.y, plot.yAxisScale.domain()[0]));

        var maxT = Math.min(
            newtonMethod(wetherill.x, plot.xAxisScale.domain()[1]),
            newtonMethod(wetherill.y, plot.yAxisScale.domain()[1]));

        var minT1 = Math.max(Math.log1p(plot.xAxisScale.domain()[0] / plot.lambda.U235),
            Math.log(1 + plot.yAxisScale.domain()[0]) / plot.lambda.U238);
        var maxT1 = Math.min(Math.log1p(plot.xAxisScale.domain()[1] / plot.lambda.U235),
            Math.log(1 + plot.yAxisScale.domain()[1]) / plot.lambda.U238);


        // build the concordia line
        plot.concordia = plot.concordiaGroup.select(".concordia")
            .attr("d", function () {
                var approximateSegment = function (path, minT, maxT) {
                    var p1 = wetherill(minT).plus(
                        wetherill.prime(minT).times((maxT - minT) / 3))
                        .scaleBy(plot.xAxisScale, plot.yAxisScale);
                    var p2 = wetherill(maxT).minus(
                        wetherill.prime(maxT).times((maxT - minT) / 3))
                        .scaleBy(plot.xAxisScale, plot.yAxisScale);
                    var p3 = wetherill(maxT).scaleBy(plot.xAxisScale, plot.yAxisScale);

                    // append a cubic bezier to the path
                    plot.cubicBezier(path, p1, p2, p3);
                };

                // initialize path
                var path = [];
                moveTo(path, wetherill(minT).scaleBy(plot.xAxisScale, plot.yAxisScale));

                // determine the step size using the number of pieces
                var pieces = 30;
                var stepSize = (maxT - minT) / pieces;

                // build the pieces
                for (var i = 0; i < pieces; i++) {
                    approximateSegment(path, minT + stepSize * i, minT + stepSize * (i + 1));
                }

                return path.join("");
            })
            .attr("stroke", plot.getProperty('Wetherill Line Fill'))
            .attr("stroke-width", 2);

        if (plot.getProperty("Wetherill Envelope")) {
            plot.concordiaGroup.select(".uncertaintyEnvelope")
                .attr("d", function () {
                    var approximateUpperSegment = function (path, minT, maxT) {
                        var p1 = wetherill.upperEnvelope(minT).plus(
                            wetherill.prime(minT).times((maxT - minT) / 3))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p2 = wetherill.upperEnvelope(maxT).minus(
                            wetherill.prime(maxT).times((maxT - minT) / 3))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p3 = wetherill.upperEnvelope(maxT).scaleBy(plot.xAxisScale, plot.yAxisScale);

                        // append a cubic bezier to the path
                        plot.cubicBezier(path, p1, p2, p3);
                    };

                    var approximateLowerSegment = function (path, minT, maxT) {
                        var p1 = wetherill.lowerEnvelope(minT).plus(
                            wetherill.prime(minT).times((maxT - minT) / 3))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p2 = wetherill.lowerEnvelope(maxT).minus(
                            wetherill.prime(maxT).times((maxT - minT) / 3))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p3 = wetherill.lowerEnvelope(maxT).scaleBy(plot.xAxisScale, plot.yAxisScale);

                        // append a cubic bezier to the path
                        plot.cubicBezier(path, p1, p2, p3);
                    };

                    var minT = Math.max(
                        newtonMethod(wetherill.upperEnvelope.x, plot.xAxisScale.domain()[0]),
                        newtonMethod(wetherill.upperEnvelope.y, plot.yAxisScale.domain()[0]));

                    var maxT = Math.min(
                        newtonMethod(wetherill.upperEnvelope.x, plot.xAxisScale.domain()[1]),
                        newtonMethod(wetherill.upperEnvelope.y, plot.yAxisScale.domain()[1]));

                    // initialize path
                    var path = [];
                    moveTo(path, wetherill.upperEnvelope(minT).scaleBy(plot.xAxisScale, plot.yAxisScale));

                    // determine the step size using the number of pieces
                    var pieces = 30;
                    var stepSize = (maxT - minT) / pieces;

                    // build the pieces
                    for (var i = 0; i < pieces; i++) {
                        approximateUpperSegment(path, minT + stepSize * i, minT + stepSize * (i + 1));
                    }

                    lineTo(path, [plot.xAxisScale.range()[1], plot.yAxisScale.range()[1]]);

                    minT = Math.max(
                        newtonMethod(wetherill.lowerEnvelope.x, plot.xAxisScale.domain()[0]),
                        newtonMethod(wetherill.lowerEnvelope.y, plot.yAxisScale.domain()[0]));

                    maxT = Math.min(
                        newtonMethod(wetherill.lowerEnvelope.x, plot.xAxisScale.domain()[1]),
                        newtonMethod(wetherill.lowerEnvelope.y, plot.yAxisScale.domain()[1]));

                    lineTo(path, wetherill.lowerEnvelope(maxT).scaleBy(plot.xAxisScale, plot.yAxisScale));

                    stepSize = (maxT - minT) / pieces;

                    // build the pieces
                    for (i = 0; i < pieces; i++) {
                        approximateLowerSegment(path, maxT - stepSize * i, maxT - stepSize * (i + 1));
                    }

                    lineTo(path, [plot.xAxisScale.range()[0], plot.yAxisScale.range()[0]]);
                    close(path);

                    return path.join("");
                })
                .attr("fill", plot.getProperty('Wetherill Envelope Fill'));
        }

        plot.t.domain([minT, maxT]);

        var concordiaTicks;
        (concordiaTicks = plot.concordiaTicks = plot.concordiaGroup.selectAll(".concordiaTicks")
            .data(plot.t.ticks()))
            .enter()
            .append("circle")
            .attr("class", "concordiaTicks")
            .attr("r", 5)
            .style('stroke-width', 2)
            .style("stroke", "black")
            .style("fill", "white");

        concordiaTicks
            .attr("cx", function (t) {
                return plot.xAxisScale(wetherill.x(t));
            })
            .attr("cy", function (t) {
                return plot.yAxisScale(wetherill.y(t));
            });

        var tickLabels;
        (tickLabels = plot.tickLabels = plot.concordiaGroup.selectAll(".tickLabel")
            .data(plot.t.ticks()))
            .enter()
            .append("text")
            .attr("font-family", "sans-serif")
            .attr("class", "tickLabel");

        tickLabels
            .attr("x", function (t) {
                return plot.xAxisScale(wetherill.x(t)) + 12;
            })
            .attr("y", function (t) {
                return plot.yAxisScale(wetherill.y(t)) + 5;
            })
            .text(function (t) {
                return t / 1000000;
            });

        plot.concordiaTicks.exit().remove();
        plot.tickLabels.exit().remove();
    }
};

plot.removeConcordia = function() {
    if (plot.concordiaVisible) {
        plot.concordiaGroup.remove();
        plot.concordiaVisible = false;
    }
};