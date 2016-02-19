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

    plot.dataKeys = ['x', 'y'];
    plot.propertiesKeys = ['Title', 'Point Fill Color'];

    plot.draw = function (data) {
        // defaults if no data is provided
        var xMin = 0;
        var xMax = 1;
        var yMin = 0;
        var yMax = 1;

        // set default bounds relative to data
        if (data.length > 0) {
            var dataXMin = d3.min(data, function (d) { return d.x; });
            var dataYMin = d3.min(data, function (d) { return d.y; });
            var dataXMax = d3.max(data, function (d) { return d.x; });
            var dataYMax = d3.max(data, function (d) { return d.y; });

            var xRange = dataXMax - dataXMin;
            var yRange = dataYMax - dataYMin;

            xMin = dataXMin - 0.05 * xRange;
            yMin = dataYMin - 0.05 * yRange;
            xMax = dataXMax + 0.05 * xRange;
            yMax = dataYMax + 0.05 * yRange;
        }

        plot.area.append("text")
                .attr("class", "title text")
                .attr("font-family", "sans-serif")
                .attr("x", plot.width / 2)
                .attr("y", -50);

        // a mathematical construct
        plot.x = d3.scale.linear()
                .domain([xMin, xMax])
                .range([0, plot.width]);

        plot.y = d3.scale.linear()
                .domain([yMin, yMax])
                .range([plot.height, 0]);

        plot.update(data);
    };

    plot.update = function (data) {
        d3.select(".title.text").text(plot.getProperty("Title"));

        // what actually makes the axis
        var xAxis = d3.svg.axis()
                .scale(plot.x)
                .orient("bottom");

        var yAxis = d3.svg.axis()
                .scale(plot.y)
                .orient("left");

        plot.area.selectAll(".axis").remove();

        plot.area.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + plot.height + ")")
                .call(xAxis);

        plot.area.append("g")
                .attr("class", "y axis")
                .call(yAxis);

        // axis styling
        plot.area.selectAll(".axis text")
                .attr("font-family", "sans-serif")
                .attr("font-size", "10px");

        plot.area.selectAll(".axis path, .axis line")
                .attr("fill", "none")
                .attr("stroke", "black")
                .attr("shape-rendering", "geometricPrecision"); // see SVG docs

        // the data join (http://bost.ocks.org/mike/join/)
        var points = plot.area.clipped
                .selectAll(".point")
                .data(data);

        // initialize new points
        points.enter()
                .append("circle")
                .attr("class", "point")
                .attr("r", 3);

        // update all points
        points
                .attr("fill", plot.getProperty("Point Fill Color"))
                .attr("cx", function (d) {
                    return plot.x(d.x);
                })
                .attr("cy", function (d) {
                    return plot.y(d.y);
                });

        // add pan/zoom
        var zoom = d3.behavior.zoom()
                .x(plot.x)
                .y(plot.y)
                .scaleExtent([0.5, 2.5])
                .on("zoom", function () {
                    plot.x.domain([zoom.x().domain()[0], zoom.x().domain()[1]]);
                    plot.y.domain([zoom.y().domain()[0], zoom.y().domain()[1]]);

                    plot.update(data);
                });

        plot.area.clipped.call(zoom);
    };
}());