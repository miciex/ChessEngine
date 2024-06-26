package ui;

import GameStates.GameResults;
import GameStates.Move;
import GameStates.Playing;
import main.Game;
import utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.currentTimeMillis;
import static utils.Constants.Boards.TestBoards.*;
import static utils.Constants.BoardInfo.BOARD_WIDTH;
import static utils.Constants.Colors.BLACK;
import static utils.Constants.Colors.WHITE;
import static utils.Constants.Field.FIELD_SIZE;
import static utils.Constants.Pieces.*;
import static utils.HelpMethods.*;

public class BoardOverlay extends UIElement {

    private Game game;
    private BoardField[] fields;
    public Playing playing;
    int activeField = -1;
    int newField = -1;
    private int mouseX, mouseY;

    private boolean promoting = false;

    private ArrayList<Integer> moves = new ArrayList<>();
    private HashMap<Integer, BufferedImage> chessPiecesImgs;

    private JButton[] piecePromotionButtons = new JButton[4];

    private Move currMove;
    private int testingStartField = -1;
    ArrayList<Move> lastMoves;
    GameFinishedOverlay gameFinishedOverlay;

    private boolean moveFinished = true;

    public BoardOverlay(Game game, int xPos, int yPos, Playing playing) {
        super(xPos, yPos, FIELD_SIZE * 8, FIELD_SIZE * 8);
        this.game = game;
        this.playing = playing;
        loadPiecesImgs();
        initClasses();
        lastMoves = new ArrayList<>();
    }

    private void initClasses(){
        createFields();
        gameFinishedOverlay = new GameFinishedOverlay(game, playing,xPos, yPos, bounds.width, bounds.height, "White won");
    }

    public void createFields() {
        fields = new BoardField[playing.board.visualBoard.length];

        for (int i = 0; i < playing.board.visualBoard.length; i++) {
            int currX = FIELD_SIZE * (i % 8);
            int currY = FIELD_SIZE * (int) (i / BOARD_WIDTH);
            fields[i] = new BoardField(xPos + currX, yPos + currY, i, playing.board.visualBoard[i], this);

            if (i % 2 == (i / 8) % 2) {
                fields[i].color = WHITE;
            } else {
                fields[i].color = BLACK;
            }
        }
    }

    public void update(){
        if(playing.board.whiteToMove == playing.engine.isWhite && playing.result == GameResults.NONE && moveFinished){
           playComputerMove();
        }
        if(playing.result != GameResults.NONE){
            gameFinishedOverlay.addComponentsToWindow();
        }
    }

    void moves(int depth, boolean isWhite){
        int all = 0;
        playing.board.whiteToMove = isWhite;
        ArrayList<Move> moves = playing.moveGenerator.generateMoves(false);
        for(Move move : moves){
            playing.board.makeMove(move);
            playing.board.moves.add(move);
            playing.board.setCastles();
            int b = MoveGenerationTest(depth - 1, !isWhite);
            playing.board.whiteToMove = isWhite;
            playing.board.unMakeMove(move);
            playing.board.moves.remove(playing.board.moves.size()-1);
            playing.board.unsetCastles();
            String a = moveToChessNotation(playing.board, move) + "  " + b;
            System.out.println(a);
            all+=b;
        }
        System.out.println("All: "+all);
    }

    int MoveGenerationTest(int depth, boolean isWhite) {
        if (depth == 0) {
            return 1;
        }
        playing.board.whiteToMove = isWhite;
        ArrayList<Move> moves = playing.moveGenerator.generateMoves(false);
        int numPosition = 0;
        for (Move move : moves) {
            playing.board.makeMove(move);
            playing.board.moves.add(move);
            playing.board.whiteToMove = isWhite;
            playing.board.setCastles();
            numPosition += MoveGenerationTest(depth - 1, !isWhite);
            playing.board.whiteToMove = isWhite;
            playing.board.unMakeMove(move);
            playing.board.moves.remove(playing.board.moves.size()-1);
            playing.board.unsetCastles();
        }
        return numPosition;
    }

