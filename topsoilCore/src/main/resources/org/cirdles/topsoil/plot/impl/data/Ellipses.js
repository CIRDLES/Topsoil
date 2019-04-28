/**
 * @author John Zeringue
 */

if (plot.ellipsesVisible == null) {
    plot.ellipsesVisible = false;
}

// Calculate constant used to draw ellipses
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

    var ellipseData = data.map(function (d) {
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
            plot.uncertainty,
            numeric.dot(controlPointsBase, r))
            .map(shift(d.x, d.y));

        points.selected = d.selected;

        return points;
    });

    return ellipseData;
};

plot.drawEllipses = function() {

    // Removes ellipses before re-drawing them
    if (plot.ellipsesVisible) {
        plot.removeEllipses();
    }

    // Creates a separate SVG group for ellipses.
    if (plot.pointGroup == null) {
        plot.ellipseGroup = plot.dataGroup.append("g")
            .attr("class", "ellipseGroup");
    } else {
        plot.ellipseGroup = plot.dataGroup.insert("g", ".pointGroup")
            .attr("class", "ellipseGroup");
    }

    plot.ellipsesVisible = true;
    plot.updateEllipses();
};

plot.updateEllipses = function() {
    if (plot.ellipsesVisible) {

        // the model join (http://bost.ocks.org/mike/join/)
        var ellipses = plot.ellipseGroup.selectAll(".ellipse").data(plot.ellipseData);

        // If the dataset is smaller than before, this will remove any unnecessary ellipse elements.
        ellipses.exit().remove();

        // If the dataset is larger than before, this will create needed additional ellipse elements.
        ellipses.enter().append("path")
            .attr("class", "ellipse");

        // The following applies variable model to all ellipse elements.
        ellipses.attr("d", function (d) {
                var ellipsePath = d3.svg.line()
                    .x(function (datum) {
                        return plot.xAxisScale(datum[0]);
                    })
                    .y(function (datum) {
                        return plot.yAxisScale(datum[1]);
                    })
                    .interpolate(function (points) {
                        var i = 1, path = [points[0][0], ",", points[0][1]];
                        while (i + 3 <= points.length) {
                            plot.cubicBezier(path, points[i++], points[i++], points[i++]);
                        }
                        return path.join("");
                    });

                return ellipsePath(d);
            })
            .attr("fill", function(d) {
                var fill;
                if (! d.selected) {
                    fill = 'gray';
                } else {
                    fill = plot.getProperty(Property.ELLIPSES_FILL);
                }
                return fill;
            })
            .attr("fill-opacity", plot.getProperty(Property.ELLIPSES_OPACITY) * 0.2)
            .attr("opacity", function (d) {
                return d.selected ? 1.0 : 0.0;
            })
            .attr("stroke", "black");
    }
};

plot.removeEllipses = function() {
    if (plot.ellipsesVisible) {
        plot.ellipseGroup.remove();
        plot.ellipsesVisible = false;
    }
};