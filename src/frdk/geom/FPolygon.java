package frdk.geom;

import processing.core.*;

/*
TO-DO LIST:

- Split FPoly into FGroup
- offset
- corner shaping: bevel, round

*/

public class FPolygon extends FShape{
    private FPath[] contours;

    //--- CONSTRUCTORS ---
    public FPolygon(){
        contours = new FPath[0];
    }
    public FPolygon(PVector[] verts){
        contours = new FPath[0];
        addContour(verts);
    }
    public FPolygon(FPath path){
        contours = new FPath[0];
        addContour(path);
    }

    //--- DRAW ---
    public void draw(PGraphics pg){
        pg.beginShape();
        for(int i = 0; i < contours.length; i++){
            if(i == 0){
                contours[i].contribute(pg);
            } else {
                pg.beginContour();
                contours[i].contribute(pg);
                pg.endContour();
            }
        }
        pg.endShape(CLOSE);
    }
    public void draw(PApplet app){
        app.beginShape();
        for(int i = 0; i < contours.length; i++){
            if(i == 0){
                contours[i].contribute(app);
            } else {
                app.beginContour();
                contours[i].contribute(app);
                app.endContour();
            }
        }
        app.endShape(CLOSE);
    }
    public void contribute(PGraphics pg){
        for(int i = 0; i < contours.length; i++){
            if(i == 0){
                contours[i].contribute(pg);
            } else {
                pg.beginContour();
                contours[i].contribute(pg);
                pg.endContour();
            }
        }
    }
    public void contribute(PApplet app){
        for(int i = 0; i < contours.length; i++){
            if(i == 0){
                contours[i].contribute(app);
            } else {
                app.beginContour();
                contours[i].contribute(app);
                app.endContour();
            }
        }
    }

    //--- MANIPULATE ---
    public void addContour(FPath contour){
        if(contour.vertCount() < 3){
            return;
        }
        FPath[] newContours = new FPath[contours.length + 1];
        System.arraycopy(contours, 0, newContours, 0, contours.length);
        newContours[contours.length] = new FPath(contour);
        //newContours[contours.length].confirmCCW();    //do NOT change direction, for now
        contours = newContours;
    }
    public void addContour(PVector[] verts){
        FPath[] newContours = new FPath[contours.length + 1];
        System.arraycopy(contours, 0, newContours, 0, contours.length);
        newContours[contours.length] = new FPath(verts);
        //newContours[contours.length].confirmCCW();    //do NOT change direction, for now
        contours = newContours;
    }

    //--- QUERY ---
    public int vertCount(){
        int count = 0;
        for(int i = 0; i < contours.length; i++){
            count += contours[i].vertCount();
        }
        return count;
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

    //--- MEASURING ---
    public PVector getCentroid(){
        PVector sum = new PVector(0,0);
        for(int i = 0; i < contours.length; i++){
            sum.add(contours[i].getCentroid());
        }
        return sum.div(contourCount());
    }
    public PVector getMidpoint(){
        PVector sum = new PVector(0,0);
        for(int i = 0; i < contours.length; i++){
            sum.add(contours[i].getMidpoint());
        }
        return sum.div(contourCount());
    }

    //--- ALIGNING ---
    public void centerAt(float centerX, float centerY){
        PVector shift = new PVector(centerX, centerY);
        shift.sub(getMidpoint());
        for(int i = 0; i < contours.length; i++){
            contours[i].translate(shift.x, shift.y);
        }
    }
    public void centerSelf(){
        PVector center = getMidpoint();
        center.mult(-1);
        for(int i = 0; i < contours.length; i++){
            contours[i].translate(center.x, center.y);
        }
    }

    //--- TRANSFORMS ---
    public void translate(float x, float y){
        for(int i = 0; i < contours.length; i++){
            contours[i].translate(x, y);
        }
    }
    public void scale(float s){
        for(int i = 0; i < contours.length; i++){
            contours[i].scale(s);
        }
    }
    public void scaleAbout(float centerX, float centerY, float s){
        for(int i = 0; i < contours.length; i++){
            contours[i].scaleAbout(centerX, centerY, s);
        }
    }
    public void rotate(float rad){
        for(int i = 0; i < contours.length; i++){
            contours[i].rotate(rad);
        }
    }
    public void rotateAbout(float centerX, float centerY, float rad){
        for(int i = 0; i < contours.length; i++){
            contours[i].rotateAbout(centerX, centerY, rad);
        }
    }
}