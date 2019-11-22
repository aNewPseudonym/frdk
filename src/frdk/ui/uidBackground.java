package frdk.ui;

import processing.core.*;

public class uidBackground extends uiDecorator{
    private int background;

    public uidBackground(int bg){
        background = bg;
    }

    public void drawDecorator(uiCanvas canvas) {
        PApplet app = uiCanvas.parent;

        app.pushStyle();
        //draw background box
        app.fill(background);
        app.noStroke();
        app.beginShape();
        app.vertex(0, 0);
        app.vertex(canvas.dim.x, 0);
        app.vertex(canvas.dim.x, canvas.dim.y);
        app.vertex(0, canvas.dim.y);
        app.endShape(CLOSE);
        
        app.popStyle();
    }

    public void setColor(int bg){
        background = bg;
    }
}