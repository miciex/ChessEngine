package GameStates;

public class Move {

    public int movedPiece = 0;
    public int startField;
    public int endField;
    public int takenPiece = 0;
    public int promotePiece;
    public boolean gaveCheck;
    public int takenPieceField;

    public Move(int movedPiece, int startField, int endField, int takenPiece, int takenPieceField, int promotePiece, boolean gaveCheck){
        this.movedPiece = movedPiece;
        this.startField = startField;
        this.endField = endField;
        this.takenPiece = takenPiece;
        this.takenPieceField = takenPieceField;
        this.promotePiece = promotePiece;
        this.gaveCheck = gaveCheck;
    }

    public Move(){

    }

}
