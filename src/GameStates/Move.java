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
        this.takenPiece = calcTakenPiece(this.movedPiece, pieces.containsKey(endField) ? pieces.get(endField) : 0, startField, endField);
        this.takenPieceField = calcTakenPieceField(this.movedPiece, pieces.containsKey(endField) ? pieces.get(endField) : 0, startField, endField);
        this.promotePiece = promotePiece;
    }

    public Move(HashMap<Integer, Integer> pieces, int startField, int endField){
        this.startField = startField;
        this.endField = endField;
        this.movedPiece = pieces.get(startField);
        this.takenPiece = calcTakenPiece(this.movedPiece, pieces.containsKey(endField) ? pieces.get(endField) : 0, startField, endField);
        this.takenPieceField = calcTakenPieceField(this.movedPiece, pieces.containsKey(endField) ? pieces.get(endField) : 0, startField, endField);
    }

    private int calcTakenPiece(int piece, int pieceOnNewField,int startPos, int endPos){
        if(piece%8 != Pawn || Math.abs((startPos%8)-(endPos%8)) == None || pieceOnNewField != None) return pieceOnNewField;
        return Pawn;
    }

    private int calcTakenPieceField(int piece, int pieceOnNewField,int startPos, int endPos){
        if(takenPiece == None) return -1;
        if(piece%8 != Pawn || Math.abs((startPos%8)-(endPos%8)) == None || pieceOnNewField != None) return endPos;
        return (startPos/8) * 8 + endPos % 8;
    }

}
