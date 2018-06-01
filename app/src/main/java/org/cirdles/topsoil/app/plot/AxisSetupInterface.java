/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.topsoil.app.plot;

import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author brycebarrett
 */
public interface AxisSetupInterface {
    
    public void buildAxis(GraphicsContext gc);
    
    public double getGraphWidth();
    
    public double getGraphHeight();
    
    public int getNumberTics();
    
    public void setNumberTics(int newTicNum);
    
}
