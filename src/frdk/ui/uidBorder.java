package frdk.ui;

import frdk.geom.*;
import processing.core.*;

public class uidBorder extends uiDecorator{
    public int stroke;
    public float weight;

    public uidBorder(int bc, float bw){
        stroke = bc;
        weight = bw;
    }

    public void drawDecorator(uiCanvas canvas){
        FPolygon shape = canvas.shape;
        PGraphics pg = canvas.pg;
        pg.pushStyle();

        pg.stroke(stroke);
        pg.strokeWeight(weight);
        pg.noFill();
        shape.draw(pg);

        pg.popStyle();
    }
}
