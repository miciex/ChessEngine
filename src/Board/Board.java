package Board;

import GameStates.GameResults;
import GameStates.Move;
import utils.CheckGameResults;
import utils.Constants;
import utils.HelpMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utils.CheckGameResults.draw50MoveRuleCheck;
import static utils.Constants.Pieces.*;
import static utils.HelpMethods.FenToIntArray;
import static utils.HelpMethods.boardToMap;

public class Board {

    public HashMap<Integer, Integer> position;
    public boolean whiteToMove;
    public int[] availableCastles;
    public ArrayList<Move> moves;
    public String fen;
    public int[] visualBoard;
    public ArrayList<HashMap<Integer, Integer>> positions;
    public int movesTo50MoveRule;

    public Board(String fenString, boolean whiteToMove, int[] availableCastles, ArrayList<Move> moves){
        this.fen = fenString;
        this.visualBoard = FenToIntArray(this.fen, 64);
        this.position = boardToMap(this.visualBoard);
        this.whiteToMove = whiteToMove;
        this.availableCastles = availableCastles;
        this.moves = moves;
        this.positions = new ArrayList<>();
        this.movesTo50MoveRule = 0;
    }

    public Board(String fenString){
        this.fen = fenString;
        this.visualBoard = FenToIntArray(this.fen, 64);
        this.position = boardToMap(this.visualBoard);
        this.whiteToMove = true;
        this.availableCastles = new int[]{0,0,0,0};
        this.moves = new ArrayList<>();
        this.positions = new ArrayList<>();
        this.movesTo50MoveRule = 0;
    }

    public Board(Board board){
        this.fen = board.fen;
        this.visualBoard = board.visualBoard;
        this.position = (HashMap<Integer, Integer>) board.position.clone();
        this.whiteToMove = board.whiteToMove;
        this.availableCastles = board.availableCastles.clone();
        this.moves = board.moves;
        this.positions = board.positions;
        this.movesTo50MoveRule = 0;
    }

    public void resetBoard(){
        this.visualBoard = FenToIntArray(this.fen, 64);
        this.position = boardToMap(this.visualBoard);
        this.whiteToMove = true;
        this.availableCastles = new int[]{0,0,0,0};
        this.moves = new ArrayList<>();
        this.positions.clear();
        this.movesTo50MoveRule = 0;
    }

    public GameResults checkGameResult() {
        GameResults result = GameResults.NONE;

        //Do the same in engine
        //if (move.takenPiece != 0 || (move.movedPiece % 8 == King && Math.abs(move.startField - move.endField) == 2))
        //positions.clear();
        //positions.add((HashMap<Integer, Integer>) Playing.ActivePieces.clone());

        //movesTo50MoveRule = CheckGameResults.draw50MoveRuleCheck(move, movesTo50MoveRule);

        if (CheckGameResults.isThreefold(this))
            result = GameResults.THREE_FOLD;
        if (CheckGameResults.draw50MoveRule(movesTo50MoveRule))
            result = GameResults.DRAW_50_MOVE_RULE;
        else if (CheckGameResults.isStalemate(this))
            result = GameResults.STALEMATE;
        else if (CheckGameResults.insufficientMaterial(this))
            result = GameResults.INSUFFICIENT_MATERIAL;
        else if (CheckGameResults.isMate(this))
            result = GameResults.MATE;
        return result;
    }

    public boolean isDraw(){
        if (CheckGameResults.insufficientMaterial(this))
            return true;
        if (CheckGameResults.isThreefold(this))
            return true;
        if (CheckGameResults.draw50MoveRule(movesTo50MoveRule))
            return true;
        return false;
    }

    public  ArrayList<Integer> PossibleMoves(int position) {
        switch (this.position.get(position) % 8) {
            case Pawn:
                return PossiblePawnMoves(position);
            case King:
                return allPossibleKingMoves(position);
            case Knight:
                return specialPossibleMoves(position, Knight);
        }

        ArrayList<Integer> moves = new ArrayList<>();

        for (int i : Constants.Directions.get(this.position.get(position) % 8)) {
            int pos = position;
            while (IsCorrect(pos, i)) {
                pos += i;
                if (!this.position.containsKey(pos)) {
                    moves.add(pos);
                } else if (this.position.get(pos) < 16 != this.position.get(position) < 16) {
                    moves.add(pos);
                    break;
                } else
                    break;
            }
        }

        return moves;
    }

