package Engine;

import Board.Board;
import Board.MoveGenerator;
import GameStates.GameResults;
import GameStates.Move;
import GameStates.Playing;
import ui.BoardOverlay;
import utils.Constants;
import utils.HelpMethods;

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
    public MoveGenerator moveGenerator;
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
        this.moveGenerator = new MoveGenerator(board);
    }

    public void setBestMoves( int depth, int alpha, int beta, ArrayList<Integer> piecesMovedDuringOpening) {
        this.originalPiecesMovedDuringOpening = (ArrayList<Integer>) piecesMovedDuringOpening.clone();
        this.piecesMovedDuringOpening = this.originalPiecesMovedDuringOpening;
        board = new Board(playing.board);
        moveGenerator.setBoard(board);
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
        GameResults gameResult = board.checkGameResult();

        if (gameResult != GameResults.NONE) {
            if(gameResult==GameResults.MATE){
                return maximizingPlayer ? Integer.MIN_VALUE + 100 - originalDepth + depth : Integer.MAX_VALUE - 100 + originalDepth - depth;
            }
            return 0;
        }

        if (depth == 0) {
            return Evaluate.evaluate(board, checkedMoves);//searchAllCaptures(alpha, beta, maximizingPlayer);
        }



        long positionHash = zobristHash.computeHash(board.position);

        board.whiteToMove = maximizingPlayer;

        //Is game finished
        ArrayList<Move> moves = moveGenerator.generateMoves(false);

        sortMoves(moves);

        if (maximizingPlayer) {

            int maxEval = Integer.MIN_VALUE;

            for (int i = 0; i < moves.size(); i++) {
                board.whiteToMove = maximizingPlayer;
                movesSearched++;

                Move move = moves.get(i);

                addMovedPiece(move);

                int eval = setEval(depth, alpha, beta, move, false, originalDepth);

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
                board.whiteToMove = false;
                movesSearched++;
                Move move = moves.get(i);

                addMovedPiece(move);

                int eval = setEval(depth, alpha, beta,move, true,originalDepth);


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

//    private int searchAllCaptures(int alpha, int beta, boolean maximizingPlayer) {
//        board.whiteToMove = maximizingPlayer;
//        int evaluation = Evaluate.evaluate(board, checkedMoves);
//
//        if(maximizingPlayer)
//
//        ArrayList<Move> moves = moveGenerator.generateMoves(true);
//        if(moves.size() == 0){
//            return evaluation;
//        }
//
//        if (maximizingPlayer) {
//
//            for (int i = 0; i < moves.size(); i++) {
//
//                movesSearched++;
//
//                Move move = moves.get(i);
//                checkedMoves.add(move);
//                board.makeMove(move);
//
//                int eval = searchAllCaptures(alpha, beta, false);
//                checkedMoves.remove(checkedMoves.size()-1);
//                board.unMakeMove(move);
//
//                alpha = Math.max(alpha, eval);
//
//                if (beta <= alpha) {
//                    return beta;
//                }
//
//
//            }
//
//            return alpha;
//        } else {
//            for (int i = 0; i < moves.size(); i++) {
//
//                movesSearched++;
//
//                Move move = moves.get(i);
//
//                board.makeMove(move);
//                checkedMoves.add(move);
//                int eval = searchAllCaptures(alpha, beta, true);
//                checkedMoves.remove(checkedMoves.size()-1);
//                board.unMakeMove(move);
//
//                beta = Math.min(beta, eval);
//
//                if (beta <= alpha) {
//                    return alpha;
//                }
//            }
//            return beta;
//        }
//    }


    private int setEval(int depth, int alpha, int beta, Move move, boolean maximizingPlayer,int originalDepth) {
        board.makeMove(move);
        long hash = zobristHash.computeHash(board.position);

        int eval;

        if (positionsTable.get(originalDepth - depth).containsKey(hash)) {
            PositionInfo info = positionsTable.get(originalDepth - depth).get(hash);
            eval = info.eval;
            transpositions++;
        } else {

            checkedMoves.add(move);
            eval = minimax( depth - 1, alpha, beta, maximizingPlayer,originalDepth);

            checkedMoves.remove(checkedMoves.size() - 1);

            piecesMovedDuringOpening = originalPiecesMovedDuringOpening;

        }
        setBestMove(eval, alpha, beta, move, depth, originalDepth, false);
        board.unMakeMove(move);
        return eval;
    }

    private void setBestMove(int eval, int alpha, int beta, Move move, int depth, int originalDepth, boolean maximizingPlayer) {
        if(depth != originalDepth) return;
        if(bestMove == null){
            //bestMovesEval = eval;
            bestMove = move;
        }else if(maximizingPlayer && eval > alpha){
            //bestMovesEval = eval;
            bestMove = move;
        }else if(!maximizingPlayer && eval < beta){
            //bestMovesEval = eval;
            bestMove = move;
        }
    }



    private int pawnStructureBonus(HashMap<Integer, Integer> pieces, Move move, int multiplier) {
        if (move.movedPiece % 8 == Pawn) {

        }

        return 0;
    }



    private void resetTranspositions(int depth) {
        for (int i = 0; i < depth; i++) {
            positionsTable.add(new HashMap<>());
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

    private ArrayList<Move> sortMoves(ArrayList<Move> moves) {
        int[] moveOrder = OrderMoves(moves);
        for (int i = 0; i < moveOrder.length; i++) {
            for (int j = 0; j < moveOrder.length - 1; j++) {
                if (moveOrder[j] < moveOrder[j + 1]) {
                    int buff = moveOrder[j + 1];
                    Move moveBuff = new Move(moves.get(j + 1));
                    moveOrder[j + 1] = moveOrder[j];
                    moveOrder[j] = buff;
                    moves.set(j + 1, new Move(moves.get(j)));
                    moves.set(j, moveBuff);
                }
            }
        }
        return moves;
    }

//    private int searchAllCaptures(int alpha, int beta, boolean maximizingPlayer){
//
//        board.whiteToMove = maximizingPlayer;
//        int evaluation = Evaluate.evaluate(board, checkedMoves);
//        if(evaluation >= beta){
//            return beta;
//        }
//
//
//    }



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
