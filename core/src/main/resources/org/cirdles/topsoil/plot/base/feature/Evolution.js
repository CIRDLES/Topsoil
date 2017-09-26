/**
 * @author Jake Marotta
 */

evolution = plot.evolution = {};

/**
 * The values of each of the displayed isochrons, in millions of years(?).
 * TODO Ask whether value is millions or thousands.
 *
 * @type {Array}
 */
evolution.isochronValues = [];

evolution.isochrons = {};

/**
 * The values of each of the displayed "horizontal" contours.
 *
 * @type {Array}
 */
evolution.ar48iContourValues = [];

/**
 * Describes the domain of "ar08" as [min, max].
 *
 * @type {[*]}
 */
evolution.ar08lim = [2];

/**
 * Describes the domain of "ar48" as [min, max].
 *
 * @type {[*]}
 */
evolution.ar48lim = [2];

/**
 * A structure that holds (M)atrix e(X)(P)onential functions used in calculations.
 *
 * @type {{}}
 */
var mxp = {};

/**
 * A structure that holds the lambdas for calculation.
 *
 * @type {{}}
 */
plot.lambda = {};

/**
 * Infinity.
 *
 * @type {Number}
 */
var INF = Number.MAX_VALUE;

/**
 * Returns a 3x3 diagonal matrix (a matrix in which all entries outside the main diagonal are zero), where the main
 * diagonal is composed of the provided three arguments.
 *
 * @param x entry (0,0)
 * @param y entry (1,1)
 * @param z entry (2,2)
 * @returns {[*,*,*]}
 */
var diag = function (x, y, z) {
    return [
        [x, 0, 0],
        [0, y, 0],
        [0, 0, z]
    ];
};

// TODO ???
var indicatorCompare = function (f) {
    return function (a, b) {
        var result = [];

        for (var i = 0; i < a.length; i++) {
            result.push(f(a[i], b) ? 1 : 0);
        }

        return result;
    };
};

/**
 * Returns true if a is greater than or equal to b.
 */
var ge = indicatorCompare(function (a, b) {
    return a >= b
});
/**
 * Returns true if a is greater than b.
 */
var gt = indicatorCompare(function (a, b) {
    return a > b
});
/**
 * Returns true if a is less than or equal to b.
 */
var le = indicatorCompare(function (a, b) {
    return a <= b
});
/**
 * Returns true if a is less than b.
 */
var lt = indicatorCompare(function (a, b) {
    return a < b
});

// see matlab documentation
// TODO ???
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

var xminpoints = [0, 0, 0, 0, 0, 0, 0, 0];
var yminpoints = [0, 0, 0, 0, 0, 0, 0, 0];

/**
 * Returns an array containing a one for each item in the array n.
 *
 * @param n
 * @returns {Array}
 */
var ones = function (n) {
    var result = [];

    for (var i = 0; i < n; i++) {
        result.push(1);
    }

    return result;
};

