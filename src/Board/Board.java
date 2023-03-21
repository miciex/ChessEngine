package Board;

import GameStates.Move;

import java.util.ArrayList;
import java.util.HashMap;

import static utils.HelpMethods.FenToIntArray;
import static utils.HelpMethods.boardToMap;

public class Board {

    public HashMap<Integer, Integer> position;
    public boolean whiteToMove;
    public int[] availableCastles;
    public ArrayList<Move> moves;
    public String fen;
    public int[] visualBoard;
    public ArrayList<HashMap<Integer, Integer>> positions;

    public Board(String fenString, boolean whiteToMove, int[] availableCastles, ArrayList<Move> moves){
        this.fen = fenString;
        this.visualBoard = FenToIntArray(this.fen, 64);
        this.position = boardToMap(this.visualBoard);
        this.whiteToMove = whiteToMove;
        this.availableCastles = availableCastles;
        this.moves = moves;
        this.positions = new ArrayList<>();
    }

    public Board(String fenString){
        this.fen = fenString;
        this.visualBoard = FenToIntArray(this.fen, 64);
        this.position = boardToMap(this.visualBoard);
        this.whiteToMove = true;
        this.availableCastles = new int[]{0,0,0,0};
        this.moves = new ArrayList<>();
        this.positions = new ArrayList<>();
    }

    public Board(Board board){
        this.fen = board.fen;
        this.visualBoard = board.visualBoard;
        this.position = (HashMap<Integer, Integer>) board.position.clone();
        this.whiteToMove = board.whiteToMove;
        this.availableCastles = board.availableCastles.clone();
        this.moves = board.moves;
        this.positions = board.positions;
    }

    public void resetBoard(){
        this.visualBoard = FenToIntArray(this.fen, 64);
        this.position = boardToMap(this.visualBoard);
        this.whiteToMove = true;
        this.availableCastles = new int[]{0,0,0,0};
        this.moves = new ArrayList<>();
        this.positions.clear();
    }

    public Move getLastMove(){
        return moves.size() > 0 ? moves.get(moves.size()-1) : new Move();
    }

}
