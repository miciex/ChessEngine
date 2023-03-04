package GameStates;

import main.Game;
import ui.BoardOverlay;

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

    BoardOverlay overlay;

    public Playing(Game game){
        super(game);
        board = FenToIntArray(classicBoard, BOARD_HEIGHT * BOARD_WIDTH);
        initClasses();
    }

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

}
