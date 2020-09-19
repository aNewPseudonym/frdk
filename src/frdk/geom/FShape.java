package frdk.geom;

import processing.core.*;

public abstract class FShape implements PConstants{
    public abstract void draw(PGraphics pg);
    public abstract void draw(PApplet app);

    public abstract void contribute(PGraphics pg);
    public abstract void contribute(PApplet app);

    /*
    ALIGNING (Only for groups?)
        fitTo/fitXTo/fitYTo
        vertAlignCenter, vertAlignTop, vertAlignBottom
        horzAlignCenter, horzAlignLeft, horzAlignRight
        vertDistribute, horzDistribute
    */

    //--- QUERY ---
    //change wording for this? or remove?
    //abstract public PVector[] getVerts();
    abstract public int vertCount();

    //--- MEASURING ---
    // abstract public float getWidth();
    // abstract public float getHeight();
    // abstract public float getArea();
    // abstract public float getLength();
    abstract public PVector getCentroid();
    abstract public PVector getMidpoint();

    //--- ALIGNING ---
    abstract public void centerAt(float centerX, float centerY);
    abstract public void centerSelf();

    //--- TRANSFORMS ---
    abstract public void translate(float x, float y);
    abstract public void scale(float s);
    //abstract public void scale(float sx, float sy);
    abstract public void scaleAbout(float centerX, float centerY, float s);
    //abstract public void scaleAbout(float centerX, float centerY, float sx, float sy);
    abstract public void rotate(float rad);
    abstract public void rotateAbout(float centerX, float centerY, float rad);
    //abstract public void skew(float angle);
    //abstract public void shear(x, y)
    //abstract public void offset(float dist);

}