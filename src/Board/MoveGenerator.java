package Board;

import GameStates.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utils.Constants.Pieces.PROMOTE_PIECES;
import static utils.Constants.Pieces.Pawn;

public class MoveGenerator {

    private Board board;

    public MoveGenerator(Board board){
        this.board = board;
    }

    public ArrayList<Move> generateMoves(boolean capturesOnly) {
        ArrayList<Move> moves = new ArrayList<>();
        HashMap<Integer, Integer> position = (HashMap<Integer, Integer>) board.position.clone();
        for (Map.Entry<Integer, Integer> entry : position.entrySet()) {
            if (entry.getValue() > 16 != board.whiteToMove)
                if(capturesOnly)
                    moves.addAll(calcCaptures(entry.getKey()));
                else
                    moves.addAll(calcMoves(entry.getKey()));
        }
        return moves;
    }

    public ArrayList<Move> calcMoves(int activeField) {
        ArrayList<Integer> endingSquares = board.deleteImpossibleMoves(board.PossibleMoves(activeField), activeField);
        ArrayList<Move> moves = new ArrayList<>();

        for (int i = 0; i< endingSquares.size(); i++) {
            int endSquare = endingSquares.get(i);
            if (board.position.get(activeField) % 8 == Pawn && (endSquare / 8 == 7 || endSquare / 8 == 0)) {
                for (int j =0 ; j<PROMOTE_PIECES.length; j++) {
                    int piece = PROMOTE_PIECES[j];
                    moves.add(new Move(board.position, activeField, endSquare, piece));
                }
            } else
                moves.add(new Move(board.position, activeField, endSquare));
        }
        return moves;
    }

    public ArrayList<Move> calcCaptures(int activeField){
        ArrayList<Integer> allEndingSquares = board.PossibleMoves(activeField);
        ArrayList<Integer> captureEndingSquares = new ArrayList<>();

        ArrayList<Move> moves = new ArrayList<>();

        for(int i = 0; i< allEndingSquares.size(); i++){
            if(board.position.containsKey(allEndingSquares.get(i)) && board.position.get(allEndingSquares.get(i)) < 16 != board.whiteToMove){
                captureEndingSquares.add(allEndingSquares.get(i));
            }
        }

        captureEndingSquares = board.deleteImpossibleMoves(captureEndingSquares, activeField);

        for (int i = 0; i< captureEndingSquares.size(); i++) {
            int endSquare = captureEndingSquares.get(i);
            if (board.position.get(activeField) % 8 == Pawn && (endSquare / 8 == 7 || endSquare / 8 == 0)) {
                for (int j =0 ; j<PROMOTE_PIECES.length; j++) {

                    int piece = PROMOTE_PIECES[j];
                    moves.add(new Move(board.position, activeField, endSquare, piece));
                }
            }
            else
                moves.add(new Move(board.position, activeField, endSquare));
        }
        return moves;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
