package frdk.geom;

import processing.core.*;

//TO-DO: test all this bullshit

public class FGroup extends FShape{
    private FShape[] children;

    //--- CONSTRUCTORS ---
    public FGroup(){
        children = new FShape[0];
    }
    public FGroup(FShape[] shapeGroup){
        children = new FShape[shapeGroup.length];
        System.arraycopy(shapeGroup, 0, children, 0, shapeGroup.length);
    }

    //--- DRAW ---
    public void draw(PGraphics pg){
        for(int i = 0; i < children.length; i++){
            children[i].draw(pg);
        }
    }

    public void draw(PApplet app){
        for(int i = 0; i < children.length; i++){
            children[i].draw(app);
        }
    }

    //--- CHILD MANIPULATION (lol) ---
    public void appendChild(FShape newShape){
        FShape[] newChildren = new FShape[children.length + 1];
        System.arraycopy(children, 0, newChildren, 0, children.length);
        newChildren[children.length] = newShape;
        children = newChildren;
    }
    public void appendChildren(FShape[] newShapes){
        FShape[] newChildren = new FShape[children.length + newShapes.length];
        System.arraycopy(children, 0, newChildren, 0, children.length);
        System.arraycopy(newShapes, 0, newChildren, children.length, newShapes.length);
        children = newChildren;
    }

    //--- QUERY ---
    public PVector[] getVerts(){
        int totalVerts = vertCount();
        PVector[] allVerts = new PVector[totalVerts];
        
        int index = 0;
        for(int i = 0; i < children.length; i++){
            PVector[] newVerts = children[i].getVerts();
            System.arraycopy(allVerts, index, newVerts, 0, newVerts.length);
            index += children[i].vertCount();
        }
        return allVerts;
    }
    public int vertCount(){
        int totalVerts = 0;
        for(int i = 0; i < children.length; i++){
            totalVerts += children[i].vertCount();
        }
        return totalVerts;
    }
    
    //--- MEASURING ---
    public float getWidth(){
        float low = 0;
        float high = 0;
        for(int i = 0; i < children.length; i++){
            PVector[] verts = children[i].getVerts();
            if( (i == 0) && (verts.length > 1) ){
                low = verts[0].x;
                high = verts[0].x;
            }
            for(int j = 0; j < verts.length; j++){
                if(verts[j].x < low){ low = verts[j].x; }
                if(verts[j].x > high){ high = verts[j].x; }
            }
        }
        return (high - low);
    }
    public float getHeight(){
        float low = 0;
        float high = 0;
        for(int i = 0; i < children.length; i++){
            PVector[] verts = children[i].getVerts();
            if( (i == 0) && (verts.length > 1) ){
                low = verts[0].y;
                high = verts[0].y;
            }
            for(int j = 0; j < verts.length; j++){
                if(verts[j].y < low){ low = verts[j].y; }
                if(verts[j].y > high){ high = verts[j].y; }
            }
        }
        return (high - low);
    }
    public PVector getCentroid(){
        PVector avg = new PVector();
        int totalVerts = 0;
        for(int i = 0; i < children.length; i++){
            totalVerts += children[i].vertCount();
            avg.add( children[i].getCentroid().mult(children[i].vertCount()) );
        }
        return avg.div(totalVerts);
    }
    public PVector getMidpoint(){
        float lowX = 0;
        float highX = 0;
        float lowY = 0;
        float highY = 0;
        for(int i = 0; i < children.length; i++){
            PVector[] verts = children[i].getVerts();
            if( (i == 0) && (verts.length > 1) ){
                lowX = verts[0].x;
                highX = verts[0].x;
                lowY = verts[0].y;
                highY = verts[0].y;
            }
            for(int j = 0; j < verts.length; j++){
                if(verts[j].x < lowX){ lowX = verts[j].x; }
                if(verts[j].x > highX){ highX = verts[j].x; }
                if(verts[j].y < lowY){ lowY = verts[j].y; }
                if(verts[j].y > highY){ highY = verts[j].y; }
            }
        }
        return new PVector( (highX+lowX)/2, (highY+lowY)/2 );
    }

    //--- ALIGNING ---
    public void centerAt(float centerX, float centerY){
        PVector shift = new PVector(centerX, centerY);
        shift.sub(getMidpoint());
        for(int i = 0; i < children.length; i++){
            children[i].translate(shift.x, shift.y);
        }
    }
    public void centerSelf(){
        PVector center = getMidpoint();
        center.mult(-1);
        for(int i = 0; i < children.length; i++){
            children[i].translate(center.x, center.y);
        }
    }
    
    //--- TRANSFORMS ---
    public void translate(float x, float y){
        for(int i = 0; i < children.length; i++){
            children[i].translate(x, y);
        }
    }
    public void scale(float s){
        for(int i = 0; i < children.length; i++){
            children[i].scale(s);
        }
    }
    public void scaleAbout(float centerX, float centerY, float s){
        for(int i = 0; i < children.length; i++){
            children[i].scaleAbout(centerX, centerY, s);
        }
    }
    public void rotate(float rad){
        for(int i = 0; i < children.length; i++){
            children[i].rotate(rad);
        }
    }
    public void rotateAbout(float centerX, float centerY, float rad){
        for(int i = 0; i < children.length; i++){
            children[i].rotateAbout(centerX, centerY, rad);
        }
    }

}