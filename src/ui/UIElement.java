package ui;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class UIElement{

    protected int xPos, yPos;
    protected boolean mouseOver, mousePressed;
    protected Rectangle bounds;

    public UIElement(int xPos, int yPos, int width, int height){
        this.xPos = xPos;
        this.yPos = yPos;
        this.bounds = new Rectangle(xPos, yPos, width, height);
    }

    public boolean isIn(MouseEvent e){
        return bounds.contains(e.getX(), e.getY());
    }
}
