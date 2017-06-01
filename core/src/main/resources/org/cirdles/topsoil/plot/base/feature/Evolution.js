/**
 * @author Jake Marotta
 */

evolution = plot.evolution = {};

evolution.isochronValues = [];
evolution.ar48iContourValues = [];

evolution.ar08lim = [2];
evolution.ar48lim = [2];

var mxp = {};

var lambda = {};

var INF = Number.MAX_VALUE;

var diag = function (x, y, z) {
    return [
        [x, 0, 0],
        [0, y, 0],
        [0, 0, z]
    ];
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

var abmat = [
    [0, 0, 0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0, 0, 0]
];

var xminpoints = [0, 0, 0, 0, 0, 0, 0, 0];
var yminpoints = [0, 0, 0, 0, 0, 0, 0, 0];

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

        if (decrement === Infinity || decrement === NaN) {  //decrement === INF?
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



if (plot.evolutionMatrixVisible == null) {
    plot.evolutionMatrixVisible = false;
}

plot.calculateIsochrons = function () {

    // TODO Allow support for custom isochrons?
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

    lambda.U238 = topsoil.defaultLambda.U238;
    // lambda.U238 = 1.55125e-10;
    lambda.U234 = topsoil.defaultLambda.U234;
    // lambda.U234 = 2.82206e-6;
    lambda.Th230 = topsoil.defaultLambda.Th230;
    // lambda.Th230 = 9.1705e-6;

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

    mxp.GUTh = function (t) {
        return diag(exp(-lambda.U238 * t), exp(-lambda.U234 * t), exp(-lambda.Th230 * t));
    };

    mxp.QinvUTh = [
        [(lambda.U234 * lambda.U238) / ((lambda.Th230 - lambda.U238) * (lambda.U234 - lambda.U238)), 0, 0],
        [-(lambda.U234 * lambda.U238) / ((lambda.Th230 - lambda.U234) * (lambda.U234 - lambda.U238)), lambda.U234 / (lambda.Th230 - lambda.U234), 0],
        [(lambda.U234 * lambda.U238) / ((lambda.Th230 - lambda.U234) * (lambda.Th230 - lambda.U238)), -lambda.U234 / (lambda.Th230 - lambda.U234), 1]
    ];

    mxp.UTh = function (t) {
        return dot(dot(mxp.QUTh, mxp.GUTh(t)), mxp.QinvUTh);
    };

    mxp.UTh_0 = function (t) {
        return dot(dot(mxp.QUTh[2], mxp.GUTh(t)), mxp.QinvUTh); // For the 230 concentration only (to solve for root)
    };

    mxp.UTh_4 = function (t) {
        return dot(dot(mxp.QUTh[1], mxp.GUTh(t)), mxp.QinvUTh); // For the 234 concentration only (to solve for root)
    };

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

};

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


        // now find where lines intesect bounding box (ar48lim, ar08lim)
        var L = add(abmat[0], dot(abmat[1], r08lim[0])); // y-coord of intersections with left boundary of box
        var R = add(abmat[0], dot(abmat[1], r08lim[1])); // y-coord of intersections with right boundary of box
        var B = div(sub(r48lim[0], abmat[0]), abmat[1]); // x-coord of intersections with bottom boundary of box
        var T = div(sub(r48lim[1], abmat[0]), abmat[1]); // x-coord of intersections with top boundary of box

        var xendpoints = [
            add(mul(dot(r08lim[0], ones(evolution.isochronValues.length)), gt(L, r48lim[0])), mul(B, le(L, r48lim[0]))),
            add(mul(dot(r08lim[1], ones(evolution.isochronValues.length)), lt(R, r48lim[1])), mul(T, ge(R, r48lim[1])))
        ];

        var yendpoints = [
            add(mul(L, gt(L, r48lim[0])), mul(dot(r48lim[0], ones(evolution.isochronValues.length)), le(L, r48lim[0]))),
            add(mul(R, lt(R, r48lim[1])), mul(dot(r48lim[1], ones(evolution.isochronValues.length)), ge(R, r48lim[1])))
        ];

        // if endpoints extend beyond min possible (n0 = [1 0 0]), truncate them further
        xendpoints[0] = max(xendpoints[0], xminpoints); // since isochrons have positive slope, use maximum
        yendpoints[0] = max(yendpoints[0], yminpoints);

        // transform into activity ratios, svg plot box coordinates
        xendpoints = mul(xendpoints, lambda.Th230 / lambda.U238);
        yendpoints = mul(yendpoints, lambda.U234 / lambda.U238);

        var nts = 10;

        var nar48is = evolution.ar48iContourValues.length;
        var tv = repmat(linspace(0, 1e6, nts - 1).concat([2e6]), nar48is, 1);

        // if any ar48i contours start above
        if (any(gt(evolution.ar48iContourValues, evolution.ar48lim[1]))) {
            var iover = find(gt(evolution.ar48iContourValues, evolution.ar48lim[1]));
            var ar48overs = select(iover, evolution.ar48iContourValues);

            // solve for when ar48i = ar48imax
            ar48overs.forEach(function (ar48over, iar48over) {
                var root = function (t) {
                    return sub(dot(mxp.UTh_4(t), [1, ar48over * lambda.U238 / lambda.U234, 0]),
                        div(mul(evolution.ar48lim[1], lambda.U238), lambda.U234));
                };
                var tstart = fzero(root, 50e3);

                tv[iover[iar48over]] = linspace(tstart, 1e6, nts - 1).concat([5e6]);
            });
        }

        // if any contours start below
        if (any(lt(evolution.ar48iContourValues, evolution.ar48lim[0]))) {
            var iunder = find(lt(evolution.ar48iContourValues, evolution.ar48lim[0]));
            var ar48unders = select(iunder, evolution.ar48iContourValues);

            // solve for when ar48i = ar48imax
            ar48unders.forEach(function (ar48under, iar48under) {
                var root = function (t) {
                    return sub(dot(mxp.UTh_4(t), [1, ar48under * lambda.U238 / lambda.U234, 0]),
                        div(mul(evolution.ar48lim[0], lambda.U238), lambda.U234));
                };
                var tstart = fzero(root, 50e3);

                tv[iunder[iar48under]] = linspace(tstart, 1e6, nts - 1).concat([5e6]);
            });
        }

        var xy = zeros(nar48is, 2, nts);
        var dardt = zeros(nar48is, 2, nts);

        evolution.ar48iContourValues.forEach(function (ar48i, iar48i) {
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
            moveTo(path, [plot.xAxisScale(xy[iar48i][0][0]), plot.yAxisScale(xy[iar48i][1][0])]);

            for (var i = 1; i < nts; i++) {
                var deltaTOver3 = (tv[iar48i][i] - tv[iar48i][i - 1]) / 3;

                var p1 = [
                    plot.xAxisScale(xy[iar48i][0][i - 1] + deltaTOver3 * dardt[iar48i][0][i - 1]),
                    plot.yAxisScale(xy[iar48i][1][i - 1] + deltaTOver3 * dardt[iar48i][1][i - 1])
                ];

                var p2 = [
                    plot.xAxisScale(xy[iar48i][0][i] - deltaTOver3 * dardt[iar48i][0][i]),
                    plot.yAxisScale(xy[iar48i][1][i] - deltaTOver3 * dardt[iar48i][1][i])
                ];

                var p3 = [
                    plot.xAxisScale(xy[iar48i][0][i]),
                    plot.yAxisScale(xy[iar48i][1][i])
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


