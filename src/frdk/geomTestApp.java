package frdk;

import frdk.geom.*;
import processing.core.*;

public class geomTestApp extends PApplet{
    
    FShape shape;
    FGroup group;
    PVector[] boxVerts = {
        new PVector(100,100),
        new PVector(200,100),
        new PVector(225,225),
        new PVector(100,200),
        };

    public static void main(String[] args) {
        PApplet.main("frdk.geomTestApp");
        System.out.println("Running frdk.geom Test App");
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        group = new FGroup();

        shape = new FPath(boxVerts,true);
        shape.centerAt(width/2, height/2);
        group.appendChild(shape);

        boxVerts[2].sub(25, 25);
        group.appendChild(new FPath(boxVerts, true));
        group.centerAt(width/2, height/2);
    }

    public void draw() {
        background(200);
        pushMatrix();
        //translate(width/2, height/2);

        group.rotateAbout(width/2, height/2, 0.01f);

        fill(255);
        stroke(0);
        strokeWeight(2);
        group.draw(this);

        fill(0xff5e73d1);
        noStroke();
        ellipse(width/2, height/2, 9, 9);

        fill(0xffe50239);
        stroke(0xffe50239);
        strokeWeight(1);

        PVector center = group.getCentroid();
        PVector mid = group.getMidpoint();
        ellipse(center.x, center.y, 3,3);

        float w = group.getWidth();
        float h = group.getHeight();
        line(mid.x-w/2, center.y, mid.x+w/2, center.y);
        line(center.x, mid.y-h/2, center.x, mid.y+h/2);

        rectMode(CENTER);
        noFill();
        rect(mid.x, mid.y, w+5, h+5);

        popMatrix();
    }

}
