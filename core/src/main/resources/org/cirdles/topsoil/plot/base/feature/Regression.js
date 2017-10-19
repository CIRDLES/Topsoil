/**
 * @author Emily Coleman
 */

if(plot.regressionVisible == null) {
    plot.regressionVisible = false;
}

/**
 *The y-intercept of the McLean Regression line.
 * @type {number}
 */
plot.regressionYIntercept = 0;

/**
 * The slope of the McLean Regression line.
 * @type {number}
 */
plot.regressionSlope = 0;

/**
 * Uses the RegressionLine.java class to calculate the McLean Regression.
 *
 */
plot.calculateRegressionLine = function() {
    var data = plot.data;

    var x_list = [];
    var y_list = [];
    var sigma_x_list = [];
    var sigma_y_list = [];
    var rho_list = [];

    var fillLists = data.map(function (d) {
        x_list.push(d.x);
        y_list.push(d.y);
        sigma_x_list.push(d.sigma_x);
        sigma_y_list.push(d.sigma_y);
        rho_list.push(d.rho);
    });

    topsoil.regression.fitLineToDataFor2D(x_list.toString(), y_list.toString(), sigma_x_list.toString(), sigma_y_list.toString(), rho_list.toString());

    plot.regressionSlope = topsoil.regression.getSlope();
    plot.regressionYIntercept = topsoil.regression.getIntercept();
};

/**
 * Creates the SVG elements required to display the regression line.
 */
plot.drawRegressionLine = function() {
    plot.calculateRegressionLine();

    // Remove line before redrawing it.
    if (plot.regressionVisible) {
        plot.removeRegressionLine();
    }

    // Create a separate SVG group for the regression line.
    plot.regressionGroup = plot.area.clipped.insert("g", ".dataGroup")
        .attr("class", "regressionGroup");

    plot.regressionVisible = true;
    plot.updateRegressionLine();
};

plot.updateRegressionLine = function() {

    if (plot.regressionVisible) {

        // Draw a line from point x1, y1 to point x2, y2.
        x1 = 0;
        y1 = plot.regressionYIntercept;
        x2 = plot.xAxisScale.domain()[0];
        y2 = (plot.regressionSlope * x2) + plot.regressionYIntercept; //y = mx + b

        line = plot.regressionGroup.selectAll(".regression")
            .append("line")
            .attr("class", "regression")
            .attr("x1", x1)
            .attr("y1", y1)
            .attr("x2", x2)
            .attr("y2", y2)
            .attr("stroke", "black")
            .attr("stroke-width", 1);
    }
};

plot.removeRegressionLine = function() {
    if (plot.regressionVisible) {
        plot.regressionGroup.remove();
        plot.regressionVisible = false;
    }
};
