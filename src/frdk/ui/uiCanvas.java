package frdk.ui;

import java.util.ArrayList;
import java.util.Iterator;

import processing.core.*;

public class uiCanvas implements PConstants{
    private static PApplet app;

    public PVector pos;     // either top-left corner, or center
    public PShape shape;    // shape of canvas - RECT by default
    public PGraphics pg;    // where canvas and decorators draw to, sized by dim
    
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

        decorations = new ArrayList<uiDecorator>();
        children = new ArrayList<uiCanvas>();

        parent = null;
    }

    // constructor with PShape
    public uiCanvas(float posX, float posY, PShape ps) {
        pos = new PVector(posX, posY);
        shape = ps;
        pg = app.createGraphics(app.width, app.height);

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

    // Positional test - how do
    public boolean isPointOn(float x, float y){
        return true;
    }

    // fundamental draw function
    // translates to itself, draws it's decorators, then all children
    public void drawCanvas() {
        pg.beginDraw();
        pg.pushMatrix();
        pg.translate(pg.width/2, pg.height/2);
        //draw decorations linearly, which render to canvas's PGraphics
        for(uiDecorator deco : decorations) {
            deco.drawDecorator(this);
        }
        pg.popMatrix();
        pg.endDraw();

        app.pushMatrix();
        app.translate(pos.x, pos.y);

        app.image(pg,-pg.width/2,-pg.height/2); //draw self

        //call upon children to call themselves
        for(uiCanvas ele : children) {
          ele.drawCanvas();
        }
        
        app.popMatrix();
    }
}
