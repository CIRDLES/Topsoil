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

    // top level containers
    window.topsoil = {data: []};
    window.plot = {dataKeys: [], properties: {}, propertiesKeys: []};

    // alias topsoil
    window.ts = topsoil;

    topsoil.setData = function (data) {
        topsoil.data = [];

        for (var index = 0; index < data.size(); index++) {
            var d = {};

            plot.dataKeys.forEach(function(key) {
                d[key] = data.get(index).get(key);
            });

            topsoil.data.push(d);
        }

        plot.draw(topsoil.data);
    };

    topsoil.setProperties = function (properties) {
        plot.properties = {};

        plot.propertiesKeys.forEach(function (key) {
            plot.properties[key] = properties.get(key);
        });

        plot.draw(ts.data);
    };

    /*
     * PLOT
     */

    // an acknoledged (seemingly arbitrary) fudge factor for the window dimensions
    // without this (which has been minimized!) scrollbars show (at least on OS X)
    var magicNumber = 3.0000375;

    plot.margin = {top: 75, right: 75, bottom: 75, left: 75};
    plot.width = window.innerWidth - magicNumber - plot.margin.left - plot.margin.right;
    plot.height = window.innerHeight - magicNumber - plot.margin.top - plot.margin.bottom;

    // set up for the plot
    plot.area = d3.select("body")
            // create the svg element
            .append("svg")
            // somewhat confusing locally, but this element should be considered
            // to be the plot externally
            .attr("id", "plot")
            .attr("width", plot.width + plot.margin.left + plot.margin.right)
            .attr("height", plot.height + plot.margin.top + plot.margin.bottom)
            // create a new coordinate space that accounts for the margins
            .append("g")
            .attr("transform", "translate(" + plot.margin.left + "," + plot.margin.top + ")");

    // create a clip path and backing for the plot area
    // this is a big performance booster
    // use this!
    plot.area.clipped = plot.area.append("g")
            .attr("clip-path", "url(#clipBox)");

    // the visible (white) backing is necessary for mouse events
    plot.plotArea = plot.area.clipped.append("rect")
            .attr("id", "plotArea")
            .attr("width", plot.width)
            .attr("height", plot.height)
            .attr("fill", "white")
            .attr("stroke", "black")
            .attr("stroke-width", "2px");

    plot.area.append("defs")
            .append("clipPath")
            .attr("id", "clipBox")
            .append("use")
            .attr("xlink:href", "#" + plot.plotArea.attr("id"));

    // PROPERTIES
    plot.getProperty = function (key) {
        return plot.properties[key];
    };

})();