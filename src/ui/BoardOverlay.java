package ui;

import GameStates.Move;
import GameStates.Playing;
import utils.Constants;
import utils.HelpMethods;
import utils.LoadSave;
import utils.Piece;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.Constants.BoardInfo.BOARD_WIDTH;
import static utils.Constants.Colors.BLACK;
import static utils.Constants.Colors.WHITE;
import static utils.Constants.Field.FIELD_SIZE;
import static utils.Constants.Pieces.*;
import static utils.HelpMethods.checkPossibleCastles;
import static utils.HelpMethods.intToCharPiece;

public class BoardOverlay extends UIElement{

    private BoardField[] fields;
    public Playing playing;
    int activeField = -1;
    int newField = -1;
    private int mouseX;
    private int mouseY;
    private ArrayList<Integer> moves= new ArrayList<>();
    private HashMap<Integer, BufferedImage> chessPiecesImgs;
    private ResetButton rb;


    public BoardOverlay(int xPos, int yPos, Playing playing){
        super(xPos, yPos, FIELD_SIZE * 8, FIELD_SIZE * 8);
        this.playing = playing;
        loadPiecesImgs();
        createFields();
    }



    public void createFields() {
        int board[] = playing.getBoard();
        fields = new BoardField[board.length];

        for(int i = 0; i<board.length; i++){
            int currX = FIELD_SIZE * (i%8);
            int currY =  FIELD_SIZE * (int)(i/BOARD_WIDTH);
            fields[i] = new BoardField(xPos + currX, yPos +currY, i, board[i], this);

                if (i%2!=(i/8)%2) {
                    fields[i].color = WHITE;
                } else {
                    fields[i].color = BLACK;
                }
            }
        }

    public void mouseDragged(MouseEvent e){
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void mousePressed(MouseEvent e) {
        int col = (e.getX() - xPos)/FIELD_SIZE;
        int row = (e.getY() - yPos)/FIELD_SIZE;

        if((row < 0 || col < 0 || row >= 8 || col >= 8)&&activeField>=0)  resetActivePieces();
        else if(row < 0 || col < 0 || row >= 8 || col >= 8) return;

        if(playing.getBoard()[col + row * BOARD_WIDTH]!= 0 && activeField<0){
            activeField = col + row * BOARD_WIDTH;
            fields[activeField].setMousePressed(true);
            if(HelpMethods.isWhite(Playing.ActivePieces.get(activeField)) == Playing.whitesMove)
            {
                moves = Piece.PossibleMoves(activeField);
                showPossibleMoves();
            }
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
            if(playing.getBoard()[newField] != 0 && activeField != newField){
                activeField = newField;
                fields[activeField].setMousePressed(true);
                if(HelpMethods.isWhite(Playing.ActivePieces.get(activeField)) == Playing.whitesMove)
                {
                    moves = Piece.PossibleMoves(activeField);
                    showPossibleMoves();
                }
            }else{
                activeField = -1;
            }
            newField = -1;

            return;
        }

        if(newField<0&&activeField>=0){
            if(HelpMethods.isWhite(Playing.ActivePieces.get(activeField)) == Playing.whitesMove)
            {
                moves = Piece.PossibleMoves(activeField);
                showPossibleMoves();
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        int col = (e.getX() - xPos)/FIELD_SIZE;
        int row = (e.getY() - yPos)/FIELD_SIZE;

        if(activeField<0){
            return;
        }

        if(row < 0 && col < 0 || row >= 8 && col >= 8) fields[activeField].setMousePressed(false);

        if(col + row * BOARD_WIDTH != activeField &&canMoveHere(col + row * BOARD_WIDTH) && newField<0) {
            movePiece(col, row);
            resetActivePieces();
            return;
        }

        if(newField>=0){
            if(HelpMethods.isWhite(Playing.ActivePieces.get(activeField)) == Playing.whitesMove)
            {
                moves = Piece.PossibleMoves(activeField);
                showPossibleMoves();
            }
        }
        fields[activeField].setMousePressed(false);

    }

    private void resetActivePieces(){
        fields[activeField].setMousePressed(false);
        activeField = -1;
        newField = -1;
        resetColors();
    }

    public void mouseClicked(MouseEvent e){
    }

    private void showPossibleMoves(){
        moves = Piece.deleteImpossibleMoves(activeField, moves);

        if(playing.getBoard()[activeField] % 8 == King)
            moves.addAll(Piece.addCastlingMoves(activeField));

        for (int move : moves) {
            fields[move].isPossibleMove = true;
        }
    }

    private void resetColors(){
        for(int i = 0; i<fields.length; i++){
                fields[i].isPossibleMove = false;
                fields[i].isActive = false;
        }
    }

    private void movePiece(int col, int row){
        int moveField = col + row * BOARD_WIDTH;

        if(activeField == moveField)
            fields[activeField].resetBools();
        else
        {
            Move move = new Move(fields[activeField].getPiece(), activeField, moveField, fields[moveField].getPiece(), 0, false);

            if(Playing.ActivePieces.containsKey(moveField) && HelpMethods.isWhite(Playing.ActivePieces.get(moveField)) != Playing.whitesMove)
                Playing.ActivePieces.remove(moveField);

            if((playing.getBoard()[activeField] % 8 == Rook) || playing.getBoard()[activeField] % 8 == King)
            {
                checkPossibleCastles(activeField);
            }

            if(playing.getBoard()[activeField] % 8 == King && playing.getBoard()[moveField] % 8 == Rook && HelpMethods.isWhite(playing.getBoard()[moveField]) == Playing.whitesMove)
            {
                int dir = (moveField % 8 > move.startField % 8) ? move.startField + 2 : move.startField - 2;

                executeMove(move, activeField, dir, false);
                executeMove(move, moveField, ((moveField % 8 > move.startField % 8) ? dir - 1 : dir + 1), true);

                System.out.println(Playing.ActivePieces);
            }
            else
            {
                move.gaveCheck = executeMove(move, activeField, moveField, false).gaveCheck;
            }
        }
    }

    private Move executeMove(Move move, int activeField, int moveField, boolean castling)
    {
        Playing.ActivePieces.put(moveField, Playing.ActivePieces.get(activeField));
        Playing.ActivePieces.remove(activeField);
        playing.updateBoard(moveField, fields[activeField].getPiece());
        playing.updateBoard(activeField, 0);
        fields[moveField].setPiece(fields[activeField].getPiece());
        fields[activeField].setPiece(0);
        //move.gaveCheck = Piece.isChecked(HelpMethods.findKing(!Playing.whitesMove)) == -1 ? false : true;
        if(!castling)
        {
            Playing.whitesMove = (Playing.whitesMove == true) ? false : true;
            playing.addMove(move);
        }

        return move;
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
                chessPiecesImgs.put((HelpMethods.CharPieceToInt2(Constants.Pieces.CHAR_PIECES[i]) + Constants.Pieces.White), img.getSubimage((img.getWidth()/6)*(i%6), 0,img.getWidth()/6, img.getHeight()/2 ));
            else
                chessPiecesImgs.put((HelpMethods.CharPieceToInt2(Constants.Pieces.CHAR_PIECES[(i%6)]) + Constants.Pieces.Black), img.getSubimage((img.getWidth()/6)*(i%6), (img.getHeight()/2),img.getWidth()/6, img.getHeight()/2 ));
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

    public HashMap<Integer, BufferedImage> getChessPiecesImgs() {
        return chessPiecesImgs;
    }
}