    public  ArrayList<Integer> addCastlingMoves(int position) {
        ArrayList<Integer> moves = new ArrayList<>();

        if (position == 4 && availableCastles[0] == 0 && this.position.containsKey(0) && this.position.get(0) % 8 == Rook && isCastlingPossible(position, -1))
            moves.add(2);
        if (position == 4 && availableCastles[1] == 0 && this.position.containsKey(7) && this.position.get(7) % 8 == Rook && isCastlingPossible(position, 1))
            moves.add(6);
        if (position == 60 && availableCastles[2] == 0 && this.position.containsKey(56) && this.position.get(56) % 8 == Rook && isCastlingPossible(position, -1))
            moves.add(58);
        if (position == 60 && availableCastles[3] == 0 && this.position.containsKey(63) && this.position.get(63) % 8 == Rook && isCastlingPossible(position, 1))
            moves.add(62);

        return moves;
    }

    private  boolean isCastlingPossible(int position, int dir) {
        int row = (int) Math.ceil((double) (position + 1) / 8);
        int checkingRow = row, checkingPosition = position + dir;
        int checkingColumn = (checkingPosition) % 8;

        while (checkingRow == row) {
            if ((checkingColumn == 0 || checkingColumn == 7) && this.position.containsKey(checkingPosition)) {
                if (this.position.get(checkingPosition) % 8 != Constants.Pieces.Rook
                        || HelpMethods.isWhite(this.position.get(checkingPosition)) != whiteToMove)
                    return false;
                else
                    return true;
            }

            if (this.position.containsKey(checkingPosition))
                return false;

            checkingPosition += dir;
            checkingColumn = checkingPosition % 8;
            checkingRow = (int) Math.ceil((double) (checkingPosition + 1) / 8);
        }

        return true;
    }

    public  ArrayList<Integer> deleteImpossibleMoves(ArrayList<Integer> moves,int activeField) {
        ArrayList<Integer> possibleMoves = new ArrayList<>();

        int multiplier = whiteToMove ? -1 : 1;

        for (int i : moves) {
            Move move = new Move(position, activeField, i);
            HashMap<Integer, Integer> copy = (HashMap<Integer, Integer>) position.clone();
            makeMove(move);

            if (isChecked() == -1) {
                if (copy.get(activeField) % 8 == King && Math.abs(i - activeField) == 2) {
                    if (isChecked(activeField) == -1 && isChecked(activeField + (i - activeField) / 2) == -1 && isChecked(i) == -1)
                        if (!(position.containsKey(activeField + (8 * multiplier)) && HelpMethods.isWhite(position.get(activeField + (8 * multiplier))) != whiteToMove && position.get(activeField + (8 * multiplier)) % 8 == Pawn))
                            possibleMoves.add(i);
                } else
                    possibleMoves.add(i);
            }
            unMakeMove(move);
        }

        return possibleMoves;
    }

    public  int isChecked() {
        int position = HelpMethods.findKing(this);
        int king = this.position.get(position);

        for(int i : PIECES_ARRAY){
            if(isPieceAttackingTarget(i, position, king < 16))
                return 1;
        }

        return -1;
    }

    public  int isChecked(int position) {
        for(int i : PIECES_ARRAY){
            if(isPieceAttackingTarget(i, position, whiteToMove))
                return 1;
        }

        return -1;
    }

    private  boolean isLongRangePieceAttackingTarget(int piece, int targetSquare, boolean isTargetWhite){
        ArrayList<Integer> directions = getPieceDirections(piece);
        int checkingPosition;
        for(int i = 0; i<directions.size(); i++){
            checkingPosition = targetSquare;
            while(IsCorrect(checkingPosition, directions.get(i))){
                checkingPosition += directions.get(i);
                if(!position.containsKey(checkingPosition)) continue;
                int foundPiece = position.get(checkingPosition);
                if(foundPiece < 16 == isTargetWhite) break;
                if((foundPiece%8 == piece%8 || foundPiece%8 == Queen))
                    return true;
                else break;
            }
        }
        return false;
    }

    private  boolean isSpecialPieceAttackingTarget(int piece, int targetSquare, boolean isTargetWhite){
        ArrayList<Integer> directions = getPieceDirections(piece);
        int checkingPosition;
        for(int i = 0; i<directions.size(); i++){
            checkingPosition = targetSquare + directions.get(i);
            if(!IsCorrect(targetSquare, directions.get(i))) continue;
            if(!position.containsKey(checkingPosition)) continue;
            int foundPiece = position.get(checkingPosition);
            if(foundPiece%8 == piece && foundPiece < 16 != isTargetWhite)
                return true;
        }
        return false;
    }

