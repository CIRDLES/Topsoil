/*
 * Copyright 2017 CIRDLES.
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

plot.dataKeys = ['x', 'sigma_x', 'y', 'sigma_y', 'rho', 'Selected'];
plot.propertiesKeys = [
    'Point Fill Color',
    'Ellipse Fill Color',
    'Title',
    'Uncertainty',
    'X Axis',
    'Y Axis',
    'Points',
    'Ellipses',
    'Concordia',
    'Isotope'];

plot.dataGroup = plot.area.clipped.append("g")
    .attr("class", "dataGroup");

plot.draw = function (data) {

    //initialize plot.currentIsotope
    if(plot.currentIsotope == null) {
        plot.currentIsotope = plot.getProperty('Isotope');
    }

    if (plot.concordiaShowing == null) {
        plot.concordiaShowing = plot.getProperty('Concordia');
    }

    // defaults if no data is provided
    var xMin = plot.xMin = 0;
    var xMax = plot.xMax = 1;
    var yMin = plot.yMin = 0;
    var yMax = plot.yMax = 1;

    //create title
    plot.area.append("text")
        .attr("class", "titleText")
        .attr("font-family", "sans-serif")
        .attr("font-size", "20px")
        .attr("x", plot.width / 2)
        .attr("y", -50);

    //create x axis label
    plot.area.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + plot.height + ")")
        .append("text")
        .attr("class", "label")
        .style("font-size", "16px")
        .attr("x", plot.width / 2)
        .attr("y", 35);

    //create y axis label
    plot.area.append("g")
        .attr("class", "y axis")
        .append("text")
        .attr("class", "label")
        .attr("transform", "rotate(-90)")
        .style("font-size", "16px")
        .attr("x", -plot.height / 2)
        .attr("y", -50)
        .attr("dy", ".1em");

    //find the extent of the points
    if (data.length > 0) {
        var dataXMin = d3.min(data, function (d) {
            return d.x - (d.sigma_x * plot.getProperty("Uncertainty"));
        });
        var dataYMin = d3.min(data, function (d) {
            return d.y - (d.sigma_y * plot.getProperty("Uncertainty"));
        });
        var dataXMax = d3.max(data, function (d) {
            return d.x + (d.sigma_x * plot.getProperty("Uncertainty"));
        });
        var dataYMax = d3.max(data, function (d) {
            return d.y + (d.sigma_y * plot.getProperty("Uncertainty"));
        });

        var xRange = dataXMax - dataXMin;
        var yRange = dataYMax - dataYMin;

        plot.xMin = dataXMin - 0.05 * xRange;
        plot.yMin =  dataYMin - 0.05 * yRange;
        plot.xMax = dataXMax + 0.05 * xRange;
        plot.yMax = dataYMax + 0.05 * yRange;
    }

    // a mathematical construct
    plot.x = d3.scale.linear()
        .domain([plot.xMin, plot.xMax])
        .range([0, plot.width]);

    plot.y = d3.scale.linear()
        .domain([plot.yMin, plot.yMax])
        .range([plot.height, 0]);

    plot.t = d3.scale.linear();

    //calculate constants used to draw ellipses
    plot.cacheData = plot.calcEllipses(data);

    // if(plot.getProperty('Isotope') != 'Generic') {
    //     plot.linkIsotopeFeatures("draw");
    // }

    plot.update(data);
};

plot.update = function (data) {
    //if the isotope type has changed, alert Java
    if (plot.currentIsotope != plot.getProperty('Isotope')) {
        plot.currentIsotope = plot.getProperty('Isotope');
    }

    if (plot.currentIsotope == 'Uranium Lead') {
        if (plot.concordiaIsShowing != plot.getProperty('Concordia')) {
            // topsoil.bridge.updateConcordia(plot.getProperty('Concordia'));
            if (plot.getProperty('Concordia')) {
                plot.drawConcordia();
            } else {
                plot.removeConcordia();
            }
        }
    } else if (plot.concordiaIsShowing == true) {
        plot.concordiaShowing = false;
        plot.removeConcordia();
    }

    //draw title and axis labels
    d3.select(".titleText")
        .text(plot.getProperty("Title"))
        .attr("x", (plot.width / 2) - (d3.select(".titleText").node().getBBox().width) / 2);

    d3.select(".x.axis .label")
        .text(plot.getProperty("X Axis"))
        .attr("x", (plot.width) - (d3.select(".x.axis .label").node().getBBox().width));
    d3.select(".y.axis .label")
        .text(plot.getProperty("Y Axis"))
        .attr("x",  -(d3.select(".y.axis .label").node().getBBox().width));

    //draw the axes
    var xAxis = d3.svg.axis()
        .scale(plot.x)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(plot.y)
        .orient("left");

    // re-tick the axes
    plot.area.selectAll(".x.axis")
        .call(xAxis);

    plot.area.selectAll(".y.axis")
        .call(yAxis);

    // axis styling
    plot.area.selectAll(".axis text")
        .attr("font-family", "sans-serif")
        .attr("font-size", "10px");

    plot.area.selectAll(".axis path, .axis line")
        .attr("fill", "none")
        .attr("stroke", "black")
        .attr("shape-rendering", "geometricPrecision"); // see SVG docs

    if (plot.concordiaIsShowing) {
        plot.updateConcordia();
    }

    plot.drawEllipses(plot.cacheData);

    // the data join (http://bost.ocks.org/mike/join/)
    var points = plot.dataGroup.selectAll(".point")
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

    plot.updateEllipses(plot.cacheData);
    plot.exit();

    //reset the plot to its original view
    var reset = topsoil.reset = function() {
        d3.transition().duration(750).tween("zoom", function() {
            var ix = d3.interpolate(plot.x.domain(), [plot.xMin, plot.xMax]);
            var iy = d3.interpolate(plot.y.domain(), [plot.yMin, plot.yMax]);
            return function(t) {
                zoom.x(plot.x.domain(ix(t))).y(plot.y.domain(iy(t)));
                zoomed();
            };
        });
    };

    // add pan/zoom
    var zoom = plot.zoom = d3.behavior.zoom()
        .x(plot.x)
        .y(plot.y)
        .scaleExtent([0.5, 2.5]);

    function zoomed() {
        plot.zoomed(data);
    }
    zoom.on("zoom", zoomed);

    plot.setEllipseVisibility(data);

    plot.area.clipped.call(zoom);
};

//remove ellipses
plot.exit = function() {
    if(plot.getProperty('Ellipses') === true) {
        var ellipses = plot.ellipses;
        ellipses.exit().remove();
    }
};

plot.zoomed = function(data) {
    var zoom = plot.zoom;
    var x = plot.x;
    var y = plot.y;

    var t = zoom.translate();
    var tx = t[0];
    var ty = t[1];

    x.domain([zoom.x().domain()[0], zoom.x().domain()[1]]);
    y.domain([zoom.y().domain()[0], zoom.y().domain()[1]]);

    plot.update(data);
};

plot.cubicBezier = function (path, p1, p2, p3) {
    path.push(
        "C", p1[0], ",", p1[1],
        ",", p2[0], ",", p2[1],
        ",", p3[0], ",", p3[1]);
};

//Calculate constants used to draw ellipses
plot.calcEllipses = function(data) {
    var k = 4 / 3 * (Math.sqrt(2) - 1);
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

    var cacheData = data.map(function (d) {
        var r = [
            [d.sigma_x, d.rho * d.sigma_y],
            [0, d.sigma_y * Math.sqrt(1 - d.rho * d.rho)]
        ];

        var shift = function (dx, dy) {
            return function (p) {
                return [p[0] + dx, p[1] + dy];
            };
        };

        var points = numeric.mul(
            plot.getProperty("Uncertainty"),
            numeric.dot(controlPointsBase, r))
            .map(shift(d.x, d.y));

        points.Selected = d.Selected;

        return points;
    });

    return cacheData;
};

plot.drawEllipses = function(cacheData) {
    var ellipses = plot.ellipses = plot.dataGroup.selectAll(".ellipse")
        .data(cacheData);

    // TODO "fill-opacity" should be a property
    ellipses.enter().append("path")
        .attr("class", "ellipse")
        .attr("fill-opacity", 0.3)
        .attr("stroke", "black");

    ellipses.attr("fill", function(d) {
        var fill;

        if (!d['Selected']) {
            fill = 'gray';
        } else {
            fill = plot.getProperty('Ellipse Fill Color');
        }

        return fill;
    });

};

plot.updateEllipses = function() {
    //don't redraw ellipses if they're not visible
    if(plot.getProperty('Ellipses') === true ) {
        var ellipses = plot.ellipses;
        var x = plot.x;
        var y = plot.y;

        ellipses.attr("d", function (d) {
            var ellipsePath = d3.svg.line()
                .x(function (datum) {
                    return x(datum[0]);
                })
                .y(function (datum) {
                    return y(datum[1]);
                })
                .interpolate(function (points) {
                    var i = 1, path = [points[0][0], ",", points[0][1]];
                    while (i + 3 <= points.length) {
                        plot.cubicBezier(path, points[i++], points[i++], points[i++]);
                    }
                    return path.join("");
                });

            return ellipsePath(d);
        });
    }
};

//show and hide ellipses
plot.setEllipseVisibility = function (data) {
    d3.selectAll(".ellipse")
        .style("opacity", plot.getProperty('Ellipses') ? 1 : 0);
};
