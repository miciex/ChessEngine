package Engine;

import GameStates.GameResults;
import GameStates.Move;
import GameStates.Playing;
import ui.BoardOverlay;
import utils.Constants;
import utils.HelpMethods;
import utils.Piece;

import java.util.*;

import static utils.Constants.Pieces.*;
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
        newBestMoves(depth);
        int eval = 0;
        eval = minimax(position, depth, alpha, beta, maximizingPlayer, lastMove);

        System.out.println(eval);
    }

    public int minimax(HashMap<Integer, Integer> position, int depth, double alpha, double beta,
            boolean maximizingPlayer,
            Move lastMove) {

        GameResults result = playing.checkGameResult(playing.getLastMove());

        if (depth == 0)
            return evaluate(position, lastMove);

        if (result != GameResults.NONE) {
            if (result == GameResults.MATE) {
                return maximizingPlayer ? Integer.MAX_VALUE: Integer.MIN_VALUE;
            }
            return 0;
        }

        ArrayList<Move> moves = Piece.generateMoves(position, maximizingPlayer, lastMove, playing.possibleCastles);

        int[] order = OrderMoves(moves);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            for (int i = 0; i < moves.size(); i++) {
                int index = findMaxIndex(order);
                Move move = moves.get(index);
                order[index] = Integer.MIN_VALUE;
                int eval = minimax(Piece.makeMove(move, position), depth - 1, alpha, beta, false, move)
                        + evaluateBonus(position, move);
                if (bestMoves.get(depth) == null || eval > maxEval) {
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

            for (int i = 0; i < moves.size(); i++) {
                int index = smallestIndex(order);
                Move move = moves.get(index);
                order[index] = Integer.MAX_VALUE;
                int eval = minimax(Piece.makeMove(move, position), depth - 1, alpha, beta, true, move)
                        + evaluateBonus(position, move);
                if (bestMoves.get(depth) == null || eval < minEval) {
                    bestMoves.put(depth, move);
                }
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    private int endgameEval(HashMap<Integer, Integer> pieces, int multiplier)
    {
        boolean isWhite = multiplier == 1 ? true : false;

        int opponentKing = HelpMethods.findKing(!isWhite, pieces);
        int king = HelpMethods.findKing(isWhite ? true : false, pieces);

        int eval = 0;

        int opponentKingRow = (int)Math.ceil((double)(opponentKing + 1) / 8);
        int opponentKingColumn = opponentKing % 8;

        int kingRow = (int)Math.ceil((double)(king + 1) / 8);
        int kingColumn = king % 8;

        int opponentDistanceToCentreColumn = Math.max(3 - opponentKingColumn, opponentKingColumn - 4);
        int opponentDistanceToCentreRow = Math.max(3 - opponentKingColumn, opponentKingColumn - 4);
        int opponentDistanceFromCentre = opponentDistanceToCentreColumn + opponentDistanceToCentreRow;
        eval += opponentDistanceFromCentre * 100 * multiplier;

        int distanceBetweenColumns = Math.abs(kingColumn - opponentKingColumn);
        int distanceBetweenRows = Math.abs(kingRow - opponentKingRow);
        int distanceBetweenKings = distanceBetweenColumns + distanceBetweenRows;
        eval += -distanceBetweenKings * 40 * multiplier;

        return eval;
    }

    private int evaluateBonus(HashMap<Integer, Integer> pieces, Move move) {
        int eval = 0;

        int multiplier = move.movedPiece < 16 ? 1 : -1;
        int moved = move.movedPiece % 8;

        if(moved == King && Math.abs(move.endField - move.startField) == 2)
            eval += (100 * multiplier);

        if (playing.getMoves().size() <= 10) {
            if ((moved == King && Math.abs(move.endField - move.startField) != 2) || moved == Rook || moved == Queen)
                eval -= (100 * multiplier);

            if (playing.piecesMovedDuringOpening.contains(move.movedPiece))
                eval -= (100 * multiplier);

            if(playing.getMoves().size() <= 1 && moved == Knight)
                eval -= (100 * multiplier);
        }

        /*int mobility = Piece.generateMoves(pieces, true, move, playing.possibleCastles).size()
                - Piece.generateMoves(pieces, false, move, playing.possibleCastles).size();

        eval += (10 * mobility);*/

        if(Playing.isEndgame)
        {
            if(moved == King)
            {
                if(multiplier == 1)
                    eval += Constants.Heatmaps.kingEndgame[0][move.endField];
                else if(multiplier == -1)
                    eval -= Constants.Heatmaps.kingEndgame[1][move.endField];
            }
            else
            {
                if(multiplier == 1)
                    eval += Constants.Heatmaps.Whites[moved][move.endField];
                else if(multiplier == -1)
                    eval -= Constants.Heatmaps.Blacks[moved][move.endField];
            }

            eval += endgameEval(pieces, multiplier);
        }
        else
        {
            if(multiplier == 1)
                eval += Constants.Heatmaps.Whites[moved][move.endField];
            else if(multiplier == -1)
                eval -= Constants.Heatmaps.Blacks[moved][move.endField];
        }

        return eval;
    }

    public int evaluate(HashMap<Integer, Integer> pieces, Move move) {
        int eval = 0;

        for (Map.Entry<Integer, Integer> entry : pieces.entrySet()) {
            eval += entry.getValue() < 16 ? getPieceValue(entry.getValue()) : -getPieceValue(entry.getValue());
        }

        eval += evaluateBonus(pieces, move);

        return eval;
    }

    private void newBestMoves(int depth) {
        this.bestMoves.clear();
        for (int i = 1; i <= depth; i++) {
            bestMoves.put(i, null);
        }
    }

    public int[] OrderMoves(ArrayList<Move> moves) {
        int[] guessScores = new int[moves.size()];
        int m = 0;
        if (moves.size() > 0)
            m = moves.get(0).movedPiece < 16 ? 1 : -1;
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            int guessScoreMove = 0;
            if (move.takenPiece != 0) {
                guessScores[i] = 10 * HelpMethods.getPieceValue(move.takenPiece)
                        - HelpMethods.getPieceValue(move.movedPiece);
            }

            if (move.promotePiece != 0) {
                guessScores[i] += HelpMethods.getPieceValue(move.promotePiece);
            }
            guessScores[i] *= m;
        }
        return guessScores;
    }

    private int smallestIndex(int[] numbers) {
        int smallest = Integer.MAX_VALUE;
        int index = 0;

        for (int i = 0; i < numbers.length; i++) {
            if (smallest > numbers[i]) {
                smallest = numbers[i];
                index = i;
            }
        }
        return index;
    }

    private int findMaxIndex(int[] numbers) {
        int smallest = Integer.MIN_VALUE;
        int index = 0;

        for (int i = 0; i < numbers.length; i++) {
            if (smallest < numbers[i]) {
                smallest = numbers[i];
                index = i;
            }
        }
        return index;
    }

    public Move getBestMove() {
        return bestMoves.get(bestMoves.size());
    }

    public void removeLastBestMove() {
        bestMoves.remove(bestMoves.size());
    }
}
