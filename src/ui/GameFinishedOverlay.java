package ui;

import GameStates.Playing;
import main.Game;
import utils.HelpMethods;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import static utils.Constants.Colors.Random_Color;

public class GameFinishedOverlay extends UIElement implements ButtonMethods{

    private Game game;
    Color color = new Color(50);
    ResetButton rb;
//    JTextField text;
    boolean componentsAdded = false;
    String text;
    Playing playing;

    public GameFinishedOverlay(Game game, Playing playing, int xPos, int yPos, int width, int height, String text) {
        super(xPos, yPos, width, height);
        this.playing = playing;
        this.game = game;
        this.text = text;
        this.rb = new ResetButton((int) (xPos + 0.25 * width), (int) (yPos + 0.5 * height), (int) (width * 0.5), (int) (height * 0.2), playing, new Color(50), "Restart game", Random_Color);
//        this.text =  new JTextField();
//        this.text.setText(text);
//        this.text.setFont(this.text.getFont().deriveFont(12f));

    }

    public void addComponentsToWindow(){
        if(componentsAdded) return;
//        game.gameWindow.addJComponent(this.text);
//        this.text.setBounds(new Rectangle(50, 50,100, 100));
        //rb = new
        rb.setText(HelpMethods.gameResultToChessNotation(playing.result, playing.board.whiteToMove));
        componentsAdded = true;
    }

    public void removeComponentsFromWindow(){
//        game.gameWindow.removeJComponent(this.text);
        componentsAdded = false;
    }

    @Override
    public void update(Graphics g) {

    }

    @Override
    public void draw(Graphics g) {
        if(!componentsAdded) return;
        g.setColor(color);
        g.fillRect(xPos, yPos, bounds.width, bounds.height);
        g.setColor(Color.WHITE);
        g.drawString(text, xPos +50, yPos + 50);
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
