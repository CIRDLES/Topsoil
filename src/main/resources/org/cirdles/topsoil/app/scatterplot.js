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

chart.draw = function (data, settings) {
    // a mathematical construct
    var x = d3.scale.linear()
            .domain([0, 100])
            .range([0, chart.width]);

    var y = d3.scale.linear()
            .domain([0, 100])
            .range([chart.height, 0]);

    // what actually makes the axis
    var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom");

    var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left");

    // actually create the axis
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

    // plot the data points
    chart.area.selectAll(".point")
            .data(data)
            .enter().append("circle")
            .attr("class", "point")
            .attr("fill", "steelblue")
            .attr("cx", function (d) {
                return x(d.x);
            })
            .attr("cy", function (d) {
                return y(d.y);
            })
            .attr("r", 3);
};