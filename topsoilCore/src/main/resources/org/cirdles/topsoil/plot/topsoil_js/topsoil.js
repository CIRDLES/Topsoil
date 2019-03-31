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
            var entry = {};
            plot.dataKeys.forEach(function(key) {
                if (data.get(index).get(key) != null) {
                    entry[key] = data.get(index).get(key);
                }
            });
            topsoil.data.push(entry);
        }
        plot.setData(topsoil.data);
    };

    topsoil.setProperties = function (properties) {
        plot.properties = {};

        plot.propertiesKeys.forEach(function (key) {
            plot.properties[key] = properties.get(key);
        });

        plot.update(ts.data);
    };

    topsoil.updateProperty = function(key, value) {
        plot.properties[key] = value;

        plot.update(ts.data)
    };

    /*
     * PLOT
     */

    // an acknowledged (seemingly arbitrary) fudge factor for the window dimensions
    // without this (which has been minimized!) scrollbars show (at least on OS X)
    var magicNumber = 3.0000375;

    plot.margin = {top: 110, right: 75, bottom: 75, left: 75};
    plot.width = window.innerWidth - magicNumber - plot.margin.left - plot.margin.right;
    plot.height = window.innerHeight - magicNumber - plot.margin.top - plot.margin.bottom;

    // somewhat confusing locally, but this element should be considered
    // to be the plot externally
    var svg = d3.select("body").append("svg")
        .attr("id", "plot")
        .attr("width", plot.width + plot.margin.left + plot.margin.right)
        .attr("height", plot.height + plot.margin.top + plot.margin.bottom);

    // create a new coordinate space that accounts for the margins
    plot.area = svg
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
            .attr("fill", "white");

    plot.area.append("defs")
            .append("clipPath")
            .attr("id", "clipBox")
            .append("use")
            .attr("xlink:xlink:href", "#" + plot.plotArea.attr("id"));

    plot.plotBorder = plot.area.append("rect")
        .attr("id", "plotBorder")
        .attr("width", plot.width)
        .attr("height", plot.height)
        .attr("fill", "none")
        .attr("stroke", "black")
        .attr("stroke-width", "2px");

    topsoil.resize = function () {
        if (plot.initialized) {

            plot.width = window.innerWidth - magicNumber - plot.margin.left - plot.margin.right;
            plot.height = window.innerHeight - magicNumber - plot.margin.top - plot.margin.bottom;

            svg
                .attr("width", plot.width + plot.margin.left + plot.margin.right)
                .attr("height", plot.height + plot.margin.top + plot.margin.bottom);

            plot.plotArea
                .attr("width", plot.width)
                .attr("height", plot.height);

            plot.plotBorder
                .attr("width", plot.width)
                .attr("height", plot.height);

            plot.removeAxes();
            plot.initialize(ts.data);
        }
    };

    // PROPERTIES
    plot.getProperty = function (key) {
        return plot.properties[key];
    };

})();