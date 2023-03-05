package ui;

import GameStates.Playing;
import utils.Constants;
import utils.LoadSave;
import utils.Piece;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
    private HashMap<Character, BufferedImage> chessPiecesImgs;

    public BoardOverlay(Playing playing){
        this.playing = playing;
        loadPiecesImgs();
        createFields();
    }

    private void createFields() {
        int board[] = playing.getBoard();
        fields = new BoardField[board.length];
        for(int i = 0; i<board.length; i++){
            int currX = FIELD_SIZE * (i%8);
            int currY =  FIELD_SIZE * (int)(i/BOARD_WIDTH);
            fields[i] = new BoardField(START_X + currX, START_Y +currY, i, intToCharPiece(board[i]), this);
                if (i%2!=(i/8)%2) {
                    fields[i].fieldColor = Color.darkGray;
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
        int col = (e.getX() - START_X)/FIELD_SIZE;
        int row = (e.getY() - START_Y)/FIELD_SIZE;



        if(row < 0 && col < 0) return;

        if(fields[col + row * BOARD_WIDTH].getPiece()!= ' ' && activeField<0){
            activeField = col + row * BOARD_WIDTH;
            fields[activeField].setMousePressed(true);
            moves = Piece.PossibleMoves(activeField);
            showPossibleMoves();
            return;
        }
        if(activeField >= 0 )
            newField = col + row * BOARD_WIDTH;

        if(activeField != newField && newField>=0 && canMoveHere(newField)){
            movePiece(col, row);
            fields[activeField].setMousePressed(false);
            activeField = -1;
            newField = -1;
            resetColors();
            return;
        }else if(newField>=0){
            fields[activeField].setMousePressed(false);
            resetColors();
            if(fields[newField].getPiece() != ' ' && activeField != newField){
                activeField = newField;
                fields[activeField].setMousePressed(true);
                moves = Piece.PossibleMoves(activeField);
                showPossibleMoves();
            }else{
                activeField = -1;
            }
            newField = -1;

            return;
        }

        if(newField<0&&activeField>=0){
            moves = Piece.PossibleMoves(activeField);
            showPossibleMoves();
        }
    }

    public void mouseReleased(MouseEvent e) {
        int col = (e.getX() - START_X)/FIELD_SIZE;
        int row = (e.getY() - START_Y)/FIELD_SIZE;
        if(activeField<0){
            return;
        }

        if(col + row * BOARD_WIDTH != activeField &&canMoveHere(col + row * BOARD_WIDTH) && newField<0) {
            movePiece(col, row);
            activeField = -1;
            newField = -1;
            resetColors();
            return;
        }

        if(newField>=0){
            moves = Piece.PossibleMoves(activeField);
            showPossibleMoves();
        }
            fields[activeField].setMousePressed(false);

    }

    public void mouseClicked(MouseEvent e){

    }

    private void showPossibleMoves(){
        for (int move : moves) {
            fields[move].fieldColor = Color.red;
        }
    }

    private void resetColors(){
        for(int i = 0; i<fields.length; i++){
            if (i%2!=(i/8)%2) {
                fields[i].fieldColor = Color.darkGray;
            } else {
                fields[i].fieldColor = Color.white;
            }
        }
    }

    private void movePiece(int col, int row){
        int moveField = col + row * BOARD_WIDTH;

        if(activeField == moveField)
            fields[activeField].resetBools();
        else
        {
            if(Playing.ActivePieces.containsKey(moveField))
                Playing.ActivePieces.remove(moveField);

            Playing.ActivePieces.put(moveField, Playing.ActivePieces.get(activeField));
            Playing.ActivePieces.remove(activeField);
            fields[moveField].setPiece(fields[activeField].getPiece());
            fields[activeField].setPiece(' ');
        }
    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void draw(Graphics g){
        for(BoardField f : fields){
            f.drawSquare(g);
        }
        for(BoardField f : fields){
            f.drawPiece(g);
        }
        if(activeField>=0)
        fields[activeField].drawPiece(g);
    }

    private void loadPiecesImgs(){
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PIECES_ATLAS);
        chessPiecesImgs = new HashMap<>();
        for(int i = 0; i<12; i++){
            if(i<6)
                chessPiecesImgs.put(Character.toUpperCase(Constants.Pieces.CHAR_PIECES[i%6]), img.getSubimage((img.getWidth()/6)*(i%6), 0,img.getWidth()/6, img.getHeight()/2 ));
            else
                chessPiecesImgs.put(Character.toLowerCase(Constants.Pieces.CHAR_PIECES[i%6]), img.getSubimage((img.getWidth()/6)*(i%6), (img.getHeight()/2),img.getWidth()/6, img.getHeight()/2 ));
        }
    }

    private boolean canMoveHere(int fieldNumber){
        for(int move: moves){
            if(move == fieldNumber) return true;
        }
        return false;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public HashMap<Character, BufferedImage> getChessPiecesImgs() {
        return chessPiecesImgs;
    }
}
