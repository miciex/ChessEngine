package GameStates;

import main.Game;

import java.awt.*;
import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;
import static utils.Constants.basicBoard;

public class Playing {

    Character[][] board;
    private final int BOARD_HEIGHT = 8;
    private final int BOARD_WIDTH = 8;
    private final int FIELD_SIZE = 32;
    private final int START_X = (GAME_WIDTH-BOARD_WIDTH*FIELD_SIZE)/2;
    private final int START_Y = (GAME_HEIGHT-BOARD_HEIGHT*FIELD_SIZE)/2;
    Game game;

    public Playing(Game game){
        this.game = game;
        board = basicBoard;
    }

    public void draw(Graphics g){
        drawBoard(g);
    }

    private void drawBoard(Graphics g){
        for(int i = 0; i<board.length; i++){
            for(int j = 0; j<board.length; j++){
                int currX = START_X + FIELD_SIZE * i;
                int currY = START_Y + FIELD_SIZE * j;
                if(i%2!=j%2){
                    g.setColor(Color.BLACK);
                }else
                g.setColor(Color.white);
                g.fillRect(currX - FIELD_SIZE/2,  currY -FIELD_SIZE/2, FIELD_SIZE, FIELD_SIZE);
                g.setColor(Color.pink);
                g.drawString(String.valueOf(board[i][j]),currX,currY);
            }
        }
    }

}
