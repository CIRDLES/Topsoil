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

    // Creates a separate SVG group for points.
    plot.pointGroup = plot.dataGroup.append("g")
        .attr("class", "pointGroup");

    plot.pointsVisible = true;
    plot.updatePoints(data);
};

plot.updatePoints = function (data) {
    if (plot.pointsVisible) {

        // the data join (http://bost.ocks.org/mike/join/)
        var points = plot.pointGroup.selectAll(".point").data(data);

        // If the dataset is smaller than before, this will remove unnecessary point elements.
        points.exit().remove();

        // If the dataset is larger than before, this will add needed additional point elements.
        points.enter().append("circle")
            .attr("class", "point")
            .attr("r", 2.5);

        // This applies variable data to all point elements.
        points
            .attr("fill", plot.getProperty("Points Fill"))
            .attr("fill-opacity", plot.getProperty("Points Opacity"))
            .attr("cx", function (d) {
                return plot.xAxisScale(d.x);
            })
            .attr("cy", function (d) {
                return plot.yAxisScale(d.y);
            });
    }
};

plot.removePoints = function () {
    if (plot.pointsVisible) {
        plot.pointGroup.remove();
        plot.pointsVisible = false;
    }
};