package ui;

import java.awt.*;
import java.awt.event.MouseEvent;

public interface ButtonMethods {

    public void update(Graphics g);

    public void draw(Graphics g);

    public void mouseClicked(MouseEvent e);

    public void mousePressed(MouseEvent e);

    public void mouseReleased(MouseEvent e);

    public void mouseMoved(MouseEvent e);

}
