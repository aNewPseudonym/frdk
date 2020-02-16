package frdk.geom;

import processing.core.*;

public class FPolygon extends FShape{
    private FPath bound;
    private FPath[] contours;

    //--- CONSTRUCTORS ---
    public FPolygon(){
        bound = new FPath();
        contours = new FPath[0];
    }
    public FPolygon(PVector[] verts){
        bound = new FPath(verts);
        bound.confirmCW();
        contours = new FPath[0];
    }
    public FPolygon(FPath toCopy){
        bound = new FPath(toCopy);
        bound.confirmCW();
        contours = new FPath[0];
    }

    //--- DRAW ---
    public void draw(PGraphics pg){
        pg.beginShape();
        bound.contribute(pg);
        for(int i = 0; i < contours.length; i++){
            pg.beginContour();
            contours[i].contribute(pg);
            pg.endContour();
        }
        pg.endShape(CLOSE);
    }
    public void draw(PApplet app){
        app.beginShape();
        bound.contribute(app);
        for(int i = 0; i < contours.length; i++){
            app.beginContour();
            contours[i].contribute(app);
            app.endContour();
        }
        app.endShape(CLOSE);
    }

    //--- MANIPULATE ---
    public void addContour(FPath contour){
        if(contour.vertCount() < 3){
            return;
        }
        if(bound.vertCount() < 3){
            bound = contour;
            return;
        }
        FPath[] newContours = new FPath[contours.length + 1];
        System.arraycopy(contours, 0, newContours, 0, contours.length);
        newContours[contours.length] = new FPath(contour);
        newContours[contours.length].confirmCCW();
        contours = newContours;
    }
    public void addContour(PVector[] verts){
        if(bound.vertCount() < 3){
            bound = new FPath(verts);
            return;
        }
        FPath[] newContours = new FPath[contours.length + 1];
        System.arraycopy(contours, 0, newContours, 0, contours.length);
        newContours[contours.length] = new FPath(verts);
        newContours[contours.length].confirmCCW();
        contours = newContours;
    }

    //--- QUERY ---
    public FPath getBound(){
        return bound;
    }
    public FPath getContour(int index){
        if(index > -1 && index < contours.length){
            return contours[index];
        } else {
            return null;
        }
    }
    public int contourCount(){
        return contours.length;
    }
    public PVector[] getVerts(){
        return bound.getVerts();
    }
    public int vertCount(){
        return bound.vertCount();
    }

    //--- MEASURING ---
    public float getWidth(){
        return bound.getWidth();
    }
    public float getHeight(){
        return bound.getHeight();
    }
    public PVector getCentroid(){
        return bound.getCentroid();
    }
    public PVector getMidpoint(){
        return bound.getMidpoint();
    }

    //--- ALIGNING ---
    public void centerAt(float centerX, float centerY){
        PVector shift = new PVector(centerX, centerY);
        shift.sub(getMidpoint());
        bound.translate(shift.x, shift.y);
        for(int i = 0; i < contours.length; i++){
            contours[i].translate(shift.x, shift.y);
        }
    }
    public void centerSelf(){
        PVector center = getMidpoint();
        center.mult(-1);
        bound.translate(center.x, center.y);
        for(int i = 0; i < contours.length; i++){
            contours[i].translate(center.x, center.y);
        }
    }

    //--- TRANSFORMS ---
    public void translate(float x, float y){
        bound.translate(x, y);
        for(int i = 0; i < contours.length; i++){
            contours[i].translate(x, y);
        }
    }
    public void scale(float s){
        bound.scale(s);
        for(int i = 0; i < contours.length; i++){
            contours[i].scale(s);
        }
    }
    public void scaleAbout(float centerX, float centerY, float s){
        bound.scaleAbout(centerX, centerY, s);
        for(int i = 0; i < contours.length; i++){
            contours[i].scaleAbout(centerX, centerY, s);
        }
    }
    public void rotate(float rad){
        bound.rotate(rad);
        for(int i = 0; i < contours.length; i++){
            contours[i].rotate(rad);
        }
    }
    public void rotateAbout(float centerX, float centerY, float rad){
        bound.rotateAbout(centerX, centerY, rad);
        for(int i = 0; i < contours.length; i++){
            contours[i].rotateAbout(centerX, centerY, rad);
        }
    }
}