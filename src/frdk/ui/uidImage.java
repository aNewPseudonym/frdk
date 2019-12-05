package frdk.ui;

import processing.core.*;

public class uidImage extends uiDecorator{
    public PImage img;
    public PVector shift;

    public uidImage(PImage img){
        this.img = img;
        shift = new PVector(0,0);
    }
    public uidImage(String imgLoc){
        img = uiCanvas.getApp().loadImage(imgLoc);
        shift = new PVector(0,0);
    }
    
    public void drawDecorator(uiCanvas canvas){
        PGraphics pg = canvas.pg;
        pg.pushStyle();

        pg.image(img,shift.x, shift.y);
        canvas.alphaSubtract();

        pg.popStyle();
    }
}