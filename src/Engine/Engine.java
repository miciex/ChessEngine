package Engine;

import GameStates.GameResults;
import GameStates.Move;
import GameStates.Playing;
import ui.BoardOverlay;
import utils.Constants;
import utils.HelpMethods;
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
    HashMap<Integer, Move> bestMoves;

    private Playing playing;

    public Engine(boolean isPlayerWhite, Playing playing) {
        this.playing = playing;
        this.isWhite = !isPlayerWhite;
        this.bestMoves = new HashMap<>();
    }

    public Move getRandomMove(HashMap<Integer, Integer> pieces, int[] castles, ArrayList<Move> lastMoves) {

        ArrayList<Move> moves = Piece.generateMoves(pieces, isWhite,
                lastMoves.size() != 0 ? lastMoves.get(lastMoves.size() - 1) : new Move(), castles);
        return getRandom(moves);
    }

    public void setBestMoves(HashMap<Integer, Integer> position, int depth, int alpha, int beta,
            boolean maximizingPlayer, Move lastMove) {
        bestMoves.clear();
        int eval = 0;
        for(int i = 1; i<=depth; i++){
            eval = minimax(position, i, alpha, beta, maximizingPlayer, lastMove);
        }

        System.out.println(eval);
    }

    public int minimax(HashMap<Integer, Integer> position, int depth, int alpha, int beta, boolean maximizingPlayer,
            Move lastMove) {
        GameResults result = playing.checkGameResult(playing.getLastMove());
        if (depth == 0)
            return evaluate(position, lastMove);
        if (result != GameResults.NONE) {
            if (result == GameResults.MATE) {
                return maximizingPlayer ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
            return 0;
        }
        ArrayList<Move> moves = new ArrayList<>();
        if(bestMoves.size()>0)
            moves.add(bestMoves.get(bestMoves.size()));
        moves.addAll(Piece.generateMoves(position, maximizingPlayer, lastMove, playing.possibleCastles));

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            for (Move move : moves) {
                int eval = minimax(Piece.makeMove(move, position), depth - 1, alpha, beta, false, move);
                if (!bestMoves.containsKey(depth)||bestMoves.get(depth) == null || eval > maxEval) {
                    bestMoves.put(depth, move);
                }
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;

            for (Move move : moves) {
                int eval = minimax(Piece.makeMove(move, position), depth - 1, alpha, beta, true, move);
                if (!bestMoves.containsKey(depth)||bestMoves.get(depth) == null || eval < minEval) {
                    bestMoves.put(depth, move);
                }
                minEval = Math.min(minEval, eval);
                alpha = Math.min(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    public int evaluate(HashMap<Integer, Integer> pieces, Move lastMove) {
        int eval = 0;

        for (Map.Entry<Integer, Integer> entry : pieces.entrySet()) {
            eval += entry.getValue() < 16 ? getPieceValue(entry.getValue()) : -getPieceValue(entry.getValue());
        }

        int mobility = Piece.generateMoves(pieces, true, lastMove, playing.possibleCastles).size()
                - Piece.generateMoves(pieces, false, lastMove, playing.possibleCastles).size();

        eval += 0.1 * mobility;

        return eval;
    }

    private void newBestMoves(int depth) {
        this.bestMoves.clear();
        for (int i = 1; i <= depth; i++) {
            bestMoves.put(i, null);
        }
    }

    public void OrderMoves(ArrayList<Move> moves){
        for(Move move : moves){
            double moveScoreGuess = 0;

            if(move.takenPiece != 0){
                moveScoreGuess = 0.1 * HelpMethods.getPieceValue(move.takenPiece) - HelpMethods.getPieceValue(move.movedPiece);
            }
        }
    }

    public Move getBestMove() {
        return bestMoves.get(bestMoves.size());
    }

    public void removeLastBestMove(){
        bestMoves.remove(bestMoves.size());
    }
}
