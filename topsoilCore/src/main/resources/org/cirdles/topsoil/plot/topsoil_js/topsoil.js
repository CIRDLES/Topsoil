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

    topsoil.emptyArray = function () {
        return [];
    };

    topsoil.getData = function () {
        return topsoil.data;
    };

    topsoil.setData = function (data) {
        topsoil.data = [];
        for (var index = 0; index < data.size(); index++) {
            var entry = {};
            var row = data.get(index);
            if (row != null) {
                plot.dataKeys.forEach(function (key) {
                    var value = row.get(key);
                    if (value != null) {
                        entry[key] = value;
                    }
                });
                topsoil.data.push(entry);
            }
        }
        plot.setData(topsoil.data);
    };

    topsoil.setProperties = function (properties) {
        plot.properties = {};

        var key;
        for (var p in Property) {
            key = Property[p];
            plot.properties[key] = properties.get(key);
        }

        plot.update(topsoil.data);
    };

    topsoil.updateProperty = function(key, value) {
        plot.properties[key] = value;

        plot.update(topsoil.data)
    };

    /*
     * PLOT
     */

    plot.margin = {top: 110, right: 75, bottom: 75, left: 75};
    plot.outerWidth = (plot.margin.left + plot.margin.right);
    plot.outerHeight = (plot.margin.top + plot.margin.bottom);
    plot.innerWidth = 0.0;
    plot.innerHeight = 0.0;

    // somewhat confusing locally, but this element should be considered
    // to be the plot externally
    var svgContainer = d3.select("body").append("div")
        .attr("id", "svgContainer");
    var svg = svgContainer.append("svg")
        .attr("id", "plot")
        .attr("width", plot.outerWidth)
        .attr("height", plot.outerHeight);

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
            .attr("width", plot.innerWidth)
            .attr("height", plot.innerHeight)
            .attr("fill", "white");

    plot.area.append("defs")
            .append("clipPath")
            .attr("id", "clipBox")
            .append("use")
            .attr("xlink:href", "#" + plot.plotArea.attr("id"));

    plot.plotBorder = plot.area.append("rect")
        .attr("id", "plotBorder")
        .attr("width", plot.innerWidth)
        .attr("height", plot.innerHeight)
        .attr("fill", "none")
        .attr("stroke", "black")
        .attr("stroke-width", "2px");

    topsoil.resize = function (width, height) {
        if (plot.initialized) {

            plot.outerWidth = width;
            plot.outerHeight = height;
            plot.innerWidth = plot.outerWidth - plot.margin.left - plot.margin.right;
            plot.innerHeight = plot.outerHeight - plot.margin.top - plot.margin.bottom;

            svg
                .attr("width", plot.outerWidth)
                .attr("height", plot.outerHeight);

            plot.plotArea
                .attr("width", plot.innerWidth)
                .attr("height", plot.innerHeight);

            plot.plotBorder
                .attr("width", plot.innerWidth)
                .attr("height", plot.innerHeight);

            plot.removeAxes();
            plot.initialize(ts.data);
        }
    };

    // PROPERTIES
    plot.getProperty = function (key) {
        return plot.properties[key];
    };

})();