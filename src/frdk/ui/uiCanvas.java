package frdk.ui;

import java.util.ArrayList;
import java.util.Iterator;

import processing.core.*;

public class uiCanvas implements PConstants{
    private static PApplet app;

    // what if it had a single arraylist that referenced all canvases
    // for iterating through to select/deselect, click, etc...
    //private static ArrayList<uiCanvas> allCanvases;

    public PVector pos;
    public PVector dim;

    protected ArrayList<uiDecorator> decorations;
    protected ArrayList<uiCanvas> children;
    protected uiCanvas parent;

    // must always be initialized with PApplet before use
    public static void init(PApplet theApp){
        app = theApp;
    }
    public static PApplet getApp(){
        return app;
    }

    public uiCanvas(float posX, float posY, float dimX, float dimY) {
        pos = new PVector(posX, posY);
        dim = new PVector(dimX, dimY);

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
    public int getIndexOf(uiCanvas child){
        return children.indexOf(child);
    }
    public boolean hasChildren(){
        return(children.size() > 0);
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

    // returns flattened list of all elements that are under the given point
    public ArrayList<uiCanvas> getByPoint(float x, float y){
        ArrayList<uiCanvas> pointedAt = new ArrayList<uiCanvas>();

        if( isPointOn(x, y) ){
            pointedAt.add(this);
        }

        for(uiCanvas child : children){
            pointedAt.addAll(child.getByPoint(x-pos.x, y-pos.y));
        }
        
        return pointedAt;
    }
    // simple positional test
    public boolean isPointOn(float x, float y){
        return ( x>pos.x && x<pos.x+dim.x && y>pos.y && y<pos.y+dim.y );
    }

    // fundamental draw function
    // translates to itself, draws it's decorators, then all children
    public void drawCanvas() {
        app.pushMatrix();
        app.translate(pos.x, pos.y);

        //draw decorations linearly
        for(uiDecorator deco : decorations) {
            deco.drawDecorator(this);
        }
        //call upon child children to call themselves
        for(uiCanvas ele : children) {
          ele.drawCanvas();
        }
        
        app.popMatrix();
    }
}
