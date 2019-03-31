/**
 * @author Jake Marotta
 */

plot.cubicBezier = function (path, p1, p2, p3) {
    path.push(
        "C", p1[0], ",", p1[1],
        ",", p2[0], ",", p2[1],
        ",", p3[0], ",", p3[1]);
};

// utilities for generating path model elements
var moveTo = function (path, p) {
    path.push("M", p[0], ",", p[1]);
};

var lineTo = function (path, p) {
    path.push("L", p[0], ",", p[1]);
};

var close = function (path) {
    path.push("Z");
};

var pluck = function (maps, key) {
    var values = [maps.length];
    maps.forEach( function (m, i) {
        values[i] = m[key];
    });
    return values
};

// math functions
var add = numeric.add;
var div = numeric.div;
var mul = numeric.mul;
var sub = numeric.sub;
var exp = Math.exp;
var dot = numeric.dot;
var neg = numeric.neg;