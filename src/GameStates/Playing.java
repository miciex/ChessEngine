package GameStates;

import main.Game;
import ui.BoardOverlay;
import ui.ButtonMethods;
import ui.ButtonOverlay;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.Constants.Boards.classicBoard;
import static utils.Constants.Field.FIELD_SIZE;
import static utils.Constants.Game_Info.*;
import static utils.Constants.BoardInfo.*;
import static utils.HelpMethods.*;

public class Playing extends State implements StateMethods{

    private int[] board;
    public static ArrayList<Move> moves;
    public static boolean[] possibleCastles;
    public static boolean whitesMove = true;
    public final int BOARD_X = (GAME_WIDTH-BOARD_WIDTH*FIELD_SIZE)/2;
    public final int BOARD_Y = (GAME_HEIGHT-BOARD_HEIGHT*FIELD_SIZE)/2;

    BoardOverlay boardOverlay;
    ButtonOverlay buttonOverlay;
    ButtonOverlay additionButtonOverlay;

    public static HashMap<Integer, Integer> ActivePieces = new HashMap<>();

    public Playing(Game game){
        super(game);
        board = FenToIntArray(classicBoard, BOARD_HEIGHT * BOARD_WIDTH);
        moves = new ArrayList<>();
        possibleCastles = new boolean[]{true, true, true, true};
        initClasses();
    }

    public void resetGame(){
        board = FenToIntArray(classicBoard, BOARD_HEIGHT * BOARD_WIDTH);
        moves = new ArrayList<>();
        boardOverlay.createFields();
        whitesMove = true;
        possibleCastles = new boolean[]{true, true, true, true};
    }

    private void initClasses(){
        boardOverlay = new BoardOverlay(BOARD_X, BOARD_Y, this);
        buttonOverlay = new ButtonOverlay((BOARD_X + BOARD_WIDTH * FIELD_SIZE), 0, 0,0, this);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics g){
        boardOverlay.draw(g);
        buttonOverlay.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        boardOverlay.mouseClicked(e);
        buttonOverlay.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        boardOverlay.mousePressed(e);
        buttonOverlay.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        boardOverlay.mouseReleased(e);
        buttonOverlay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        boardOverlay.mouseMoved(e);
        buttonOverlay.mouseMoved(e);
    }

    public void mouseDragged(MouseEvent e){
        boardOverlay.mouseDragged(e);
    }



    public int[] getBoard(){
        return board;
    }

    public ArrayList<Move> getMoves() {
        return moves;
    }

    public void setMoves(ArrayList<Move> moves) {
        this.moves = moves;
    }

    public void addMove(Move move){
        this.moves.add(move);
    }

    public void updateBoard(int index, int piece){
        this.board[index] = piece;
    }

}
