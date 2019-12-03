package frdk.ui;

import processing.core.*;

public class uiWindow extends uiCanvas{
    private uiButton randomizer;
    private uidBackground bg;

    public uiWindow(String WindowName, float x, float y, float w, float h){
        super(x, y, w, h);

        bg = new uidBackground(100);
        //addDecorator(bg);
        PShape circ = uiCanvas.getApp().createShape(ELLIPSE,0,0,12,12);
        circ.setStroke(false);
        circ.setFill(255);
        circ.setStrokeWeight(4);
        addDecorator(new uidPattern(circ,24,21,12));
        addDecorator(new uidBorder(255, 4));
        
        PFont times = uiCanvas.getApp().createFont("Times New Roman Bold", 32);
        uiText header = new uiText(w/2,25, WindowName);
        header.setFont(times);
        header.setStyle(CENTER, CENTER);
        addChild(header);

        comRandomize clickCommand = new comRandomize(bg);
        randomizer = new uiButton(w/2, h+50, 100, 100, clickCommand);
        randomizer.addDecorator(new uidBackground(0xFFFFC857));
        randomizer.addDecorator(new uidBorder(255, 25));
        randomizer.addDecorator(new uidClip());

        addChild(randomizer);

        uiCanvas hoverBox = new uiHoverBox((int)(w+50), 100);
        uiCanvas.getApp().rectMode(CENTER);
        PShape rect = uiCanvas.getApp().createShape(RECT,0,0,8,8);
        rect.setStroke(false);
        rect.setFill(0);
        rect.rotate(PI/4);
        hoverBox.addDecorator(new uidPattern(rect,16,16,2,2,4));
        hoverBox.addDecorator(new uidBorder(0xffffd700, 4));
        addChild(hoverBox);

        uiCanvas texTest = new uiCanvas(450,350,200,200);
        texTest.addDecorator(new uidTexture("frdk/ACCA_7515_V1.jpg"));
        texTest.addDecorator(new uidBorder(0x00000000, 4));
        addChild(texTest);
    }

}