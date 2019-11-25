package frdk.ui;

import processing.core.*;

public class uidBackground extends uiDecorator{
    public int fill;

    public uidBackground(int bg){
        fill = bg;
    }

    public void drawDecorator(uiCanvas canvas) {
        PApplet app = uiCanvas.getApp();

        app.pushStyle();
        //draw background box
        app.fill(fill);
        app.noStroke();
        app.beginShape();
        app.vertex(0, 0);
        app.vertex(canvas.dim.x, 0);
        app.vertex(canvas.dim.x, canvas.dim.y);
        app.vertex(0, canvas.dim.y);
        app.endShape(CLOSE);
        
        app.popStyle();
    }
}