package ui;

import GameStates.GameResults;
import GameStates.Move;
import GameStates.Playing;
import utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.Constants.Boards.TestBoards.*;
import static utils.Constants.BoardInfo.BOARD_WIDTH;
import static utils.Constants.Colors.BLACK;
import static utils.Constants.Colors.WHITE;
import static utils.Constants.Field.FIELD_SIZE;
import static utils.Constants.Pieces.*;
import static utils.HelpMethods.*;
import static utils.Piece.*;

public class BoardOverlay extends UIElement {

    private BoardField[] fields;
    public Playing playing;
    int activeField = -1;
    int newField = -1;
    private int mouseX, mouseY;

    private boolean promoting = false;

    //private int promotionPiece = -1;

    private ArrayList<Integer> moves = new ArrayList<>();
    private HashMap<Integer, BufferedImage> chessPiecesImgs;

    private JButton[] piecePromotionButtons = new JButton[4];

    private int movesTo50MoveRule = 0;
    private HashMap<Integer, Integer> boardMap;
    private boolean whitesMove = true;
    private int castles[] = new int[] { 0,0,0,0 };
    private Move currMove;
    ArrayList<Move> lastMoves;

    public BoardOverlay(int xPos, int yPos, Playing playing) {
        super(xPos, yPos, FIELD_SIZE * 8, FIELD_SIZE * 8);
        this.playing = playing;
        loadPiecesImgs();
        initClasses();
        boardMap = boardToMap(FenToIntArray(playing.currentBoard, 64));
        lastMoves = new ArrayList<>();
        //System.out.println(MoveGenerationTest(4, true));
    }

    private void initClasses(){
        createFields();

    }

    public void createFields() {
        int board[] = playing.getBoard();
        fields = new BoardField[board.length];

        for (int i = 0; i < board.length; i++) {
            int currX = FIELD_SIZE * (i % 8);
            int currY = FIELD_SIZE * (int) (i / BOARD_WIDTH);
            fields[i] = new BoardField(xPos + currX, yPos + currY, i, board[i], this);

            if (i % 2 == (i / 8) % 2) {
                fields[i].color = WHITE;
            } else {
                fields[i].color = BLACK;
            }
        }
    }

    public void update(){
        if(Playing.whitesMove == playing.engine.isWhite){
            playComputerMove();
        }
    }

    void moves(int depth, boolean isWhite){
        int all = 0;
        ArrayList<Move> moves = Piece.generateMoves(boardMap, isWhite,
                lastMoves.size() != 0 ? lastMoves.get(lastMoves.size() - 1) : new Move(), castles);
        for(Move move : moves){
            boardMap = makeMove(move, boardMap);
            lastMoves.add(move);
            castles = Piece.setCastles(castles, lastMoves);
            int b = MoveGenerationTest(depth - 1, !isWhite);
            boardMap = unMakeMove(move, boardMap);
            lastMoves.remove(lastMoves.size() - 1);
            castles = Piece.setCastles(castles, lastMoves);
            String a = moveToChessNotation(move, mapToBoard(boardMap)) + "  " + b;
            System.out.println(a);
            all+=b;
        }
        System.out.println("All: "+all);
    }

    int MoveGenerationTest(int depth, boolean isWhite) {
        if (depth == 0) {
            return 1;
        }

        ArrayList<Move> moves = Piece.generateMoves(boardMap, isWhite,
                lastMoves.size() != 0 ? lastMoves.get(lastMoves.size() - 1) : new Move(), castles);
        int numPosition = 0;
        for (Move move : moves) {
            boardMap = makeMove(move, boardMap);
            lastMoves.add(move);
            castles = Piece.setCastles(castles, lastMoves);
            numPosition += MoveGenerationTest(depth - 1, !isWhite);
            boardMap = unMakeMove(move, boardMap);
            lastMoves.remove(lastMoves.size() - 1);
            castles = Piece.unsetCastles(castles, lastMoves);
        }
        return numPosition;
    }

