package GameStates;

public class Move {

    int movedPiece;
    int startField;
    int endField;
    int takenPiece;
    int takenPieceField;

    public Move(int movedPiece, int startField, int endField, int takenPiece, int takenPieceField){
        this.movedPiece = movedPiece;
        this.startField = startField;
        this.endField = endField;
        this.takenPiece = takenPiece;
        this.takenPieceField = takenPieceField;
    }

}
