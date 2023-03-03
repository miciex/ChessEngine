package GameStates;

import main.Game;

import java.awt.*;
import java.awt.event.MouseEvent;

import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;
import static utils.Constants.Boards.classicBoard;
import static utils.HelpMethods.FenToIntArray;
import static utils.HelpMethods.intToCharPiece;

public class Playing extends State implements StateMethods{

    private int[] board;
    private final int BOARD_HEIGHT = 8;
    private final int BOARD_WIDTH = 8;
    private final int FIELD_SIZE = 32;
    private final int START_X = (GAME_WIDTH-BOARD_WIDTH*FIELD_SIZE)/2;
    private final int START_Y = (GAME_HEIGHT-BOARD_HEIGHT*FIELD_SIZE)/2;

    public Playing(Game game){
        super(game);
        board = FenToIntArray(classicBoard, BOARD_HEIGHT * BOARD_WIDTH);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics g){
        drawBoard(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

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

    private void drawBoard(Graphics g){
        for(int i = 0; i<BOARD_HEIGHT; i++){
            for(int j = 0; j<BOARD_WIDTH; j++){
                int currX = START_X + FIELD_SIZE * j;
                int currY = START_Y + FIELD_SIZE * i;
                if(i%2!=j%2){
                    g.setColor(Color.BLACK);
                }else
                g.setColor(Color.white);
                g.fillRect(currX - FIELD_SIZE/2,  currY -FIELD_SIZE/2, FIELD_SIZE, FIELD_SIZE);
                g.setColor(Color.pink);
                g.drawString(String.valueOf(intToCharPiece(board[i*BOARD_WIDTH + j])),currX,currY);
            }
        }
    }

    public int[] getBoard() {
        return board;
    }
}
