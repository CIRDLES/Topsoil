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

// top level containers
var topsoil = ts = {data: []}, chart = {};

// adds a row of data
topsoil.addData = function (data) {
    var convertedData = {};
    
    // store the data in the 
    convertedData.x = data[0];
    convertedData.sigmaX = convertedData.sigma_x = convertedData.σx = data[1];
    convertedData.y = data[2];
    convertedData.sigmaY = convertedData.sigma_y = convertedData.σy = data[3];
    convertedData.rho = convertedData.ρ = data[4];
    
    ts.data.push(convertedData);
};

// clears all stored data
topsoil.clearData = function () {
    ts.data = [];
};

topsoil.showData = function () {
    chart.draw(ts.data, {});
};

// an acknoledged (seemingly arbitrary) fudge factor for the window dimensions
// without this (which has been minimized!) scrollbars show (at least on OS X)
var magicNumber = 3.0000375;

chart.margin = {top: 75, right: 75, bottom: 75, left: 75};
chart.width = window.innerWidth - magicNumber - chart.margin.left - chart.margin.right;
chart.height = window.innerHeight - magicNumber - chart.margin.top - chart.margin.bottom;

// set up for the chart
chart.area = d3.select("body")
        // create the svg element
        .append("svg")
        // somewhat confusing locally, but this element should be considered
        // to be the chart externally
        .attr("id", "chart")
        .attr("width", chart.width + chart.margin.left + chart.margin.right)
        .attr("height", chart.height + chart.margin.top + chart.margin.bottom)
        // create a new coordinate space that accounts for the margins
        .append("g")
        .attr("transform", "translate(" + chart.margin.left + "," + chart.margin.top + ")");