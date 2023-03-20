package Engine;

import GameStates.GameResults;
import GameStates.Move;
import GameStates.Playing;
import ui.BoardOverlay;
import utils.Constants;
import utils.HelpMethods;
import utils.Piece;

import java.sql.Array;
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
    private ZobristHash zobristHash;
    private ArrayList<HashMap<Long, PositionInfo>> positionsTable;
    int transpositions;
    int cutoffs;
    int movesSearched;
    int[] possibleCastles;
    ArrayList<Integer> piecesMovedDuringOpening;
    ArrayList<Integer> originalPiecesMovedDuringOpening;

    public Engine(boolean isPlayerWhite, Playing playing) {
        this.playing = playing;
        this.isWhite = !isPlayerWhite;
        this.checkedMoves = new ArrayList<>();
        this.zobristHash = new ZobristHash();
        this.zobristHash.initTable();
        this.positionsTable = new ArrayList<>();
    }

    public void setBestMoves(HashMap<Integer, Integer> position, int depth, int alpha, int beta,
            boolean maximizingPlayer, Move lastMove, int[] possibleCastles,
            ArrayList<Integer> piecesMovedDuringOpening) {
        this.possibleCastles = possibleCastles.clone();
        this.originalPiecesMovedDuringOpening = (ArrayList<Integer>) piecesMovedDuringOpening.clone();
        this.piecesMovedDuringOpening = this.originalPiecesMovedDuringOpening;
        //newBestMoves(depth, maximizingPlayer);
        resetTranspositions(depth);
        int eval = 0;
        transpositions = 0;
        bestMove = null;
        bestMovesEval = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        eval = minimax(position, depth, alpha, beta, maximizingPlayer, lastMove, depth);
        System.out.println("Transpositions: " + transpositions);
        System.out.println("Cut offs: " + cutoffs);
        System.out.println("Moves searched" + movesSearched);
        System.out.println("Evaluation: " + eval);
        positionsTable.clear();
    }

    public int minimax(HashMap<Integer, Integer> position, int depth, int alpha, int beta,
            boolean maximizingPlayer,
            Move lastMove, int originalDepth) {
        GameResults result = playing.checkGameResult(lastMove, new ArrayList<>() {
            {
                addAll(playing.positions);
                addAll(positions);
            }
        }, position, maximizingPlayer);

        if (result != GameResults.NONE) {
            if (result == GameResults.MATE) {
                return maximizingPlayer ? Integer.MIN_VALUE + 100 - originalDepth - depth
                        : Integer.MAX_VALUE - 100 + originalDepth - depth;
            }
            return 0;
        }

        if (depth == 0) {
            return searchAllCaptures(Integer.MIN_VALUE, Integer.MAX_VALUE, position, maximizingPlayer);// evaluate(position);
        }

        long positionHash = zobristHash.computeHash(position);

        ArrayList<Move> moves = Piece.generateMoves(position, maximizingPlayer, lastMove, playing.possibleCastles,
                false);

        Move currBestMove;
        currBestMove = moves.get(0);

        int[] order = OrderMoves(moves);

        moves = sortMoves(moves, order);

        if (maximizingPlayer) {

            int maxEval = Integer.MIN_VALUE;

            for (int i = 0; i < moves.size(); i++) {
                movesSearched++;

                Move move = moves.get(i);

                HashMap<Integer, Integer> brd = Piece.makeMove(move, position);

                addMovedPiece(move);

                int eval = setEval(brd, depth, alpha, beta, false, move, originalDepth);
                setBestMove(eval, move, depth, originalDepth, true);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if (beta <= alpha) {
                    cutoffs += moves.size() - i - 1;
                    break;

                }
            }

            positionsTable.get(originalDepth - depth).put(positionHash,
                    new PositionInfo(currBestMove, maxEval, alpha, beta, true));

            return maxEval;
        }

        else {
            int minEval = Integer.MAX_VALUE;

            for (int i = 0; i < moves.size(); i++) {

                movesSearched++;

                Move move = moves.get(i);

                HashMap<Integer, Integer> brd = Piece.makeMove(move, position);

                int eval = setEval(brd, depth, alpha, beta, true, move, originalDepth);

                if (eval < bestMovesEval && originalDepth + 1 - depth == 1) {
                    bestMovesEval = eval;
                    bestMove = move;
                    currBestMove = move;
                }

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                if (beta <= alpha) {
                    cutoffs += moves.size() - i - 1;
                    break;
                }

            }

            positionsTable.get(originalDepth - depth).put(positionHash,
                    new PositionInfo(currBestMove, minEval, alpha, beta, false));

            return minEval;
        }
    }

    private int setEval(HashMap<Integer, Integer> brd, int depth, int alpha, int beta, boolean maximizingPlayer,
            Move move, int originalDepth) {
        long hash = zobristHash.computeHash(brd);

        int eval;

        if (positionsTable.get(originalDepth - depth).containsKey(hash)) {
            PositionInfo info = positionsTable.get(originalDepth - depth).get(hash);
            eval = info.eval;
            transpositions++;
        } else {
            possibleCastles = Piece.setCastles(possibleCastles, new ArrayList<>() {
                {
                    addAll(playing.getMoves());
                    addAll(checkedMoves);
                }
            });

            checkedMoves.add(move);
            positions.add((HashMap<Integer, Integer>) brd.clone());
            eval = minimax((HashMap<Integer, Integer>) brd.clone(), depth - 1, alpha, beta, maximizingPlayer, move,
                    originalDepth);
            positions.remove(positions.size() - 1);
            checkedMoves.remove(checkedMoves.size() - 1);

            possibleCastles = Piece.unsetCastles(possibleCastles, new ArrayList<>() {
                {
                    addAll(playing.getMoves());
                    addAll(checkedMoves);
                }
            });
            piecesMovedDuringOpening = originalPiecesMovedDuringOpening;
        }
        return eval;
    }

    private void setBestMove(int eval, Move move, int depth, int originalDepth, boolean maximizingPlayer) {
        if (((maximizingPlayer && eval > bestMovesEval) || (!maximizingPlayer && eval < bestMovesEval))
                && originalDepth == depth) {
            bestMovesEval = eval;
            bestMove = move;
        }
    }

    private int endgameEval(HashMap<Integer, Integer> pieces, int multiplier) {
        boolean isWhite = multiplier == 1 ? true : false;

        int opponentKing = HelpMethods.findKing(!isWhite, pieces);
        int king = HelpMethods.findKing(isWhite ? true : false, pieces);

        int eval = 0;

        int opponentKingRow = (int) Math.ceil((double) (opponentKing + 1) / 8);
        int opponentKingColumn = opponentKing % 8;

        int kingRow = (int) Math.ceil((double) (king + 1) / 8);
        int kingColumn = king % 8;

        int opponentDistanceToCentreColumn = Math.max(3 - opponentKingColumn, opponentKingColumn - 4);
        int opponentDistanceToCentreRow = Math.max(3 - opponentKingColumn, opponentKingColumn - 4);
        int opponentDistanceFromCentre = opponentDistanceToCentreColumn + opponentDistanceToCentreRow;
        eval += opponentDistanceFromCentre * 100 * multiplier;

        int distanceBetweenColumns = Math.abs(kingColumn - opponentKingColumn);
        int distanceBetweenRows = Math.abs(kingRow - opponentKingRow);
        int distanceBetweenKings = distanceBetweenColumns + distanceBetweenRows;
        eval += (14 - distanceBetweenKings) * 10 * multiplier;

        return eval;
    }

    private int pawnStructureBonus(HashMap<Integer, Integer> pieces, Move move, int multiplier) {
        if (move.movedPiece % 8 == Pawn) {

        }

        return 0;
    }

    private int evaluateBonus(HashMap<Integer, Integer> pieces) {
        int eval = 0;

        for (Move move : checkedMoves) {

            int multiplier = move.movedPiece < 16 ? 1 : -1;
            int moved = move.movedPiece % 8;

            if (moved == King && Math.abs(move.endField - move.startField) != 2)
                eval -= (100 * multiplier);
            else if (moved == King && Math.abs(move.endField - move.startField) == 2)
                eval += (100 * multiplier);

            if (playing.getMoves().size() <= 10) {
                if ((moved == King && Math.abs(move.endField - move.startField) != 2) || moved == Rook
                        || moved == Queen)
                    eval -= (100 * multiplier);

                if (playing.getMovedPieces().contains(move.startField))
                    eval -= (20 * multiplier);

                if (playing.getMoves().size() <= 1 && moved == Knight)
                    eval -= (100 * multiplier);
            }

            if (Playing.isEndgame) {
                if (moved == King) {
                    // if(multiplier == 1)
                    // eval += Constants.Heatmaps.kingEndgame[0][move.endField];
                    // else if(multiplier == -1)
                    // eval -= Constants.Heatmaps.kingEndgame[1][move.endField];
                } else {
                    if (multiplier == 1)
                        eval += Constants.Heatmaps.Whites[moved - 1][move.endField];
                    else if (multiplier == -1)
                        eval -= Constants.Heatmaps.Blacks[moved - 1][move.endField];
                }

                eval += endgameEval(pieces, multiplier);
            } else if (moved != King) {
                if (multiplier == 1)
                    eval += Constants.Heatmaps.Whites[moved - 1][move.endField];
                else if (multiplier == -1)
                    eval -= Constants.Heatmaps.Blacks[moved - 1][move.endField];
            }

        }

        return eval;
    }

    public int evaluate(HashMap<Integer, Integer> pieces) {
        int eval = 0;

        for (Map.Entry<Integer, Integer> entry : pieces.entrySet()) {
            eval += entry.getValue() < 16 ? getPieceValue(entry.getValue()) : -getPieceValue(entry.getValue());
        }

        eval += evaluateBonus(pieces);

        return eval;
    }

    private void resetTranspositions(int depth) {
        for (int i = 0; i < depth; i++) {
            positionsTable.add(new HashMap<Long, PositionInfo>());
        }
    }

    public int[] OrderMoves(ArrayList<Move> moves) {

        int[] guessScores = new int[moves.size()];

        if (moves.size() == 0)
            return guessScores;

        for (int i = 0; i < moves.size() - 1; i++) {
            Move move = moves.get(i);

            if (move.takenPiece != 0) {
                guessScores[i] += 100000;
            }

            if (move.movedPiece % 8 == King && Math.abs(move.startField - move.endField) == 2) {
                guessScores[i] += 10000;
            }

            // if(move.gaveCheck){
            // guessScores[i] += 1000;
            // }

            if (playing.getMovedPieces().contains(move.startField)) {
                guessScores[i] += 100;
            }

            if (move.takenPiece != 0) {
                guessScores[i] = 10
                        * (HelpMethods.getPieceValue(move.takenPiece) - HelpMethods.getPieceValue(move.movedPiece));
            }

            if (move.promotePiece > 0) {
                guessScores[i] += HelpMethods.getPieceValue(move.promotePiece);
            }
        }

        return guessScores;
    }

    private ArrayList<Move> sortMoves(ArrayList<Move> moves, int[] moveOrder) {
        for (int i = 0; i < moveOrder.length; i++) {
            for (int j = 0; j < moveOrder.length - 1; j++) {
                if (moveOrder[j] < moveOrder[j + 1]) {
                    int buff = moveOrder[j + 1];
                    Move moveBuff = new Move(moves.get(j + 1));
                    moveOrder[j + 1] = moveOrder[j];
                    moveOrder[j] = buff;
                    moves.set(j + 1, new Move(moves.get(i)));
                    moves.set(j, moveBuff);
                }
            }
        }
        return moves;
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

    private int searchAllCaptures(int alpha, int beta, HashMap<Integer, Integer> position, boolean maximizingPlayer) {
        int evaluation = evaluate(position);

        if (evaluation >= beta) {
            return beta;
        }

        alpha = Math.max(evaluation, alpha);

        ArrayList<Move> moves = Piece.generateMoves(position, maximizingPlayer, lastMove, playing.possibleCastles,
                true);

        if (maximizingPlayer) {

            for (int i = 0; i < moves.size(); i++) {

                movesSearched++;

                Move move = moves.get(i);

                HashMap<Integer, Integer> brd = Piece.makeMove(move, position);

                int eval = searchAllCaptures(alpha, beta, brd, false);

                if (evaluation >= beta) {
                    return beta;
                }

                alpha = Math.max(alpha, eval);
            }

            return alpha;
        } else {

            for (int i = 0; i < moves.size(); i++) {

                movesSearched++;

                Move move = moves.get(i);

                HashMap<Integer, Integer> brd = Piece.makeMove(move, position);

                int eval = searchAllCaptures(alpha, beta, brd, true);

                beta = Math.min(beta, eval);

                if (evaluation >= beta) {
                    return alpha;
                }

                beta = Math.min(beta, eval);
            }
            return beta;
        }
    }

    public Move getBestMove() {
        // return bestMoves.get(1);
        return bestMove;
    }

    public void removeLastBestMove() {
        // bestMoves.remove(bestMoves.size());
    }

    public void clearPosition() {
        positions.clear();
    }

    private void addMovedPiece(Move move) {
        if (this.piecesMovedDuringOpening.size() <= 10)
            if (this.piecesMovedDuringOpening.contains(move.endField)) {
                // piecesMovedDuringOpening.remove(move.startField);
                // piecesMovedDuringOpening.add(0);
            } else {
                this.piecesMovedDuringOpening.add(move.endField);
                // piecesMovedDuringOpening.remove(move.startField);
                // piecesMovedDuringOpening.add(0);
            }
    }
}
