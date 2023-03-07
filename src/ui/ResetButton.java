package ui;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ResetButton extends UIElement implements ButtonMethods{

    private Color textColor;
    private ButtonOverlay buttonOverlay;
    private String text = "";

    public ResetButton(int xPos, int yPos, int width, int height, ButtonOverlay buttonOverlay, Color textColor, String text) {
        super(xPos, yPos, width, height);
        this.textColor = textColor;
        this.buttonOverlay = buttonOverlay;
        this.text = text;
    }

    @Override
    public void update(Graphics g){
        draw(g);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.PINK);
        g.fillRect(xPos, yPos, bounds.width, bounds.height);
        g.setColor(textColor);
        g.drawString(text, xPos+25, yPos + 30);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(isIn(e)){
            buttonOverlay.playing.resetGame();
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
