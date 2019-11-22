package frdk.ui;

interface Broadcaster{
    public void addSub(Subscriber sub);
    public void removeSub(Subscriber sub);
    public void broadcast();               //tells all subscribers to tunein()
}

