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
        PShape ps = canvas.shape;
        ps.disableStyle();

        PGraphics pg = canvas.pg;
        pg.pushStyle();

        pg.stroke(stroke);
        pg.strokeWeight(weight);
        pg.noFill();
        pg.shape(ps,0,0);

        pg.popStyle();
    }
}
