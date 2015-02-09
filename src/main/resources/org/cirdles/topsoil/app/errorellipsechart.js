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


    chart.settings
            .addSetting(X_MAX, 100)
            .addSetting(X_MIN, 0)
            .addSetting(Y_MAX, 100)
            .addSetting(Y_MIN, 0)
            .addSetting("X Label", "207Pb*/235U")
            .addSetting("Y Label", "206Pb*/238U")
            .addSetting("Ellipse Fill", "red");

    chart.draw = function (data) {
        var x = chart.x = d3.scale.linear()
                .range([0, chart.width]);

        var y = chart.y = d3.scale.linear()
                .range([chart.height, 0]);

        if (data.length > 0) {
            chart.settings.transaction(function (t) {
                t.set(X_MIN, d3.min(data, function (d) {
                    return d.x - d.sigma_x;
                }));
                t.set(Y_MIN, d3.min(data, function (d) {
                    return d.y - d.sigma_y;
                }));
                t.set(X_MAX, d3.max(data, function (d) {
                    return d.x + d.sigma_x;
                }));
                t.set(Y_MAX, d3.max(data, function (d) {
                    return d.y + d.sigma_y;
                }));
            });
        }

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

        x.domain([chart.settings[X_MIN], chart.settings[X_MAX]]);
        y.domain([chart.settings[Y_MIN], chart.settings[Y_MAX]]);

        d3.select(".x.axis .label").text(chart.settings["X Label"]);
        d3.select(".y.axis .label").text(chart.settings["Y Label"]);

        var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom");

        var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left");

        var ellipses;
        (ellipses = chart.area.clipped.selectAll(".ellipse")
                .data(data)
                .attr("fill", chart.settings["Ellipse Fill"]))
                .enter().append("path")
                .attr("class", "ellipse")
                .attr("fill-opacity", 0.3)
                .attr("stroke", "black");

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

        var cubicBezier = function (path, p1, p2, p3) {
            path.push(
                    "C", p1[0], ",", p1[1],
                    ",", p2[0], ",", p2[1],
                    ",", p3[0], ",", p3[1]);
        };

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

                    var minT = Math.max(
                            newtonMethod(wetherill.x, x.domain()[0]),
                            newtonMethod(wetherill.y, y.domain()[0]));

                    var maxT = Math.min(
                            newtonMethod(wetherill.x, x.domain()[1]),
                            newtonMethod(wetherill.y, y.domain()[1]));

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

            return ellipsePath(numeric.dot(controlPointsBase, r));
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

        var zoom = d3.behavior.zoom()
                .x(x)
                .y(y)
                .scaleExtent([.1, 25])
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

                    chart.settings.transaction(function (t) {
                        t.set(X_MIN, zoom.x().domain()[0]);
                        t.set(Y_MIN, zoom.y().domain()[0]);
                        t.set(X_MAX, zoom.x().domain()[1]);
                        t.set(Y_MAX, zoom.y().domain()[1]);
                    });

                    chart.update(data);
                });
        chart.area.clipped.call(zoom);
    };
})();