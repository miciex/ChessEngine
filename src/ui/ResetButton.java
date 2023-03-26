package ui;

import GameStates.Playing;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Random;

public class ResetButton extends UIElement implements ButtonMethods{

    private Color textColor;
    private Playing playing;
    private String text = "";
    private Random rnd = new Random();
    private String color;

    public ResetButton(int xPos, int yPos, int width, int height, Playing playing, Color textColor, String text, String color) {
        super(xPos, yPos, width, height);
        this.textColor = textColor;
        this.playing = playing;
        this.text = text;
        this.color = color;
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
            playing.resetGame(color);
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

    public void setText(String text){
        this.text = text;
    }


}
