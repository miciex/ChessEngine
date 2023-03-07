package GameStates;

import main.Game;
import ui.BoardOverlay;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.Constants.Boards.classicBoard;
import static utils.HelpMethods.FenToIntArray;

public class Playing extends State implements StateMethods{

    private int[] board;
    private final int BOARD_HEIGHT = 8;
    private final int BOARD_WIDTH = 8;
    private ArrayList<Move> moves;

    BoardOverlay overlay;

    public Playing(Game game){
        super(game);
        board = FenToIntArray(classicBoard, BOARD_HEIGHT * BOARD_WIDTH);
        moves = new ArrayList<>();
        initClasses();
    }

    public static HashMap<Integer, Integer> ActivePieces = new HashMap<>();

    private void initClasses(){
        overlay = new BoardOverlay(this);
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
        overlay.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        overlay.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        overlay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        overlay.mouseMoved(e);
    }

    public void mouseDragged(MouseEvent e){
        overlay.mouseDragged(e);
    }

    private void drawBoard(Graphics g){
        overlay.draw(g);
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
