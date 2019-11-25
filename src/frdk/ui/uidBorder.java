package frdk.ui;

import processing.core.*;

public class uidBorder extends uiDecorator{
    public int stroke;
    public float weight;

    public uidBorder(int bc, float bw){
        stroke = bc;
        weight = bw;
    }

    public void drawDecorator(uiCanvas canvas){
        PApplet app = uiCanvas.getApp();

        app.pushStyle();
        
        app.noFill();
        app.stroke(stroke);
        app.strokeWeight(weight);
        
        app.beginShape();
        app.vertex(0, 0);
        app.vertex(canvas.dim.x, 0);
        app.vertex(canvas.dim.x, canvas.dim.y);
        app.vertex(0, canvas.dim.y);
        app.endShape(CLOSE);
        
        app.popStyle();
    }
}