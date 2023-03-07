package GameStates;

public class Move {

    public int movedPiece;
    public int startField;
    public int endField;
    public int takenPiece;
    public int takenPieceField;
    public boolean took;

    public Move(int movedPiece, int startField, int endField, int takenPiece, int takenPieceField){
        this.movedPiece = movedPiece;
        this.startField = startField;
        this.endField = endField;
        this.takenPiece = takenPiece;
        this.takenPieceField = takenPieceField;
    }

}
