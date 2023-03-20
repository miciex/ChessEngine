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

    public Board(String fenString, boolean whiteToMove, int[] availableCastles, ArrayList<Move> moves){
        this.position = boardToMap(FenToIntArray(fenString, 64));
        this.whiteToMove = whiteToMove;
        this.availableCastles = availableCastles;
        this.moves = moves;
    }

    public Board(String fenString){
        this.position = boardToMap(FenToIntArray(fenString, 64));
        this.whiteToMove = true;
        this.availableCastles = new int[]{0,0,0,0};
        this.moves = new ArrayList<>();
    }

}
