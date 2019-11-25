package frdk;

import java.util.Iterator;
import frdk.ui.*;
import processing.core.*;

public class uiTestApp extends PApplet{

    uiWindow myCanvas;
    PFont font;

    public static void main(String[] args) {
        PApplet.main("frdk.uiTestApp");
        System.out.println("Running frdk.ui Test App");
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        uiCanvas.init(this);
        myCanvas = new uiWindow("My Window!", 50, 50, 300, 250);
    }

    public void draw() {
        background(200);
        myCanvas.drawCanvas();

        checkSelectables(myCanvas, mouseX, mouseY);
    }

    public void mouseClicked(){
        checkClickables(myCanvas, mouseX, mouseY);
    }

    public void checkClickables(uiCanvas canvas, float x, float y){
        if(canvas instanceof uiButton){
            if( canvas.isPointOn(x, y) ){
                ((uiButton)canvas).click();
                System.out.println(canvas.getAbsolutePosition().toString());
            }
        }
        Iterator<uiCanvas> iter = canvas.getElementIterator();
        while(iter.hasNext()){
            checkClickables(iter.next(), x-canvas.pos.x, y-canvas.pos.y);
        }
    }

    public void checkSelectables(uiCanvas canvas, float x, float y){
        if(canvas instanceof Selectable){
            if( canvas.isPointOn(x, y) && !((Selectable)canvas).isSelected() ){
                ((Selectable)canvas).select();
                System.out.println(canvas.getAbsolutePosition().toString());
            }
            else if( !canvas.isPointOn(x, y) && ((Selectable)canvas).isSelected() ){
                ((Selectable)canvas).deselect();
            }
        }
        Iterator<uiCanvas> iter = canvas.getElementIterator();
        while(iter.hasNext()){
            checkSelectables(iter.next(), x-canvas.pos.x, y-canvas.pos.y);
        }
    }

}

