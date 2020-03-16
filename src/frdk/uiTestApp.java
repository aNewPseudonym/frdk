package frdk;

import java.util.Iterator;
import frdk.ui.*;
import processing.core.*;

public class uiTestApp extends PApplet{

    uiWindow myCanvas;
    PFont font;
    CanvasPicker cp;

    public static void main(String[] args) {
        PApplet.main("frdk.uiTestApp");
        System.out.println("Running frdk.ui Test App");
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        uiCanvas.init(this);
        myCanvas = new uiWindow("My Window!", 50, 50, 300, 400);
        cp = new CanvasPicker(myCanvas);
    }

    public void draw() {
        background(200);
        myCanvas.drawCanvas(0,0);

        checkSelectables(myCanvas, mouseX, mouseY);
    }

    public void mouseClicked(){
        println(cp.getID(mouseX, mouseY));
        checkClickables(cp.getCanvas(mouseX, mouseY));
    }

    public void checkClickables(uiCanvas canvas){
        if(canvas instanceof uiButton){
            ((uiButton)canvas).click();
            System.out.println(canvas.getAbsolutePosition().toString());
        }
    }

    // TO-DO: selecting seems slow? iterating may be to slow...
    public void checkSelectables(uiCanvas canvas, int x, int y){
        if(canvas instanceof Selectable){
            if( (canvas == cp.getCanvas(x, y)) && !((Selectable)canvas).isSelected() ){
                ((Selectable)canvas).select();
                System.out.println(canvas.getAbsolutePosition().toString());
            }
            else if( (canvas != cp.getCanvas(x, y)) && ((Selectable)canvas).isSelected() ){
                ((Selectable)canvas).deselect();
            }
        }
        Iterator<uiCanvas> iter = canvas.getElementIterator();
        while(iter.hasNext()){
            checkSelectables(iter.next(), x, y);
        }
    }

}

