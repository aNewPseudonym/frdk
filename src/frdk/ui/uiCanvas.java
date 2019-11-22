package frdk.ui;

import java.util.ArrayList;
import processing.core.*;

public class uiCanvas{
    protected static PApplet parent;
    
    public PVector pos;
    public PVector dim;

    protected ArrayList<uiDecorator> decorations;
    protected ArrayList<uiCanvas> elements;

    public static void init(PApplet app){
        parent = app;
    }

    public uiCanvas(float posX, float posY, float dimX, float dimY) {
        pos = new PVector(posX, posY);
        dim = new PVector(dimX, dimY);

        decorations = new ArrayList<uiDecorator>();
        elements = new ArrayList<uiCanvas>();
    }
    
    // Note: order matters when adding decorators, drawn in order
    public void addDecorator(uiDecorator deco){
        decorations.add(deco);
    }

    public void addElement(uiCanvas ele){
        elements.add(ele);
    }

    public void drawCanvas() {
        parent.pushMatrix();
        parent.translate(pos.x, pos.y);

        //draw decorations linearly
        for(uiDecorator deco : decorations) {
            deco.drawDecorator(this);
        }
        //call upon child elements to call themselves
        for(uiCanvas ele : elements) {
          ele.drawCanvas();
        }
        
        parent.popMatrix();
    }

}
