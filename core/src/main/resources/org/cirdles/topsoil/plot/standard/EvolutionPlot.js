/*
 * Copyright 2015 CIRDLES.
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

    plot.dataKeys = ['x', 'sigma_x', 'y', 'sigma_y'];
    plot.propertiesKeys = [];

    var INF = Number.MAX_VALUE;

    var lambda = {};
    lambda.U238 = 1.55125e-10;
    lambda.U234 = 2.82206e-6;
    lambda.Th230 = 9.1705e-6;

    var mxp = {};

    mxp.A = [
        [-lambda.U238, 0, 0],
        [lambda.U238, -lambda.U234, 0],
        [0, lambda.U234, -lambda.Th230]
    ];

    mxp.QUTh = [
        [((lambda.Th230 - lambda.U238) * (lambda.U234 - lambda.U238)) / (lambda.U234 * lambda.U238), 0, 0],
        [(lambda.Th230 - lambda.U238) / lambda.U234, (lambda.Th230 - lambda.U234) / lambda.U234, 0],
        [1, 1, 1]
    ];

    var diag = function (x, y, z) {
        return [
            [x, 0, 0],
            [0, y, 0],
            [0, 0, z]
        ];
    };

    var exp = Math.exp;

    mxp.GUTh = function (t) {
        return diag(exp(-lambda.U238 * t), exp(-lambda.U234 * t), exp(-lambda.Th230 * t));
    };

    mxp.QinvUTh = [
        [(lambda.U234 * lambda.U238) / ((lambda.Th230 - lambda.U238) * (lambda.U234 - lambda.U238)), 0, 0],
        [-(lambda.U234 * lambda.U238) / ((lambda.Th230 - lambda.U234) * (lambda.U234 - lambda.U238)), lambda.U234 / (lambda.Th230 - lambda.U234), 0],
        [(lambda.U234 * lambda.U238) / ((lambda.Th230 - lambda.U234) * (lambda.Th230 - lambda.U238)), -lambda.U234 / (lambda.Th230 - lambda.U234), 1]
    ];

    var dot = numeric.dot;

    mxp.UTh = function (t) {
        return dot(dot(mxp.QUTh, mxp.GUTh(t)), mxp.QinvUTh);
    };

    mxp.UTh_0 = function (t) {
        return dot(dot(mxp.QUTh[2], mxp.GUTh(t)), mxp.QinvUTh); // For the 230 concentration only (to solve for root)
    };

    mxp.UTh_4 = function (t) {
        return dot(dot(mxp.QUTh[1], mxp.GUTh(t)), mxp.QinvUTh); // For the 234 concentration only (to solve for root)
    };

    plot.initialProperties = {
        "Title": "Isochron Plot",
        "X Axis": "230Th/238U",
        "Y Axis": "234U/238U",
        "Uncertainty": 2.0
    };

    plot.draw = function (data) {
        var x = plot.x = d3.scale.linear()
                .range([0, plot.width]);

        var y = plot.y = d3.scale.linear()
                .range([plot.height, 0]);

        plot.area.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + plot.height + ")")
                .append("text")
                .attr("class", "label")
                .attr("x", plot.width / 2)
                .attr("y", 35);

        plot.area.append("g")
                .attr("class", "y axis")
                .append("text")
                .attr("class", "label")
                .attr("transform", "rotate(-90)")
                .attr("x", -plot.height / 2)
                .attr("y", -50)
                .attr("dy", ".71em");

        var xMin = 0;
        var xMax = 2;
        var yMin = 0;
        var yMax = 1.5;

        if (data.length < 0){
            var dataXMin = d3.min(data, function (d) {
                return d.x - d.sigma_x * plot.getProperty("Uncertainty");
            });

            var dataYMin = d3.min(data, function (d) {
                return d.y - d.sigma_y * plot.getProperty("Uncertainty");
            });

            var dataXMax = d3.max(data, function (d) {
                return d.x + d.sigma_x * plot.getProperty("Uncertainty");
            });

            var dataYMax = d3.max(data, function (d) {
                return d.y + d.sigma_y * plot.getProperty("Uncertainty");
            });

            var xRange = dataXMax - dataXMin;
            var yRange = dataYMax - dataYMin;

            xMin = dataXMin - 0.05 * xRange;
            yMin =  dataYMin - 0.05 * yRange;
            xMax = dataXMax + 0.05 * xRange;
            yMax = dataYMax + 0.05 * yRange;
        }

        x.domain([xMin, xMax]);
        y.domain([yMin, yMax]);

        plot.ar08lim = x.domain();
        plot.ar48lim = y.domain();

        plot.update(data);
    };

    plot.update = function (data) {
        var x = plot.x;
        var y = plot.y;

        var r08lim = dot(plot.ar08lim, lambda.U238 / lambda.Th230);
        var r48lim = dot(plot.ar48lim, lambda.U238 / lambda.U234);

        x.domain(plot.ar08lim);
        y.domain(plot.ar48lim);

        d3.select(".x.axis .label").text(plot.getProperty("X Axis"));
        d3.select(".y.axis .label").text(plot.getProperty("Y Axis"));

        var xAxis = d3.svg.axis()
                .scale(x)
                .orient("bottom");

        var yAxis = d3.svg.axis()
                .scale(y)
                .orient("left");

        var ellipses;
        (ellipses = plot.area.clipped.selectAll(".ellipse")
                .data(data))
                .enter().append("path")
                .attr("class", "ellipse")
                .attr("fill-opacity", 0.3)
                .attr("fill", "red")
                .attr("stroke", "black");

        var dots;
        (dots = plot.area.clipped.selectAll(".dot")
                .data(data))
                .enter().append("circle")
                .attr("class", "dot")
                .attr("r", 1.5);

        var tisochrons = [
            25000,
            50000,
            75000,
            100000,
            150000,
            200000,
            300000,
            INF
        ];

        var isochrons = plot.area.clipped.selectAll(".isochron")
                .data(tisochrons);

        var abmat = [
            [0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0]
        ];

        var xminpoints = [0, 0, 0, 0, 0, 0, 0, 0];
        var yminpoints = [0, 0, 0, 0, 0, 0, 0, 0];

        tisochrons.forEach(function (t, it) {
            if (t === INF) {
                abmat[1][it] = lambda.Th230 / lambda.U234 - 1; // note: works, but not sure how to evaluate this limit
                abmat[0][it] = lambda.U238 / (lambda.Th230 - lambda.U238); // y-int with above slope through transient eqbm
                xminpoints[it] = mxp.QUTh[2][0] / mxp.QUTh[0][0]; // limit is transient eqbm.  Lower starts all end up here after ~5 Myr
                yminpoints[it] = mxp.QUTh[1][0] / mxp.QUTh[0][0];
            } else {
                var mxpNegAt = mxp.UTh(-t);
                abmat[1][it] = -mxpNegAt[2][2] / mxpNegAt[2][1]; // slope

                var mxpAt = mxp.UTh(t);
                var x = -mxpAt[2][0] / mxpAt[2][1];
                abmat[0][it] = dot(mxp.UTh_4(t), [1, x, 0]);   // y-int

                var mxpAtmin = dot(mxpAt, [1, 0, 0]);
                xminpoints[it] = mxpAtmin[2] / mxpAtmin[0];
                yminpoints[it] = mxpAtmin[1] / mxpAtmin[0];
            }
        });

        var add = numeric.add;
        var div = numeric.div;
        var mul = numeric.mul;
        var sub = numeric.sub;

        // now find where lines intesect bounding box (ar48lim, ar08lim)
        var L = add(abmat[0], dot(abmat[1], r08lim[0])); // y-coord of intersections with left boundary of box
        var R = add(abmat[0], dot(abmat[1], r08lim[1])); // y-coord of intersections with right boundary of box
        var B = div(sub(r48lim[0], abmat[0]), abmat[1]); // x-coord of intersections with bottom boundary of box
        var T = div(sub(r48lim[1], abmat[0]), abmat[1]); // x-coord of intersections with top boundary of box

        var ones = function (n) {
            var result = [];

            for (var i = 0; i < n; i++) {
                result.push(1);
            }

            return result;
        };

        var indicatorCompare = function (f) {
            return function (a, b) {
                var result = [];

                for (var i = 0; i < a.length; i++) {
                    result.push(f(a[i], b) ? 1 : 0);
                }

                return result;
            };
        };

        var ge = indicatorCompare(function (a, b) {
            return a >= b
        });
        var gt = indicatorCompare(function (a, b) {
            return a > b
        });
        var le = indicatorCompare(function (a, b) {
            return a <= b
        });
        var lt = indicatorCompare(function (a, b) {
            return a < b
        });

        var xendpoints = [
            add(mul(dot(r08lim[0], ones(tisochrons.length)), gt(L, r48lim[0])), mul(B, le(L, r48lim[0]))),
            add(mul(dot(r08lim[1], ones(tisochrons.length)), lt(R, r48lim[1])), mul(T, ge(R, r48lim[1])))
        ];

        var yendpoints = [
            add(mul(L, gt(L, r48lim[0])), mul(dot(r48lim[0], ones(tisochrons.length)), le(L, r48lim[0]))),
            add(mul(R, lt(R, r48lim[1])), mul(dot(r48lim[1], ones(tisochrons.length)), ge(R, r48lim[1])))
        ];

        // see matlab documentation
        var max = function (a, b) {
            var result = [];

            b.forEach(function (b) {
                if (a > b) {
                    result.push(a);
                } else {
                    result.push(b);
                }
            });

            return result;
        };

        // if endpoints extend beyond min possible (n0 = [1 0 0]), truncate them further
        xendpoints[0] = max(xendpoints[0], xminpoints); // since isochrons have positive slope, use maximum
        yendpoints[0] = max(yendpoints[0], yminpoints);

        // transform into activity ratios, svg plot box coordinates
        xendpoints = mul(xendpoints, lambda.Th230 / lambda.U238);
        yendpoints = mul(yendpoints, lambda.U234 / lambda.U238);

        var ar48icntrs = [0, 0.25, 0.5, 0.75, 1, 1.25, 1.5, 1.75, 2.0, 2.25];

        var any = function (results) {
            return results.reduce(function (prev, curr) {
                return prev || curr == 1;
            }, false);
        };

        var repmat = function (mat, rows, cols) {
            var result = [];

            for (var i = 0; i < rows; i++) {
                result.push([]);

                for (var j = 0; j < cols; j++) {
                    result[i] = result[i].concat(mat);
                }
            }

            return result;
        };

        var linspace = function (x1, x2, n) {
            var result = [];
            var interval = (x2 - x1) / (n - 1);

            for (var i = 0; i < n; i++) {
                result.push(x1 + i * interval);
            }

            return result;
        };

        var neg = numeric.neg;

        var mean = function (xs) {
            var result = 0;

            for (var i = 0; i < xs.length; i++) {
                result += xs[i];
            }

            return result / xs.length;
        };

        var fzero = function (f, x0) {
            var ε = 0.0001;

            var df = function (x) {
                return (f(x + ε) - f(x)) / ε;
            };

            var x = x0;

            for (var i = 0; i < 100; i++) {
                var decrement = f(x) / df(x);

                if (decrement === Infinity || decrement === NaN) {
                    break;
                }

                x -= decrement;
            }

            return x;
        };

        var find = function (xs) {
            var result = [];

            for (var i = 0; i < xs.length; i++) {
                if (xs[i] !== 0) {
                    result.push(i);
                }
            }

            return result;
        };

        var select = function (indices, xs) {
            var result = [];

            indices.forEach(function (index) {
                result.push(xs[index]);
            });

            return result;
        };

        var nts = 10;

        var nar48is = ar48icntrs.length;
        var tv = repmat(linspace(0, 1e6, nts - 1).concat([2e6]), nar48is, 1);

        // if any ar48i contours start above
        if (any(gt(ar48icntrs, plot.ar48lim[1]))) {
            var iover = find(gt(ar48icntrs, plot.ar48lim[1]));
            var ar48overs = select(iover, ar48icntrs);

            // solve for when ar48i = ar48imax
            ar48overs.forEach(function (ar48over, iar48over) {
                var root = function (t) {
                    return sub(dot(mxp.UTh_4(t), [1, ar48over * lambda.U238 / lambda.U234, 0]),
                            div(mul(plot.ar48lim[1], lambda.U238), lambda.U234));
                };
                var tstart = fzero(root, 50e3);

                tv[iover[iar48over]] = linspace(tstart, 1e6, nts - 1).concat([5e6]);
            });
        }

        // if any contours start below
        if (any(lt(ar48icntrs, plot.ar48lim[0]))) {
            var iunder = find(lt(ar48icntrs, plot.ar48lim[0]));
            var ar48unders = select(iunder, ar48icntrs);

            // solve for when ar48i = ar48imax
            ar48unders.forEach(function (ar48under, iar48under) {
                var root = function (t) {
                    return sub(dot(mxp.UTh_4(t), [1, ar48under * lambda.U238 / lambda.U234, 0]),
                            div(mul(plot.ar48lim[0], lambda.U238), lambda.U234));
                };
                var tstart = fzero(root, 50e3);

                tv[iunder[iar48under]] = linspace(tstart, 1e6, nts - 1).concat([5e6]);
            })
        }

        var zeros = function (args_) {
            var args;

            // magic
            if (Array.isArray(args_)) {
                args = args_;
            } else {
                args = new Array(arguments.length);

                for (var i = 0; i < args.length; i++) {
                    args[i] = arguments[i];
                }
            }

            // actual logic
            var result = [];

            for (var i = 0; i < args[0]; i++) {
                if (args.length === 1) { // base case
                    result.push(0);
                } else { // recurse
                    result.push(zeros(args.slice(1)));
                }
            }

            return result;
        };

        var xy = zeros(nar48is, 2, nts);
        var dardt = zeros(nar48is, 2, nts);

        ar48icntrs.forEach(function (ar48i, iar48i) {
            tv[iar48i].forEach(function (t, it) {
                var n0 = [1, ar48i * lambda.U238 / lambda.U234, 0];
                var nt = dot(mxp.UTh(t), n0);

                xy[iar48i][0][it] = nt[2] / nt[0] * lambda.Th230 / lambda.U238;
                xy[iar48i][1][it] = nt[1] / nt[0] * lambda.U234 / lambda.U238;

                var dar48dnt1 = -nt[1] / nt[0] / nt[0] * lambda.U234 / lambda.U238;
                var dar48dnt2 = 1 / nt[0] * lambda.U234 / lambda.U238;
                var dar48dnt3 = 0;
                var dar08dnt1 = -nt[2] / nt[0] / nt[0] * lambda.Th230 / lambda.U238;
                var dar08dnt2 = 0;
                var dar08dnt3 = 1 / nt[0] * lambda.Th230 / lambda.U238;

                var dardnt = [[dar08dnt1, dar08dnt2, dar08dnt3], [dar48dnt1, dar48dnt2, dar48dnt3]];
                var dntdt = dot(dot(mxp.A, mxp.UTh(t)), n0);

                dardt[iar48i][0][it] = dot(dardnt, dntdt)[0];
                dardt[iar48i][1][it] = dot(dardnt, dntdt)[1];
            })
        });

        isochrons.enter()
                .append("line")
                .attr("class", "isochron")
                .attr("stroke", "red");

        // utilities for generating path data elements
        var moveTo = function (path, p) {
            path.push("M", p[0], ",", p[1]);
        };

        var lineTo = function (path, p) {
            path.push("L", p[0], ",", p[1]);
        };

        var close = function (path) {
            path.push("Z");
        };

        var cubicBezier = function (path, p1, p2, p3) {
            path.push(
                    "C", p1[0], ",", p1[1],
                    ",", p2[0], ",", p2[1],
                    ",", p3[0], ",", p3[1]);
        };

        isochrons
                .attr("x1", function (isochron, index) {
                    return x(xendpoints[0][index]);
                })
                .attr("y1", function (isochron, index) {
                    return y(yendpoints[0][index]);
                })
                .attr("x2", function (isochron, index) {
                    return x(xendpoints[1][index]);
                })
                .attr("y2", function (isochron, index) {
                    return y(yendpoints[1][index]);
                });

        // update the ellipses
        ellipses.attr("d", function (d) {
            var k = 4 / 3 * (Math.sqrt(2) - 1);
            var r = [
                [d.sigma_x, 0],
                [0, d.sigma_y]
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

            return ellipsePath(numeric.mul(
                plot.getProperty("Uncertainty"),
                numeric.dot(controlPointsBase, r)));
        });

        // update the center points
        dots
                .attr("cx", function (d) {
                    return x(d.x);
                })
                .attr("cy", function (d) {
                    return y(d.y);
                });

        var ar48iContours = plot.area.clipped.selectAll('.ar48iContour')
                .data(ar48icntrs);

        ar48iContours.enter()
                .append('path')
                .attr('class', 'ar48iContour')
                .attr('fill', 'none')
                .attr('stroke', 'blue');

        ar48iContours.attr('d', function (ar48i, iar48i) {
                    var path = [];
                    moveTo(path, [x(xy[iar48i][0][0]), y(xy[iar48i][1][0])]);

                    for (var i = 1; i < nts; i++) {
                        var deltaTOver3 = (tv[iar48i][i] - tv[iar48i][i - 1]) / 3;

                        var p1 = [
                            x(xy[iar48i][0][i - 1] + deltaTOver3 * dardt[iar48i][0][i - 1]),
                            y(xy[iar48i][1][i - 1] + deltaTOver3 * dardt[iar48i][1][i - 1])
                        ];

                        var p2 = [
                            x(xy[iar48i][0][i] - deltaTOver3 * dardt[iar48i][0][i]),
                            y(xy[iar48i][1][i] - deltaTOver3 * dardt[iar48i][1][i])
                        ];

                        var p3 = [
                            x(xy[iar48i][0][i]),
                            y(xy[iar48i][1][i])
                        ];

                        cubicBezier(path, p1, p2, p3);
                    }

                    return path.join('');
                });

        ar48iContours.exit();

        // retick the axes
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

        var zoom = d3.behavior.zoom()
                .x(x)
                .y(y)
                .scaleExtent([.5, 2.5])
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

                    plot.x.domain([zoom.x().domain()[0], zoom.x().domain()[1]]);
                    plot.y.domain([zoom.y().domain()[0], zoom.y().domain()[1]]);

                    plot.ar08lim = [zoom.x().domain()[0], zoom.x().domain()[1]];
                    plot.ar48lim = [zoom.y().domain()[0], zoom.y().domain()[1]];

                    plot.update(data);
                });
        plot.area.clipped.call(zoom);
    }
})();