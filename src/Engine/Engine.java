package Engine;

import GameStates.Move;
import utils.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static utils.HelpMethods.getRandom;

public class Engine {

    public boolean isWhite;
    private Random  rnd = new Random();

    public Engine(boolean isPlayerWhite){
        this.isWhite = !isPlayerWhite;
    }

    public Move getRandomMove(HashMap<Integer, Integer> pieces, int[] castles, ArrayList<Move> lastMoves){
        ArrayList<Move> moves = Piece.generateMoves(pieces, isWhite, lastMoves.size() != 0 ? lastMoves.get(lastMoves.size() - 1) : new Move(), castles);
        return getRandom(moves);
    }

}
