package frdk.ui;

import processing.core.*;

// TO-DO: add hide/show functionality here, baked in
//  maybe have drawDecorator be final, with abstract function called within it?

public abstract class uiDecorator implements PConstants{
    public abstract void drawDecorator(uiCanvas canvas);
}