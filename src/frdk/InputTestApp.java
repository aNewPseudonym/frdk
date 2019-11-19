package frdk;

import frdk.input.*;
import processing.core.*;

public class InputTestApp extends PApplet{

    KeyManager myManager;
    int count;

    public static void main(String[] args) {
        PApplet.main("frdk.InputTestApp");
        System.out.println("Running Test App");
    }

    public void settings() {
        size(400, 300);
    }

    public void setup() {
        myManager = new KeyManager(this);
        myManager.enablePrintKeys();
        count = 0;
    }

    public void draw() {

        if(myManager.getState(' ')) {
            background(80,117,146);
            fill(218,194,145);
        } else {
            background(218,194,145);
            fill(80,117,146);
        }

        textAlign(CENTER);
        textSize(24);
        text("Hold Space; Tap UP/DOWN", width/2, 96);
        textSize(48);
        text( (float)myManager.timePressed(' ')/1000, width/2, height/2 );
        text( (float)myManager.timeReleased(' ')/1000, width/2, (height/2)+48 );

        if(myManager.wasTapped(UP)) {count++;}
        if(myManager.wasTapped(DOWN)) {count--;}
        text(count, width/2, 60);
    }
}