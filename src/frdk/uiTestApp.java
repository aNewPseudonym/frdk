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
        size(1600, 1200);
    }

    public void setup() {
        uiCanvas.init(this);
        myCanvas = new uiWindow("My Window!");
    }

    public void draw() {
        background(200);
        myCanvas.drawCanvas();

        deselectAll(myCanvas);
        checkSelectables(myCanvas, mouseX, mouseY);
    }

    public void mouseClicked(){
        checkClickables(myCanvas, mouseX, mouseY);
    }

    public void checkClickables(uiCanvas canvas, float x, float y){
        ArrayList<uiCanvas> onPoint = canvas.getByPoint(x, y);
        for(uiCanvas toClick : onPoint){
            if(toClick instanceof Clickable){
                ((Clickable) toClick).click();
            }
        }
    }

    public void checkSelectables(uiCanvas canvas, float x, float y){
        ArrayList<uiCanvas> onPoint = canvas.getByPoint(x, y);
        for(uiCanvas toSelect : onPoint){
            if(toSelect instanceof Selectable){
                ((Selectable) toSelect).select();
            }
        }
    }

    public void deselectAll(uiCanvas canvas){
        ArrayList<uiCanvas> children = canvas.getChildren();
        for(uiCanvas toDeselect : children){
            if(toDeselect instanceof Selectable){
                if(( (Selectable)toDeselect).isSelected() ){
                    ((Selectable)toDeselect).deselect();
                }
            }
        }
    }

}

