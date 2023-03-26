package ui;

import GameStates.Playing;

import java.awt.*;
import java.awt.event.MouseEvent;

import utils.Constants;
import static utils.Constants.Colors.*;

public class ButtonOverlay extends UIElement implements ButtonMethods{

    public Playing playing;
    private ResetButton rb;
    private ResetButton rbb;
    private ResetButton rbw;
    private int resetButtonWidth = 100;
    private int resetButtonHeight=  50;
    private int resetButtonGap = 25;

    public ButtonOverlay(int xPos, int yPos, int width, int height, Playing playing) {
        super(xPos, yPos, width, height);
        this.playing = playing;

        initButtons();
    }

    private void initButtons(){
        rb = new ResetButton(xPos, playing.BOARD_Y, resetButtonWidth, resetButtonHeight, playing, Color.black, "Play again", Random_Color);
        rbw = new ResetButton(xPos, playing.BOARD_Y + resetButtonHeight + resetButtonGap , resetButtonWidth, resetButtonHeight, playing, Color.black, "Play as white", WHITE);
        rbb = new ResetButton(xPos, playing.BOARD_Y + (resetButtonHeight + resetButtonGap) * 2, resetButtonWidth, resetButtonHeight, playing, Color.black, "Play as black", BLACK);
    }

    @Override
    public void update(Graphics g) {

    }

    @Override
    public void draw(Graphics g) {
        rb.draw(g);
        rbb.draw(g);
        rbw.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        rb.mouseClicked(e);
        rbb.mouseClicked(e);
        rbw.mouseClicked(e);
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
