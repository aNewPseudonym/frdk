package frdk.ui;

import java.util.ArrayList;
import java.util.Iterator;

import processing.core.*;

public class uiCanvas implements PConstants{
    private static PApplet app;

    public PVector pos;     // either top-left corner, or center
    public PShape shape;    // shape of canvas - RECT by default
    public PGraphics pg;    // where canvas and decorators draw to, sized by dim
    public PGraphics alphaMask;
    
    public int tint, opacity;
    public boolean showSelf, showChildren;
    
    protected ArrayList<uiDecorator> decorations;   // list of decorators
    protected ArrayList<uiCanvas> children;         // nested list of other canvases
    protected uiCanvas parent;      // parent in canvas tree, for upwards traversal

    // must always be statically initialized with PApplet before use
    public static void init(PApplet theApp){
        app = theApp;
    }
    //for passing app to subclasses and decorators
    public static PApplet getApp(){
        return app;
    }

    // constructor w/o PShape, generates RECT PShape by default
    public uiCanvas(float posX, float posY, float dimX, float dimY) {
        pos = new PVector(posX, posY);
        shape = app.createShape(RECT,0,0,dimX,dimY);
        pg = app.createGraphics(app.width, app.height);
        alphaMask = app.createGraphics(pg.width, pg.height);

        tint = app.color(255);
        opacity = 255;
        showSelf = true;
        showChildren = true;

        decorations = new ArrayList<uiDecorator>();
        children = new ArrayList<uiCanvas>();

        parent = null;
    }

    // constructor with PShape
    public uiCanvas(float posX, float posY, PShape ps) {
        pos = new PVector(posX, posY);
        shape = ps;
        pg = app.createGraphics(app.width, app.height);
        alphaMask = app.createGraphics(pg.width, pg.height);
        
        tint = app.color(255);
        opacity = 255;
        showSelf = true;
        showChildren = true;

        decorations = new ArrayList<uiDecorator>();
        children = new ArrayList<uiCanvas>();

        parent = null;
    }
    
    // Note: order matters when adding decorators and children, drawn linearly
    public void addDecorator(uiDecorator deco){
        decorations.add(deco);
    }
    public void addDecoratorList(ArrayList<uiDecorator> decos){
        decorations.addAll(decos);
    }

    // addChild and setParent, should always be coupled to ensure valid tree
    final public void addChild(uiCanvas child){
        children.add(child);
        child.setParent(this);
    }
    // child must be added to tree before parent is set
    final public void setParent(uiCanvas p){
        if(p.getIndexOf(this) > -1){
            parent = p;
        }
    }

    // helpful getter functions, querying children tree
    public boolean hasChildren(){
        return(children.size() > 0);
    }
    public int getIndexOf(uiCanvas child){
        return children.indexOf(child);
    }
    public uiCanvas getByIndex(int index){
        if(index>=0 && index<children.size()){
            return children.get(index);
        } else {
            return null;
        }
    }

    // provides an iterator for navigating the children list
    public Iterator<uiCanvas> getElementIterator(){
        return children.iterator();
    }

    // requests position from parent, recursively up to the top of tree
    public PVector getAbsolutePosition(){
        PVector absPos = new PVector(pos.x, pos.y);
        if(parent != null){
            absPos.add(parent.getAbsolutePosition());
        }
        return absPos;
    }

    public void alphaSubtract(){
        pg.loadPixels();
        alphaMask.loadPixels();
        if(pg.pixels.length != alphaMask.pixels.length){
            return;
        }
        for(int j = 0; j<pg.height; j++){
            for(int i = 0; i<pg.width; i++){
                // get argb values
                int argb = pg.pixels[(j*pg.width) + i];
                int a = argb >> 24 & 0xFF;
                int r = argb >> 16 & 0xFF;
                int g = argb >> 8 & 0xFF;
                int b = argb & 0xFF;
                
                //grab blue value from mask pixel, 'invert' it's value
                int maskPixel = alphaMask.pixels[(j*pg.width) + i];
                int alphaShift = 0xFF - (maskPixel & 0xFF);
            
                // subtract alphaShift from pixel's alpha value;
                pg.pixels[(j*pg.width) + i] = app.color(r,g,b,a-alphaShift);
            }
        }
        pg.updatePixels();
    }

    // fundamental draw function
    // translates to itself, draws it's decorators, then all children
    public void drawCanvas(float x, float y) {
        if(showSelf){
            // update clipping mask
            shape.disableStyle();
            alphaMask.beginDraw();
            alphaMask.background(0);
            alphaMask.noStroke();
            alphaMask.fill(255);
            alphaMask.shape(shape, x+pos.x, y+pos.y);
            alphaMask.endDraw();

            // prepare PGraphics buffer, translating to absolute position
            pg.beginDraw();
            pg.clear();
            pg.pushMatrix();
            pg.translate(x+pos.x, y+pos.y);
            // draw decorations linearly, which render to canvas's PGraphics
            for(uiDecorator deco : decorations) {
                deco.drawDecorator(this);
            }
            pg.popMatrix();
            pg.endDraw();

            app.tint(tint, opacity);
            app.image(pg,0,0); //draw self
            app.noTint();
        }

        //call upon children to call themselves, passing position down tree
        if(showChildren){
            for(uiCanvas ele : children) {
                ele.drawCanvas(x + pos.x, y + pos.y);
            }
        }
        
    }
}
