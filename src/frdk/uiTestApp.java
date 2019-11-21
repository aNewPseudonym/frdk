package frdk;

import frdk.ui.*;
import processing.core.*;

public class uiTestApp extends PApplet{

    uiCanvas myCanvas;
    PFont font;

    public static void main(String[] args) {
        PApplet.main("frdk.uiTestApp");
        System.out.println("Running frdk.ui Test App");
    }

    public void settings() {
        size(400, 300);
    }

    public void setup() {
        myCanvas = new uiCanvas(this, 50, 50, 300, 200);
        myCanvas.addDecorator(new uidBackground(color(204, 153, 0)));
        
        font = createFont("Times New Roman Bold", 28);
        myCanvas.addDecorator(new uidText("My Canvas", font, color(117,99,139), color(229,144,165) ) );
    }

    public void draw() {
        background(200);
        myCanvas.drawCanvas();
    }
}

