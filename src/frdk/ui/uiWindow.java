package frdk.ui;

import processing.core.*;

public class uiWindow extends uiCanvas implements Subscriber{
    private uiButton randomizer;
    private uidBackground bg;

    public uiWindow(String WindowName){
        super(50, 50, 300, 200);

        bg = new uidBackground(parent.color(218,194,145));
        addDecorator(bg);
        
        PFont font = parent.createFont("Times New Roman Bold", 28);
        addDecorator( new uidText(WindowName, font, 50, 0) );
        
        randomizer = new uiButton(250, 25, 25, 25);
        randomizer.addSub(this);
        randomizer.addDecorator(new uidBackground(parent.color(80,117,146)));

        addElement(randomizer);
    }

    public void tuneIn(Broadcaster bc) {
        bg.setColor( parent.color(parent.random(255),parent.random(255),parent.random(255)) );
    }
}