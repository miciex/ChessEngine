package GameStates;

import java.util.HashMap;

import static utils.Constants.Pieces.*;

public class Move {

    public int movedPiece = 0;
    public int startField = -1;
    public int endField  =-1;
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
        this.takenPiece = calcTakenPiece(pieces);
        this.takenPieceField = calcTakenPieceField(pieces);
        this.promotePiece = promotePiece;
    }

    public Move(HashMap<Integer, Integer> pieces, int startField, int endField){
        this.startField = startField;
        this.endField = endField;
        this.movedPiece = pieces.get(startField);
        this.takenPiece = calcTakenPiece(pieces);
        this.takenPieceField = calcTakenPieceField(pieces);
    }

    public Move(Move move){
        this.startField = move.startField;
        this.endField = move.endField;
        this.movedPiece = move.movedPiece;
        this.takenPiece = move.takenPiece;
        this.takenPieceField = move.takenPieceField;
        this.promotePiece = move.promotePiece;
    }

    private int calcTakenPiece(HashMap<Integer, Integer> pieces){
        if(pieces.containsKey(this.endField)) return pieces.get(this.endField);
        if((movedPiece%8) == Pawn && (startField - endField) % 8 != 0)
            return Pawn + (movedPiece > 16 ? White : Black);
        return 0;
    }

    private int calcTakenPieceField(HashMap<Integer, Integer> pieces){
        if(this.takenPiece == 0)  return -1;
        if(movedPiece%8 == Pawn && !pieces.containsKey(endField))
                return startField + endField % 8 - startField % 8;
        return endField;
    }

}
