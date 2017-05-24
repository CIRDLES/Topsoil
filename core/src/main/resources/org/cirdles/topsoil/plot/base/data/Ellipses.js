/**
 * @author John Zeringue
 */

if (plot.ellipsesVisible == null) {
    plot.ellipsesVisible = false;
}

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

        points.Selected = d.Selected;

        return points;
    });

    return ellipseData;
};

plot.drawEllipses = function(ellipseData) {

    // Removes ellipses before re-drawing them
    if (plot.ellipsesVisible) {
        topsoil.bridge.println("HERE");
        plot.removeEllipses();
    }

    if (plot.pointGroup == null) {
        plot.ellipseGroup = plot.dataGroup.append("g")
            .attr("class", "ellipseGroup");
    } else {
        plot.ellipseGroup = plot.dataGroup.insert("g", ".pointGroup")
            .attr("class", "ellipseGroup");
    }

    var ellipses = plot.ellipses = plot.ellipseGroup.selectAll(".ellipse")
        .data(ellipseData);

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


    plot.ellipsesVisible = true;
    topsoil.bridge.println("End of plot.updateEllipses");
    plot.updateEllipses();
};

plot.updateEllipses = function() {
    //don't redraw ellipses if they're not visible
    topsoil.bridge.println("plot.ellipsesVisible: " + plot.ellipsesVisible);
    if (plot.ellipsesVisible) {
        var ellipses = plot.ellipses;

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
            });
        ellipses.attr("fill", function(d) {
                var fill;

                if (!d['Selected']) {
                    fill = 'gray';
                } else {
                    fill = plot.getProperty('Ellipse Fill Color');
                }

                return fill;
        });

        ellipses.exit().remove();
    }
};

plot.removeEllipses = function() {
    if (plot.ellipsesVisible) {
        plot.ellipseGroup.remove();
        plot.ellipsesVisible = false;
    }
};