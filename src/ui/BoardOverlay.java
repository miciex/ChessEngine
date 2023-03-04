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
    int activeField = -1;
    int newField = -1;
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
                if (i%2!=(i/8)%2) {
                    fields[i].fieldColor = Color.BLACK;
                } else {
                    fields[i].fieldColor = Color.white;
                }
            }
        }

    public void mouseDragged(MouseEvent e){
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void mousePressed(MouseEvent e) {
        System.out.println("Mouse Pressed");
        int col = (e.getX() - START_X)/FIELD_SIZE;
        int row = (e.getY() - START_Y)/FIELD_SIZE;
        newField = -1;
        resetColors();
        if(row < 0 && col < 0) return;
        if(activeField >= 0 ) newField = col + row * BOARD_WIDTH;

        if(activeField==col + row * BOARD_WIDTH){
            fields[activeField].setMousePressed(true);
        }else if(fields[col + row * BOARD_WIDTH].getPiece()!= ' '){
            activeField = col + row * BOARD_WIDTH;
            fields[activeField].setMousePressed(true);
        }
        if(activeField>=0){
            for (int move : moves) {
                fields[move].fieldColor = Color.red;
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        //if(!mousePressed) return;
        System.out.println("Mouse released");
        int col = (e.getX() - START_X)/FIELD_SIZE;
        int row = (e.getY() - START_Y)/FIELD_SIZE;
        if(activeField<0){
            return;
        }

        boolean canMove = false;
        for(int move: moves){
            canMove = move == col + row * BOARD_WIDTH;
            if(canMove) break;
        }
        if(canMove) {
            movePiece(col, row);
            activeField = -1;
            return;
        }
        if(newField>=0){
            fields[activeField].setMousePressed(false);
            activeField = -1;
            resetColors();
            return;
        }
            fields[activeField].setMousePressed(false);

    }

    public void mouseClicked(MouseEvent e){

    }

    private void resetColors(){
        for(int i = 0; i<fields.length; i++){
            if (i%2!=(i/8)%2) {
                fields[i].fieldColor = Color.BLACK;
            } else {
                fields[i].fieldColor = Color.white;
            }
        }
    }

    private void movePiece(int col, int row){
        if(activeField == col + row * BOARD_WIDTH)
            fields[activeField].resetBools();
        else{
            fields[col + row * BOARD_WIDTH].setPiece(fields[activeField].getPiece());
            fields[activeField].setPiece(' ');
        }
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
