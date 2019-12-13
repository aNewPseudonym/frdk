package frdk.geom;

import processing.core.*;

public class FPath extends FShape{
    private PVector[] verts;
    public boolean isClosed;

    /*constructors for:
    empty FShape,
    SVG parse,
    PShape parse?,
    ellipse dimensions,
    vertex array - closed/open
    */
    public FPath(){
        verts = new PVector[0];
        isClosed = false;
    }

    public FPath(PVector[] verts, boolean isClosed){
        this.verts = new PVector[0];
        appendVertArray(verts);
        this.isClosed = isClosed;
    }

    //draw
    public void draw(PGraphics pg){
        pg.beginShape();
        for(int i = 0; i < verts.length; i++){
            pg.vertex(verts[i].x, verts[i].y);
        }
        if(isClosed){
            pg.endShape(CLOSE);
        } else {
            pg.endShape();
        }
    }

    public void draw(PApplet app){
        app.beginShape();
        for(int i = 0; i < verts.length; i++){
            app.vertex(verts[i].x, verts[i].y);
        }
        if(isClosed){
            app.endShape(CLOSE);
        } else {
            app.endShape();
        }
    }

    //--- QUERY ---
    public PVector[] getVerts(){
        return verts;
    }
    public int vertCount(){
        return verts.length;
    }

    //--- MEASURING ---
    public float getWidth(){
        float low, high;
        if(verts.length > 1){
            low = verts[0].x;
            high = verts[0].x;
        } else {
            return 0.0f;
        }

        for(int i = 1; i < verts.length; i++){
            if(verts[i].x < low){ low = verts[i].x; }
            if(verts[i].x > high){ high = verts[i].x; }
        }

        return (high - low);
    }
    public float getHeight(){
        float low, high;
        if(verts.length > 1){
            low = verts[0].y;
            high = verts[0].y;
        } else {
            return 0.0f;
        }

        for(int i = 1; i < verts.length; i++){
            if(verts[i].y < low){ low = verts[i].y; }
            if(verts[i].y > high){ high = verts[i].y; }
        }

        return (high - low);
    }
    // returns 'vertex centroid'
    public PVector getCentroid(){
        if(verts.length < 1){
            return null;
        }
        PVector sum = new PVector();
        for(int i = 0; i < verts.length; i++){
            sum.add(verts[i]);
        }
        return (sum.div(verts.length));
    }
    // returns center of extreme points
    public PVector getMidpoint(){
        float lowX, highX, lowY, highY;
        if(verts.length > 1){
            lowX = verts[0].x;
            highX = verts[0].x;
            lowY = verts[0].y;
            highY = verts[0].y;
        } else {
            return null;
        }

        for(int i = 1; i < verts.length; i++){
            if(verts[i].x < lowX){ lowX = verts[i].x; }
            if(verts[i].x > highX){ highX = verts[i].x; }
            if(verts[i].y < lowY){ lowY = verts[i].y; }
            if(verts[i].y > highY){ highY = verts[i].y; }
        }

        return new PVector( (highX+lowX)/2, (highY+lowY)/2 );
    }

    //--- ALIGNING ---
    public void centerAt(float centerX, float centerY){
        PVector shift = new PVector(centerX, centerY);
        shift.sub(getMidpoint());
        for(int i = 0; i < verts.length; i++){
            verts[i].add(shift);
        }
    }
    public void centerSelf(){
        PVector shift = getMidpoint();
        for(int i = 0; i < verts.length; i++){
            verts[i].sub(shift);
        }
    }

    //--- TRANSFORMING ---
    public void translate(PVector t){
        for(int i = 0; i < verts.length; i++){
            verts[i].add(t);
        }
    }
    public void scale(float s){
        for(int i = 0; i < verts.length; i++){
            verts[i].mult(s);
        }
    }
    public void scaleAbout(PVector center, float s){
        for(int i = 0; i < verts.length; i++){
            verts[i].sub(center);
            verts[i].mult(s);
            verts[i].add(center);
        }
    }
    public void rotate(float rad){
        for(int i = 0; i < verts.length; i++){
            verts[i].rotate(rad);
        }
    }
    public void rotateAbout(PVector center, float rad){
        for(int i = 0; i < verts.length; i++){
            verts[i].sub(center);
            verts[i].rotate(rad);
            verts[i].add(center);
        }
    }

    //appendVerts
    public void appendVertex(PVector v){
        PVector[] newVerts = new PVector[verts.length + 1];
        System.arraycopy(verts, 0, newVerts, 0, verts.length);
        newVerts[verts.length + 1] = v;
        verts = newVerts;
    }

    public void appendVertArray(PVector[] va){
        PVector[] newVerts = new PVector[verts.length + va.length];
        System.arraycopy(verts, 0, newVerts, 0, verts.length);
        System.arraycopy(va, 0, newVerts, verts.length, va.length);
        verts = newVerts;
    }

    //insertVerts
    public void insertVertex(PVector v, int index){
        if(index < 0 || index >= verts.length){
            return;
        }
        PVector[] newVerts = new PVector[verts.length + 1];
        System.arraycopy(verts, 0, newVerts, 0, index);
        newVerts[index] = v;
        System.arraycopy(verts, index, newVerts, index+1, verts.length-index);
        verts = newVerts;
    }

    public void insertVertArray(PVector[] va, int index){
        if(index < 0 || index >= verts.length){
            return;
        }
        PVector[] newVerts = new PVector[verts.length + va.length];
        System.arraycopy(verts, 0, newVerts, 0, index);
        System.arraycopy(va, 0, newVerts, index, va.length);
        System.arraycopy(verts, index, newVerts, index+va.length, verts.length-index);
        verts = newVerts;
    }

    //getVert
    public PVector getVertex(int index){
        if(index >= 0 && index < verts.length){
            return verts[index];
        } else {
            return null;
        }
    }
}