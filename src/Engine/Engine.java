package Engine;

import Board.Board;
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
    private Playing playing;
    private ZobristHash zobristHash;
    private ArrayList<HashMap<Long, PositionInfo>> positionsTable;
    int transpositions;
    int cutoffs;
    int movesSearched;
    ArrayList<Integer> piecesMovedDuringOpening;
    ArrayList<Integer> originalPiecesMovedDuringOpening;
    Board board;
    ArrayList<Move> checkedMoves;

    public Engine(boolean isPlayerWhite, Playing playing) {
        this.playing = playing;
        this.isWhite = !isPlayerWhite;
        this.checkedMoves = new ArrayList<>();
        initClasses();
    }

    private void initClasses(){
        this.zobristHash = new ZobristHash();
        this.zobristHash.initTable();
        this.positionsTable = new ArrayList<>();
        this.board = new Board(playing.board);
    }

    public void setBestMoves( int depth, int alpha, int beta, ArrayList<Integer> piecesMovedDuringOpening) {
        this.originalPiecesMovedDuringOpening = (ArrayList<Integer>) piecesMovedDuringOpening.clone();
        this.piecesMovedDuringOpening = this.originalPiecesMovedDuringOpening;
        board = new Board(playing.board);
        checkedMoves.clear();
        resetTranspositions(depth);
        int eval;
        transpositions = 0;
        bestMove = null;
        bestMovesEval = board.whiteToMove ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        eval = minimax(depth, alpha, beta, board.whiteToMove, depth);
        System.out.println("Transpositions: " + transpositions);
        System.out.println("Cut offs: " + cutoffs);
        System.out.println("Moves searched" + movesSearched);
        System.out.println("Evaluation: " + eval);
        positionsTable.clear();
    }

    public int minimax(int depth, int alpha, int beta, boolean maximizingPlayer ,int originalDepth) {
        //board.whiteToMove = maximizingPlayer;
        GameResults result = playing.checkGameResult(board);

        if (result != GameResults.NONE) {
            if (result == GameResults.MATE) {
                return maximizingPlayer ? Integer.MIN_VALUE + 100 - originalDepth - depth
                        : Integer.MAX_VALUE - 100 + originalDepth - depth;
            }
            return 0;
        }
        if (depth == 0) {
            return evaluate();
        }
        long positionHash = zobristHash.computeHash(board.position);

        board.whiteToMove = maximizingPlayer;

        ArrayList<Move> moves = Piece.generateMoves(board, false);

        int[] order = OrderMoves(moves);

        sortMoves(moves, order);

        if (maximizingPlayer) {

            int maxEval = Integer.MIN_VALUE;

            for (int i = 0; i < moves.size(); i++) {
                board.whiteToMove = maximizingPlayer;
                movesSearched++;

                Move move = moves.get(i);

                board.moves.add(move);
                board.position = Piece.makeMove(board, move);
                board.availableCastles = Piece.setCastles(board);

                addMovedPiece(move);

                int eval = setEval(depth, alpha, beta, move, false, originalDepth);
                setBestMove(eval, move, depth, originalDepth, true);

                board.position = Piece.unMakeMove(board, move);
                board.availableCastles = Piece.unsetCastles(board);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if (beta <= alpha) {
                    cutoffs += moves.size() - i - 1;
                    break;

                }
            }
            positionsTable.get(originalDepth - depth).put(positionHash,
                    new PositionInfo(null, maxEval, alpha, beta, true));

            return maxEval;
        }

        else {
            int minEval = Integer.MAX_VALUE;

            for (int i = 0; i < moves.size(); i++) {
                board.whiteToMove = maximizingPlayer;
                movesSearched++;
                Move move = moves.get(i);

                board.position = Piece.makeMove(board, move);
                board.availableCastles = Piece.setCastles(board);

                int eval = setEval(depth, alpha, beta,move, true,originalDepth);
                setBestMove(eval, move, depth, originalDepth, false);

                board.moves.remove(board.moves.size()-1);
                board.position = Piece.unMakeMove(board, move);
                board.availableCastles = Piece.unsetCastles(board);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);


                if (beta <= alpha) {
                    cutoffs += moves.size() - i - 1;
                    break;
                }

            }

            positionsTable.get(originalDepth - depth).put(positionHash,
                    new PositionInfo(null, minEval, alpha, beta, false));

            return minEval;
        }
    }

    private int setEval(int depth, int alpha, int beta, Move move, boolean maximizingPlayer,int originalDepth) {

        long hash = zobristHash.computeHash(board.position);

        int eval;

        if (positionsTable.get(originalDepth - depth).containsKey(hash)) {
            PositionInfo info = positionsTable.get(originalDepth - depth).get(hash);
            eval = info.eval;
            transpositions++;
        } else {

            board.moves.add(move);

            board.availableCastles = Piece.setCastles(board);

            checkedMoves.add(move);
            board.positions.add((HashMap<Integer, Integer>) board.position.clone());

            eval = minimax( depth - 1, alpha, beta, maximizingPlayer,originalDepth);

            board.moves.remove(board.moves.size()-1);
            board.positions.remove(board.positions.size() - 1);
            checkedMoves.remove(checkedMoves.size() - 1);

            board.availableCastles = Piece.unsetCastles(board);
            piecesMovedDuringOpening = originalPiecesMovedDuringOpening;

        }
        return eval;
    }

    private void setBestMove(int eval, Move move, int depth, int originalDepth, boolean maximizingPlayer) {
        if(depth != originalDepth) return;
        if(maximizingPlayer && eval > bestMovesEval){
            bestMovesEval = eval;
            bestMove = move;
        }else if(!maximizingPlayer && eval < bestMovesEval){
            bestMovesEval = eval;
            bestMove = move;
        }
    }

    private int endgameEval(int multiplier) {
        boolean isWhite = multiplier == 1;

        int opponentKing = HelpMethods.findKing(board.position, !isWhite);
        int king = HelpMethods.findKing(board.position, isWhite);

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

    private int evaluateBonus() {
        int eval = 0;

        for (Move move : checkedMoves) {

            int multiplier = move.movedPiece < 16 ? 1 : -1;
            int moved = move.movedPiece % 8;

            if (moved == King && Math.abs(move.endField - move.startField) != 2)
                eval -= (100 * multiplier);
            else if (moved == King && Math.abs(move.endField - move.startField) == 2)
                eval += (100 * multiplier);

            if (board.moves.size() <= 10) {
                if ((moved == King && Math.abs(move.endField - move.startField) != 2) || moved == Rook
                        || moved == Queen)
                    eval -= (100 * multiplier);

                if (playing.getMovedPieces().contains(move.startField))
                    eval -= (20 * multiplier);

                if (board.moves.size() <= 1 && moved == Knight)
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

                eval += endgameEval(multiplier);
            } else if (moved != King) {
                if (multiplier == 1)
                    eval += Constants.Heatmaps.Whites[moved - 1][move.endField];
                else if (multiplier == -1)
                    eval -= Constants.Heatmaps.Blacks[moved - 1][move.endField];
            }

        }

        return eval;
    }

    public int evaluate() {
        int eval = 0;

        for (Map.Entry<Integer, Integer> entry : board.position.entrySet()) {
            eval += entry.getValue() < 16 ? getPieceValue(entry.getValue()) : -getPieceValue(entry.getValue());
        }

        eval += evaluateBonus();

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
                guessScores[i] += 1000;
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
        int evaluation = evaluate();

        if (evaluation >= beta) {
            return beta;
        }

        alpha = Math.max(evaluation, alpha);

        ArrayList<Move> moves = Piece.generateMoves(board, true);

        if (maximizingPlayer) {

            for (int i = 0; i < moves.size(); i++) {

                movesSearched++;

                Move move = moves.get(i);

                HashMap<Integer, Integer> brd = Piece.makeMove(board, move);

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

                HashMap<Integer, Integer> brd = Piece.makeMove(board, move);

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
