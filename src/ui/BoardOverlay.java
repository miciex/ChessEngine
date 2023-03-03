package ui;

import GameStates.Playing;
import utils.Constants;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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
    int activeField;
    private int mouseX;
    private int mouseY;
    private ArrayList<Integer> moves= new ArrayList<>();

    public BoardOverlay(Playing playing){
        this.playing = playing;
        createFields();
    }

    private void createFields() {
        moves.add(4);
        moves.add(2);
        moves.add(12);
        moves.add(43);
        int board[] = playing.getBoard();
        fields = new BoardField[board.length];
        for(int i = 0; i<board.length; i++){
            int currX = FIELD_SIZE * (i%8);
            int currY =  FIELD_SIZE * (int)(i/BOARD_WIDTH);
            fields[i] = new BoardField(START_X + currX, START_Y +currY, i, intToCharPiece(board[i]), this);
        }
    }

    public void mouseDragged(MouseEvent e){
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void mousePressed(MouseEvent e) {
        activeField = -1;
        for(int i = 0; i<fields.length; i++){
            fields[i].fieldColor = null;
            fields[i].setMousePressed(false);
            if(fields[i].isIn(e)){
                activeField = i;
            }
        }
        if(activeField>=0 && fields[activeField].getPiece()!= ' ') {
            fields[activeField].setMousePressed(true);
            for (int move : moves) {
                fields[move].fieldColor = Color.red;
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        for(int i = 0; i<fields.length; i++){
            fields[i].fieldColor = null;
            if(i==activeField) {
                fields[i].resetBools();
                continue;
            }
            if(fields[i].isIn(e) && activeField>=0 &&fields[activeField].getPiece()!=' ' ){
                fields[i].setPiece(fields[activeField].getPiece());
                fields[activeField].setPiece(' ');
            }
            fields[i].resetBools();
        }
        activeField = -1;
    }

    public void mouseClicked(MouseEvent e){

    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void draw(Graphics g){
        for(BoardField f : fields){
            f.draw(g);
        }
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
}
