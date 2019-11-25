package frdk.ui;

import processing.core.*;

public class uidCorners extends uiDecorator{
    public PShape shape;
    public int fill;
    public float weight;

    public uidCorners(int bc, float bw, PShape s){
        fill = bc;
        weight = bw;
        shape = s;
    }

    public void drawDecorator(uiCanvas canvas){
        PApplet app = uiCanvas.getApp();

        app.pushStyle();
        
        app.noStroke();
        app.fill(fill);
        app.strokeWeight(weight);
        
        app.beginShape();
        app.shape(shape, 0, 0);
        app.shape(shape, canvas.dim.x, 0);
        app.shape(shape, canvas.dim.x, canvas.dim.y);
        app.shape(shape, 0, canvas.dim.y);
        app.endShape(CLOSE);
        
        app.popStyle();
    }
}