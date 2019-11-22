package frdk.ui;

import processing.core.*;
import java.util.ArrayList;

public class uiButton extends uiCanvas implements Clickable, Broadcaster {
    
    private ArrayList<Subscriber> subscribers;
    uiButton(float x, float y, float w, float h) {
        super(x,y,w,h);
        subscribers = new ArrayList<Subscriber>();
    }

    public void addSub(Subscriber sub){
        subscribers.add(sub);
    }
    public void removeSub(Subscriber sub){
        subscribers.remove(sub);
    }
    public void broadcast() {
        for(Subscriber sub : subscribers){
            sub.tuneIn( (Broadcaster)this );
        }
    }
    public void click() {
        broadcast();
    }
}