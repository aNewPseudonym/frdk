package frdk.ui;

import processing.core.*;

public class uiText extends uiCanvas implements PConstants{
    String content;
    PFont font;
    int fill, stroke;
    int horzStyle, vertStyle;

    public uiText(float x, float y, float w, float h, String s){
        super(x,y,w,h);

        content = s;

        font = new PFont();
        fill = 0x00000000;
        stroke = 0x00000000;

        horzStyle = LEFT;
        vertStyle = TOP;
    }

    public void drawCanvas() {
        PApplet app = uiCanvas.getApp();

        app.pushStyle();
        //app.clip(0, 0, canvas.dim.x, canvas.dim.y);
    
        app.textFont(font);
        app.textLeading(font.getSize() + 2);
        app.textAlign(horzStyle, vertStyle);
        app.stroke(stroke);
        app.fill(fill);
        app.text(content, 0, 0);
    
        //app.noClip();
        app.popStyle();
        
        super.drawCanvas();
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