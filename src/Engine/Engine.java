package Engine;

import GameStates.GameResults;
import GameStates.Move;
import GameStates.Playing;
import ui.BoardOverlay;
import utils.Constants;
import utils.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static utils.HelpMethods.getPieceValue;
import static utils.HelpMethods.getRandom;

public class Engine {

    public boolean isWhite;
    public Move lastMove;
    private Random  rnd = new Random();

    private Playing playing;

    public Engine(boolean isPlayerWhite, Playing playing){
        this.playing = playing;
        this.isWhite = !isPlayerWhite;
    }

    public Move getRandomMove(HashMap<Integer, Integer> pieces, int[] castles, ArrayList<Move> lastMoves){
        ArrayList<Move> moves = Piece.generateMoves(pieces, isWhite, lastMoves.size() != 0 ? lastMoves.get(lastMoves.size() - 1) : new Move(), castles);
        return getRandom(moves);
    }

    public int minimax(HashMap<Integer, Integer> position, int depth, int alpha, int beta, boolean maximizingPlayer,  Move lastMove){
        if(depth == 0 || playing.checkGameResult(playing.getLastMove()) != GameResults.NONE)
            return evaluate(position);

        ArrayList<Move> moves = Piece.generateMoves(position, maximizingPlayer, lastMove, playing.possibleCastles);

        if(maximizingPlayer){
            int maxEval = Integer.MIN_VALUE;

            for(Move move : moves){
                int eval = minimax(Piece.makeMove(move, position), depth - 1, alpha, beta, false , lastMove);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if(beta <= alpha){
                    break;
                }
            }
            return maxEval;
        }else {
            int minEval = Integer.MAX_VALUE;

            for(Move move : moves){
                int eval = minimax(Piece.makeMove(move, position), depth - 1, alpha, beta, true, lastMove);
                minEval = Math.min(minEval, eval);
                alpha = Math.min(alpha, eval);
                if(beta <= alpha){
                    break;
                }
            }
            return minEval;
        }
    }

    public int evaluate(HashMap<Integer, Integer> pieces){
        int eval = 0;
        for(Map.Entry<Integer, Integer> entry : pieces.entrySet()){
                eval += entry.getValue() < 16 ? getPieceValue(entry.getValue()) : -getPieceValue(entry.getValue());
        }
        return eval;
    }

}
