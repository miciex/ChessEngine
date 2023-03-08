package GameStates;

public class Move {

    public int movedPiece;
    public int startField;
    public int endField;
    public int takenPiece;
    public int promotePiece;
    public boolean gaveCheck;

    public Move(int movedPiece, int startField, int endField, int takenPiece, int promotePiece, boolean gaveCheck){
        this.movedPiece = movedPiece;
        this.startField = startField;
        this.endField = endField;
        this.takenPiece = takenPiece;
        this.promotePiece = promotePiece;
        this.gaveCheck = gaveCheck;
    }

}