    public void mouseDragged(MouseEvent e) {
        if(promoting) return;
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void mousePressed(MouseEvent e) {
        pieceMovementOnMousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
        pieceMovementOnMouseReleased(e);
    }

    public void pieceMovementOnMousePressed(MouseEvent e){
        int col = (e.getX() - xPos) / FIELD_SIZE;
        int row = (e.getY() - yPos) / FIELD_SIZE;

        if ((row < 0 || col < 0 || row >= 8 || col >= 8) && activeField >= 0 ){
            resetActivePieces();
            return;
        }

        else if (row < 0 || col < 0 || row >= 8 || col >= 8 || promoting || playing.result != GameResults.NONE || (playing.board.visualBoard[col + row * BOARD_WIDTH] == 0 && activeField < 0))
            return;

        if (activeField < 0 ) {
            activeField = col + row * BOARD_WIDTH;
            if(playing.board.isWhite(activeField) == playing.board.whiteToMove && playing.playerWhite == playing.board.whiteToMove) {
                fields[activeField].setMousePressed(true);
                moves = playing.board.PossibleMoves(activeField);
                showPossibleMoves();
            }
            return;
        }
        if (activeField >= 0)
            newField = col + row * BOARD_WIDTH;

        if (activeField != newField && newField >= 0 && canMoveHere(newField) && !HelpMethods.isPromotionNeeded(playing.board)) {
            movePiece(col, row);
            fields[activeField].setMousePressed(false);
            activeField = -1;
            newField = -1;
            resetColors();
            return;
        } else if (newField >= 0) {
            fields[activeField].setMousePressed(false);
            resetColors();
            if (playing.board.visualBoard[newField] != 0 && activeField != newField) {
                activeField = newField;
                fields[activeField].setMousePressed(true);
                if (playing.board.isWhite(activeField) == playing.board.whiteToMove && playing.playerWhite == playing.board.whiteToMove) {
                    moves = playing.board.PossibleMoves(activeField);
                    showPossibleMoves();
                }
            } else {
                activeField = -1;
            }
            newField = -1;

            return;
        }

        if (newField < 0 && activeField >= 0) {
            if (playing.board.isWhite(activeField) == playing.board.whiteToMove == playing.playerWhite) {
                moves = playing.board.PossibleMoves(activeField);
                showPossibleMoves();
            }
        }
    }

    private void pieceMovementOnMouseReleased(MouseEvent e){
        if(promoting) return;
        int col = (e.getX() - xPos) / FIELD_SIZE;
        int row = (e.getY() - yPos) / FIELD_SIZE;

        if (activeField < 0) {
            return;
        }

        if (row < 0 && col < 0 || row >= 8 && col >= 8)
            fields[activeField].setMousePressed(false);

        if (col + row * BOARD_WIDTH != activeField && canMoveHere(col + row * BOARD_WIDTH) && newField < 0
                && !HelpMethods.isPromotionNeeded(playing.board) && !promoting ) {
            movePiece(col, row);
            resetActivePieces();
            return;
        }

        if (newField >= 0 && playing.result == GameResults.NONE) {
            if (playing.board.isWhite(activeField) == playing.board.whiteToMove) {
                moves = playing.board.PossibleMoves(activeField);
                showPossibleMoves();
            }
        }
        fields[activeField].setMousePressed(false);
    }

    public void mouseClicked(MouseEvent e) {
        if(promoting) return;
    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private void resetActivePieces() {
        fields[activeField].setMousePressed(false);
        activeField = -1;
        newField = -1;
        resetColors();
    }



    private void showPossibleMoves() {
        if (!HelpMethods.isPromotionNeeded(playing.board)) {
            moves = playing.board.deleteImpossibleMoves(moves, activeField);

            for (int move : moves) {
                fields[move].isPossibleMove = true;
            }
        }
    }

    private void resetColors() {
        for (int i = 0; i < fields.length; i++) {
            fields[i].isPossibleMove = false;
            fields[i].isActive = false;
        }
    }

    private void updateFieldsValue(int[] board){
        for(int i = 0; i<board.length; i++){
            fields[i].setPiece(board[i]);
        }
    }

    private void playMove(int endField, int promotingPiece){
        moveFinished = false;
        if(!promoting && promotingPiece == -1)
        {
            currMove = new Move(playing.board.position, (activeField == -1) ? testingStartField : activeField, endField);
            if ((endField / 8 == 0 || endField / 8 == 7) && currMove.movedPiece % 8 == Pawn) {
                promoting = true;
                if(promotingPiece == -1) GetPromotionPiece(endField);
                return;
            }
        }
        if(promotingPiece != -1)
            currMove.promotePiece = promotingPiece;
        playMoveOnBoard(currMove);

    }

    private void playComputerMove(){
        moveFinished = false;
        if(playing.result != GameResults.NONE) return;
        long milis = -currentTimeMillis();
        playing.engine.setBestMoves( 4, Integer.MIN_VALUE, Integer.MAX_VALUE,playing.getMovedPieces());
        milis += currentTimeMillis();
        System.out.println("Miliseconds: " + milis);
        Move move = playing.engine.getBestMove();

        playMoveOnBoard(move);
        moveFinished = true;
    }

    private void playMoveOnBoard(Move move){
        playing.board.makeMove(move);
        playing.updateWholeBoard(mapToBoard(playing.board.position));
        updateFieldsValue(playing.board.visualBoard);
        playing.addMovedPiece(move);
        playing.movesTo50MoveRule = CheckGameResults.draw50MoveRuleCheck(move, playing.movesTo50MoveRule);
        playing.board.whiteToMove = !playing.board.whiteToMove;
        playing.board.moves.get(playing.board.moves.size()-1).gaveCheck = playing.board.isChecked() != -1;
        removeIndexes(moveToChessNotation(playing.board,playing.board.moves.get(playing.board.moves.size()-1)));
        playing.result = playing.board.checkGameResult();
        playing.engine.removeLastBestMove();
        Playing.isEndgame = playing.board.isEndgame();
    }

    private void movePiece(int col, int row) {
        if(promoting) return;
        if(playing.board.whiteToMove == playing.playerWhite  && playing.board.position.containsKey(activeField) ){
            if(playing.board.position.get(activeField) < 16 == playing.playerWhite) {
                playMove(col + row * 8, -1);
                promoting = false;
            }
        }
    }


    private int GetPromotionPiece(int moveField) {
        testingStartField = moveField;
        final int[] promotedPiece = { -1 };

        JFrame frame = new JFrame("Piece Promotion");
        frame.setSize(80, 320);
        frame.setSize(512, 550);
        frame.setDefaultCloseOperation(0);
        frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.requestFocus();

        frame.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                frame.requestFocus();
            }
        });

