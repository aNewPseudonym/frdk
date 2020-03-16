package frdk.ui;

import processing.core.*;

public class uiText extends uiCanvas{
    String content;
    PFont font;
    int fill, stroke;
    int horzStyle, vertStyle;

    public uiText(float x, float y, String s){
        super(x,y,0,0);

        content = s;

        font = new PFont();
        fill = 0x00000000;
        stroke = 0x00000000;

        horzStyle = LEFT;
        vertStyle = TOP;
    }

    public void drawCanvas(float x, float y) {
        PApplet app = uiCanvas.getApp();

        app.pushMatrix();
        app.translate(x, y);
        app.pushStyle();
    
        app.textFont(font);
        app.textLeading(font.getSize() + 2);
        app.textAlign(horzStyle, vertStyle);
        app.stroke(stroke);
        app.fill(fill);
        app.text(content, pos.x, pos.y);

        app.popStyle();
        app.popMatrix();

        
        super.drawCanvas(x, y);
    }

    public void setText(String s){
        content = s;
    }
    public void setTextColors(int tf, int ts) {
        fill = tf;
        stroke = ts;
    }
    public void setFont(PFont pf) {
        font = pf;
    }
    public void setFont(String fontLocation, float size) {
        font = uiCanvas.getApp().createFont(fontLocation, size);
    }
    public void setStyle(int h, int v){
        horzStyle = h;
        vertStyle = v;
    }
}