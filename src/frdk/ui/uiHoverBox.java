package frdk.ui;

import processing.core.*;

public class uiHoverBox extends uiCanvas implements Selectable{
    boolean isSelected;
    uidBackground bg;
    
    public uiHoverBox(int x, int y){
        super(x, y, 50, 50);
        bg = new uidBackground(parent.color(226,232,221));
        addDecorator(bg);
        isSelected = false;
    }

    public boolean isSelected(){
        return isSelected;
    }
    public void select() {
        isSelected = true;
        bg.setColor(parent.color(233,128,110));
    }
    public void deselect() {
        isSelected = false;
        bg.setColor(parent.color(226,232,221));
    }
}