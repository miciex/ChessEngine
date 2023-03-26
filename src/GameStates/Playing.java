package GameStates;

import Board.Board;
import Board.MoveGenerator;
import Engine.Engine;
import main.Game;
import ui.BoardOverlay;
import ui.ButtonMethods;
import ui.ButtonOverlay;
import utils.CheckGameResults;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static utils.Constants.Boards.*;
import static utils.Constants.Boards.TestBoards.*;
import static utils.Constants.Field.FIELD_SIZE;
import static utils.Constants.Game_Info.*;
import static utils.Constants.BoardInfo.*;
import static utils.Constants.Pieces.King;
import static utils.HelpMethods.*;

public class Playing extends State implements StateMethods {

    public Board board;
    public MoveGenerator moveGenerator;
    public String currentBoard = classicBoard;
    public final int BOARD_X = (GAME_WIDTH - BOARD_WIDTH * FIELD_SIZE) / 2;
    public final int BOARD_Y = (GAME_HEIGHT - BOARD_HEIGHT * FIELD_SIZE) / 2;

    public GameResults result;
    public boolean playerWhite;
    public int movesTo50MoveRule = 0;
    public static boolean isEndgame = false;
    BoardOverlay boardOverlay;
    ButtonOverlay buttonOverlay;
    public Engine engine;
    private ArrayList<Integer> piecesMovedDuringOpening = new ArrayList<>();
    public ArrayList<ArrayList<String>> gmGames;
    public ArrayList<Integer> gmGamesIndexes = new ArrayList<>();
    Random rnd = new Random();

    public Playing(Game game) {
        super(game);
        playerWhite = rnd.nextFloat() > 0.5;
        result = GameResults.NONE;
        gmGames = LoadSave.getGrandmasterGames(LoadSave.GM_GAMES);
        setGmGamesIndexes();
        initClasses();
    }

    public void resetGame() {
        playerWhite = rnd.nextFloat() > 0.5;
        engine.isWhite = !playerWhite;
        board.resetBoard();
        boardOverlay.createFields();
        piecesMovedDuringOpening = new ArrayList<>();
        result = GameResults.NONE;
        setGmGamesIndexes();
    }

    private void initClasses() {
        board = new Board(classicBoard);
        moveGenerator = new MoveGenerator(board);
        engine = new Engine(playerWhite, this);
        boardOverlay = new BoardOverlay(BOARD_X, BOARD_Y, this);
        buttonOverlay = new ButtonOverlay((BOARD_X + BOARD_WIDTH * FIELD_SIZE), 0, 0, 0, this);

    }

    public void setGmGamesIndexes() {
        gmGamesIndexes.clear();
        for (int i = 0; i < gmGames.size(); i++) {
            gmGamesIndexes.add(i);
        }
    }

    @Override
    public void update() {
        boardOverlay.update();
    }

    @Override
    public void draw(Graphics g) {
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

    public void mouseDragged(MouseEvent e) {
        boardOverlay.mouseDragged(e);
    }

    public void setMoves(ArrayList<Move> moves) {
        this.board.moves = moves;
    }

    public void updateWholeBoard(int[] board) {
        this.board.visualBoard = board;
    }

    public ArrayList<Integer> getMovedPieces() {
        return piecesMovedDuringOpening;
    }

    public void addMovedPiece(Move move) {
        if (piecesMovedDuringOpening.size() <= 10)
            if (piecesMovedDuringOpening.contains(move.endField)) {
                // piecesMovedDuringOpening.remove(move.startField);
                // piecesMovedDuringOpening.add(0);
            } else {
                piecesMovedDuringOpening.add(move.endField);
                // piecesMovedDuringOpening.remove(move.startField);
                // piecesMovedDuringOpening.add(0);
            }
    }
}
