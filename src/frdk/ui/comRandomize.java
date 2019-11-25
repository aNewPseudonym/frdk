package frdk.ui;

public class comRandomize extends Command{
    uidBackground target;
    
    comRandomize(uidBackground target){
        this.target = target;
    }

    public void execute(){
        target.fill = uiCanvas.getApp().color(uiCanvas.getApp().random(255),uiCanvas.getApp().random(255),uiCanvas.getApp().random(255));
    }
}