package frdk;

import frdk.geom.*;
import processing.core.*;

public class geomTestApp extends PApplet{
    
    FShape shape;

    public static void main(String[] args) {
        PApplet.main("frdk.geomTestApp");
        System.out.println("Running frdk.geom Test App");
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        PVector[] va = {
            new PVector(50,50),
            new PVector(200,50),
            new PVector(225,225),
            new PVector(50,200),
            };
        shape = new FPath(va,true);
    }

    public void draw() {
        background(200);

        fill(255);
        stroke(0);
        strokeWeight(2);
        //shape.scaleAbout(new PVector(125, 125), 1.005f);
        shape.rotateAbout(new PVector(125, 125), 0.01f);
        shape.draw(this);

        stroke(0xffe50239);
        strokeWeight(1);
        noFill();
        PVector center = shape.getCenter();
        float w = shape.getWidth();
        float h = shape.getHeight();
        line(center.x-w/2, center.y, center.x+w/2, center.y);
        line(center.x, center.y-h/2, center.x, center.y+h/2);

        System.out.println(center.toString());
        System.out.println(w + ", " + h);
    }

}
