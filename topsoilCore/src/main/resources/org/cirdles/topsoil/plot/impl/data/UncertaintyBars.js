/**
 * @author marottajb
 */

if (plot.uncertaintyBarsVisible == null) {
    plot.uncertaintyBarsVisible = false;
}

plot.drawUncertaintyBars = function () {

    // Removes Uncertainty Bars before redrawing them.
    if (plot.uncertaintyBarsVisible) {
        plot.removeUncertaintyBars();
    }

    // Creates a separate SVG group for Uncertainty Bar elements.
    if (plot.pointGroup == null) {
        plot.uncertaintyBarGroup = plot.dataGroup.append("g")
            .attr("class", "uncertaintyBarGroup");
    } else {
        plot.uncertaintyBarGroup = plot.dataGroup.insert("g", ".pointGroup")
            .attr("class", "uncertaintyBarGroup");
    }

    plot.uncertaintyBarsVisible = true;
    plot.updateUncertaintyBars();

};

plot.updateUncertaintyBars = function () {
    if (plot.uncertaintyBarsVisible) {

        var fill = plot.getProperty(Property.UNCTBARS_FILL),
            opacity = plot.getProperty(Property.UNCTBARS_OPACITY);

        var uncertaintyBars = plot.uncertaintyBarGroup.selectAll(".uncertaintyBar").data(plot.data);

        uncertaintyBars.exit().remove();

        var cEnter = uncertaintyBars.enter().append("g").data(plot.data)
            .attr("class", "uncertaintyBar");

        // Adds the parts of the Uncertainty Bar
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
        if (opacity < 1.0) {
            strokeWidth = 2;
        } else {
            strokeWidth = 1;
        }

        uncertaintyBars.select(".HLine")
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
            .attr("opacity", function (d) {
                return d.selected ? opacity : 0.0;
            })
            .attr("stroke-width", strokeWidth)
            .attr("stroke", fill);

        uncertaintyBars.select(".VLine")
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
            .attr("opacity", function (d) {
                return d.selected ? opacity : 0.0;
            })
            .attr("stroke-width", strokeWidth)
            .attr("stroke", fill);

        uncertaintyBars.select(".topCap")
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
            .attr("opacity", function (d) {
                return d.selected ? opacity : 0.0;
            })
            .attr("stroke-width", strokeWidth)
            .attr("stroke", fill);

        uncertaintyBars.select(".leftCap")
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
            .attr("opacity", function (d) {
                return d.selected ? opacity : 0.0;
            })
            .attr("stroke-width", strokeWidth)
            .attr("stroke", fill);

        uncertaintyBars.select(".bottomCap")
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
            .attr("opacity", function (d) {
                return d.selected ? opacity : 0.0;
            })
            .attr("stroke-width", strokeWidth)
            .attr("stroke", fill);

        uncertaintyBars.select(".rightCap")
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
            .attr("opacity", function (d) {
                return d.selected ? opacity : 0.0;
            })
            .attr("stroke-width", strokeWidth)
            .attr("stroke", fill);
    }
};

plot.removeUncertaintyBars = function () {
    if (plot.uncertaintyBarsVisible) {
        plot.uncertaintyBarGroup.remove();
        plot.uncertaintyBarsVisible = false;
    }
};