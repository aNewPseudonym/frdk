package frdk;

import frdk.geom.*;
import processing.core.*;

//import java.util.ArrayList;

public class geomTestApp extends PApplet{

    int traceType = FG.AND;
    
    FPolygon cursor;
    FPolygon box;
    FPolygon boxA, boxB, boxC, boxD, boxE, boxF, boxG, boxH, boxI, boxJ, boxK, boxL;
    FPolygon hollow, twisted;
    FPolygon hollow2, smallBox;

    PVector[] circVerts = {
        new PVector(100,0),
        new PVector(70.7f,-70.7f),
        new PVector(0,-100),
        new PVector(-70.7f,-70.7f),
        new PVector(-100,0),
        new PVector(-70.7f,70.7f),
        new PVector(0,100),
        new PVector(70.7f,70.7f)
    };
    PVector[] boxVerts = {
        new PVector(100,100),
        new PVector(200,100),
        new PVector(200,200),
        new PVector(100,200)
    };
    PVector[] diamondVerts = {
        new PVector(0,100),
        new PVector(100,0),
        new PVector(200,100),
        new PVector(100,200)
    };
    PVector[] cutBoxVerts = {
        new PVector(100,100),
        new PVector(200,100),
        new PVector(200,200),
        new PVector(100,200),
        new PVector(150,150)
    };
    PVector[] twistedVerts = {
        new PVector(0,0),
        new PVector(200,0),
        new PVector(0,200),
        new PVector(200,200)
    };
    PVector[] longBoxVerts = {
        new PVector(0,0),
        new PVector(400,0),
        new PVector(400,100),
        new PVector(0,100)
    };
    PVector[] longHoleVerts = {
        new PVector(100,25),
        new PVector(100,75),
        new PVector(300,75),
        new PVector(300,25)
    };

    // - - -

    // FPolygon box;
    // PVector[] boxVerts = {
    //     new PVector(0,0),
    //     new PVector(100,0),
    //     new PVector(100,100),
    //     new PVector(0,100),
    // };

    // FPolygon cursor;
    // PVector[] diamondVerts = {
    //     new PVector(0,100),
    //     new PVector(100,0),
    //     new PVector(200,100),
    //     new PVector(100,200),
    // };

    // PVector[] holeVerts = {
    //     new PVector(85,85),
    //     new PVector(85,115),
    //     new PVector(115,115),
    //     new PVector(115,85),
    // };

    // PVector[] voidVerts = {
    //     new PVector(50,35),
    //     new PVector(40,40),
    //     new PVector(60,65),
    //     new PVector(65,50)
    // };

    public static void main(String[] args) {
        PApplet.main("frdk.geomTestApp");
        System.out.println("Running frdk.geom Test App");
    }

    public void settings() {
        size(1600, 800);
        pixelDensity(displayDensity());
    }

    public void setup() {

        // box = new FPolygon(boxVerts);
        // box.addContour(voidVerts);
        // box.centerAt(width/2, height/2);
        
        // cursor = new FPolygon(diamondVerts);
        // cursor.addContour(holeVerts);
        // cursor.scale(1.5f);
        // cursor.centerAt(225, 218);

        boxA = new FPolygon(boxVerts);
        boxA.centerAt(200, 200);
        boxB = new FPolygon(boxVerts);
        boxB.centerAt(250, 250);

        boxC = new FPolygon(boxVerts);
        boxC.centerAt(500, 200);
        boxD = new FPolygon(boxVerts);
        boxD.centerAt(550, 200);

        boxE = new FPolygon(diamondVerts);
        boxE.centerAt(200, 450);
        boxF = new FPolygon(boxVerts);
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
        twisted.centerAt(1200, 200);
        hollow = new FPolygon(longBoxVerts);
        hollow.addContour(longHoleVerts);
        hollow.centerAt(1200, 200);

        hollow2 = new FPolygon(longBoxVerts);
        hollow2.addContour(longHoleVerts);
        hollow2.centerAt(1200, 600);
        smallBox = new FPolygon(boxVerts);
        smallBox.centerAt(1200, 600);

    }

    public void draw() {
        background(128);

        //test boolean operations
        stroke(64);
        fill(0xff0ed1a3);

        FG.booleanOp_debug(boxA, boxB, traceType, this).draw(this);
        FG.booleanOp_debug(boxC, boxD, traceType, this).draw(this);
        FG.booleanOp_debug(boxE, boxF, traceType, this).draw(this);
        FG.booleanOp_debug(boxG, boxH, traceType, this).draw(this);
        FG.booleanOp_debug(boxI, boxJ, traceType, this).draw(this);
        FG.booleanOp_debug(boxK, boxL, traceType, this).draw(this);
        FG.booleanOp_debug(twisted, hollow, traceType, this).draw(this);
        FG.booleanOp_debug(smallBox, hollow2, traceType, this).draw(this);

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
        hollow2.draw(this);
        stroke(0);
        strokeWeight(2);
        boxB.draw(this);
        boxD.draw(this);
        boxF.draw(this);
        boxH.draw(this);
        boxJ.draw(this);
        boxL.draw(this);
        hollow.draw(this);
        smallBox.draw(this);

    }

    public void keyPressed(){
        if(key == ' '){
            if(traceType == FG.OR){
                traceType = FG.AND;
            } else if(traceType == FG.AND){
                traceType = FG.NOT;
            } else {
                traceType = FG.OR;
            }
        }
    }

}
