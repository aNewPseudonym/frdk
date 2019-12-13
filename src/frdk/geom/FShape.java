package frdk.geom;

import processing.core.*;

public abstract class FShape implements PConstants{
    public float width, height;

    public abstract void draw(PGraphics pg);
    public abstract void draw(PApplet app);

    /*
    useful functions:

    Copy function!
    
    transforms - use PVector functionality...
        translate
        scale, scaleFrom
        rotate, rotateAbout(Point)
        offset

    align
        vertAlign, horzAlign
        vertDistribute, horzDistribute

    sizing/fitting
        center, centerAt
        fitTo
    
    */

    abstract public PVector[] getVerts();
    abstract public int vertCount();

    abstract public float getWidth();
    abstract public float getHeight();
    abstract public PVector getCentroid();
    abstract public PVector getMidpoint();

    abstract public void translate(PVector t);
    abstract public void scale(float s);
    abstract public void scaleAbout(PVector center,float s);
    abstract public void rotate(float rad);
    abstract public void rotateAbout(PVector center, float rad);

    abstract public void centerAt(float centerX, float centerY);
    abstract public void centerSelf();

}