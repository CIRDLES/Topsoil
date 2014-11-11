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

//    chart.settings
//            .addSetting(X_MAX, 100)
//            .addSetting(X_MIN, 0)
//            .addSetting(Y_MAX, 100)
//            .addSetting(Y_MIN, 0);

    chart.draw = function (data) {
        // a mathematical construct
        var x = d3.scale.linear()
                .domain(d3.extent(data, function (d) { return d.x; }))
                .range([0, chart.width]);

        var y = d3.scale.linear()
                .domain(d3.extent(data, function (d) { return d.y; }))
                .range([chart.height, 0]);

        // what actually makes the axis
        var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom");

        var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left");

        chart.area.selectAll(".axis").remove();

        chart.area.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + chart.height + ")")
                .call(xAxis);

        chart.area.append("g")
                .attr("class", "y axis")
                .call(yAxis);

        // axis styling
        chart.area.selectAll(".axis text")
                .attr("font-family", "sans-serif")
                .attr("font-size", "10px");

        chart.area.selectAll(".axis path, .axis line")
                .attr("fill", "none")
                .attr("stroke", "black")
                .attr("shape-rendering", "geometricPrecision"); // see SVG docs

        // the data join (http://bost.ocks.org/mike/join/)
        var points = chart.area.clipped
                .selectAll(".point")
                .data(data);

        // initialize new points
        points.enter()
                .append("circle")
                .attr("class", "point")
                .attr("fill", "steelblue")
                .attr("r", 3);

        // update all points
        points
                .attr("cx", function (d) {
                    return x(d.x);
                })
                .attr("cy", function (d) {
                    return y(d.y);
                });

        // remove unused points
        points.exit().remove();

        // add pan/zoom
        var zoom = d3.behavior.zoom()
                .x(x)
                .y(y)
                .scaleExtent([.1, 25])
                .on("zoom", function () {
                    chart.settings[X_MIN] = x.domain()[0];
                    chart.settings[Y_MIN] = y.domain()[0];
                    chart.settings[X_MAX] = x.domain()[1];
                    chart.settings[Y_MAX] = y.domain()[1];

                    chart.draw(ts.data);
                });

        chart.area.clipped.call(zoom);
    };
}());