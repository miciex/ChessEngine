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
    HashMap<Integer,Move> bestMoves;


    private Playing playing;

    public Engine(boolean isPlayerWhite, Playing playing){
        this.playing = playing;
        this.isWhite = !isPlayerWhite;
        this.bestMoves = new HashMap<>();
    }

    public Move getRandomMove(HashMap<Integer, Integer> pieces, int[] castles, ArrayList<Move> lastMoves){

        ArrayList<Move> moves = Piece.generateMoves(pieces, isWhite, lastMoves.size() != 0 ? lastMoves.get(lastMoves.size() - 1) : new Move(), castles);
        return getRandom(moves);
    }

    public void setBestMoves(HashMap<Integer, Integer> position, int depth, int alpha, int beta, boolean maximizingPlayer,  Move lastMove){
        newBestMoves(depth);
        int eval = minimax(position, depth, alpha, beta, maximizingPlayer, lastMove);
        System.out.println(eval);
    }

    public int minimax(HashMap<Integer, Integer> position, int depth, int alpha, int beta, boolean maximizingPlayer,  Move lastMove){
        GameResults result = playing.checkGameResult(playing.getLastMove());
        if(depth == 0 )
            return evaluate(position);
        if(result != GameResults.NONE){
            if(result == GameResults.MATE){
                return maximizingPlayer ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
        }

        ArrayList<Move> moves = Piece.generateMoves(position, maximizingPlayer, lastMove, playing.possibleCastles);

        if(maximizingPlayer){
            int maxEval = Integer.MIN_VALUE;

            for(Move move : moves){
                int eval = minimax(Piece.makeMove(move, position), depth - 1, alpha, beta, false , lastMove);
                if(bestMoves.get(depth) == null || eval > maxEval){
                    bestMoves.put(depth, move);
                }
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
                if(bestMoves.get(depth) == null || eval < minEval){
                    bestMoves.put(depth, move);
                }
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

    private void newBestMoves(int depth){
        this.bestMoves.clear();
        for(int i = 1; i <= depth; i++){
            bestMoves.put(i, null);
        }
    }

    public Move getBestMove(){
        return bestMoves.get(bestMoves.size());
    }

}
