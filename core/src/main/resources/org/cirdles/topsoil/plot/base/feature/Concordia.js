/**
 * @author Emily Coleman
 */

if (plot.concordiaVisible == null) {
    plot.concordiaVisible = false;
}

plot.concordiaGroup = plot.area.clipped.insert("g", ".dataGroup")
    .attr("class", ".concordiaGroup");

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
        .attr("fill", "lightgray")
        .attr("stroke", "none")
        .attr("shape-rendering", "geometricPrecision");

    // initialize the concordia
    plot.concordiaGroup.append("path")
        .attr("class", "concordia")
        .attr("fill", "none")
        .attr("stroke", "blue")
        .attr("stroke-width", 2)
        .attr("shape-rendering", "geometricPrecision");

    plot.concordiaVisible = true;
    plot.updateConcordia();
};

plot.updateConcordia = function() {
    if (plot.concordiaVisible) {
        initializeWetherill({
            LAMBDA_235: 9.8485e-10,
            LAMBDA_238: 1.55125e-10
        });

        // utilities for generating path data elements
        var moveTo = function (path, p) {
            path.push("M", p[0], ",", p[1]);
        };

        var lineTo = function (path, p) {
            path.push("L", p[0], ",", p[1]);
        };

        var close = function (path) {
            path.push("Z");
        };

        var minT = Math.max(
            newtonMethod(wetherill.x, plot.xAxisScale.domain()[0]),
            newtonMethod(wetherill.y, plot.yAxisScale.domain()[0]));

        var maxT = Math.min(
            newtonMethod(wetherill.x, plot.xAxisScale.domain()[1]),
            newtonMethod(wetherill.y, plot.yAxisScale.domain()[1]));

        // build the concordia line
        plot.concordiaGroup.select(".concordia")
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
            });

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
            });

        plot.t.domain([minT, maxT]);

        var concordiaTicks;
        (concordiaTicks = plot.concordiaTicks = plot.concordiaGroup.selectAll(".concordiaTicks")
            .data(plot.t.ticks()))
            .enter()
            .append("circle")
            .attr("class", "concordiaTicks")
            .attr("r", 5);

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
