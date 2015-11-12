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

(function () {
    "use strict";

    chart.initialProperties = {
        "Title": "Uncertainty Ellipse Chart",
        "X Axis": "207Pb*/235U",
        "Y Axis": "206Pb*/238U",
        "Uncertainty": 2.0,
        "Ellipse Fill Color": "red"
    };

    chart.draw = function (data) {
        var x = chart.x = d3.scale.linear()
                .range([0, chart.width]);

        var y = chart.y = d3.scale.linear()
                .range([chart.height, 0]);

        chart.t = d3.scale.linear();

        var xMin = 0;
        var xMax = 1;
        var yMin = 0;
        var yMax = 1;

        if (data.length > 0){
            var dataXMin = d3.min(data, function (d) {
                return d.x - d.sigma_x;
            });

            var dataYMin = d3.min(data, function (d) {
                return d.y - d.sigma_y;
            });

            var dataXMax = d3.max(data, function (d) {
                return d.x + d.sigma_x;
            });

            var dataYMax = d3.max(data, function (d) {
                return d.y + d.sigma_y;
            });

            var xRange = dataXMax - dataXMin;
            var yRange = dataYMax - dataYMin;

            xMin = dataXMin - 0.05 * xRange;
            yMin =  dataYMin - 0.05 * yRange;
            xMax = dataXMax + 0.05 * xRange;
            yMax = dataYMax + 0.05 * yRange;
        }

        x.domain([xMin, xMax]);
        y.domain([yMin, yMax]);

        // initialize the concordia envelope
        chart.area.clipped.append("path")
                .attr("class", "uncertaintyEnvelope")
                .attr("fill", "lightgray")
                .attr("stroke", "none")
                .attr("shape-rendering", "geometricPrecision");

        // initialize the concordia
        chart.area.clipped.append("path")
                .attr("class", "concordia")
                .attr("fill", "none")
                .attr("stroke", "blue")
                .attr("stroke-width", 2)
                .attr("shape-rendering", "geometricPrecision");

        chart.area.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + chart.height + ")")
                .append("text")
                .attr("class", "label")
                .attr("x", chart.width)
                .attr("y", -6);

        chart.area.append("g")
                .attr("class", "y axis")
                .append("text")
                .attr("class", "label")
                .attr("transform", "rotate(-90)")
                .attr("y", 6)
                .attr("dy", ".71em");

        chart.update(data);
    };

    chart.update = function (data) {
        var x = chart.x;
        var y = chart.y;

        d3.select(".x.axis .label").text(chart.getProperty("X Axis"));
        d3.select(".y.axis .label").text(chart.getProperty("Y Axis"));

        var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom");

        var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left");

        var ellipses;
        (ellipses = chart.area.clipped.selectAll(".ellipse")
                .data(data))
                .enter().append("path")
                .attr("class", "ellipse")
                .attr("fill-opacity", 0.3)
                .attr("stroke", "black");

        ellipses.attr("fill", chart.getProperty("Ellipse Fill Color"));

        var dots;
        (dots = chart.area.clipped.selectAll(".dot")
                .data(data))
                .enter().append("circle")
                .attr("class", "dot")
                .attr("r", 1.5);

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

        var cubicBezier = function (path, p1, p2, p3) {
            path.push(
                    "C", p1[0], ",", p1[1],
                    ",", p2[0], ",", p2[1],
                    ",", p3[0], ",", p3[1]);
        };

        var minT = Math.max(
                newtonMethod(wetherill.x, x.domain()[0]),
                newtonMethod(wetherill.y, y.domain()[0]));

        var maxT = Math.min(
                newtonMethod(wetherill.x, x.domain()[1]),
                newtonMethod(wetherill.y, y.domain()[1]));

        // build the concordia line
        chart.area.clipped.select(".concordia")
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
                        cubicBezier(path, p1, p2, p3);
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

        chart.area.clipped.select(".uncertaintyEnvelope")
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
                        cubicBezier(path, p1, p2, p3);
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
                        cubicBezier(path, p1, p2, p3);
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

                    var minT = Math.max(
                            newtonMethod(wetherill.lowerEnvelope.x, x.domain()[0]),
                            newtonMethod(wetherill.lowerEnvelope.y, y.domain()[0]));

                    var maxT = Math.min(
                            newtonMethod(wetherill.lowerEnvelope.x, x.domain()[1]),
                            newtonMethod(wetherill.lowerEnvelope.y, y.domain()[1]));

                    lineTo(path, wetherill.lowerEnvelope(maxT).scaleBy(x, y));

                    var stepSize = (maxT - minT) / pieces;

                    // build the pieces
                    for (var i = 0; i < pieces; i++) {
                        approximateLowerSegment(path, maxT - stepSize * i, maxT - stepSize * (i + 1));
                    }

                    lineTo(path, [x.range()[0], y.range()[0]]);
                    close(path);

                    return path.join("");
                });

        chart.t.domain([minT, maxT]);

        var ticks;
        (ticks = chart.area.clipped.selectAll(".tick")
                .data(chart.t.ticks()))
                .attr("cx", function (t) { return x(wetherill.x(t)); })
                .attr("cy", function (t) { return y(wetherill.y(t)); })
                .enter()
                .append("circle")
                .attr("class", "tick")
                .attr("r", 5);

        var tickLabels;
        (tickLabels = chart.area.clipped.selectAll(".tickLabel")
                .data(chart.t.ticks()))
                .attr("x", function (t) { return x(wetherill.x(t)) + 12; })
                .attr("y", function (t) { return y(wetherill.y(t)) + 5; })
                .text(function (t) { return t / 1000000; })
                .enter()
                .append("text")
                .attr("font-family", "sans-serif")
                .attr("class", "tickLabel");

        // update the ellipses
        ellipses.attr("d", function (d) {
            var k = 4 / 3 * (Math.sqrt(2) - 1);
            var r = [
                [d.sigma_x, d.rho * d.sigma_y],
                [0, d.sigma_y * Math.sqrt(1 - d.rho * d.rho)]
            ];
            var controlPointsBase = [
                [1, 0],
                [1, k],
                [k, 1],
                [0, 1],
                [-k, 1],
                [-1, k],
                [-1, 0],
                [-1, -k],
                [-k, -1],
                [0, -1],
                [k, -1],
                [1, -k],
                [1, 0]
            ];

            var ellipsePath = d3.svg.line()
                    .x(function (datum) {
                        return x(datum[0] + d.x);
                    })
                    .y(function (datum) {
                        return y(datum[1] + d.y);
                    })
                    .interpolate(function (points) {
                        var i = 1, path = [points[0][0], ",", points[0][1]];
                        while (i + 3 <= points.length) {
                            cubicBezier(path, points[i++], points[i++], points[i++]);
                        }
                        return path.join("");
                    });

            return ellipsePath(numeric.mul(
                chart.getProperty("Uncertainty"),
                numeric.dot(controlPointsBase, r)));
        });

        // update the center points
        dots
                .attr("cx", function (d) {
                    return x(d.x);
                })
                .attr("cy", function (d) {
                    return y(d.y);
                });

        // retick the axes
        chart.area.selectAll(".x.axis")
                .call(xAxis);

        chart.area.selectAll(".y.axis")
                .call(yAxis);

        // axis styling
        chart.area.selectAll(".axis text")
                .attr("font-family", "sans-serif")
                .attr("font-size", "10px");

        chart.area.selectAll(".axis path, .axis line")
                .attr("fill", "none")
                .attr("stroke", "black")
                .attr("shape-rendering", "geometricPrecision"); // see SVG docs

        // exit
        ellipses.exit().remove();
        dots.exit().remove();
        ticks.exit().remove();
        tickLabels.exit().remove();

        var zoom = d3.behavior.zoom()
                .x(x)
                .y(y)
                .scaleExtent([.5, 2.5])
                .on("zoom", function () {
                    var t = zoom.translate();
                    var tx = t[0];
                    var ty = t[1];

                    // keep the viewbox northeast of (0, 0)
                    if (x.domain()[0] < 0)
                        tx += x.range()[0] - x(0);
                    if (y.domain()[0] < 0)
                        ty += y.range()[0] - y(0);
                    zoom.translate([tx, ty]);

                    chart.x.domain([zoom.x().domain()[0], zoom.x().domain()[1]]);
                    chart.y.domain([zoom.y().domain()[0], zoom.y().domain()[1]]);

                    chart.update(data);
                });
        chart.area.clipped.call(zoom);
    };
})();
