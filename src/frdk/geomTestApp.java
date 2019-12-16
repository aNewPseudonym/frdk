package frdk;

import frdk.geom.*;
import processing.core.*;
import java.util.ArrayList;

public class geomTestApp extends PApplet{
    
    FShape shape;
    FGroup group;
    FPolygon cursor;

    PVector[] circVerts = {
        new PVector(100,0),
        new PVector(70.7f,-70.7f),
        new PVector(0,-100),
        new PVector(-70.7f,-70.7f),
        new PVector(-100,0),
        new PVector(-70.7f,70.7f),
        new PVector(0,100),
        new PVector(70.7f,70.7f),
    };
    PVector[] boxVerts = {
        new PVector(100,100),
        new PVector(200,100),
        new PVector(225,225),
        new PVector(100,200),
    };
    PVector[] contourVerts = {
        new PVector(125,125),
        new PVector(175,125),
        new PVector(175,175),
        new PVector(125,175),
    };
    PVector[] secondContourVerts = {
        new PVector(140,140),
        new PVector(160,140),
        new PVector(160,160),
        new PVector(140,160),
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

        shape = new FPolygon(boxVerts);
        shape.centerAt(width/2, height/2);
        group.appendChild(shape);

        boxVerts[2].sub(25, 25);
        FPolygon hasHole = new FPolygon(boxVerts);
        hasHole.addContour(contourVerts);
        hasHole.addContour(secondContourVerts);
        group.appendChild(hasHole);
        group.centerAt(width/2, height/2);

        cursor = new FPolygon(circVerts);
    }

    public void draw() {
        background(200);

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

        
        cursor.centerAt(mouseX, mouseY);
        noFill();
        strokeWeight(4);
        stroke(0);
        for(int i = 0; i < group.childCount(); i++){
            FShape child = group.getChild(i);
            if(child instanceof FPolygon){
                if(FG.pointInPoly( new PVector(mouseX, mouseY), (FPolygon)child )){
                    stroke(0xff5e73d1);
                }
            }
        }
        cursor.draw(this);

        ArrayList<PVector> allInts = new ArrayList<PVector>();
        PVector[] intPoints = new PVector[0];
        for(int i = 0; i < group.childCount(); i++){
            FShape child = group.getChild(i);
            if(child instanceof FPolygon){
                intPoints = FG.polyPolyIntersection(cursor, (FPolygon)child);
                println(intPoints.length);
            }
            for(int j = 0; j < intPoints.length; j++){
                allInts.add(intPoints[j]);
            }
        }
        for(PVector p : allInts){
            fill(0xffff0000);
            ellipse(p.x, p.y, 6, 6);
        }
    }

}
