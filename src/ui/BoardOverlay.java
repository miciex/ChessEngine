package ui;

import GameStates.Playing;
import utils.Constants;

import java.awt.event.MouseEvent;

import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;
import static utils.Constants.Field.FIELD_SIZE;
import static utils.HelpMethods.intToCharPiece;

public class BoardOverlay {

    private final int BOARD_HEIGHT = 8;
    private final int BOARD_WIDTH = 8;
    private BoardField[] fields;
    private Playing playing;
    private final int START_X = (GAME_WIDTH-BOARD_WIDTH*FIELD_SIZE)/2;
    private final int START_Y = (GAME_HEIGHT-BOARD_HEIGHT*FIELD_SIZE)/2;

    public BoardOverlay(Playing playing){
        this.playing = playing;
        createFields();
    }

    private void createFields() {
        int board[] = playing.getBoard();
        fields = new BoardField[board.length];
        for(int i = 0; i<board.length; i++){
            int currX = START_X + FIELD_SIZE * i%BOARD_WIDTH;
            int currY = START_Y + FIELD_SIZE * i/BOARD_WIDTH;
            fields[i] = new BoardField(currX, currY, i, intToCharPiece(board[i]));
        }
    }

    public void mouseDragged(MouseEvent e){

    }

    public void mousePressed(MouseEvent e) {


    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {

    }
}
