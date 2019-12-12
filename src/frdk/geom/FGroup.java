package frdk.geom;

import processing.core.*;

public class FGroup extends FShape{
    private FShape[] children;

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
    
    //--- MEASURING ---
    public float getWidth(){
        float max = 0;
        float w;
        for(int i = 0; i < children.length; i++){
            w = children[i].getWidth();
            if(w > max){ max = w; }
        }
        return max;
    }
    public float getHeight(){
        float max = 0;
        float h;
        for(int i = 0; i < children.length; i++){
            h = children[i].getHeight();
            if(h > max){ max = h; }
        }
        return max;
    }
    public PVector getCentroid(){
        PVector avg = new PVector();
        for(int i = 0; i < children.length; i++){
            avg.add(children[i].getCentroid());
        }
        return avg.div(children.length);
    }
    public PVector getCenter(){
        PVector avg = new PVector();
        for(int i = 0; i < children.length; i++){
            avg.add(children[i].getCenter());
        }
        return avg.div(children.length);
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