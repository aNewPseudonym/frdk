package frdk.ui;

public class uiHoverBox extends uiCanvas implements Selectable{
    boolean isSelected;
    uidBackground bg;
    
    public uiHoverBox(int x, int y){
        super(x, y, 100, 100);
        bg = new uidBackground(0xffe2e8dd);
        addDecorator(bg);
        isSelected = false;
    }

    public boolean isSelected(){
        return isSelected;
    }
    public void select() {
        isSelected = true;
        bg.fill = 0xffe9806e;
    }
    public void deselect() {
        isSelected = false;
        bg.fill = 0xffe2e8dd;
    }
}