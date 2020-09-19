package frdk.ui;

import processing.core.*;

import java.util.HashMap;
import java.util.Iterator;

import frdk.geom.FPolygon;

//TO-DO: add ability to specify specific canvases for picking - 
//  i.e., pass it a set/list and only have it track those,
//  as opposed to automatically tracking a whole nested tree.

public class CanvasPicker{
    PApplet app;
    uiCanvas rootCanvas;
    private PGraphics buffer;
    private int currentID;

    private HashMap<Integer, uiCanvas> canvasDict;

    public CanvasPicker(uiCanvas canvas) {
        app = uiCanvas.getApp();
        app.registerMethod("pre", this);

        buffer = app.createGraphics(app.width, app.height);
        currentID = -1;
        rootCanvas = canvas;

        canvasDict = new HashMap<Integer, uiCanvas>();
        updateDict();
    }

    public void updateDict(){
        currentID = 0;
        defineCanvas(rootCanvas);
    }

    public void defineCanvas(uiCanvas currentCanvas){
        canvasDict.put( Integer.valueOf(currentID), currentCanvas);
        Iterator<uiCanvas> iter = currentCanvas.getElementIterator();
        while(iter.hasNext()){
            currentID += 1;
            defineCanvas(iter.next());
        }
    }

    public void pre(){
        buffer.beginDraw();

        buffer.background(0xffffffff);
        buffer.noStroke();

        currentID = 0;
        drawCanvas(rootCanvas);

        buffer.endDraw();
    }

    private void drawCanvas(uiCanvas currentCanvas){
        FPolygon shape = currentCanvas.shape;
        
        buffer.pushMatrix();
        
        PVector absPos = currentCanvas.getAbsolutePosition();
        buffer.translate(absPos.x, absPos.y);
        buffer.fill(currentID - 16777215);
        shape.draw(buffer);

        buffer.popMatrix();

        //call upon children to call themselves
        Iterator<uiCanvas> iter = currentCanvas.getElementIterator();
        while(iter.hasNext()){
            currentID += 1;
            drawCanvas(iter.next());
        }
    }

    public int getID(int x, int y){
        buffer.loadPixels();
        if((x>=0 && x<buffer.pixelWidth) && (y>=0 && y<buffer.pixelHeight)){
            int c = buffer.pixels[y*buffer.pixelWidth + x];
            return (c == -1) ? c : c + 16777215;
        } else { return -1; }
    }

    public uiCanvas getCanvas(int x, int y){
        return canvasDict.get(getID(x,y));
    }

    public void printBuffer(){
        app.image(buffer,0,0);
    }
}