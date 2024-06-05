// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T2, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;

/** Pencil   */
public class Pencil{
    private double lastX;
    private double lastY;

    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addButton("Quit", UI::quit);
        UI.setLineWidth(3);
        UI.setDivider(0.0);
    }

    /**
     * Respond to mouse events
     */
    public void doMouse(String action, double x, double y) {
        if (action.equals("pressed")){
            lastX = x;
            lastY = y;
        }
        else if (action.equals("dragged")){
            UI.drawLine(lastX, lastY, x, y);
            lastX = x;
            lastY = y;
        }
        else if (action.equals("released")){
            UI.drawLine(lastX, lastY, x, y);
        }
    }

    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }

}
