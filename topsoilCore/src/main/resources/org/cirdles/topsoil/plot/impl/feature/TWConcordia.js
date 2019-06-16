
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

        initializeWasserburg({
            LAMBDA_235: plot.lambda.U235,
            LAMBDA_238: plot.lambda.U238,
            R238_235S: plot.lambda.R238_235S
        });

        var minX = Math.max(1.0, plot.xAxisScale.domain()[0]),
            maxX = Math.min(6500.0, plot.xAxisScale.domain()[1]),
            minY = Math.max(0.04613, plot.yAxisScale.domain()[0]), // Covers down to ~4.2 Ma; lowest without glitchy envelope
            maxY = Math.min(0.625, plot.yAxisScale.domain()[1]);

        var minT_X = (Math.log1p(maxX) - Math.log(maxX))
            / plot.lambda.U238;
        var minT_Y = calculateDate(minY, 0.0);
        var minT = Math.max(minT_X, minT_Y);

        var maxT_X = (Math.log1p(minX) - Math.log(minX))
            / plot.lambda.U238;
        var maxT_Y = calculateDate(maxY, 0.0);
        var maxT = Math.min(maxT_X, maxT_Y);

        // build the concordia line
        plot.twconcordia = plot.twconcordiaGroup.select(".twconcordia")
            .attr("d", function () {
                var approximateSegment = function (path, startT, endT) {
                    var p1 = wasserburg(startT).plus(
                        wasserburg.prime(startT).times(3 / (endT - startT))
                    ).scaleBy(plot.xAxisScale, plot.yAxisScale);
                    var p2 = wasserburg(endT).minus(
                        wasserburg.prime(endT).times(3 / (endT - startT))
                    ).scaleBy(plot.xAxisScale, plot.yAxisScale);
                    var p3 = wasserburg(endT)
                        .scaleBy(plot.xAxisScale, plot.yAxisScale);

                    // append a cubic bezier to the path
                    plot.cubicBezier(path, p1, p2, p3);
                };

                // initialize path
                var path = [];
                moveTo(path, wasserburg(minT).scaleBy(plot.xAxisScale, plot.yAxisScale));

                // determine the step size using the number of pieces
                var pieces = 30;
                var stepSize = (maxT - minT) / pieces;

                // build the pieces
                for (var i = 0; i < pieces; i++) {
                    approximateSegment(path, minT + stepSize * i, minT + stepSize * (i + 1));
                }

                return path.join("");
            })
            .attr("stroke", plot.getOption(PlotOption.CONCORDIA_LINE_FILL))
            .attr("stroke-width", 2);

        if (plot.getOption(PlotOption.CONCORDIA_ENVELOPE)) {
            plot.twconcordiaGroup.select(".twuncertaintyEnvelope")
                .attr("d", function () {
                    var approximateUpperSegment = function (path, startT, endT) {
                        var p1 = wasserburg.upperEnvelope(startT).plus(
                            wasserburg.prime(startT).times(3 / (endT - startT)))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p2 = wasserburg.upperEnvelope(endT).minus(
                            wasserburg.prime(endT).times(3 / (endT - startT)))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p3 = wasserburg.upperEnvelope(endT).scaleBy(plot.xAxisScale, plot.yAxisScale);

                        // append a cubic bezier to the path
                        plot.cubicBezier(path, p1, p2, p3);
                    };

                    var approximateLowerSegment = function (path, startT, endT) {
                        var p1 = wasserburg.lowerEnvelope(startT).plus(
                            wasserburg.prime(startT).times(3 / (endT - startT)))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p2 = wasserburg.lowerEnvelope(endT).minus(
                            wasserburg.prime(endT).times(3 / (endT - startT)))
                            .scaleBy(plot.xAxisScale, plot.yAxisScale);
                        var p3 = wasserburg.lowerEnvelope(endT).scaleBy(plot.xAxisScale, plot.yAxisScale);

                        // append a cubic bezier to the path
                        plot.cubicBezier(path, p1, p2, p3);
                    };

                    var upperMinT = Math.min(
                        newtonMethod(wasserburg.upperEnvelope.x, minX),
                        newtonMethod(wasserburg.upperEnvelope.y, minY)
                    );

                    var upperMaxT = Math.min(
                        newtonMethod(wasserburg.upperEnvelope.x, maxX),
                        newtonMethod(wasserburg.upperEnvelope.y, maxY)
                    );


                    // initialize path
                    var path = [];
                    moveTo(path, wasserburg.upperEnvelope(upperMinT).scaleBy(plot.xAxisScale, plot.yAxisScale));

                    // determine the step size using the number of pieces
                    var pieces = 30;
                    var stepSize = (upperMaxT - upperMinT) / pieces;

                    // build the pieces
                    for (var i = 0; i < pieces; i++) {
                        approximateUpperSegment(path, upperMinT + stepSize * i, upperMinT + stepSize * (i + 1));
                    }

                    var lowerMinT = Math.min(
                        newtonMethod(wasserburg.lowerEnvelope.x, minX),
                        newtonMethod(wasserburg.lowerEnvelope.y, minY)
                    );

                    var lowerMaxT = Math.min(
                        newtonMethod(wasserburg.lowerEnvelope.x, maxX),
                        newtonMethod(wasserburg.lowerEnvelope.y, maxY)
                    );

                    lineTo(path, wasserburg.lowerEnvelope(lowerMaxT).scaleBy(plot.xAxisScale, plot.yAxisScale));

                    stepSize = (lowerMaxT - lowerMinT) / pieces;

                    // build the pieces
                    for (i = 0; i < pieces; i++) {
                        approximateLowerSegment(path, lowerMaxT - stepSize * i, lowerMaxT - stepSize * (i + 1));
                    }

                    close(path);

                    return path.join("");
                })
                .attr("fill", plot.getOption(PlotOption.CONCORDIA_ENVELOPE_FILL));
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