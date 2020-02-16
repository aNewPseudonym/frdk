package frdk.geom;

import processing.core.*;

class Node{

    final static int ON_ON = 0;
    final static int ON_RIGHT = 1;
    final static int ON_LEFT = 2;
    final static int RIGHT_ON = 3;
    final static int RIGHT_RIGHT = 4;
    final static int RIGHT_LEFT = 5;
    final static int LEFT_ON = 6;
    final static int LEFT_RIGHT = 7;
    final static int LEFT_LEFT = 8;

    PVector pos;
    Node next, prev, cross;
    int sidedness;
    boolean isCrossing;
    boolean isEntry;

    boolean traced;

    Node(PVector p){
        pos = new PVector(p.x, p.y);
        next = null;
        prev = null;
        cross = null;

        // labels
        sidedness = -1;
        isCrossing = false;
        isEntry = false;

        traced = false;
    }

    void intersect(Node x){
        cross = x;
    }

    boolean isIntersection(){
        return (cross != null);
    }
}