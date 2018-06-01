package org.cirdles.topsoil.app.plot;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;



/**
 *
 * @author brycebarrett
 */
public class AxisSetup implements AxisSetupInterface {
    
    //This is not the width of the Canvas object itself, but the width of the 
    //area within the Canvas object that is being used to generate the graph
    private double graphWidth;
    private double graphHeight;
    private String graphType;
    private int numOfTics = 10;
    private double graphOffset = 50;
    private float disBetweenDashesYAxis = 20;
    private float disBetweenDashesXAxis = 20;
    
    private double xMin = 10;
    private double xMax = 100;
    private double yMin = 20;
    private double yMax = 300;
    
    
    public AxisSetup(String graphType, double graphWidth, double graphHeight){
        this.graphWidth = graphWidth;
        this.graphHeight = graphHeight;
        this.graphType = graphType;
        
    }
    
    @Override
    public void buildAxis(GraphicsContext gc){
        
        //Will setup the axis with labels
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        //Y-Axis bound
        gc.strokeLine(graphOffset, gc.getCanvas().getHeight() - (graphOffset + graphHeight), 
                graphOffset, gc.getCanvas().getHeight() - graphOffset);
        //X-Axis bound
        gc.strokeLine(graphOffset, gc.getCanvas().getHeight() - graphOffset, 
                graphWidth + graphOffset, gc.getCanvas().getHeight() - graphOffset); 
        //top bound for the graph
        gc.strokeLine(graphOffset, gc.getCanvas().getHeight() - (graphOffset + graphHeight), 
                graphWidth + graphOffset, gc.getCanvas().getHeight() - (graphOffset + graphHeight));
        //right bound for the graph
        gc.strokeLine(graphWidth + graphOffset, gc.getCanvas().getHeight() - (graphHeight + graphOffset), 
                graphWidth + graphOffset, gc.getCanvas().getHeight() - graphOffset);
        
        gc.fillText("X-Axis", gc.getCanvas().getWidth()/2, gc.getCanvas().getHeight() - (graphOffset/3));
        
        gc.rotate(-90);
        gc.fillText("Y-Axis", -(gc.getCanvas().getHeight()/2), graphOffset/3);
        gc.rotate(90);
        
        //Setup axis tics
        this.buildXAxis(gc);
        this.buildYAxis(gc);
 
    }
    
    private void buildXAxis(GraphicsContext gc){
        //Creating a new path/reseting any old paths in the GraphicsContext
        gc.beginPath();
        gc.setLineWidth(1);
        
        double xCoordinate = graphOffset;
        double disToTic = graphWidth/numOfTics;
        double disToNextNum = (xMax - xMin) / numOfTics;
        double graphNumTracker = xMin;
        
        for(int i = 0; i < numOfTics; i++){
            gc.strokeLine(xCoordinate, gc.getCanvas().getHeight() - graphOffset,
                    xCoordinate, (gc.getCanvas().getHeight() - graphOffset) + 3);
            
            gc.fillText(Double.toString(graphNumTracker), xCoordinate, 
                    (gc.getCanvas().getHeight() - graphOffset) + 20);
            
            xCoordinate += disToTic;
            graphNumTracker += disToNextNum;
            
        }
    }
    
    private void buildYAxis(GraphicsContext gc){
        //Creating a new path/reseting any old paths in the GraphicsContext
        gc.beginPath();
        gc.setLineWidth(1);
        
        double yCoordinate = gc.getCanvas().getHeight() - graphOffset;
        double disToTic = graphHeight/numOfTics;
        double disToNextNum = (yMax - yMin) / numOfTics;
        double graphNumTracker = yMin;
        
        for(int i = 0; i < numOfTics; i++){
            gc.strokeLine(graphOffset, yCoordinate, 
                    graphOffset - 3, yCoordinate);
            
            gc.fillText(Double.toString(graphNumTracker), graphOffset - 20, yCoordinate);
            
            yCoordinate -= disToTic;
            graphNumTracker += disToNextNum;
            
            
        }
        
    }
    
    @Override
    public double getGraphWidth(){
        
        return this.graphWidth;
    }
    
    @Override
    public double getGraphHeight(){
        
        return this.graphHeight;
    }
    
    @Override
    public int getNumberTics(){
    
        return this.numOfTics;
    }
    
    @Override
    public void setNumberTics(int newTicNum){
        
        numOfTics = newTicNum;
    }
    
}
