import input.*;
import processing.core.*;

public class InputTestApp extends PApplet{

    KeyManager myManager;

    public static void main(String[] args) {
        PApplet.main("InputTestApp");
        System.out.println("What's up?");
        
    }

    public void settings() {
        size(400, 300);
    }

    public void setup() {
        myManager = new KeyManager(this);
        myManager.enablePrintKeys();
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
        text("Hold Space", width/2, 96);
        textSize(48);
        text( (float)myManager.timePressed(' ')/1000, width/2, height/2 );
        text( (float)myManager.timeReleased(' ')/1000, width/2, (height/2)+48 );
    }
}