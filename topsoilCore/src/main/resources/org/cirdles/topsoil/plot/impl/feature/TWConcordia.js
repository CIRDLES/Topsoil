
if (plot.twconcordiaVisible == null) {
    plot.twconcordiaVisible = false;
}

function constrainTValue(tValue) {
  return Math.max(1000000.0, Math.min(4544000000.0, tValue));
}

function approximateSegment(concordiaFn, path, startT, endT) {
  if (startT === endT) {
    return;
  }

  var p1 = concordiaFn(startT).plus(
      concordiaFn.prime(startT).times(3 / (endT - startT))
  ).scaleBy(plot.xAxisScale, plot.yAxisScale);
  var p2 = concordiaFn(endT).minus(
      concordiaFn.prime(endT).times(3 / (endT - startT))
  ).scaleBy(plot.xAxisScale, plot.yAxisScale);
  var p3 = concordiaFn(endT)
      .scaleBy(plot.xAxisScale, plot.yAxisScale);

  // append a cubic bezier to the path
  plot.cubicBezier(path, p1, p2, p3);
}

plot.drawTWConcordia = function() {

    // Removes the concordia before redrawing it
    if (plot.twconcordiaVisible) {
        plot.removeTWConcordia();
    }

    plot.twconcordiaGroup = plot.area.clipped.insert("g", ".dataGroup")
        .attr("class", "twconcordiaGroup");

    // initialize the concordia envelope
    plot.twconcordiaGroup.append("path")
        .attr("class", "twuncertaintyEnvelope")
        .attr("stroke", "none")
        .attr("shape-rendering", "geometricPrecision");

    // initialize the concordia
    plot.twconcordiaGroup.append("path")
        .attr("class", "twconcordia")
        .attr("fill", "none")
        .attr("shape-rendering", "geometricPrecision");

    plot.twconcordiaVisible = true;
    plot.updateTWConcordia();
};

plot.updateTWConcordia = function() {
    if (plot.twconcordiaVisible) {

        initializeWasserburg({
            LAMBDA_235: plot.lambda.U235,
            LAMBDA_238: plot.lambda.U238,
            R238_235S: plot.lambda.R238_235S
        });

        var xScale = plot.xAxisScale,
            yScale = plot.yAxisScale;

        var minX = Math.max(1, xScale.domain()[0]),
            maxX = Math.min(6500, xScale.domain()[1]),
            minY = Math.max(0.046, yScale.domain()[0]),
            maxY = Math.min(0.625, yScale.domain()[1]);

        var minT = Math.max(
            constrainTValue((Math.log1p(maxX) - Math.log(maxX)) / plot.lambda.U238),   // min T according to x
            constrainTValue(calculateDate(minY, 0.0))                                // min T according to y
        );

        var maxT = Math.min(
            constrainTValue((Math.log1p(minX) - Math.log(minX)) / plot.lambda.U238),   // max T according to x
            constrainTValue(calculateDate(maxY, 0.0))                                // max T according to y
        );

        var startPoint = wasserburg(minT).scaleBy(xScale, yScale);
        var endPoint = wasserburg(maxT).scaleBy(xScale, yScale);

        // build the concordia line
        plot.twconcordia = plot.twconcordiaGroup.select(".twconcordia")
            .attr("d", function () {
                // initialize path
                var path = [];
                moveTo(path, startPoint);

                // determine the step size using the number of pieces
                var pieces = 30;
                var stepSize = (maxT - minT) / pieces;

                // build the pieces
                for (var i = 0; i < pieces; i++) {
                    approximateSegment(wasserburg, path, minT + stepSize * i, minT + stepSize * (i + 1));
                }
                return path.join("");
            })
            .attr("stroke", plot.getOption(PlotOption.CONCORDIA_LINE_FILL))
            .attr("stroke-width", 2);

        if (plot.getOption(PlotOption.CONCORDIA_ENVELOPE)) {
            plot.twconcordiaGroup.select(".twuncertaintyEnvelope")
                .attr("d", function () {

                    var upperMinT = Math.min(
                        constrainTValue(newtonMethod(wasserburg.upperEnvelope.x, minX)),
                        constrainTValue(newtonMethod(wasserburg.upperEnvelope.y, minY))
                    );

                    var upperMaxT = Math.min(
                        constrainTValue(newtonMethod(wasserburg.upperEnvelope.x, maxX)),
                        constrainTValue(newtonMethod(wasserburg.upperEnvelope.y, maxY))
                    );


                    // initialize path
                    var path = [];
                    moveTo(path, wasserburg.upperEnvelope(upperMinT).scaleBy(xScale, yScale));

                    // determine the step size using the number of pieces
                    var pieces = 30;
                    var stepSize = (upperMaxT - upperMinT) / pieces;

                    // build the pieces
                    for (var i = 0; i < pieces; i++) {
                        approximateSegment(wasserburg.upperEnvelope, path, upperMinT + stepSize * i, upperMinT + stepSize * (i + 1));
                    }

                    var lowerMinT = Math.min(
                        constrainTValue(newtonMethod(wasserburg.lowerEnvelope.x, minX)),
                        constrainTValue(newtonMethod(wasserburg.lowerEnvelope.y, minY))
                    );

                    var lowerMaxT = Math.min(
                        constrainTValue(newtonMethod(wasserburg.lowerEnvelope.x, maxX)),
                        constrainTValue(newtonMethod(wasserburg.lowerEnvelope.y, maxY))
                    );

                    lineTo(path, wasserburg.lowerEnvelope(lowerMaxT).scaleBy(xScale, yScale));

                    stepSize = (lowerMaxT - lowerMinT) / pieces;

                    // build the pieces
                    for (i = 0; i < pieces; i++) {
                        approximateSegment(wasserburg.lowerEnvelope, path, lowerMaxT - stepSize * i, lowerMaxT - stepSize * (i + 1));
                    }

                    close(path);

                    return path.join("");
                })
                .attr("fill", plot.getOption(PlotOption.CONCORDIA_ENVELOPE_FILL));
        }

        plot.t.domain([minT, maxT]);

        var tDistance = Math.sqrt((startPoint.x - endPoint.x)^2 + (startPoint.y - endPoint.y)^2);
        var ticks = plot.t.ticks(Math.max(3, Math.floor(tDistance / 3)));

        var concordiaTicks = plot.twconcordiaGroup.selectAll(".concordiaTicks")
            .data(ticks);
        concordiaTicks
            .enter()
            .append("circle")
            .attr("class", "concordiaTicks")
            .attr("r", 5)
            .style('stroke-width', 2)
            .style("stroke", "black")
            .style("fill", "white");
        concordiaTicks
            .attr("cx", function (t) {
                return xScale(wasserburg.x(t));
            })
            .attr("cy", function (t) {
                return yScale(wasserburg.y(t));
            });
        concordiaTicks
            .exit()
            .remove();

        var tickLabels = plot.twconcordiaGroup.selectAll(".tickLabel")
            .data(ticks);
        tickLabels
            .enter()
            .append("text")
            .attr("font-family", "sans-serif")
            .attr("class", "tickLabel");
        tickLabels
            .attr("x", function (t) {
                return xScale(wasserburg.x(t)) + 12;
            })
            .attr("y", function (t) {
                return yScale(wasserburg.y(t)) + 5;
            })
            .text(function (t) {
                return t / 1000000;
            });
        tickLabels
            .exit()
            .remove();
    }
};

plot.removeTWConcordia = function() {
    if (plot.twconcordiaVisible) {
        plot.twconcordiaGroup.remove();
        plot.twconcordiaVisible = false;
    }
};