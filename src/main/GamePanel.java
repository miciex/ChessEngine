package main;

//import Inputs.KeyboardInputs;
//import Inputs.MouseInputs;

import Inputs.MouseInputs;

import javax.swing.*;
import java.awt.*;

import static utils.Constants.Game_Info.*;

public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private Game game;

    public GamePanel(Game game){
        mouseInputs = new MouseInputs(this);
        this.game = game;
        setPanelSize();
        //addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);

    }

    private void setPanelSize(){
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        System.out.println("size: " + GAME_WIDTH + " : " + GAME_HEIGHT);
        setMinimumSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
    }

    public void updateGame(){

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        game.render(g);
    }

    public Game getGame() {
        return game;
    }
}
