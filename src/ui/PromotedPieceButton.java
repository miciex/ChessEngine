package ui;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PromotedPieceButton extends UIElement implements ButtonMethods
{
    private Color textColor;
    private ButtonOverlay buttonOverlay;
    private String text = "";
    private int piece;

    public PromotedPieceButton(int xPos, int yPos, int width, int height, ButtonOverlay buttonOverlay, Color textColor, String text, int piece) {
        super(xPos, yPos, width, height);
        this.textColor = textColor;
        this.buttonOverlay = buttonOverlay;
        this.text = text;
        this.piece = piece;
    }

    @Override
    public void update(Graphics g){
        draw(g);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(xPos, yPos, bounds.width, bounds.height);
        g.setColor(textColor);
        g.drawString(text, xPos+6, yPos + 30);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(isIn(e)){
            BoardOverlay.promotedPiece = piece;
        }
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