    private  boolean isPawnAttackingTarget(int targetSquare, boolean isTargetWhite){
        ArrayList<Integer> directions = getPieceDirections(Pawn);
        int checkingPosition,m = isTargetWhite ? -1 : 1;
        for(int i = 2; i<directions.size(); i++){
            checkingPosition = targetSquare + directions.get(i) * m;
            if(!IsCorrect(targetSquare, directions.get(i) * m)) continue;
            if(!position.containsKey(checkingPosition)) continue;
            int piece = position.get(checkingPosition);
            if(piece%8 == Pawn && piece < 16 != isTargetWhite)
                return true;
        }
        return false;
    }

    private  boolean isPieceAttackingTarget(int piece, int targetSquare, boolean isTargetWhite){
        switch (piece%8){
            case Rook:
            case Bishop:
            case Queen:
                return isLongRangePieceAttackingTarget(piece, targetSquare, isTargetWhite);
            case King:
            case Knight:
                return isSpecialPieceAttackingTarget(piece, targetSquare, isTargetWhite);
            case Pawn:
                return isPawnAttackingTarget(targetSquare, isTargetWhite);
            default: return false;
        }
    }

    private  ArrayList<Integer> getPieceDirections(int piece){
        return Constants.Directions.get(piece%8);
    }

    private  ArrayList<Integer> PossiblePawnMoves(int position) {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(this.position.get(position));
        int mulptiplier = isWhite ? -1 : 1;
        ArrayList<Integer> directions = Constants.Directions.get(Constants.Pieces.Pawn);
        for (int i = 0; i < directions.size(); i++) {
            if (!IsCorrect(position, mulptiplier * directions.get(i))) continue;
            int pos = mulptiplier * directions.get(i) + position;
            if (i < 2 && !this.position.containsKey(pos)) {
                if (i == 0)
                    moves.add(pos);
                else if ((int) (3.5 - (float) mulptiplier * 2.5) == position / 8 && !this.position.containsKey(pos - 8 * mulptiplier)) {
                    moves.add(pos);
                }
            } else if (i > 1 && this.position.containsKey(pos) && (this.position.get(pos) < 16 != isWhite)) {
                moves.add(pos);
            } else if (i > 1 && getLastMove().movedPiece % 8 == Pawn && Math.abs((getLastMove().startField / 8) - (getLastMove().endField / 8)) == 2 && pos == getLastMove().endField + 8 * mulptiplier) {
                moves.add(pos);
            }
        }

        return moves;
    }

    private  ArrayList<Integer> specialPossibleMoves(int position, int piece) {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(this.position.get(position));

        int checkingPosition;

        for (int i : Constants.Directions.get(piece)) {
            checkingPosition = position + i;

            if (IsCorrect(position, i)) {
                if (this.position.containsKey(checkingPosition) && HelpMethods.isWhite(this.position.get(checkingPosition)) == isWhite)
                    continue;

                moves.add(checkingPosition);
            }
        }
        return moves;
    }

    private  ArrayList<Integer> allPossibleKingMoves(int position) {
        ArrayList<Integer> moves = specialPossibleMoves(position, King);
        moves.addAll(addCastlingMoves(position));
        return moves;
    }

    public  ArrayList<Integer> canMoveToSquare(int startPosition, int endPosition, int piece) {
        ArrayList<Integer> moveList = new ArrayList<>();
        if (piece % 8 == Knight)
            for (int i : Constants.Directions.get(piece % 8)) {
                if (IsCorrect(endPosition, i) && visualBoard[i + endPosition] == piece && startPosition != i + endPosition) {
                    moveList.add(endPosition + i);
                }
            }
        else if (piece % 8 != King) {
            for (int i : Constants.Directions.get(piece % 8)) {
                int pos = endPosition += i;
                while (IsCorrect(pos - i, i)) {
                    if (visualBoard[pos] == piece) {
                        moveList.add(pos);
                        break;
                    }
                    if (visualBoard[pos] != 0)
                        break;
                    pos += i;
                }
            }
        }
        return moveList;
    }

    public  ArrayList<Integer> canMoveToSquare(int startPosition, int piece) {
        ArrayList<Integer> moveList = new ArrayList<>();
        if (piece % 8 == Knight)
            for (int i : Constants.Directions.get(piece % 8)) {
                if (IsCorrect(startPosition, i) && visualBoard[i + startPosition] == piece) {
                    moveList.add(startPosition + i);
                }
            }
        else if (piece % 8 != King) {
            for (int i : Constants.Directions.get(piece % 8)) {
                int pos = startPosition;
                while (IsCorrect(pos, i)) {
                    pos += i;
                    if (visualBoard[pos] == 0)
                        continue;
                    if (visualBoard[pos] == piece) {
                        moveList.add(pos);
                        break;
                    }
                    if (visualBoard[pos] != 0)
                        break;
                }
            }
        }
        return moveList;
    }

