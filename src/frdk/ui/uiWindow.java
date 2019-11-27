package frdk.ui;

import processing.core.*;

public class uiWindow extends uiCanvas{
    private uiButton randomizer;
    private uidBackground bg;

    public uiWindow(String WindowName, float x, float y, float w, float h){
        super(x, y, w, h);

        bg = new uidBackground(100);
        addDecorator(bg);
        addDecorator(new uidBorder(255, 8));
        
        PFont times = uiCanvas.getApp().createFont("Times New Roman Bold", 32);
        uiText header = new uiText((w/2),25,100,100, WindowName);
        header.setFont(times);
        header.setStyle(CENTER, CENTER);
        addChild(header);

        comRandomize clickCommand = new comRandomize(bg);
        randomizer = new uiButton(w/2, h-50, 100, 25, clickCommand);
        randomizer.addDecorator(new uidBackground(0xFFFFC857));
        randomizer.addDecorator(new uidBorder(255, 2));

        addChild(randomizer);

        addChild(new uiHoverBox(100, 100));
    }

}