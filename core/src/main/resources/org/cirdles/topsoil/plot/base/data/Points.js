/**
 * @author John Zeringue
 */


if (plot.pointsVisible == null) {
    plot.pointsVisible = false;
}

plot.drawPoints = function (data) {

    // Removes points before redrawing them.
    if (plot.pointsVisible) {
        plot.removePoints();
    }

    plot.pointGroup = plot.dataGroup.append("g")
        .attr("class", "pointGroup");

    // the data join (http://bost.ocks.org/mike/join/)
    var points = plot.points = plot.pointGroup.selectAll(".point")
        .data(data);

    // initialize new points
    points.enter().append("circle")
        .attr("class", "point")
        .attr("fill", plot.getProperty("Point Fill Color"))
        .attr("cx", function (d) {
            return plot.xAxisScale(d.x);
        })
        .attr("cy", function (d) {
            return plot.yAxisScale(d.y);
        })
        .attr("r", 3);

    plot.pointsVisible = true;
    plot.updatePoints();
};

plot.updatePoints = function () {
    if (plot.pointsVisible) {
        var points = plot.points;

        points
            .attr("fill", plot.getProperty("Point Fill Color"))
            .attr("cx", function (d) {
                return plot.xAxisScale(d.x);
            })
            .attr("cy", function (d) {
                return plot.yAxisScale(d.y);
            });

        points.exit().remove();
    }
};

plot.removePoints = function () {
    if (plot.pointsVisible) {
        plot.pointGroup.remove();
        plot.pointsVisible = false;
    }
};