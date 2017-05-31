/**
 * @author Jake Marotta
 */

if (plot.crossesVisible == null) {
    plot.crossesVisible = false;
}

plot.drawCrosses = function (data) {

    if (plot.crossesVisible) {
        // plot.removeCrosses();
    }

    if (plot.pointGroup == null) {
        plot.crossGroup = plot.dataGroup.append("g")
            .attr("class", "crossGroup");
    } else {
        plot.crossGroup = plot.dataGroup.insert("g", ".pointGroup")
            .attr("class", "crossGroup");
    }

    var crosses = plot.crosses = plot.crossGroup.selectAll(".cross")
        .data(data);
    var c = crosses.enter().append("g").data(data);

    c.attr("class", "cross");

    // Adds the parts of the cross
    c.append("line").attr("class", "HLine");
    c.append("line").attr("class", "VLine");
    c.append("line").attr("class", "topCap");
    c.append("line").attr("class", "leftCap");
    c.append("line").attr("class", "bottomCap");
    c.append("line").attr("class", "rightCap");

    plot.crossesVisible = true;
    plot.updateCrosses();

};

plot.updateCrosses = function () {
    if (plot.crossesVisible) {

        var strokeWidth;

        if (plot.getProperty("Cross Opacity") < 1.0) {
            strokeWidth = 2;
        } else {
            strokeWidth = 1;
        }

        plot.crosses.select(".HLine")
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

        plot.crosses.select(".VLine")
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

        plot.crosses.select(".topCap")
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

        plot.crosses.select(".leftCap")
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

        plot.crosses.select(".bottomCap")
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

        plot.crosses.select(".rightCap")
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

        plot.crosses.exit().remove();
    }
};

plot.removeCrosses = function () {
    if (plot.crossesVisible) {
        plot.crossGroup.remove();
        plot.crossesVisible = false;
    }
};