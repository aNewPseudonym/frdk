package frdk.geom;

import processing.core.*;

public abstract class FShape{
    public float width, height;

    public abstract void draw(PGraphics pg);
    public abstract void draw(PApplet app);

    /*
    useful functions:

    boolean functions: intersect, diff, union, xor
        returns new fshape

    intersection functions
        FShape/FShape
        FShape/line
        FShape/ray
    
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
}