package frdk.ui;

public class uiButton extends uiCanvas implements Clickable{
    
    Command clickCommand;

    public uiButton(float x, float y, float w, float h, Command c) {
        super(x,y,w,h);
        clickCommand = c;
    }
    public void click() {
        clickCommand.execute();
    }
}