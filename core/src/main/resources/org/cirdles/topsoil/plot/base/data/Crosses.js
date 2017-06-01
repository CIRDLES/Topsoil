/**
 * @author Jake Marotta
 */

if (plot.crossesVisible == null) {
    plot.crossesVisible = false;
}

plot.drawCrosses = function () {

    // Removes crosses before redrawing them.
    if (plot.crossesVisible) {
        plot.removeCrosses();
    }

    // Creates a separate SVG group for cross elements.
    if (plot.pointGroup == null) {
        plot.crossGroup = plot.dataGroup.append("g")
            .attr("class", "crossGroup");
    } else {
        plot.crossGroup = plot.dataGroup.insert("g", ".pointGroup")
            .attr("class", "crossGroup");
    }

    plot.crossesVisible = true;
    plot.updateCrosses();

};

plot.updateCrosses = function () {
    if (plot.crossesVisible) {

        var crosses = plot.crossGroup.selectAll(".cross").data(plot.data);

        crosses.exit().remove();

        var cEnter = crosses.enter().append("g").data(plot.data)
            .attr("class", "cross");

        // Adds the parts of the cross
        cEnter.append("line").attr("class", "HLine");
        cEnter.append("line").attr("class", "VLine");
        cEnter.append("line").attr("class", "topCap");
        cEnter.append("line").attr("class", "leftCap");
        cEnter.append("line").attr("class", "bottomCap");
        cEnter.append("line").attr("class", "rightCap");

        // Because 1px lines are so thin, their appearance is inconsistent with zooming in the plot when the opacity is
        // anything less than 100%. So, if the opacity is less than 100%, the stroke width is updated to be 2px instead
        // of 1px. This doesn't completely solve the problem, and there is a slight jump in line appearance between 100%
        // opacity and 99% opacity, but overall, zooming looks much, much smoother.
        var strokeWidth;
        if (plot.getProperty("Cross Opacity") < 1.0) {
            strokeWidth = 2;
        } else {
            strokeWidth = 1;
        }

        crosses.select(".HLine")
            .attr("y1", function (d) {
                return plot.yAxisScale(d.y);
            })
            .attr("y2", function (d) {
                return plot.yAxisScale(d.y);
            })
            .attr("x1", function (d) {
                return plot.xAxisScale(d.x - (plot.uncertainty * d.sigma_x));
            })
            .attr("x2", function (d) {
                return plot.xAxisScale(d.x + (plot.uncertainty * d.sigma_x));
            })
            .attr("opacity", plot.getProperty("Cross Opacity"))
            .attr("stroke-width", strokeWidth)
            .attr("stroke", plot.getProperty("Cross Fill Color"));

        crosses.select(".VLine")
            .attr("y1", function (d) {
                return plot.yAxisScale(d.y - (plot.uncertainty * d.sigma_y));
            })
            .attr("y2", function (d) {
                return plot.yAxisScale(d.y + (plot.uncertainty * d.sigma_y));
            })
            .attr("x1", function (d) {
                return plot.xAxisScale(d.x);
            })
            .attr("x2", function (d) {
                return plot.xAxisScale(d.x);
            })
            .attr("opacity", plot.getProperty("Cross Opacity"))
            .attr("stroke-width", strokeWidth)
            .attr("stroke", plot.getProperty("Cross Fill Color"));

        crosses.select(".topCap")
            .attr("y1", function (d) {
                return plot.yAxisScale(d.y + (plot.uncertainty * d.sigma_y));
            })
            .attr("y2", function (d) {
                return plot.yAxisScale(d.y + (plot.uncertainty * d.sigma_y));
            })
            .attr("x1", function (d) {
                return plot.xAxisScale(d.x - 0.2*(plot.uncertainty * d.sigma_x));
            })
            .attr("x2", function (d) {
                return plot.xAxisScale(d.x + 0.2*(plot.uncertainty * d.sigma_x));
            })
            .attr("opacity", plot.getProperty("Cross Opacity"))
            .attr("stroke-width", strokeWidth)
            .attr("stroke", plot.getProperty("Cross Fill Color"));

        crosses.select(".leftCap")
            .attr("y1", function (d) {
                return plot.yAxisScale(d.y - 0.2*(plot.uncertainty * d.sigma_y));
            })
            .attr("y2", function (d) {
                return plot.yAxisScale(d.y + 0.2*(plot.uncertainty * d.sigma_y));
            })
            .attr("x1", function (d) {
                return plot.xAxisScale(d.x - (plot.uncertainty * d.sigma_x));
            })
            .attr("x2", function (d) {
                return plot.xAxisScale(d.x - (plot.uncertainty * d.sigma_x));
            })
            .attr("opacity", plot.getProperty("Cross Opacity"))
            .attr("stroke-width", strokeWidth)
            .attr("stroke", plot.getProperty("Cross Fill Color"));

        crosses.select(".bottomCap")
            .attr("y1", function (d) {
                return plot.yAxisScale(d.y - (plot.uncertainty * d.sigma_y));
            })
            .attr("y2", function (d) {
                return plot.yAxisScale(d.y - (plot.uncertainty * d.sigma_y));
            })
            .attr("x1", function (d) {
                return plot.xAxisScale(d.x - 0.2*(plot.uncertainty * d.sigma_x));
            })
            .attr("x2", function (d) {
                return plot.xAxisScale(d.x + 0.2*(plot.uncertainty * d.sigma_x));
            })
            .attr("opacity", plot.getProperty("Cross Opacity"))
            .attr("stroke-width", strokeWidth)
            .attr("stroke", plot.getProperty("Cross Fill Color"));

        crosses.select(".rightCap")
            .attr("y1", function (d) {
                return plot.yAxisScale(d.y - 0.2*(plot.uncertainty * d.sigma_y));
            })
            .attr("y2", function (d) {
                return plot.yAxisScale(d.y + 0.2*(plot.uncertainty * d.sigma_y));
            })
            .attr("x1", function (d) {
                return plot.xAxisScale(d.x + (plot.uncertainty * d.sigma_x));
            })
            .attr("x2", function (d) {
                return plot.xAxisScale(d.x + (plot.uncertainty * d.sigma_x));
            })
            .attr("opacity", plot.getProperty("Cross Opacity"))
            .attr("stroke-width", strokeWidth)
            .attr("stroke", plot.getProperty("Cross Fill Color"));
    }
};

plot.removeCrosses = function () {
    if (plot.crossesVisible) {
        plot.crossGroup.remove();
        plot.crossesVisible = false;
    }
};