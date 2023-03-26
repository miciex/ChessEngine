package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import static utils.Constants.Game_Info.GAME_HEIGHT;
import static utils.Constants.Game_Info.GAME_WIDTH;

public class GameWindow{
    private JFrame jframe;

    public GameWindow(int width, int height, GamePanel gamePanel){
        jframe = new JFrame();

        //jframe.getContentPane().setLayout(null);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        jframe.add(gamePanel);

        jframe.setResizable(true);

        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);

        jframe.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                //gamePanel.getGame().windowFocusLost();
            }
        });
    }

//    private void addLabel(){
//        JLabel mailLabel = new JLabel("e-mail:");
//        mailLabel.setLocation(10, 10);
//        mailLabel.setSize(50, 30);
//        jframe.add(mailLabel);
//        mailLabel.update();
//    }

    public void addJComponent(JComponent component){
        jframe.add(component);
    }

    public void removeJComponent(JComponent component){
        jframe.remove(component);
    }
}