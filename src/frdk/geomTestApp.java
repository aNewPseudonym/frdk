package frdk;

import frdk.geom.*;
import processing.core.*;
//import java.util.ArrayList;

public class geomTestApp extends PApplet{
    
    FPolygon cursor;
    FPolygon boxA, boxB, boxC, boxD, boxE, boxF, boxG, boxH, boxI, boxJ, boxK, boxL;
    FPolygon hollow, twisted;

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
        new PVector(200,200),
        new PVector(100,200),
    };
    PVector[] diamondVerts = {
        new PVector(0,100),
        new PVector(100,0),
        new PVector(200,100),
        new PVector(100,200),
    };
    PVector[] cutBoxVerts = {
        new PVector(100,100),
        new PVector(200,100),
        new PVector(200,200),
        new PVector(100,200),
        new PVector(150,150),
    };
    PVector[] twistedVerts = {
        new PVector(0,0),
        new PVector(200,0),
        new PVector(0,200),
        new PVector(200,200),
    };
    PVector[] longBoxVerts = {
        new PVector(0,0),
        new PVector(400,0),
        new PVector(400,100),
        new PVector(0,100),
    };
    PVector[] longHoleVerts = {
        new PVector(100,25),
        new PVector(300,25),
        new PVector(300,75),
        new PVector(100,75),
    };

    public static void main(String[] args) {
        PApplet.main("frdk.geomTestApp");
        System.out.println("Running frdk.geom Test App");
    }

    public void settings() {
        size(800, 1200);
    }

    public void setup() {
        boxA = new FPolygon(boxVerts);
        boxA.centerAt(200, 200);
        boxB = new FPolygon(boxVerts);
        boxB.centerAt(250, 250);

        boxC = new FPolygon(boxVerts);
        boxC.centerAt(500, 200);
        boxD = new FPolygon(boxVerts);
        boxD.centerAt(550, 200);

        boxE = new FPolygon(boxVerts);
        boxE.centerAt(200, 450);
        boxF = new FPolygon(diamondVerts);
        boxF.centerAt(200, 450);

        boxG = new FPolygon(boxVerts);
        boxG.centerAt(500, 450);
        boxH = new FPolygon(boxVerts);
        boxH.centerAt(600, 500);

        boxI = new FPolygon(boxVerts);
        boxI.centerAt(200, 700);
        boxJ = new FPolygon(boxVerts);
        boxJ.centerAt(200, 700);
        
        boxK = new FPolygon(cutBoxVerts);
        boxK.centerAt(500, 700);
        boxK.rotateAbout(boxK.getCentroid().x, boxK.getCentroid().y, PI);
        boxL = new FPolygon(cutBoxVerts);
        boxL.centerAt(500, 700);

        twisted = new FPolygon(twistedVerts);
        twisted.centerAt(width/2, 1000);
        hollow = new FPolygon(longBoxVerts);
        hollow.addContour(longHoleVerts);
        hollow.centerAt(width/2, 1000);

        cursor = new FPolygon(circVerts);
        cursor.centerAt(width/2, height/2);
    }

    public void draw() {
        background(128);
        
        // draw cursor
        cursor.centerAt(mouseX, mouseY);
        noFill();
        strokeWeight(4);
        cursor.draw(this);

        // test boolean operations
        noStroke();
        fill(0xff0ed1a3);
        FG.booleanOp(boxA, boxB, this).draw(this);
        FG.booleanOp(boxC, boxD, this).draw(this);
        FG.booleanOp(boxE, boxF, this).draw(this);
        FG.booleanOp(boxG, boxH, this).draw(this);
        FG.booleanOp(boxI, boxJ, this).draw(this);
        FG.booleanOp(boxK, boxL, this).draw(this);

        FG.booleanOp(twisted, hollow, this).draw(this);

        noFill();
        stroke(255);
        strokeWeight(4);
        boxA.draw(this);
        boxC.draw(this);
        boxE.draw(this);
        boxG.draw(this);
        boxI.draw(this);
        boxK.draw(this);
        twisted.draw(this);
        stroke(0);
        strokeWeight(2);
        boxB.draw(this);
        boxD.draw(this);
        boxF.draw(this);
        boxH.draw(this);
        boxJ.draw(this);
        boxL.draw(this);
        hollow.draw(this);

        //FG.booleanOp(cursor, boxE, this);

    }

}
