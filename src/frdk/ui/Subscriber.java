package frdk.ui;

public interface Subscriber{
    public void tuneIn(Broadcaster bc);    //called by broadcaster in broadcast()
}