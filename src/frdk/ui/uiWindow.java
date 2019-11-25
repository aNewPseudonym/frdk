package frdk.ui;

import processing.core.*;

public class uiWindow extends uiCanvas implements Subscriber{
    private uiButton randomizer;
    private uidBackground bg;

    public uiWindow(String WindowName){
        super(50, 50, 600, 400);

        bg = new uidBackground(parent.color(218,194,145));
        addDecorator(bg);
        
        PFont times = parent.createFont("Times New Roman Bold", 48);
        uiText header = new uiText(25,25,100,100, WindowName);
        header.setFont(times);
        addElement(header);

        
        randomizer = new uiButton(dim.x-50, 25, 25, 25);
        randomizer.addSub(this);
        randomizer.addDecorator(new uidBackground(parent.color(80,117,146)));

        addElement(randomizer);

        addElement(new uiHoverBox(100, 100));
    }

    public void tuneIn(Broadcaster bc) {
        bg.setColor( parent.color(parent.random(255),parent.random(255),parent.random(255)) );
    }
}