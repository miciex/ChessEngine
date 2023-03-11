package ui;

import GameStates.Playing;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ButtonOverlay extends UIElement implements ButtonMethods{

    public Playing playing;
    private ResetButton rb;
    private PromotedPieceButton[] promotedPieceButtons = new PromotedPieceButton[4];

    public ButtonOverlay(int xPos, int yPos, int width, int height, Playing playing) {
        super(xPos, yPos, width, height);
        this.playing = playing;

        initButtons();
    }

    private void initButtons(){
        rb = new ResetButton(xPos, playing.BOARD_Y, 100, 50, this, Color.black, "Restart");
        promotedPieceButtons[0] = new PromotedPieceButton(xPos+10, playing.BOARD_Y+100, 50, 50, this, Color.black, "Queen", 6);
        promotedPieceButtons[1] = new PromotedPieceButton(xPos+10, playing.BOARD_Y+150, 50, 50, this, Color.black, "Knight",4);
        promotedPieceButtons[2] = new PromotedPieceButton(xPos+10, playing.BOARD_Y+200, 50, 50, this, Color.black, "Rook",3);
        promotedPieceButtons[3] = new PromotedPieceButton(xPos+10, playing.BOARD_Y+250, 50, 50, this, Color.black, "Bishop",5);
    }

    @Override
    public void update(Graphics g) {

    }

    @Override
    public void draw(Graphics g) {
        rb.draw(g);
        for(PromotedPieceButton ppb : promotedPieceButtons)
            if(ppb != null)
                ppb.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        rb.mouseClicked(e);
        for(PromotedPieceButton ppb : promotedPieceButtons)
            if(ppb != null)
                ppb.mouseClicked(e);
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
