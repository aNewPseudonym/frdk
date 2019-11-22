package frdk;

import java.util.ArrayList;

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
        size(400, 300);
    }

    public void setup() {
        uiCanvas.init(this);
        myCanvas = new uiWindow("My Window!");
    }

    public void draw() {
        background(200);
        myCanvas.drawCanvas();

        ArrayList<uiCanvas> selected = myCanvas.getByPoint(mouseX, mouseY);
        for(uiCanvas uic : selected){
            println(uic);
        }
    }

    public void mouseClicked(){
        checkClickables(myCanvas, mouseX, mouseY);
    }
      
    //recursive function to find Clickable canvases within uiCollections
    public void checkClickables(uiCanvas canvas, float x, float y){
        ArrayList<uiCanvas> onPoint = canvas.getByPoint(x, y);
        for(uiCanvas toClick : onPoint){
            if(toClick instanceof Clickable){
                ((Clickable) toClick).click();
            }
        }
    }

}

