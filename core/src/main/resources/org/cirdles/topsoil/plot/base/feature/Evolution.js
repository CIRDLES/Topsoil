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
var lambda = {};

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

/**
 * A matrix with two columns (they look like rows below) and a row for each plotted isochron. The first column contains
 * slopes, and the second column contains y-intercepts, so that y = (abmat[n][0] * x) + abmat[n][1].
 *
 * @type {[*]}
 */
var abmat = [
    [0, 0, 0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0, 0, 0]
];

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

    if (plot.getProperty('U238') != null && !isNaN(plot.getProperty('U238'))) {
        lambda.U238 = plot.getProperty('U238');
    } else {
        lambda.U238 = topsoil.defaultLambda.U238;
    }

    if (plot.getProperty('U234') != null && !isNaN(plot.getProperty('U234'))) {
        lambda.U234 = plot.getProperty('U234');
    } else {
        lambda.U234 = topsoil.defaultLambda.U234;
    }

    if (plot.getProperty('Th230') != null && !isNaN(plot.getProperty('Th230'))) {
        lambda.Th230 = plot.getProperty('Th230');
    } else {
        lambda.Th230 = topsoil.defaultLambda.Th230;
    }

    mxp.A = [
        [-lambda.U238, 0, 0],
        [lambda.U238, -lambda.U234, 0],
        [0, lambda.U234, -lambda.Th230]
    ];

    // I don't know what Q is, but this is some kind of 3x3 matrix describing something to do with UTh.
    mxp.QUTh = [
        [((lambda.Th230 - lambda.U238) * (lambda.U234 - lambda.U238)) / (lambda.U234 * lambda.U238), 0, 0],
        [(lambda.Th230 - lambda.U238) / lambda.U234, (lambda.Th230 - lambda.U234) / lambda.U234, 0],
        [1, 1, 1]
    ];

    // Returns a 3x3 diagonal matrix where (0,0) = e^((opposite of U238) * parameter),
    //                                     (1,1) = e^((opposite of U234) * parameter), and
    //                                     (2,2) = e^((opposite of Th230) * parameter)
    //     ... where e is Euler's number.
    mxp.GUTh = function (t) {
        return diag(exp(-lambda.U238 * t), exp(-lambda.U234 * t), exp(-lambda.Th230 * t));
    };

    // I assume this is the "Q" of the inverse of UTh
    mxp.QinvUTh = [
        [(lambda.U234 * lambda.U238) / ((lambda.Th230 - lambda.U238) * (lambda.U234 - lambda.U238)), 0, 0],
        [-(lambda.U234 * lambda.U238) / ((lambda.Th230 - lambda.U234) * (lambda.U234 - lambda.U238)), lambda.U234 / (lambda.Th230 - lambda.U234), 0],
        [(lambda.U234 * lambda.U238) / ((lambda.Th230 - lambda.U234) * (lambda.Th230 - lambda.U238)), -lambda.U234 / (lambda.Th230 - lambda.U234), 1]
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

    // t =
    evolution.isochronValues.forEach(function (t, it) {
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

    evolution.nts = 10;
    var nar48is = evolution.ar48iContourValues.length;
    evolution.tv = repmat(linspace(0, 1e6, evolution.nts - 1).concat([2e6]), nar48is, 1);

    evolution.xy = zeros(nar48is, 2, evolution.nts);
    evolution.dardt = zeros(nar48is, 2, evolution.nts);

    evolution.ar48iContourValues.forEach(function (ar48i, iar48i) {
        evolution.tv[iar48i].forEach(function (t, it) {
            var n0 = [1, ar48i * lambda.U238 / lambda.U234, 0];
            var nt = dot(mxp.UTh(t), n0);

            evolution.xy[iar48i][0][it] = nt[2] / nt[0] * lambda.Th230 / lambda.U238;
            evolution.xy[iar48i][1][it] = nt[1] / nt[0] * lambda.U234 / lambda.U238;

            var dar48dnt1 = -nt[1] / nt[0] / nt[0] * lambda.U234 / lambda.U238;
            var dar48dnt2 = 1 / nt[0] * lambda.U234 / lambda.U238;
            var dar48dnt3 = 0;
            var dar08dnt1 = -nt[2] / nt[0] / nt[0] * lambda.Th230 / lambda.U238;
            var dar08dnt2 = 0;
            var dar08dnt3 = 1 / nt[0] * lambda.Th230 / lambda.U238;

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

    if (evolution.isochronValues.length <= 0 || evolution.ar48iContourValues <= 0) {
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

    plot.evolutionMatrixVisible = true;
    plot.updateEvolutionMatrix();
};

plot.updateEvolutionMatrix = function () {

    if (plot.evolutionMatrixVisible) {

        evolution.ar08lim = plot.xAxisScale.domain();
        evolution.ar48lim = plot.yAxisScale.domain();

        var r08lim = dot(evolution.ar08lim, lambda.U238 / lambda.Th230);
        var r48lim = dot(evolution.ar48lim, lambda.U238 / lambda.U234);

        // now find where lines intersect bounding box (ar48lim, ar08lim)
        var L = add(abmat[0], dot(abmat[1], r08lim[0])); // y-coord of intersections with left boundary of box
        var R = add(abmat[0], dot(abmat[1], r08lim[1])); // y-coord of intersections with right boundary of box
        var B = div(sub(r48lim[0], abmat[0]), abmat[1]); // x-coord of intersections with bottom boundary of box
        var T = div(sub(r48lim[1], abmat[0]), abmat[1]); // x-coord of intersections with top boundary of box

        var xendpoints = [
            add(mul(dot(r08lim[0], ones(evolution.isochronValues.length)), gt(L, r48lim[0])), mul(B, le(L, r48lim[0]))),
            add(mul(dot(r08lim[1], ones(evolution.isochronValues.length)), lt(R, r48lim[1])), mul(T, ge(R, r48lim[1])))
        ];
        //
        // var xendpoints = [
        //     add(mul(dot(r08lim[0], ones(evolution.isochronValues.length)), r48lim[0]), mul(B, r48lim[0])),
        //     add(mul(dot(r08lim[1], ones(evolution.isochronValues.length)), r48lim[1]), mul(T, r48lim[1]))
        // ];
        //
        var yendpoints = [
            add(mul(L, gt(L, r48lim[0])), mul(dot(r48lim[0], ones(evolution.isochronValues.length)), le(L, r48lim[0]))),
            add(mul(R, lt(R, r48lim[1])), mul(dot(r48lim[1], ones(evolution.isochronValues.length)), ge(R, r48lim[1])))
        ];
        //
        // var yendpoints = [
        //     add(mul(L, gt(L, r48lim[0])), mul(dot(r48lim[0], ones(evolution.isochronValues.length)), le(L, r48lim[0]))),
        //     add(mul(R, lt(R, r48lim[1])), mul(dot(r48lim[1], ones(evolution.isochronValues.length)), ge(R, r48lim[1])))
        // ];


        // if endpoints extend beyond min possible (n0 = [1 0 0]), truncate them further
        xendpoints[0] = max(xendpoints[0], xminpoints); // since isochrons have positive slope, use maximum
        yendpoints[0] = max(yendpoints[0], yminpoints);

        // transform into activity ratios, svg plot box coordinates
        xendpoints = mul(xendpoints, lambda.Th230 / lambda.U238);
        yendpoints = mul(yendpoints, lambda.U234 / lambda.U238);

        // var nts = 10;
        //
        // var nar48is = evolution.ar48iContourValues.length;
        // var tv = repmat(linspace(0, 1e6, nts - 1).concat([2e6]), nar48is, 1);

        // if any ar48i contours start above
        // if (any(gt(evolution.ar48iContourValues, evolution.ar48lim[1]))) {
        //     var iover = find(gt(evolution.ar48iContourValues, evolution.ar48lim[1]));
        //     var ar48overs = select(iover, evolution.ar48iContourValues);
        //
        //     // solve for when ar48i = ar48imax
        //     ar48overs.forEach(function (ar48over, iar48over) {
        //         var root = function (t) {
        //             return sub(dot(mxp.UTh_4(t), [1, ar48over * lambda.U238 / lambda.U234, 0]),
        //                 div(mul(evolution.ar48lim[1], lambda.U238), lambda.U234));
        //         };
        //         var tstart = fzero(root, 50e3);
        //
        //         tv[iover[iar48over]] = linspace(tstart, 1e6, nts - 1).concat([5e6]);
        //     });
        // }
        //
        // // if any contours start below
        // if (any(lt(evolution.ar48iContourValues, evolution.ar48lim[0]))) {
        //     var iunder = find(lt(evolution.ar48iContourValues, evolution.ar48lim[0]));
        //     var ar48unders = select(iunder, evolution.ar48iContourValues);
        //
        //     // solve for when ar48i = ar48imax
        //     ar48unders.forEach(function (ar48under, iar48under) {
        //         var root = function (t) {
        //             return sub(dot(mxp.UTh_4(t), [1, ar48under * lambda.U238 / lambda.U234, 0]),
        //                 div(mul(evolution.ar48lim[0], lambda.U238), lambda.U234));
        //         };
        //         var tstart = fzero(root, 50e3);
        //
        //         tv[iunder[iar48under]] = linspace(tstart, 1e6, nts - 1).concat([5e6]);
        //     });
        // }

        // var xy = zeros(nar48is, 2, nts);
        // var dardt = zeros(nar48is, 2, nts);

        // evolution.ar48iContourValues.forEach(function (ar48i, iar48i) {
        //     tv[iar48i].forEach(function (t, it) {
        //         var n0 = [1, ar48i * lambda.U238 / lambda.U234, 0];
        //         var nt = dot(mxp.UTh(t), n0);
        //
        //         xy[iar48i][0][it] = nt[2] / nt[0] * lambda.Th230 / lambda.U238;
        //         xy[iar48i][1][it] = nt[1] / nt[0] * lambda.U234 / lambda.U238;
        //
        //         var dar48dnt1 = -nt[1] / nt[0] / nt[0] * lambda.U234 / lambda.U238;
        //         var dar48dnt2 = 1 / nt[0] * lambda.U234 / lambda.U238;
        //         var dar48dnt3 = 0;
        //         var dar08dnt1 = -nt[2] / nt[0] / nt[0] * lambda.Th230 / lambda.U238;
        //         var dar08dnt2 = 0;
        //         var dar08dnt3 = 1 / nt[0] * lambda.Th230 / lambda.U238;
        //
        //         var dardnt = [[dar08dnt1, dar08dnt2, dar08dnt3], [dar48dnt1, dar48dnt2, dar48dnt3]];
        //         var dntdt = dot(dot(mxp.A, mxp.UTh(t)), n0);
        //
        //         dardt[iar48i][0][it] = dot(dardnt, dntdt)[0];
        //         dardt[iar48i][1][it] = dot(dardnt, dntdt)[1];
        //     })
        // });

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

        plot.ar48iContours.attr('d', function (ar48i, iar48i) {
            var path = [];
            moveTo(path, [plot.xAxisScale(evolution.xy[iar48i][0][0]), plot.yAxisScale(evolution.xy[iar48i][1][0])]);

            for (var i = 1; i < evolution.nts; i++) {
                var deltaTOver3 = (evolution.tv[iar48i][i] - evolution.tv[iar48i][i - 1]) / 3;

                var p1 = [
                    plot.xAxisScale(evolution.xy[iar48i][0][i - 1] + deltaTOver3 * evolution.dardt[iar48i][0][i - 1]),
                    plot.yAxisScale(evolution.xy[iar48i][1][i - 1] + deltaTOver3 * evolution.dardt[iar48i][1][i - 1])
                ];

                var p2 = [
                    plot.xAxisScale(evolution.xy[iar48i][0][i] - deltaTOver3 * evolution.dardt[iar48i][0][i]),
                    plot.yAxisScale(evolution.xy[iar48i][1][i] - deltaTOver3 * evolution.dardt[iar48i][1][i])
                ];

                var p3 = [
                    plot.xAxisScale(evolution.xy[iar48i][0][i]),
                    plot.yAxisScale(evolution.xy[iar48i][1][i])
                ];

                plot.cubicBezier(path, p1, p2, p3);
            }

            return path.join('');
        });

        plot.ar48iContours.exit().remove();
        plot.isochrons.exit().remove();
    }
};

plot.removeEvolutionMatrix = function () {
    if (plot.evolutionMatrixVisible) {
        plot.evolutionGroup.remove();
        plot.evolutionMatrixVisible = false;
    }
};


