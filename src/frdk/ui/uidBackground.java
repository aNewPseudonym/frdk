package frdk.ui;

import frdk.geom.*;
import processing.core.*;

public class uidBackground extends uiDecorator{
    public int fill;

    public uidBackground(int bg){
        fill = bg;
    }

    public void drawDecorator(uiCanvas canvas) {
        FPolygon shape = canvas.shape;

        PGraphics pg = canvas.pg;
        pg.pushStyle();

        pg.noStroke();
        pg.fill(fill);
        shape.draw(pg);

        pg.popStyle();
    }
}