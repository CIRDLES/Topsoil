package org.cirdles.topsoil.plot.base;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.cirdles.topsoil.plot.AbstractDataView;
import org.cirdles.topsoil.plot.TicGeneratorForAxes;

/**
 *
 * @author brycebarrett
 */
public class BasePlotDataView extends AbstractDataView {
    
    private double[] xValues;
    private double[] yValues;
    
    //private String graphPointType;
    
    
    
    public BasePlotDataView(Rectangle bounds, double[] xValues, double[] yValues){
        super(bounds, 75, 75);

        this.xValues = xValues;
        this.yValues = yValues;

        
        //init min/max values for axis
        minX = 0;
        maxX = 0;
        minY = 0;
        maxY = 0;
        
        //this.graphPointType = graphPointType;
    }

    @Override
    public void paint(GraphicsContext gc){
        super.paint(gc);
        
        //set up the points within the graph window
        gc.setFill(Color.BLACK);
        for (int i = 0; i < xValues.length; i++){
            
            //check to be sure values are within current graph area
            if(xValues[i] > minX && xValues[i] < maxX 
                    && yValues[i] > minY && yValues[i] < maxY){
                
                /*first two values(x/y coordinates) need to be offset
                as the fillOval method actually maps those first 2 inputs
                to the upper left point of the oval/circle
                */
                gc.fillOval(mapX(xValues[i]) - 2.5, mapY(yValues[i]) - 2.5, 5, 5);
            }
            
        }
        
        // tics for Y Axis
        float verticalTextShift = 3.1f;
        gc.setFont(Font.font("Lucida Sans", 10));
        gc.setFill(Color.BLACK);
        if (ticsY != null) {
            for (int i = 0; i < ticsY.length; i++) {
                if(ticsY[i].doubleValue() >= minY && ticsY[i].doubleValue() <= maxY){
                    try {
                        gc.strokeLine(
                                mapX(minX), mapY(ticsY[i].doubleValue()), mapX(minX) - 8, mapY(ticsY[i].doubleValue()));

                        gc.fillText(ticsY[i].toPlainString(),//
                                (float) mapX(minX) - 45f,
                                (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);
                    } catch (Exception e) {
                    }
                }
            }
        }
        
        // tics for X Axis
        float horizontalTextShift = 0.3f;
        if (ticsX != null) {
            for (int i = 0; i < ticsX.length; i++) {
                if(ticsX[i].doubleValue() >= minX && ticsX[i].doubleValue() <= maxX){
                    try {
                        gc.strokeLine(
                                mapX(ticsX[i].doubleValue()), mapY(maxY), mapX(ticsX[i].doubleValue()), mapY(maxY) - 8);

                        gc.fillText(ticsX[i].toPlainString(),//
                                (float) mapX(ticsX[i].doubleValue()),// - horizontalTextShift),
                                (float) mapY(maxY) - 30f);
                    } catch (Exception e) {
                    }
                }
            }
        }
        
            
    }
    
    
    @Override
    public void preparePanel() {
        
        //setDisplayOffsetY(-(getLeftMargin()/20));
        //setDisplayOffsetX(getTopMargin()/10);
        minX = xValues[0];
        maxX = xValues[0];
                
        for(int i = 0; i < xValues.length; i++){
            minX = Math.min(xValues[i], minX);
            maxX = Math.max(xValues[i], maxX);
        }
        
        
        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.2);
        minX -= xMarginStretch;
        maxX += xMarginStretch;
        
        
        ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphWidth/50.0));
        
        minY = yValues[0];
        maxY = yValues[0];
        
        for(int i = 0; i < yValues.length; i++){
            minY = Math.min(yValues[i], minY);
            maxY = Math.max(yValues[i], maxY);
        }
        
        
        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.2);
        minY -= yMarginStretch;
        maxY += yMarginStretch;
        
        
        ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight/50.0));
        
    }
    
    /*
        Method to update the data being passed into the graph for when new data needs to be
        displayed
    */
    public void refreshData(double[] xValuesNew, double[] yValuesNew, GraphicsContext gc){
        
        xValues = new double[xValuesNew.length];
        yValues = new double[yValuesNew.length];
        
       
        for (int i = 0; i < xValuesNew.length; i++){
            xValues[i] = xValuesNew[i];
        }
        
        for (int i = 0; i < yValuesNew.length; i ++){
            yValues[i] = yValuesNew[i];
        }
        
        //redraw graph
        this.paint(gc);
    }
    
}
