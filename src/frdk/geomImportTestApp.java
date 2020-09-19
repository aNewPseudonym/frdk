package frdk;

import frdk.geom.*;
import processing.core.*;

public class geomImportTestApp extends PApplet{
    FGroup myGroup;
    PShape p;

    public static void main(String[] args) {
        PApplet.main("frdk.geomImportTestApp");
        System.out.println("Running frdk.geom Import Test App");
    }

    @Override
    public void settings() {
        size(800,600);
        println("From settings: " + displayDensity());
    }

    @Override
    public void setup() {
        println("From setup: " + displayDensity());
        p = loadShape("C:\\Users\\kixst\\Documents\\GitHub\\frdk\\src\\frdk\\data\\Star_slim.svg");
        
        // testing PRIMITIVE PShapes >
        // p.addChild(createShape(RECT, 50,50,100,75));
        // p.addChild(createShape(RECT, 150,75,50,40,20,20,15,10));
        // p.addChild(createShape(ARC, 200,50,100,60,PI+QUARTER_PI,TWO_PI,OPEN));
        // p.addChild(createShape(ELLIPSE, 300,300,150,100));
        // p.addChild(createShape(TRIANGLE, 300,300,200,300,250,200));
        // p.addChild(createShape(QUAD, 25,250,100,220,110,300,25,275));
        // p.addChild(createShape(LINE, 25,25,100,45));
        // p.addChild(createShape(POINT, 15,15));

        //testing GEOMETRY PShapes
        PShape geom = createShape();
        // geom.beginShape(LINES);
        // geom.vertex(30, 20);
        // geom.bezierVertex(85, 20, 85, 75, 30, 75);
        // geom.vertex(85, 20);
        // geom.vertex(85, 75);
        // geom.vertex(30, 75);
        // geom.beginContour();
        // geom.vertex(50, 40);
        // geom.vertex(50, 55);
        // geom.vertex(65, 55);
        // geom.vertex(65, 40);
        // geom.endContour();

        // geom.beginShape(TRIANGLE_STRIP);
        // geom.vertex(30, 75);
        // geom.vertex(40, 20);
        // geom.vertex(50, 75);
        // geom.vertex(60, 20);
        // geom.vertex(70, 75);
        // geom.vertex(80, 20);
        // geom.vertex(57, 50);
        // geom.vertex(57, 15); 
        // geom.vertex(92, 50); 
        // geom.vertex(57, 85); 
        // geom.vertex(22, 50); 
        // geom.vertex(57, 15); 
        // geom.endShape(CLOSE);

        geom.beginShape(QUAD_STRIP);
        geom.vertex(30, 20); 
        geom.vertex(30, 75); 
        geom.vertex(50, 20);
        geom.vertex(50, 75);
        geom.vertex(65, 20); 
        geom.vertex(65, 75); 
        geom.vertex(85, 20);
        geom.vertex(85, 75);
        geom.endShape(CLOSE);

        p.addChild(geom);

        myGroup = FG.PShapeToGroup(p);
    }

    public void draw() {
        background(128);

        // draw PShape
        pushMatrix();
        translate(300,0);
        shape(p);
        popMatrix();

        // draw FShape
        stroke(0);
        noFill();

        strokeWeight(2);
        beginShape(POINTS);
        myGroup.contribute(this);
        endShape();

        strokeWeight(4);
        myGroup.draw(this);
    }

    public void keyPressed(){
        if(key == 's'){
            save("screenshot.png");
        }
    }

}
