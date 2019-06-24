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

        initializeWetherill({
            LAMBDA_235: plot.lambda.U235,
            LAMBDA_238: plot.lambda.U238
        });

        var minX = Math.max(0.0, plot.xAxisScale.domain()[0]),
            maxX = Math.min(93.0, plot.xAxisScale.domain()[1]),
            minY = Math.max(0.0, plot.yAxisScale.domain()[0]),
            maxY = Math.min(2.05, plot.yAxisScale.domain()[1]);

        var minT = Math.max(
            newtonMethod(wetherill.x, minX),
            newtonMethod(wetherill.y, minY));

        var maxT = Math.min(
            newtonMethod(wetherill.x, maxX),
            newtonMethod(wetherill.y, maxY));

        var minT1 = Math.max(Math.log1p(minX / plot.lambda.U235),
            Math.log(1 + minY) / plot.lambda.U238);
        var maxT1 = Math.min(Math.log1p(maxX / plot.lambda.U235),
            Math.log(1 + maxY) / plot.lambda.U238);


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
            .attr("stroke", plot.getOption(PlotOption.CONCORDIA_LINE_FILL))
            .attr("stroke-width", 2);

        if (plot.getOption(PlotOption.CONCORDIA_ENVELOPE)) {
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
                        newtonMethod(wetherill.upperEnvelope.x, minX),
                        newtonMethod(wetherill.upperEnvelope.y, minY));

                    var maxT = Math.min(
                        newtonMethod(wetherill.upperEnvelope.x, maxX),
                        newtonMethod(wetherill.upperEnvelope.y, maxY));

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

                    minT = Math.max(
                        newtonMethod(wetherill.lowerEnvelope.x, minX),
                        newtonMethod(wetherill.lowerEnvelope.y, minY));

                    maxT = Math.min(
                        newtonMethod(wetherill.lowerEnvelope.x, maxX),
                        newtonMethod(wetherill.lowerEnvelope.y, maxY));

                    lineTo(path, wetherill.lowerEnvelope(maxT).scaleBy(plot.xAxisScale, plot.yAxisScale));

                    stepSize = (maxT - minT) / pieces;

                    // build the pieces
                    for (i = 0; i < pieces; i++) {
                        approximateLowerSegment(path, maxT - stepSize * i, maxT - stepSize * (i + 1));
                    }

                    close(path);

                    return path.join("");
                })
                .attr("fill", plot.getOption(PlotOption.CONCORDIA_ENVELOPE_FILL));
        }

        plot.t.domain([minT, maxT]);

        var concordiaTicks = plot.concordiaGroup.selectAll(".concordiaTicks")
            .data(plot.t.ticks());
        concordiaTicks
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
        concordiaTicks
            .exit()
            .remove();

        var tickLabels = plot.concordiaGroup.selectAll(".tickLabel")
            .data(plot.t.ticks());
        tickLabels
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
        tickLabels
            .exit()
            .remove();

    }
};

plot.removeConcordia = function() {
    if (plot.concordiaVisible) {
        plot.concordiaGroup.remove();
        plot.concordiaVisible = false;
    }
};