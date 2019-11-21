package frdk.ui;

import processing.core.*;

public class uidText extends uiDecorator{
    String content;
    PFont font;
    int fill, stroke;

    public uidText(String content, PFont font, int fill, int stroke){
        this.content = content;
        this.font = font;
        this.fill = fill;
        this.stroke = stroke;
    }

    public void drawDecorator(uiCanvas canvas) {
        PApplet app = canvas.parent;

        app.pushStyle();
        app.clip(0, 0, canvas.dim.x, canvas.dim.y);
    
        app.textFont(font);
        app.textLeading(font.getSize() + 2);
        app.textAlign(LEFT, TOP);
        app.stroke(stroke);
        app.fill(fill);
        app.text(content, 0, 0);  //textAlign CENTER skews low, added "- (textAscent()/6)" to compensate
    
        app.noClip();
        app.popStyle();
    }
}