var any = function (results) {
    return results.reduce(function (prev, curr) {
        return prev || curr == 1; //use ===?
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

/**
 * Returns the average of all items in the array xs.
 *
 * @param xs
 * @returns {number}
 */
var mean = function (xs) {
    var result = 0;

    for (var i = 0; i < xs.length; i++) {
        result += xs[i];
    }

    return result / xs.length;
};

/**
 *
 *
 * @param f
 * @param x0
 * @returns {*}
 */
var fzero = function (f, x0) {
    var ε = 0.0001;

    var df = function (x) {
        return (f(x + ε) - f(x)) / ε;
    };

    var x = x0;

    for (var i = 0; i < 100; i++) {
        var decrement = f(x) / df(x);

        if (decrement === Infinity || isNaN(decrement)) {  //decrement === INF?
            break;
        }

        x -= decrement;
    }

    return x;
};

/**
 * Returns an array containing the non-zero values in the supplied array.
 *
 * @param xs
 * @returns {Array}
 */
var find = function (xs) {
    var result = [];

    for (var i = 0; i < xs.length; i++) {
        if (xs[i] !== 0) {
            result.push(i);
        }
    }

    return result;
};

/**
 * Returns an array containing the values at the specified indices in xs.
 *
 * @param indices
 * @param xs
 * @returns {Array}
 */
var select = function (indices, xs) {
    var result = [];

    indices.forEach(function (index) {
        result.push(xs[index]);
    });

    return result;
};

/**
 *
 *
 * @param args_
 * @returns {Array}
 */
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

/**
 * True if the evolution matrix is toggled on.
 */
if (plot.evolutionMatrixVisible == null) {
    plot.evolutionMatrixVisible = false;
}

/**
 * Calculates the necessary values for the SVG elements for each isochron and "contour".
 */
plot.calculateIsochrons = function () {

    // TODO Allow support for custom isochrons
    evolution.isochronValues = [
        25000,
        50000,
        75000,
        100000,
        150000,
        200000,
        300000,
        INF
    ];

    evolution.isochrons = [];

    evolution.isochronValues.forEach( function(t) {
        var isochron = {};
        isochron.value = t;
        evolution.isochrons.push(isochron);
    });

    // TODO Allow support for custom contour lines
    evolution.ar48iContourValues = [
        0,
        0.25,
        0.5,
        0.75,
        1,
        1.25,
        1.5,
        1.75,
        2.0,
        2.25
    ];

    mxp.A = [
        [-plot.lambda.U238, 0, 0],
        [plot.lambda.U238, -plot.lambda.U234, 0],
        [0, plot.lambda.U234, -plot.lambda.Th230]
    ];

    // I don't know what Q is, but this is some kind of 3x3 matrix describing something to do with UTh.
    mxp.QUTh = [
        [((plot.lambda.Th230 - plot.lambda.U238) * (plot.lambda.U234 - plot.lambda.U238)) / (plot.lambda.U234 * plot.lambda.U238), 0, 0],
        [(plot.lambda.Th230 - plot.lambda.U238) / plot.lambda.U234, (plot.lambda.Th230 - plot.lambda.U234) / plot.lambda.U234, 0],
        [1, 1, 1]
    ];

    // Returns a 3x3 diagonal matrix: [ e^((-U238) * t) ,        0        ,        0         ]
    //                                [        0        , e^((-U234) * t) ,        0         ]
    //                                [        0        ,        0        , e^((-Th230) * t) ]
    //
    //                                                   e = Euler's number.
    mxp.GUTh = function (t) {
        return diag(exp(-plot.lambda.U238 * t), exp(-plot.lambda.U234 * t), exp(-plot.lambda.Th230 * t));
    };

    // I assume this is the "Q" of the inverse of UTh
    mxp.QinvUTh = [
        [(plot.lambda.U234 * plot.lambda.U238) / ((plot.lambda.Th230 - plot.lambda.U238) * (plot.lambda.U234 - plot.lambda.U238)), 0, 0],
        [-(plot.lambda.U234 * plot.lambda.U238) / ((plot.lambda.Th230 - plot.lambda.U234) * (plot.lambda.U234 - plot.lambda.U238)), plot.lambda.U234 / (plot.lambda.Th230 - plot.lambda.U234), 0],
        [(plot.lambda.U234 * plot.lambda.U238) / ((plot.lambda.Th230 - plot.lambda.U234) * (plot.lambda.Th230 - plot.lambda.U238)), -plot.lambda.U234 / (plot.lambda.Th230 - plot.lambda.U234), 1]
    ];

    // Returns the dot product of (the dot product of whatever QUTh is and mxp.GUTh) and mxp.QinvUTh
    mxp.UTh = function (t) {
        return dot(dot(mxp.QUTh, mxp.GUTh(t)), mxp.QinvUTh);
    };

    // A second Uranium-Thorium function for the 230 concentration
    mxp.UTh_0 = function (t) {
        return dot(dot(mxp.QUTh[2], mxp.GUTh(t)), mxp.QinvUTh); // For the 230 concentration only (to solve for root)
    };

    // A third Uranium-Thorium function for the 234 concentration
    mxp.UTh_4 = function (t) {
        return dot(dot(mxp.QUTh[1], mxp.GUTh(t)), mxp.QinvUTh); // For the 234 concentration only (to solve for root)
    };

    evolution.isochrons.forEach( function (isochron, i) {
        if (isochron.value === INF) {
            isochron.slope = plot.lambda.Th230 / plot.lambda.U234 - 1;
            isochron.yIntercept = plot.lambda.U238 / (plot.lambda.Th230 - plot.lambda.U238);

            xminpoints[i] = mxp.QUTh[2][0] / mxp.QUTh[0][0];
            yminpoints[i] = mxp.QUTh[1][0] / mxp.QUTh[0][0];
        } else {
            var mxpNegAt = mxp.UTh(-isochron.value);
            isochron.slope = -mxpNegAt[2][2] / mxpNegAt[2][1]; // slope

            var mxpAt = mxp.UTh(isochron.value);
            var x = -mxpAt[2][0] / mxpAt[2][1];
            isochron.yIntercept = dot(mxp.UTh_4(isochron.value), [1, x, 0]);   // y-int

            var mxpAtmin = dot(mxpAt, [1, 0, 0]);
            xminpoints[i] = mxpAtmin[2] / mxpAtmin[0];
            yminpoints[i] = mxpAtmin[1] / mxpAtmin[0];
        }
    });

    evolution.nts = 10;
    var nar48is = evolution.ar48iContourValues.length;
    evolution.tv = repmat(linspace(0, 1e6, evolution.nts - 1).concat([2e6]), nar48is, 1);

    evolution.xy = zeros(nar48is, 2, evolution.nts);
    evolution.dardt = zeros(nar48is, 2, evolution.nts);

    evolution.ar48iContourValues.forEach(function (ar48i, iar48i) {
        evolution.tv[iar48i].forEach(function (t, it) {
            var n0 = [1, ar48i * plot.lambda.U238 / plot.lambda.U234, 0];
            var nt = dot(mxp.UTh(t), n0);

            evolution.xy[iar48i][0][it] = nt[2] / nt[0] * plot.lambda.Th230 / plot.lambda.U238;
            evolution.xy[iar48i][1][it] = nt[1] / nt[0] * plot.lambda.U234 / plot.lambda.U238;

            var dar48dnt1 = -nt[1] / nt[0] / nt[0] * plot.lambda.U234 / plot.lambda.U238;
            var dar48dnt2 = 1 / nt[0] * plot.lambda.U234 / plot.lambda.U238;
            var dar48dnt3 = 0;
            var dar08dnt1 = -nt[2] / nt[0] / nt[0] * plot.lambda.Th230 / plot.lambda.U238;
            var dar08dnt2 = 0;
            var dar08dnt3 = 1 / nt[0] * plot.lambda.Th230 / plot.lambda.U238;

            var dardnt = [[dar08dnt1, dar08dnt2, dar08dnt3], [dar48dnt1, dar48dnt2, dar48dnt3]];
            var dntdt = dot(dot(mxp.A, mxp.UTh(t)), n0);

            evolution.dardt[iar48i][0][it] = dot(dardnt, dntdt)[0];
            evolution.dardt[iar48i][1][it] = dot(dardnt, dntdt)[1];
        })
    });

};

/**
 * Creates the SVG elements required to display the evolution matrix.
 */
plot.drawEvolutionMatrix = function () {

    if (plot.evolutionMatrixVisible) {
        plot.removeEvolutionMatrix();
    }

    if (evolution.isochrons.length <= 0 || evolution.ar48iContourValues <= 0) {
        plot.calculateIsochrons();
    }

    plot.evolutionGroup = plot.area.clipped.insert("g", ".dataGroup")
        .attr("class", "evolutionGroup");

    plot.isochrons = plot.evolutionGroup.selectAll(".isochron")
        .data(evolution.isochronValues);

    plot.isochrons.enter()
        .append("line")
        .attr("class", "isochron")
        .attr("stroke", "red");

    plot.ar48iContours = plot.evolutionGroup.selectAll('.ar48iContour')
        .data(evolution.ar48iContourValues);

    plot.ar48iContours.enter()
        .append('path')
        .attr('class', 'ar48iContour')
        .attr('fill', 'none')
        .attr('stroke', 'blue');

    var plotSVG = d3.select("#plot");

    plotSVG.append("clipPath")
        .attr("id", "edge-clip")
        .append("rect")
        .attr("x", plot.margin.left)
        .attr("y", 0)
        .attr("width", plot.width + plot.margin.right)
        .attr("height", plot.height + plot.margin.top);

    plot.area.aroundEdge = plotSVG.append("g")
        .attr("clip-path", "url(#edge-clip)")
        .attr("width", window.innerWidth - plot.margin.left)
        .attr("height", window.innerHeight - plot.margin.bottom);

    plot.evolutionMatrixVisible = true;
    plot.updateEvolutionMatrix();
};

plot.updateEvolutionMatrix = function () {

    if (plot.evolutionMatrixVisible) {

        var contourXLimits = dot(plot.xAxisScale.domain(), plot.lambda.U238 / plot.lambda.Th230);
        var contourYLimits = dot(plot.yAxisScale.domain(), plot.lambda.U238 / plot.lambda.U234);

        var slopes = pluck(evolution.isochrons, 'slope');
        var yIntercepts = pluck(evolution.isochrons, 'yIntercept');

        // now find where lines intersect bounding box (at the countour limits)
        var L = add(yIntercepts, dot(slopes, contourXLimits[0])); // y-coord of intersections with left boundary of box
        var R = add(yIntercepts, dot(slopes, contourXLimits[1])); // y-coord of intersections with right boundary of box
        var B = div(sub(contourYLimits[0], yIntercepts), slopes); // x-coord of intersections with bottom boundary of box
        var T = div(sub(contourYLimits[1], yIntercepts), slopes); // x-coord of intersections with top boundary of box

        var xendpoints = [
            add(mul(dot(contourXLimits[0], ones(evolution.isochrons.length)), gt(L, contourYLimits[0])), mul(B, le(L, contourYLimits[0]))),
            add(mul(dot(contourXLimits[1], ones(evolution.isochrons.length)), lt(R, contourYLimits[1])), mul(T, ge(R, contourYLimits[1])))
        ];
        var yendpoints = [
            add(mul(L, gt(L, contourYLimits[0])), mul(dot(contourYLimits[0], ones(evolution.isochrons.length)), le(L, contourYLimits[0]))),
            add(mul(R, lt(R, contourYLimits[1])), mul(dot(contourYLimits[1], ones(evolution.isochrons.length)), ge(R, contourYLimits[1])))
        ];

        // if endpoints extend beyond min possible (n0 = [1 0 0]), truncate them further
        xendpoints[0] = max(xendpoints[0], xminpoints); // since isochrons have positive slope, use maximum
        yendpoints[0] = max(yendpoints[0], yminpoints);

        // transform into activity ratios, svg plot box coordinates
        xendpoints = mul(xendpoints, plot.lambda.Th230 / plot.lambda.U238);
        yendpoints = mul(yendpoints, plot.lambda.U234 / plot.lambda.U238);


        // ACTUAL UPDATING PART

        plot.isochrons
            .attr("x1", function (isochron, index) {
                return plot.xAxisScale(xendpoints[0][index]);
            })
            .attr("y1", function (isochron, index) {
                return plot.yAxisScale(yendpoints[0][index]);
            })
            .attr("x2", function (isochron, index) {
                return plot.xAxisScale(xendpoints[1][index]);
            })
            .attr("y2", function (isochron, index) {
                return plot.yAxisScale(yendpoints[1][index]);
            });

        plot.ar48iContours.attr('d', function (ar48i, i) {
            var path = [];

            moveTo(path, [plot.xAxisScale(evolution.xy[i][0][0]), plot.yAxisScale(evolution.xy[i][1][0])]);

            for (var j = 1; j < evolution.nts; j++) {
                var deltaTOver3 = (evolution.tv[i][j] - evolution.tv[i][j - 1]) / 3;

                var p1 = [
                    plot.xAxisScale(evolution.xy[i][0][j - 1] + deltaTOver3 * evolution.dardt[i][0][j - 1]),
                    plot.yAxisScale(evolution.xy[i][1][j - 1] + deltaTOver3 * evolution.dardt[i][1][j - 1])
                ];

                var p2 = [
                    plot.xAxisScale(evolution.xy[i][0][j] - deltaTOver3 * evolution.dardt[i][0][j]),
                    plot.yAxisScale(evolution.xy[i][1][j] - deltaTOver3 * evolution.dardt[i][1][j])
                ];

                var p3 = [
                    plot.xAxisScale(evolution.xy[i][0][j]),
                    plot.yAxisScale(evolution.xy[i][1][j])
                ];

                plot.cubicBezier(path, p1, p2, p3);
            }

            return path.join('');
        });

        var labels = plot.area.aroundEdge.selectAll(".isochron-label")
            .data(evolution.isochrons);

        labels.enter()
            .append("text")
            .attr("class", "isochron-label")
            .text(function (d) {
                return (d.value == INF ? "INF" : d.value / 1000);
            })
            // .text(function (d) {
            //     return (d.value == INF ? "∞" : d.value / 1000);
            // })
            .attr("text-anchor", "start")
            .attr("font-family", "sans-serif")
            .attr("font-size", "14px");
            // .attr("font-size", function (d) {
            //     return (d.value == INF ? "30px" : "14px");
            // });
            // .attr("fill", "red");

        labels
            .attr("transform", function (d, i) {

                var x;
                var y;
                var slope = d.slope * ((plot.lambda.U234 / plot.lambda.U238) / (plot.lambda.Th230 / plot.lambda.U238));
                var xOffset;
                var yOffset;

                if (R[i] > contourYLimits[1]) {
                    // isochron intersects plot boundary at top
                    xOffset = 5 / slope;
                    x = plot.margin.left + plot.xAxisScale(T[i] * (plot.lambda.Th230 / plot.lambda.U238)) + xOffset;
                    y = plot.margin.top - 5;
                    // if (d.value == INF) {
                    //     // TODO '15' is a stand-in for [I don't even know anymore, 1/6?] of the font size in px.
                    //     y = y + 5;
                    // }
                } else if (R[i] < contourYLimits[1]) {
                    // isochron intersects plot boundary at right
                    yOffset = 10 * slope;
                    x = window.innerWidth - plot.margin.right + 10;
                    y = plot.margin.top + plot.yAxisScale(R[i] * (plot.lambda.U234 / plot.lambda.U238)) - yOffset;
                    // if (d.value == INF) {
                    //     // TODO '15' is a stand-in for half of the font size in px.
                    //     y = y + 15;
                    // }
                } else {
                    x = window.innerWidth - plot.margin.right + 10;
                    y = plot.margin.top - 5;
                }

                var angle = -(Math.atan(slope) * (180 / Math.PI));  // Must convert from radians to degrees
                return "translate (" + x + "," + y + ") rotate(" + angle + ")";
                })
            .attr("fill-opacity", function (d, i) {
                if ((T[i] < xminpoints[i]) || (R[i] < yminpoints[i])) {
                    return 0;
                } else {
                    return 1;
                }
            });

        labels.exit().remove();

        plot.ar48iContours.exit().remove();
        plot.isochrons.exit().remove();

    }
};

plot.removeEvolutionMatrix = function () {
    if (plot.evolutionMatrixVisible) {
        plot.evolutionGroup.remove();
        plot.area.aroundEdge.remove();
        plot.evolutionMatrixVisible = false;
    }
};


