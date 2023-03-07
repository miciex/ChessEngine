package ui;

import GameStates.Playing;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ButtonOverlay extends UIElement implements ButtonMethods{

    public Playing playing;
    private ResetButton rb;

    public ButtonOverlay(int xPos, int yPos, int width, int height, Playing playing) {
        super(xPos, yPos, width, height);
        this.playing = playing;
        initButtons();
    }

    private void initButtons(){
        rb = new ResetButton(xPos, playing.BOARD_Y, 100, 50, this, Color.black, "Restart");
    }

    @Override
    public void update(Graphics g) {

    }

    @Override
    public void draw(Graphics g) {
        rb.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        rb.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
