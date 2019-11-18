package input;

import processing.core.*;
import processing.event.*;

public class KeyManager implements PConstants {

    private class Key {
        boolean isPressed;      // indicates pressed state
        int lastChange;         // holds time in millis of last state change
        Key() {
            isPressed = false;
            lastChange = 0;
        }
    }
    
    private Key[] keys;         // array for non-coded ASCII key inputs
    private Key[] keyCodes;     // array for coded
    private boolean printKeys;
    PApplet parent;
    
    public KeyManager(PApplet p) {
        parent = p;
        printKeys = false;

        // keys array covers all 256 ASCII keys
        keys = new Key[256];
        for(int i = 0; i < keys.length; i++) {
            keys[i] = new Key();
        }

        // keycodes array covers all Processing keyCodes ->
        //  UP, DOWN, LEFT, RIGHT, ALT, CONTROL, SHIFT
        // Also covers MOST Java virtual keys (VK), but not all
        //  VKs like VK_F13 and higher, or VK_CUT, _COPY, _PASTE are not covered.
        keyCodes = new Key[526];
        for(int i = 0; i < keyCodes.length; i++) {
            keyCodes[i] = new Key();
        }
        parent.registerMethod("keyEvent", this);
    }

    // Registered method to automatically respond to Processing keyEvents.
    public void keyEvent(KeyEvent e) {
        if(printKeys){
            if(e.getKey() == CODED) {
                PApplet.println("Coded Key: ", e.getKey(), " KeyCode: ", e.getKeyCode());
            } else {
                PApplet.println(" Reg. Key: ", e.getKey(), " KeyCode: ", e.getKeyCode());
            }
        }

        switch (e.getAction()) {
            case KeyEvent.PRESS:
                handlePress(e);
                break;
            case KeyEvent.RELEASE:
                handleRelease(e);
                break;
        }
    }

    // Functions to toggle printing info on all keyEvents.
    //  Good for testing what physical keys map to what digital key inputs.
    public void enablePrintKeys() {
        printKeys = true;
    }
    public void disablePrintKeys() {
        printKeys = false;
    }

    // If key is released, press it and reset lastChange
    private void handlePress(KeyEvent e) {
        char c = e.getKey();
        int i = e.getKeyCode();

        if(c != CODED) {
            if(!keys[c].isPressed) {
                keys[c].isPressed = true;
                keys[c].lastChange = parent.millis();
            }
        } else {
            if(!keyCodes[i].isPressed) {
                keyCodes[i].isPressed = true;
                keyCodes[i].lastChange = parent.millis();
            }
        }
    }
    
    // If key is pressed, release it and reset lastChange
    private void handleRelease(KeyEvent e) {
        char c = e.getKey();
        int i = e.getKeyCode();

        if(c != CODED) {
            if(keys[c].isPressed) {
                keys[c].isPressed = false;
                keys[c].lastChange = parent.millis();
            }
        } else {
            if(keyCodes[i].isPressed) {
                keyCodes[i].isPressed = false;
                keyCodes[i].lastChange = parent.millis();
            }
        }
    }

    // Functions to return pressed state of key.
    public boolean getState(char c) {
        return keys[c].isPressed;
    }
    public boolean getState(int i) {
        return keyCodes[i].isPressed;
    }

    // Functions to return time since last key release.
    // Returns 0 if currently pressed.
    public int timeReleased(char c) {
        if(keys[c].isPressed) {
            return 0;
        } else {
            return (parent.millis() - keys[c].lastChange);
        }
    }
    public int timeReleased(int i) {
        if(keyCodes[i].isPressed) {
            return 0;
        } else {
            return (parent.millis() - keyCodes[i].lastChange);
        }
    }

    // Functions to return time since last key press.
    // Returns 0 if currently released.
    public int timePressed(char c) {
        if(keys[c].isPressed) {
            return (parent.millis() - keys[c].lastChange);
        } else {
            return 0;
        }
    }
    public int timePressed(int i) {
        if(keyCodes[i].isPressed) {
            return (parent.millis() - keyCodes[i].lastChange);
        } else {
            return 0;
        }
    }

}