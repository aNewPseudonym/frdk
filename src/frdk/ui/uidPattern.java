package frdk.ui;

import processing.core.*;

public class uidPattern extends uiDecorator{
    public PShape unit;
    public PVector spacing, startPos;
    public float rowOffset;
    protected PGraphics patternBuffer;

    public uidPattern(PShape unit, float spacingX, float spacingY){
        this.unit = unit;
        spacing = new PVector(spacingX,spacingY);
        startPos = new PVector(0,0);
        rowOffset = 0;

        PApplet app = uiCanvas.getApp();
        patternBuffer = app.createGraphics(app.width, app.height);
        createPattern();
    }
    public uidPattern(PShape unit, float spacingX, float spacingY, float offset){
        this.unit = unit;
        spacing = new PVector(spacingX,spacingY);
        startPos = new PVector(0,0);
        rowOffset = offset;
        
        PApplet app = uiCanvas.getApp();
        patternBuffer = app.createGraphics(app.width, app.height);
        createPattern();
    }
    public uidPattern(PShape unit, float spacingX, float spacingY, float startX, float startY){
        this.unit = unit;
        spacing = new PVector(spacingX,spacingY);
        startPos = new PVector(startX,startY);
        rowOffset = 0;
        
        PApplet app = uiCanvas.getApp();
        patternBuffer = app.createGraphics(app.width, app.height);
        createPattern();
    }
    public uidPattern(PShape unit, float spacingX, float spacingY, float startX, float startY, float offset){
        this.unit = unit;
        spacing = new PVector(spacingX,spacingY);
        startPos = new PVector(startX,startY);
        rowOffset = offset;
        
        PApplet app = uiCanvas.getApp();
        patternBuffer = app.createGraphics(app.width, app.height);
        createPattern();
    }

    public void createPattern(){
        PGraphics pb = patternBuffer;
        pb.beginDraw();

        pb.clear();

        float offset = 0;
        for(float j = 0; j<pb.height; j+=spacing.y){
            offset += rowOffset;
            offset = offset%spacing.x;
            for(float i = 0; i<pb.width; i+=spacing.x){
                pb.shape(unit,startPos.x+i+offset,startPos.y+j);
            }
        }

        pb.endDraw();
    }

    //NOTE: still somewhat concerned about efficiency of this, 
    //  watch for slowdown when using patterns
    public void drawDecorator(uiCanvas canvas){
        PGraphics pg = canvas.pg;
        pg.pushStyle();

        PVector abs = canvas.getAbsolutePosition();
        float shiftX = canvas.pos.x % spacing.x;
        float shiftY = canvas.pos.y % spacing.y;

        pg.image(patternBuffer,shiftX-abs.x,shiftY-abs.y);
        //pg.mask(canvas.clippingMask);

        pg.popStyle();
    }
}