    public void mouseDragged(MouseEvent e) {
        if(promoting) return;
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void mousePressed(MouseEvent e) {
        int col = (e.getX() - xPos) / FIELD_SIZE;
        int row = (e.getY() - yPos) / FIELD_SIZE;

        if ((row < 0 || col < 0 || row >= 8 || col >= 8) && activeField >= 0){
            resetActivePieces();
            return;
        }

        else if (row < 0 || col < 0 || row >= 8 || col >= 8 || promoting || playing.result != GameResults.NONE)
            return;

        if(promoting){
            currMove.promotePiece = GetPromotionPiece(col + row * 8) + (Playing.whitesMove ? 8 : 16);
            promoting = false;
            playMove(0);
            return;
        }

        if (playing.getBoard()[col + row * BOARD_WIDTH] != 0 && activeField < 0 ) {
            activeField = col + row * BOARD_WIDTH;
            fields[activeField].setMousePressed(true);
            if (HelpMethods.isWhite(Playing.ActivePieces.get(activeField)) == Playing.whitesMove) {
                moves = Piece.PossibleMoves(activeField, Playing.ActivePieces, playing.getLastMove(),
                        Playing.whitesMove, playing.possibleCastles);
                showPossibleMoves();
            }
            return;
        }
        if (activeField >= 0)
            newField = col + row * BOARD_WIDTH;

        if (activeField != newField && newField >= 0 && canMoveHere(newField) && !HelpMethods.isPromotionNeeded()) {
            movePiece(col, row);
            fields[activeField].setMousePressed(false);
            activeField = -1;
            newField = -1;
            resetColors();
            return;
        } else if (newField >= 0) {
            fields[activeField].setMousePressed(false);
            resetColors();
            if (playing.getBoard()[newField] != 0 && activeField != newField) {
                activeField = newField;
                fields[activeField].setMousePressed(true);
                if (HelpMethods.isWhite(Playing.ActivePieces.get(activeField)) == Playing.whitesMove) {
                    moves = Piece.PossibleMoves(activeField, Playing.ActivePieces, playing.getLastMove(),
                            Playing.whitesMove, playing.possibleCastles);
                    showPossibleMoves();
                }
            } else {
                activeField = -1;
            }
            newField = -1;

            return;
        }

        if (newField < 0 && activeField >= 0) {
            if (HelpMethods.isWhite(Playing.ActivePieces.get(activeField)) == Playing.whitesMove) {
                moves = Piece.PossibleMoves(activeField, Playing.ActivePieces, playing.getLastMove(),
                        Playing.whitesMove, playing.possibleCastles);
                showPossibleMoves();
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if(promoting) return;
        int col = (e.getX() - xPos) / FIELD_SIZE;
        int row = (e.getY() - yPos) / FIELD_SIZE;

        if (activeField < 0) {
            return;
        }

        if (row < 0 && col < 0 || row >= 8 && col >= 8)
            fields[activeField].setMousePressed(false);

        if (col + row * BOARD_WIDTH != activeField && canMoveHere(col + row * BOARD_WIDTH) && newField < 0
                && !HelpMethods.isPromotionNeeded() && !promoting) {
            movePiece(col, row);
            resetActivePieces();
            return;
        }

        if (newField >= 0 && playing.result == GameResults.NONE) {
            if (HelpMethods.isWhite(Playing.ActivePieces.get(activeField)) == Playing.whitesMove) {
                moves = Piece.PossibleMoves(activeField, Playing.ActivePieces, playing.getLastMove(),
                        Playing.whitesMove, playing.possibleCastles);
                showPossibleMoves();
            }
        }
        fields[activeField].setMousePressed(false);

    }

    private void resetActivePieces() {
        fields[activeField].setMousePressed(false);
        activeField = -1;
        newField = -1;
        resetColors();
    }

    public void mouseClicked(MouseEvent e) {
        if(promoting) return;
    }

    private void showPossibleMoves() {
        if (!HelpMethods.isPromotionNeeded()) {
            moves = Piece.deleteImpossibleMoves(activeField, moves, Playing.ActivePieces, Playing.whitesMove,
                    playing.getLastMove(), playing.possibleCastles);

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

    private void playMove(int endField){
        if(!promoting) {
            currMove = new Move(Playing.ActivePieces, activeField, endField);
            if ((endField / 8 == 0 || endField / 8 == 7) && currMove.movedPiece % 8 == Pawn) {
                promoting = true;
                GetPromotionPiece(endField);
                return;
            }
        }
        Playing.ActivePieces = makeMove(currMove, Playing.ActivePieces);
        playing.updateWholeBoard(mapToBoard(Playing.ActivePieces));
        updateFieldsValue(playing.getBoard());
        Playing.moves.add(currMove);
        setCastles(castles, Playing.moves);
        Playing.whitesMove  = !Playing.whitesMove;
        currMove = null;
    }

    private void playComputerMove(){
        Move move = playing.engine.getRandomMove(Playing.ActivePieces, castles, Playing.moves);
        Playing.ActivePieces = makeMove(move, Playing.ActivePieces);
        playing.updateWholeBoard(mapToBoard(Playing.ActivePieces));
        updateFieldsValue(playing.getBoard());
        Playing.moves.add(move);
        setCastles(castles, Playing.moves);
        Playing.whitesMove  = !Playing.whitesMove;
    }

    private void movePiece(int col, int row) {
        if(promoting) return;
        if(Playing.whitesMove == playing.playerWhite  && Playing.ActivePieces.containsKey(activeField)){
            playMove(col + row * 8);
            promoting = false;
        }
    }

    private void checkGameResult(Move move) {
        if (move.takenPiece != 0 || (move.movedPiece % 8 == King && Math.abs(move.startField - move.endField) == 2))
            playing.positions.clear();
        playing.positions.add((HashMap<Integer, Integer>) Playing.ActivePieces.clone());

        movesTo50MoveRule = CheckGameResults.draw50MoveRuleCheck(move, movesTo50MoveRule);

        if (CheckGameResults.isThreefold(playing.positions))
            playing.result = GameResults.THREE_FOLD;
        else if (CheckGameResults.draw50MoveRule(movesTo50MoveRule))
            playing.result = GameResults.DRAW_50_MOVE_RULE;
        else if (CheckGameResults.insufficientMaterial(Playing.ActivePieces))
            playing.result = GameResults.STALEMATE;
        else if (CheckGameResults.insufficientMaterial(Playing.ActivePieces))
            playing.result = GameResults.INSUFFICIENT_MATERIAL;
        else if (CheckGameResults.isMate(Playing.ActivePieces, Playing.whitesMove, playing.getLastMove(),
                playing.possibleCastles))
            playing.result = GameResults.MATE;
        if (playing.result != GameResults.NONE)
            System.out.println(gameResultToChessNotation(playing.result, Playing.whitesMove));
    }

    private Move executeMove(Move move, int activeField, int moveField, boolean castling) {
        Playing.ActivePieces.put(moveField, Playing.ActivePieces.get(activeField));
        Playing.ActivePieces.remove(activeField);
        playing.updateBoard(moveField, fields[activeField].getPiece());
        playing.updateBoard(activeField, 0);
        fields[moveField].setPiece(fields[activeField].getPiece());
        fields[activeField].setPiece(0);
        if (!castling) {
            Playing.whitesMove = (Playing.whitesMove == true) ? false : true;
            playing.addMove(move);
        }

        return move;
    }

    private int GetPromotionPiece(int moveField) {
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
                //makePiecePromotion(moveField, promotedPiece[0]);
                frame.setVisible(false);
            });

            frame.add(piecePromotionButtons[i]);
            y += 80;
        }
        promotedPiece[0] += Playing.whitesMove ? White : Black;
        return promotedPiece[0];
    }

    private void makePiecePromotion(int moveField, int promotionPiece) {
        promotionPiece += ((Playing.whitesMove) ? Black : White);

        playing.updateBoard(moveField, promotionPiece);
        Playing.ActivePieces.remove(moveField);
        Playing.ActivePieces.put(moveField, promotionPiece);
        fields[moveField].setPiece(promotionPiece);
    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void draw(Graphics g) {
        for (BoardField f : fields) {
            f.drawSquare(g);
        }
        for (BoardField f : fields) {
            f.drawPiece(g);
        }
        if (activeField >= 0)
            fields[activeField].drawPiece(g);
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