    private  boolean IsCorrect(int position, int checkingDir) {
        int checkingRow = ((position) / 8) + (int) Math.round((double) checkingDir / 8);
        int help = Math.abs(checkingDir % 8) > 4 ? (checkingDir > 0 ? checkingDir % 8 - 8 : 8 + checkingDir % 8)
                : checkingDir % 8;
        int checkingColumn = position % 8 + help;

        return checkingRow < 8 && checkingRow >= 0 && checkingColumn < 8 && checkingColumn >= 0;
    }

    public  void movePiece(Move move) {
        if (move.movedPiece % 8 == King && Math.abs(move.startField - move.endField) == 2) {
            //Changing rooks placement in castling
            position.put((move.startField / 8) * 8 + move.startField % 8 + (move.endField - move.startField) / 2, position.get((move.startField / 8) * 8 + ((move.endField % 8) / 4) * 7));
            position.remove((move.startField / 8) * 8 + ((move.endField % 8) / 4) * 7);
        } else if (move.movedPiece % 8 == Pawn && move.takenPiece % 8 == Pawn && move.endField != move.takenPieceField) {
            //Removing the pawn which was taken end passant
            position.remove(move.takenPieceField);
        }
        position.put(move.endField, move.promotePiece == 0 ? move.movedPiece : move.promotePiece + move.movedPiece - Pawn);
        position.remove(move.startField);
    }

    public void unMovePiece(Move move) {
        if (move.movedPiece % 8 == King && Math.abs(move.startField - move.endField) == 2) {
            position.put((move.startField / 8) * 8 + ((move.endField % 8) / 4) * 7, position.get(move.startField + (move.endField - move.startField) / 2));
            position.remove(move.startField + (move.endField - move.startField) / 2);
        }
        position.put(move.startField, move.movedPiece);
        position.remove(move.endField);
        if (move.takenPiece > 0)
            position.put(move.takenPieceField, move.takenPiece);
    }

    public  void setCastles() {
        if (moves.size() == 0) return;
        Move lastMove = moves.get(moves.size() - 1);
        if (lastMove.movedPiece % 8 == King) {
            for (int i = lastMove.movedPiece > 16 ? 0 : 2; i - (lastMove.movedPiece > 16 ? 0 : 2) < 2; i++) {
                if (availableCastles[i] == 0)
                    availableCastles[i] = moves.size();

            }
            return;
        }
        if (lastMove.movedPiece == Rook) {
            for (int i = 0; i < availableCastles.length; i++) {
                if (availableCastles[i] == 0 && lastMove.startField == (7 * (i % 2)) + (i / 2) * 56) {
                    availableCastles[i] = moves.size();
                    return;
                }
            }
        }
    }

    public  int[] unsetCastles() {
        for (int i = 0; i < availableCastles.length; i++) {
            if (availableCastles[i] > moves.size())
                availableCastles[i] = 0;
        }
        return availableCastles;
    }

    public void makeMove(Move move){
        movePiece(move);
        setCastles();
        moves.add(move);
        positions.add((HashMap<Integer, Integer>) position.clone());
        //whiteToMove = !whiteToMove;
        //draw50MoveRuleCheck(move, movesTo50MoveRule);
    }

    public void unMakeMove(Move move){
        unMovePiece(move);
        unsetCastles();
        removeLastMove();
        positions.remove(positions.size()-1);
        //whiteToMove = !whiteToMove;
    }

    public  boolean isEndgame() {
        if (!position.containsValue(Queen + White) && !position.containsValue(Queen + Black))
            return true;

        if (position.containsValue(Queen + White)) {
            boolean minorPieces = false;

            for (int j : position.values()) {
                if (j < 16) {
                    if (j % 8 == Rook)
                        return false;
                    if (j % 8 == Knight || j % 8 == Bishop) {
                        if (minorPieces == true)
                            return false;
                        minorPieces = true;
                    }
                }
            }

        }

        if (position.containsValue(Queen + Black)) {
            boolean minorPieces = false;

            for (int j : position.values()) {
                if (j >= 16) {
                    if (j % 8 == Rook)
                        return false;
                    if (j % 8 == Knight || j % 8 == Bishop) {
                        if (minorPieces == true)
                            return false;
                        minorPieces = true;
                    }
                }
            }
        }

        return true;
    }

    public Move getLastMove(){
        return moves.size() > 0 ? moves.get(moves.size()-1) : new Move();
    }

    public void removeLastMove(){ if(moves.size()>0) moves.remove(moves.size()-1);}

    public HashMap<Integer,Integer> getPositionCopy(){
        return (HashMap<Integer, Integer>) position.clone();
    }

}
