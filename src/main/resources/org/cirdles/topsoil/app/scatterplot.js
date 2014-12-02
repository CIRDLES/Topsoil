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
            .addSetting(Y_MIN, 0);

    chart.draw = function (data) {
        if (data.length > 0) {
            chart.settings[X_MIN] = d3.min(data, function (d) {
                return d.x;
            });
            chart.settings[Y_MIN] = d3.min(data, function (d) {
                return d.y;
            });
            chart.settings[X_MAX] = d3.max(data, function (d) {
                return d.x;
            });
            chart.settings[Y_MAX] = d3.max(data, function (d) {
                return d.y;
            });
            
            chart.settings.apply();
        }

        // a mathematical construct
        chart.x = d3.scale.linear()
                .domain([chart.settings[X_MIN], chart.settings[X_MAX]])
                .range([0, chart.width]);

        chart.y = d3.scale.linear()
                .domain([chart.settings[Y_MIN], chart.settings[Y_MAX]])
                .range([chart.height, 0]);

        chart.update(data);
    };

    chart.update = function (data) {
        chart.x.domain([chart.settings[X_MIN], chart.settings[X_MAX]]);
        chart.y.domain([chart.settings[Y_MIN], chart.settings[Y_MAX]]);

        // what actually makes the axis
        var xAxis = d3.svg.axis()
                .scale(chart.x)
                .orient("bottom");

        var yAxis = d3.svg.axis()
                .scale(chart.y)
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
                    return chart.x(d.x);
                })
                .attr("cy", function (d) {
                    return chart.y(d.y);
                });

        // remove unused points
        points.exit().remove();

        // add pan/zoom
        var zoom = d3.behavior.zoom()
                .x(chart.x)
                .y(chart.y)
                .on("zoom", function () {
                    alert("\nZOOM!!!\n");

                    chart.settings[X_MIN] = zoom.x().domain()[0];
                    chart.settings[Y_MIN] = zoom.y().domain()[0];
                    chart.settings[X_MAX] = zoom.x().domain()[1];
                    chart.settings[Y_MAX] = zoom.y().domain()[1];
                    
                    chart.settings.apply();
                });

        chart.area.clipped.call(zoom);
    };
}());