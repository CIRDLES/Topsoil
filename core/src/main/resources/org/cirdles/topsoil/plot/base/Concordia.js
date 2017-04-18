/**
 * Created by Emily on 2/24/17.
 */

plot.drawIsotopeFeatures = function() {
    plot.drawConcordia();
};

plot.updateIsotopeFeatures = function() {
    plot.updateConcordia();
};

plot.exitIsotopeFeatures = function() {
     plot.concordiaTicks.exit().remove();
     plot.tickLabels.exit().remove();
};

plot.drawConcordia = function() {

    // initialize the concordia envelope
    plot.area.clipped.append("path")
        .attr("class", "uncertaintyEnvelope")
        .attr("fill", "lightgray")
        .attr("stroke", "none")
        .attr("shape-rendering", "geometricPrecision");

    // initialize the concordia
    plot.area.clipped.append("path")
        .attr("class", "concordia")
        .attr("fill", "none")
        .attr("stroke", "blue")
        //.attr("stroke-width", "Uncertainty")
        .attr("stroke-width", 2)
        .attr("shape-rendering", "geometricPrecision");
};

plot.updateConcordia = function() {
    initializeWetherill({
        LAMBDA_235: 9.8485e-10,
        LAMBDA_238: 1.55125e-10
    });

    var x = plot.x;
    var y = plot.y;

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
        newtonMethod(wetherill.x, x.domain()[0]),
        newtonMethod(wetherill.y, y.domain()[0]));

    var maxT = Math.min(
        newtonMethod(wetherill.x, x.domain()[1]),
        newtonMethod(wetherill.y, y.domain()[1]));

    // build the concordia line
    plot.area.clipped.select(".concordia")
        .attr("d", function () {
            var approximateSegment = function (path, minT, maxT) {
                var p1 = wetherill(minT).plus(
                    wetherill.prime(minT).times((maxT - minT) / 3))
                    .scaleBy(x, y);
                var p2 = wetherill(maxT).minus(
                    wetherill.prime(maxT).times((maxT - minT) / 3))
                    .scaleBy(x, y);
                var p3 = wetherill(maxT).scaleBy(x, y);

                // append a cubic bezier to the path
                plot.cubicBezier(path, p1, p2, p3);
            };

            // initialize path
            var path = [];
            moveTo(path, wetherill(minT).scaleBy(x, y));

            // determine the step size using the number of pieces
            var pieces = 30;
            var stepSize = (maxT - minT) / pieces;

            // build the pieces
            for (var i = 0; i < pieces; i++) {
                approximateSegment(path, minT + stepSize * i, minT + stepSize * (i + 1));
            }

            return path.join("");
        });

    plot.area.clipped.select(".uncertaintyEnvelope")
        .attr("d", function () {
            var approximateUpperSegment = function (path, minT, maxT) {
                var p1 = wetherill.upperEnvelope(minT).plus(
                    wetherill.prime(minT).times((maxT - minT) / 3))
                    .scaleBy(x, y);
                var p2 = wetherill.upperEnvelope(maxT).minus(
                    wetherill.prime(maxT).times((maxT - minT) / 3))
                    .scaleBy(x, y);
                var p3 = wetherill.upperEnvelope(maxT).scaleBy(x, y);

                // append a cubic bezier to the path
                plot.cubicBezier(path, p1, p2, p3);
            };

            var approximateLowerSegment = function (path, minT, maxT) {
                var p1 = wetherill.lowerEnvelope(minT).plus(
                    wetherill.prime(minT).times((maxT - minT) / 3))
                    .scaleBy(x, y);
                var p2 = wetherill.lowerEnvelope(maxT).minus(
                    wetherill.prime(maxT).times((maxT - minT) / 3))
                    .scaleBy(x, y);
                var p3 = wetherill.lowerEnvelope(maxT).scaleBy(x, y);

                // append a cubic bezier to the path
                plot.cubicBezier(path, p1, p2, p3);
            };

            var minT = Math.max(
                newtonMethod(wetherill.upperEnvelope.x, x.domain()[0]),
                newtonMethod(wetherill.upperEnvelope.y, y.domain()[0]));

            var maxT = Math.min(
                newtonMethod(wetherill.upperEnvelope.x, x.domain()[1]),
                newtonMethod(wetherill.upperEnvelope.y, y.domain()[1]));

            // initialize path
            var path = [];
            moveTo(path, wetherill.upperEnvelope(minT).scaleBy(x, y));

            // determine the step size using the number of pieces
            var pieces = 30;
            var stepSize = (maxT - minT) / pieces;

            // build the pieces
            for (var i = 0; i < pieces; i++) {
                approximateUpperSegment(path, minT + stepSize * i, minT + stepSize * (i + 1));
            }

            lineTo(path, [x.range()[1], y.range()[1]]);

            minT = Math.max(
                newtonMethod(wetherill.lowerEnvelope.x, x.domain()[0]),
                newtonMethod(wetherill.lowerEnvelope.y, y.domain()[0]));

            maxT = Math.min(
                newtonMethod(wetherill.lowerEnvelope.x, x.domain()[1]),
                newtonMethod(wetherill.lowerEnvelope.y, y.domain()[1]));

            lineTo(path, wetherill.lowerEnvelope(maxT).scaleBy(x, y));

            stepSize = (maxT - minT) / pieces;

            // build the pieces
            for (var i = 0; i < pieces; i++) {
                approximateLowerSegment(path, maxT - stepSize * i, maxT - stepSize * (i + 1));
            }

            lineTo(path, [x.range()[0], y.range()[0]]);
            close(path);

            return path.join("");
        });

    plot.t.domain([minT, maxT]);

    var concordiaTicks;
    (concordiaTicks = plot.concordiaTicks = plot.area.clipped.selectAll(".concordiaTicks")
        .data(plot.t.ticks()))
        .enter()
        .append("circle")
        .attr("class", "concordiaTicks")
        .attr("r", 5);

    concordiaTicks
        .attr("cx", function (t) { return x(wetherill.x(t)); })
        .attr("cy", function (t) { return y(wetherill.y(t)); });

    var tickLabels;
    (tickLabels = plot.tickLabels = plot.area.clipped.selectAll(".tickLabel")
        .data(plot.t.ticks()))
        .enter()
        .append("text")
        .attr("font-family", "sans-serif")
        .attr("class", "tickLabel");

    tickLabels
        .attr("x", function (t) { return x(wetherill.x(t)) + 12; })
        .attr("y", function (t) { return y(wetherill.y(t)) + 5; })
        .text(function (t) { return t / 1000000; });

};

plot.setConcordiaVisibility = function (isVisible) {
    d3.selectAll(".concordiaTick")
        .style("opacity", isVisible ? 1 : 0);
    d3.selectAll(".tickLabel")
        .style("opacity", isVisible ? 1 : 0);
    d3.selectAll(".uncertaintyEnvelope")
        .style("opacity", isVisible ? 1 : 0);
    d3.selectAll(".concordia")
        .style("opacity", isVisible ? 1 : 0);
};
