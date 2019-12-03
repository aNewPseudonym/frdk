package frdk.ui;

import processing.core.*;

public class uidTexture extends uiDecorator{
    public PImage img;
    public PVector shift;
    protected PGraphics textureBuffer;

    public uidTexture(PImage img){
        this.img = img;
        shift = new PVector(0,0);

        PApplet app = uiCanvas.getApp();
        textureBuffer = app.createGraphics(app.width, app.height);
        createTexture();
    }
    public uidTexture(String imgLoc){
        img = uiCanvas.getApp().loadImage(imgLoc);
        shift = new PVector(0,0);
        
        PApplet app = uiCanvas.getApp();
        textureBuffer = app.createGraphics(app.width, app.height);
        createTexture();
    }

    private void createTexture(){
        PGraphics tb = textureBuffer;
        tb.beginDraw();
        tb.background(0, 0);

        for(float j = 0; j<tb.height; j+=tb.height){
            for(float i = 0; i<tb.width; i+=tb.width){
                tb.image(img,i,j);
            }
        }

        tb.endDraw();
    }

    public void resize(int x, int y){
        img.resize(x, y);
        createTexture();
    }

    public void drawDecorator(uiCanvas canvas){
        PGraphics pg = canvas.pg;
        pg.pushStyle();

        PVector abs = canvas.getAbsolutePosition();

        pg.image(textureBuffer,shift.x-abs.x,shift.y-abs.y);
        pg.mask(canvas.clippingMask);

        pg.popStyle();
    }
}