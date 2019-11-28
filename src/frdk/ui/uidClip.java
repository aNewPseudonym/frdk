package frdk.ui;

import processing.core.*;

public class uidClip extends uiDecorator{
    public boolean clip;

    public uidClip(){
        clip = true;
    }

    public void setClip(boolean clip) {
        this.clip = clip;
    }

    public void drawDecorator(uiCanvas canvas){
        PGraphics pg = canvas.pg;
        pg.mask(canvas.clippingMask);
    }
}