package frdk.geom;

import processing.core.*;

//TO-DO: test all this bullshit

public class FGroup extends FShape{
    private FShape[] children;

    //CONSTRUCTORS

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
            if(verts.length > 1){
                low = verts[0].x;
                high = verts[0].x;
            } else {
                return 0.0f;
            }

            for(int j = 1; j < verts.length; j++){
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
            if(verts.length > 1){
                low = verts[0].y;
                high = verts[0].y;
            } else {
                return 0.0f;
            }

            for(int j = 1; j < verts.length; j++){
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
            if(verts.length > 1){
                lowX = verts[0].x;
                highX = verts[0].x;
                lowY = verts[0].y;
                highY = verts[0].y;
            } else {
                return null;
            }

            for(int j = 1; j < verts.length; j++){
                if(verts[i].x < lowX){ lowX = verts[i].x; }
                if(verts[i].x > highX){ highX = verts[i].x; }
                if(verts[i].y < lowY){ lowY = verts[i].y; }
                if(verts[i].y > highY){ highY = verts[i].y; }
            }
        }
        return new PVector( (highX+lowX)/2, (highY+lowY)/2 );
    }

    //--- ALIGNING ---
    public void centerAt(float centerX, float centerY){
        for(int i = 0; i < children.length; i++){
            //centerAt(somewhere...)
            //children[i].translate(t);
        }
    }
    public void centerSelf(){
        for(int i = 0; i < children.length; i++){
            //centerAt(somewhere...)
            //children[i].translate(t);
        }
    }
    
    //--- TRANSFORMS ---
    public void translate(PVector t){
        for(int i = 0; i < children.length; i++){
            children[i].translate(t);
        }
    }
    public void scale(float s){
        for(int i = 0; i < children.length; i++){
            children[i].scale(s);
        }
    }
    public void scaleAbout(PVector center, float s){
        for(int i = 0; i < children.length; i++){
            children[i].scaleAbout(center, s);
        }
    }
    public void rotate(float rad){
        for(int i = 0; i < children.length; i++){
            children[i].rotate(rad);
        }
    }
    public void rotateAbout(PVector center, float rad){
        for(int i = 0; i < children.length; i++){
            children[i].rotateAbout(center, rad);
        }
    }

}