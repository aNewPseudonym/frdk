package frdk.geom;

import processing.core.*;

class Node{

    final static int OUTSIDE = 0;
    final static int INSIDE = 1;
    final static int ENTRY = 2;
    final static int EXIT = 3;
    final static int BOUNCE = 4;

    PVector pos;
    boolean isIntersection;
    boolean processed;
    int label;
    Node next, prev, cross;

    Node(PVector p){
        pos = new PVector(p.x, p.y);
        isIntersection = false;
        processed = false;
        label = -1;
        next = null;
        prev = null;
        cross = null;
    }

    void intersect(Node x){
        isIntersection = true;
        cross = x;
    }
}