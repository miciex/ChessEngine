package GameStates;

import java.util.HashMap;

import static utils.Constants.Pieces.*;

public class Move {

    public int movedPiece = 0;
    public int startField;
    public int endField;
    public int takenPiece = 0;
    public int promotePiece;
    public boolean gaveCheck;
    public int takenPieceField;

    public Move(){
    }

    public Move(int movedPiece, int startField, int endField, int takenPiece, int takenPieceField, int promotePiece, boolean gaveCheck){
        this.movedPiece = movedPiece;
        this.startField = startField;
        this.endField = endField;
        this.takenPiece = takenPiece;
        this.takenPieceField = takenPieceField;
        this.promotePiece = promotePiece;
        this.gaveCheck = gaveCheck;
    }



    public Move(HashMap<Integer, Integer> pieces, int startField, int endField, int promotePiece){
        this.startField = startField;
        this.endField = endField;
        this.movedPiece = pieces.get(startField);
        this.takenPiece = pieces.containsKey(endField) ? pieces.get(endField) : 0;
        this.takenPieceField = takenPiece != 0 ? endField : -1;
        this.promotePiece = promotePiece;
    }

    public Move(HashMap<Integer, Integer> pieces, int startField, int endField){
        this.startField = startField;
        this.endField = endField;
        this.movedPiece = pieces.get(startField);
        this.takenPiece = pieces.containsKey(endField) ? pieces.get(endField) : 0;
        this.takenPieceField = takenPiece != 0 ? endField : -1;
    }

    public Move(Move move){
        this.startField = move.startField;
        this.endField = move.endField;
        this.movedPiece = move.movedPiece;
        this.takenPiece = move.takenPiece;
        this.takenPieceField = move.takenPieceField;
        this.promotePiece = move.promotePiece;
    }

}