        int y = 0;

        for (int i = 0; i < piecePromotionButtons.length; i++) {
            piecePromotionButtons[i] = new JButton(new ImageIcon(getChessPiecesImgs()
                    .get(Constants.PromotionPiecesInts[i] + 8).getScaledInstance(80, 80, Image.SCALE_DEFAULT)));
            piecePromotionButtons[i].setBounds(0, y, 80, 80);
            piecePromotionButtons[i].setBorder(BorderFactory.createEmptyBorder());
            piecePromotionButtons[i].setContentAreaFilled(false);
            piecePromotionButtons[i].setVisible(true);

            int finalI = i;

            piecePromotionButtons[i].addActionListener(e -> {
                promotedPiece[0] = Constants.PromotionPiecesInts[finalI];
                playMove(moveField, Constants.PromotionPiecesInts[finalI]);
                frame.setVisible(false);
            });

            frame.add(piecePromotionButtons[i]);
            y += 80;
        }

        return promotedPiece[0];
    }

    private void removeIndexes(String move){
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i<playing.gmGamesIndexes.size(); i++){
            int index = playing.gmGamesIndexes.get(i);
            String gmMove = playing.gmGames.get(index).get(playing.board.moves.size() - 1);
            if(gmMove.equals(move) && playing.gmGames.get(index).size() > playing.board.moves.size()){
                indexes.add(index);
            }
        }
        playing.gmGamesIndexes = indexes;
    }

    private void removeIndexes(String[] moves) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < moves.length; i++) {
            indexes.clear();
            for (int j = 0; j < playing.gmGamesIndexes.size(); j++) {
                int index = playing.gmGamesIndexes.get(j);
                String gmMove = playing.gmGames.get(index).get(i);
                if(gmMove.equals(moves[i])){
                    indexes.add(index);
                }
            }
            playing.gmGamesIndexes = (ArrayList<Integer>) indexes.clone();
        }

    }



    public void draw(Graphics g) {
        drawBoard(g);
        if(playing.result != GameResults.NONE){
            gameFinishedOverlay.draw(g);
        }
    }

    private void drawBoard(Graphics g){
        for (BoardField f : fields) {
            f.drawSquare(g);
        }
        for (BoardField f : fields) {
            f.drawPiece(g);
        }
        if (activeField >= 0)
            fields[activeField].drawPiece(g);
        moveFinished = true;
    }

    private void loadPiecesImgs() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PIECES_ATLAS);
        chessPiecesImgs = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            if (i < 6)
                chessPiecesImgs.put(
                        (HelpMethods.CharPieceToInt2(Constants.Pieces.CHAR_PIECES[i]) + Constants.Pieces.White),
                        img.getSubimage((img.getWidth() / 6) * (i % 6), 0, img.getWidth() / 6, img.getHeight() / 2));
            else
                chessPiecesImgs.put(
                        (HelpMethods.CharPieceToInt2(Constants.Pieces.CHAR_PIECES[(i % 6)]) + Constants.Pieces.Black),
                        img.getSubimage((img.getWidth() / 6) * (i % 6), (img.getHeight() / 2), img.getWidth() / 6,
                                img.getHeight() / 2));
        }
    }

    private boolean canMoveHere(int fieldNumber) {
        for (int move : moves) {
            if (move == fieldNumber)
                return true;
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