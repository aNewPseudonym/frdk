package frdk.ui;

import processing.core.*;

public class uiWindow extends uiCanvas{
    private uiButton randomizer;
    private uidBackground bg;

    public uiWindow(String WindowName, float x, float y, float w, float h){
        super(x, y, w, h);

        bg = new uidBackground(100);
        addDecorator(bg);
        
        PFont times = uiCanvas.getApp().createFont("Times New Roman Bold", 32);
        uiText header = new uiText(25,25,100,100, WindowName);
        header.setFont(times);
        addChild(header);

        comRandomize clickCommand = new comRandomize(bg);
        randomizer = new uiButton(dim.x-50, 25, 25, 25, clickCommand);
        randomizer.addDecorator(new uidBackground(0xFFFFC857));

        addChild(randomizer);

        addChild(new uiHoverBox(100, 100));
    }

}