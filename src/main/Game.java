package main;

import GameStates.Playing;

import java.awt.*;
import static utils.Constants.Game_Info.*;

public class Game implements Runnable{
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    GamePanel gamePanel;
    GameWindow gameWindow;
    Playing playing;

    public Game(){
        initClasses();
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();

        startGameLoop();
    }

    private void initClasses(){
        gamePanel = new GamePanel(this);
        playing = new Playing(this);
        gameWindow = new GameWindow(800, 600, gamePanel);
    }

    private  void startGameLoop(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = 0;

        double deltaU = 0;
        double deltaF = 0;

        while(true){
            long currentTime = System.nanoTime();

            deltaU += (currentTime-previousTime)/timePerUpdate;
            deltaF += (currentTime-previousTime)/timePerFrame;


            previousTime = currentTime;

            if(deltaU>=1){
                //update();
                updates++;
                deltaU--;
            }

            if(deltaF >= 1){
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            if(System.currentTimeMillis()-lastCheck>=1000){
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames+ " | UPS: "+updates);
                frames = 0;
                updates = 0;
            }
        }

    }

    public void render(Graphics g) {
        playing.draw(g);
    }

    public Playing getPlaying() {
        return playing;
    }

}
