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
    Move bestMove;
    int bestMovesEval;
    ArrayList<Move> checkedMoves;
    ArrayList<HashMap<Integer, Integer>> positions = new ArrayList<>();
    private Playing playing;

    public Engine(boolean isPlayerWhite, Playing playing) {
        this.playing = playing;
        this.isWhite = !isPlayerWhite;
        //this.bestMoves = new HashMap<>();
        //this.bestMovesEval = new HashMap<>();
        this.checkedMoves = new ArrayList<>();
    }

    public void setBestMoves(HashMap<Integer, Integer> position, int depth, int alpha, int beta,
            boolean maximizingPlayer, Move lastMove) {
        newBestMoves(depth, maximizingPlayer);
        int eval = 0;
        bestMove = null;
        for(int i = 1; i <= depth; i++)
        {
            bestMovesEval = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            eval = minimax(position, i, alpha, beta, maximizingPlayer, lastMove, i);
        }

        System.out.println(eval);
    }

    public int minimax(HashMap<Integer, Integer> position, int depth, double alpha, double beta,
            boolean maximizingPlayer,
            Move lastMove, int originalDepth) {
        GameResults result = playing.checkGameResult(lastMove, new ArrayList<>() {{
            addAll(playing.positions);
            addAll(positions);
        }}, position, maximizingPlayer);

        if (result != GameResults.NONE) {
            if (result == GameResults.MATE) {
                return maximizingPlayer ? Integer.MIN_VALUE + 100 - originalDepth - depth : Integer.MAX_VALUE - 100 + originalDepth - depth;
            }
            return 0;
        }

        if (depth == 0)
            return evaluate(position, lastMove);

        ArrayList<Move> moves = Piece.generateMoves(position, maximizingPlayer, lastMove, playing.possibleCastles);

        if(bestMove != null && originalDepth + 1 - depth == 1)
            moves.add(bestMove);


        /*if(!bestMoves.containsKey(originalDepth + 1 - depth)) {
            bestMoves.put(originalDepth + 1 - depth, null);
        }*/

        /*if(bestMoves.get(originalDepth + 1 - depth) != null)
            moves.add(bestMoves.get(originalDepth + 1 - depth));*/

        int[] order = OrderMoves(moves, depth, originalDepth);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            for (int i = 0; i < moves.size(); i++) {
                int index = findMaxIndex(order);
                Move move = moves.get(index);
                order[index] = Integer.MIN_VALUE;

                if(move == null)
                    System.out.println("sdfhsadfjk");

                checkedMoves.add(move);
                HashMap<Integer, Integer> brd = Piece.makeMove(move, position);
                positions.add((HashMap<Integer, Integer>) brd.clone());
                int eval = minimax(brd, depth - 1, alpha, beta, false, move, originalDepth) + evaluateBonus(position, move);

                positions.remove(positions.size()-1);

                if(eval > bestMovesEval && originalDepth + 1 - depth == 1)
                {
                    bestMovesEval = eval;
                    bestMove = move;
                }

                /*if (bestMoves.get(originalDepth + 1 - depth) == null || eval > bestMovesEval.get(originalDepth + 1 - depth)) {
                    bestMoves.put(originalDepth + 1 - depth, move);
                    bestMovesEval.put(originalDepth + 1 - depth, eval);
                }*/

                checkedMoves.remove(checkedMoves.size()-1);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        }

        else {
            int minEval = Integer.MAX_VALUE;

            for (int i = 0; i < moves.size(); i++) {
                int index = findMaxIndex(order);
                Move move = moves.get(index);
                order[index] = Integer.MIN_VALUE;

                if(move == null)
                    System.out.println("sdfhsadfjk");

                checkedMoves.add(move);
                HashMap<Integer, Integer> brd = Piece.makeMove(move, position);
                positions.add((HashMap<Integer, Integer>) brd.clone());
                int eval = minimax(Piece.makeMove(move, position), depth - 1, alpha, beta, true, move, originalDepth) + evaluateBonus(position, move);

                positions.remove(positions.size()-1);

                if(eval < bestMovesEval && originalDepth + 1 - depth == 1)
                {
                    bestMovesEval = eval;
                    bestMove = move;
                }

                /*if (bestMoves.get(originalDepth + 1 - depth) == null || eval < bestMovesEval.get(originalDepth + 1 - depth)) {
                    bestMoves.put(originalDepth + 1 - depth, move);
                    bestMovesEval.put(originalDepth + 1 - depth, eval);
                }*/
                checkedMoves.remove(checkedMoves.size()-1);

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
        eval += (14 - distanceBetweenKings) * 40 * multiplier;

        return eval;
    }

    private int evaluateBonus(HashMap<Integer, Integer> pieces, Move move) {
        int eval = 0;

        //for(Move move : checkedMoves) {

            int multiplier = move.movedPiece < 16 ? 1 : -1;
            int moved = move.movedPiece % 8;

            if (moved == King && Math.abs(move.endField - move.startField) == 2)
                eval += (100 * multiplier);

            if (playing.getMoves().size() <= 10) {
                if ((moved == King && Math.abs(move.endField - move.startField) != 2) || moved == Rook || moved == Queen)
                    eval -= (100 * multiplier);

                if (playing.piecesMovedDuringOpening.contains(move.movedPiece) && Playing.moves.size() < 10)
                    eval -= (50 * multiplier);

                if(playing.getMoves().size() <= 1 && moved == Knight)
                    eval -= (100 * multiplier);
            }

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
                        eval += Constants.Heatmaps.Whites[moved-1][move.endField];
                    else if(multiplier == -1)
                        eval -= Constants.Heatmaps.Blacks[moved-1][move.endField];
                }

                eval += endgameEval(pieces, multiplier);
            }
            else
            {
                if(multiplier == 1)
                    eval += Constants.Heatmaps.Whites[moved-1][move.endField];
                else if(multiplier == -1)
                    eval -= Constants.Heatmaps.Blacks[moved-1][move.endField];
            }

        //}

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

    private void newBestMoves(int depth, boolean maximizingPlayer) {
        /*this.bestMoves.clear();
        this.bestMovesEval.clear();
        /*for (int i = 1; i <= depth; i++) {
            bestMoves.put(i, null);
        }
        for (int i = 1; i <= depth; i++) {
            bestMovesEval.put(i, ((i % 2 != 0) != maximizingPlayer) ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        }*/
        bestMove = null;
        bestMovesEval = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    }

    public int[] OrderMoves(ArrayList<Move> moves, int depth, int originalDepth) {

        int[] guessScores = new int[moves.size()];

        if(moves.size() == 0)
            return guessScores;

        //if(bestMoves.size() > 0 && bestMoves.get(originalDepth + 1 - depth) != null)
            //guessScores[guessScores.length - 1] = Integer.MAX_VALUE;

        for (int i = 0; i < moves.size()-1; i++) {
            Move move = moves.get(i);

            if(move.takenPiece!=0){
                guessScores[i] += 100000;
            }

            if(move.movedPiece%8 == King && Math.abs(move.startField - move.endField) == 2){
                guessScores[i] += 10000;
            }

//            if(move.gaveCheck){
//                guessScores[i] += 1000;
//            }

            if(Playing.moves.size() < 10 && !playing.piecesMovedDuringOpening.contains(move.movedPiece)){
                guessScores[i] += 100;
            }


            if (move.takenPiece != 0) {
                guessScores[i] = 10 * (HelpMethods.getPieceValue(move.takenPiece) - HelpMethods.getPieceValue(move.movedPiece));
            }

            if (move.promotePiece > 0) {
                guessScores[i] += HelpMethods.getPieceValue(move.promotePiece);
            }
        }

        return guessScores;
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
        //return bestMoves.get(1);
        return bestMove;
    }

    public void removeLastBestMove() {
        //bestMoves.remove(bestMoves.size());
    }
}
