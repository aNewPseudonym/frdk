package frdk.ui;

import processing.core.*;

public class uidBackground extends uiDecorator{
    public int fill;

    public uidBackground(int bg){
        fill = bg;
    }

    public void drawDecorator(uiCanvas canvas) {
        PShape ps = canvas.shape;
        ps.disableStyle();

        PGraphics pg = canvas.pg;
        pg.pushStyle();

        pg.noStroke();
        pg.fill(fill);
        pg.shape(ps);

        pg.popStyle();
    }
}