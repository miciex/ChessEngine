package utils;

import GameStates.Move;
import GameStates.Playing;
import ui.BoardOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static utils.Constants.Pieces.*;

public class Piece {

    public static ArrayList<Move> generateMoves(HashMap<Integer, Integer> pieces, boolean whitesMove, Move lastMove,  boolean[] possibleCastles){
        ArrayList<Move> moves = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry : pieces.entrySet()){
            if(entry.getValue() > 16 != whitesMove)
            moves.addAll(Piece.calcMoves(entry.getKey(), pieces, whitesMove, lastMove, possibleCastles));
        }
        return moves;
    }

    public static ArrayList<Move> calcMoves(int activeField, HashMap<Integer, Integer> activePieces, boolean whitesMove, Move lastMove, boolean[] possibleCastles){
        ArrayList<Integer> endingSquares = deleteImpossibleMoves(activeField, PossibleMoves(activeField, activePieces, lastMove, whitesMove, possibleCastles), activePieces, whitesMove, lastMove, possibleCastles);
        ArrayList<Move> moves = new ArrayList<>();

        for(int endSquare : endingSquares){
            if(activePieces.get(activeField)%8 == Pawn && (endSquare/8==7 || endSquare/8==0)){
                for(int i : PROMOTE_PIECES){
                    moves.add(new Move(activePieces, activeField, endSquare, i));
                }
            }else
                moves.add(new Move(activePieces, activeField, endSquare));
        }
        return moves;
    }

    public static ArrayList<Integer> PossibleMoves(int position, HashMap<Integer, Integer> activePieces, Move lastMove, boolean whitesMove, boolean[] possibleCastles) {
        switch (activePieces.get(position) % 8) {
            case Pawn:
                return PossiblePawnMoves(position, activePieces, lastMove);
            case King:
                return allPossibleKingMoves(position, activePieces, whitesMove, possibleCastles, lastMove);
            case Knight:
                return PossibleKnightMoves(position, activePieces);
        }

        int piece = activePieces.get(position) % 8;
        boolean isWhite = HelpMethods.isWhite(activePieces.get(position));

        int row = (int) Math.ceil((double) (position + 1) / 8);
        int column = (position) % 8;

        ArrayList<Integer> moves = new ArrayList<>();
        int checkingDir, checkingPosition, checkingRow, checkingColumn;

        for (int i = 0; i < Constants.Directions.get(piece).size(); i++) {
            checkingDir = Constants.Directions.get(piece).get(i);
            checkingPosition = position + checkingDir;
            checkingRow = (int) Math.ceil((double) (checkingPosition + 1) / 8);
            checkingColumn = checkingPosition % 8;

            while (checkingPosition >= 0 && checkingPosition < Constants.Field.FIELD_SIZE
                    && IsCorrect(position, checkingDir)) {
                if (Constants.Directions.get(Constants.Pieces.Rook).contains(checkingDir)
                        && (checkingColumn != column && checkingRow != row))
                    break;

                if (activePieces.containsKey(checkingPosition))
                    if ((HelpMethods.isWhite(activePieces.get(checkingPosition)) != isWhite)) {
                        moves.add(checkingPosition);
                        break;
                    } else
                        break;

                moves.add(checkingPosition);

                if ((Constants.Directions.get(Constants.Pieces.Bishop).contains(checkingDir)
                        && (checkingColumn == 0 || checkingColumn == 7)))
                    break;

                checkingPosition += checkingDir;
                checkingColumn = checkingPosition % 8;
                checkingRow = (int) Math.ceil((double) (checkingPosition + 1) / 8);
            }
        }

        return moves;
    }

    public static ArrayList<Integer> addCastlingMoves(int position, HashMap<Integer, Integer> activePieces, boolean whitesMove, boolean[] possibleCastles, Move lastMove) {
        ArrayList<Integer> moves = new ArrayList<>();

        if (position == 4 && possibleCastles[0] && isCastlingPossible(position, -1, activePieces, whitesMove, lastMove, possibleCastles))
            moves.add(0);
        if (position == 4 && possibleCastles[1] && isCastlingPossible(position, 1, activePieces, whitesMove, lastMove, possibleCastles))
            moves.add(7);
        if (position == 60 && possibleCastles[2] && isCastlingPossible(position, -1, activePieces, whitesMove, lastMove, possibleCastles))
            moves.add(56);
        if (position == 60 && possibleCastles[3] && isCastlingPossible(position, 1, activePieces, whitesMove, lastMove, possibleCastles))
            moves.add(62);

        return moves;
    }

    private static boolean isCastlingPossible(int position, int dir, HashMap<Integer, Integer> pieces, boolean whitesMove, Move lastMove, boolean[] possibleCastles) {
        int row = (int) Math.ceil((double) (position + 1) / 8);
        int checkingRow = row, checkingPosition = position + dir;
        int checkingColumn = (checkingPosition) % 8;

        while (checkingRow == row) {
            if (checkingColumn == 0 || checkingColumn == 7) {
                if (pieces.get(checkingPosition) % 8 != Constants.Pieces.Rook
                        || HelpMethods.isWhite(pieces.get(checkingPosition)) != whitesMove)
                    return false;
                else
                    return true;
            }

            if (pieces.containsKey(checkingPosition))
                return false;

            if (Arrays.asList(2, 3, 5, 6).contains(checkingColumn))
                if (isChecked(checkingPosition, pieces, whitesMove, lastMove, possibleCastles) != -1)
                    return false;

            checkingPosition += dir;
            checkingColumn = checkingPosition % 8;
            checkingRow = (int) Math.ceil((double) (checkingPosition + 1) / 8);
        }

        return true;
    }

    public static ArrayList<Integer> deleteImpossibleMoves(int activeField, ArrayList<Integer> moves, HashMap<Integer, Integer> activePieces, boolean whitesMove, Move lastMove, boolean[] possibleCastles) {
        ArrayList<Integer> possibleMoves = new ArrayList<>();
        HashMap<Integer, Integer> copy = (HashMap<Integer, Integer>) activePieces.clone();
        for (int i : moves) {
            Move move = new Move(copy, activeField, i);
            copy = makeMove(move, copy, possibleCastles);

            if(isChecked(copy, whitesMove, lastMove, possibleCastles) == -1){
                possibleMoves.add(i);
            }
            copy = unMakeMove(move, copy, possibleCastles);
        }

        return moves;
    }

    public static int isChecked( HashMap<Integer, Integer> activePieces, boolean whitesMove, Move lastMove, boolean[] possibleCastles) {
        int position = HelpMethods.findKing(whitesMove, activePieces);
        int positionChecking = -1;

        boolean isWhite = whitesMove;

        int checkingDir, checkingPosition;

        for (int i = 0; i < Constants.Directions.get(2137).size(); i++) {
            checkingDir = Constants.Directions.get(2137).get(i);
            checkingPosition = position + checkingDir;

            while (checkingPosition >= 0 && checkingPosition < Constants.Field.FIELD_SIZE
                    && IsCorrect(position, checkingDir)) {
                if (i > 7) {
                    if (activePieces.containsKey(checkingPosition)
                            && HelpMethods.isWhite(activePieces.get(checkingPosition)) != isWhite
                            && activePieces.get(checkingPosition) % 8 == Knight) {
                        positionChecking = checkingPosition;
                        break;
                    } else {
                        break;
                    }
                }

                if (activePieces.containsKey(checkingPosition)
                        && HelpMethods.isWhite(activePieces.get(checkingPosition)) != isWhite
                        && PossibleMoves(checkingPosition, activePieces, lastMove, whitesMove, possibleCastles).contains(position)) {
                    positionChecking = checkingPosition;
                    break;
                }

                checkingPosition += checkingDir;
            }
        }

        return positionChecking;
    }

    public static int isChecked(int position, HashMap<Integer, Integer> activePieces, boolean whitesMove, Move lastMove, boolean[] possibleCastles) {
        int positionChecking = -1;

        boolean isWhite = whitesMove;

        int checkingDir, checkingPosition;

        for (int i = 0; i < Constants.Directions.get(2137).size(); i++) {
            checkingDir = Constants.Directions.get(2137).get(i);
            checkingPosition = position + checkingDir;

            while (checkingPosition >= 0 && checkingPosition < Constants.Field.FIELD_SIZE
                    && IsCorrect(position, checkingDir)) {
                if (i > 7) {
                    if (activePieces.containsKey(checkingPosition)
                            && HelpMethods.isWhite(activePieces.get(checkingPosition)) != isWhite
                            && activePieces.get(checkingPosition) % 8 == Knight) {
                        positionChecking = checkingPosition;
                        break;
                    } else {
                        break;
                    }
                }

                if (activePieces.containsKey(checkingPosition)
                        && HelpMethods.isWhite(activePieces.get(checkingPosition)) != isWhite
                        && PossibleMoves(checkingPosition, activePieces, lastMove, whitesMove, possibleCastles).contains(position)) {
                    positionChecking = checkingPosition;
                    break;
                }

                checkingPosition += checkingDir;
            }
        }

        return positionChecking;
    }

    private static ArrayList<Integer> PossiblePawnMoves(int position, HashMap<Integer, Integer> pieces, Move lastMove) {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(pieces.get(position));

        int checkingPosition, checkingDir;

        int row = (int) Math.ceil((double) (position + 1) / 8);

        for (int i = 0; i < Constants.Directions.get(Constants.Pieces.Pawn).size(); i++) {
            checkingDir = isWhite ? -Constants.Directions.get(Constants.Pieces.Pawn).get(i)
                    : Constants.Directions.get(Constants.Pieces.Pawn).get(i);
            checkingPosition = position + checkingDir;

            if (IsCorrect(position, checkingDir)) {
                if (i > 0)
                    if (pieces.containsKey(checkingPosition)
                            && HelpMethods.isWhite(pieces.get(checkingPosition)) != isWhite)
                        moves.add(checkingPosition);
                    else
                        continue;

                if (!pieces.containsKey(checkingPosition))
                    moves.add(checkingPosition);
            }
        }

        if (isWhite == false && row == 2 && !pieces.containsKey(position + 8)
                && !pieces.containsKey(position + 16))
            moves.add(position + 16);
        else if (isWhite == true && row == 7 && !pieces.containsKey(position - 8)
                && !pieces.containsKey(position - 16))
            moves.add(position - 16);

        if (Playing.moves.size() > 0) {

            if (isWhite == true && row == 4 && lastMove.movedPiece % 8 == Pawn
                    && Math.abs(lastMove.endField - lastMove.startField) == 16) {
                if (lastMove.endField == position + 1 && !pieces.containsKey(position - 7))
                    moves.add(position - 7);
                else if (lastMove.endField == position - 1 && !pieces.containsKey(position - 9))
                    moves.add(position - 9);
            }
            if (isWhite == false && row == 5 && lastMove.movedPiece % 8 == Pawn
                    && Math.abs(lastMove.endField - lastMove.startField) == 16) {
                if (lastMove.endField == position + 1 && !pieces.containsKey(position + 9))
                    moves.add(position + 9);
                else if (lastMove.endField == position - 1 && !pieces.containsKey(position + 7))
                    moves.add(position + 7);
            }
        }

        return moves;
    }

    private static ArrayList<Integer> PossibleKingMoves(int position, HashMap<Integer, Integer> pieces, boolean whitesMove, Move lastMove, boolean[] possibleCastles) {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(pieces.get(position));

        int checkingPosition, checkingDir;

        for (int i = 0; i < Constants.Directions.get(King).size(); i++) {
            checkingDir = Constants.Directions.get(King).get(i);

            checkingPosition = position + checkingDir;

            if (IsCorrect(position, checkingDir)) {
                if (pieces.containsKey(checkingPosition))
                    if (HelpMethods.isWhite(pieces.get(checkingPosition)) != isWhite) {
                        moves.add(checkingPosition);
                        continue;
                    } else {
                        continue;
                    }

                moves.add(checkingPosition);
            }
        }
        return moves;
    }

    private static ArrayList<Integer> allPossibleKingMoves(int position, HashMap<Integer, Integer> activePieces, boolean whitesMove, boolean[] possibleCastles, Move lastMove){
        ArrayList<Integer> moves = PossibleKingMoves(position, activePieces, whitesMove, lastMove, possibleCastles);
        moves.addAll(addCastlingMoves(position, activePieces, whitesMove, possibleCastles,lastMove));
        return moves;
    }

    private static ArrayList<Integer> PossibleKnightMoves(int position, HashMap<Integer, Integer> pieces) {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(pieces.get(position));

        int checkingPosition, checkingDir;

        for (int i = 0; i < Constants.Directions.get(Knight).size(); i++) {
            checkingDir = Constants.Directions.get(Knight).get(i);
            checkingPosition = position + checkingDir;

            if (IsCorrect(position, checkingDir)) {
                if (pieces.containsKey(checkingPosition))
                    if (HelpMethods.isWhite(pieces.get(checkingPosition)) != isWhite) {
                        moves.add(checkingPosition);
                        continue;
                    } else {
                        continue;
                    }

                moves.add(checkingPosition);
            }
        }

        return moves;
    }

    public static ArrayList<Integer> canMoveToSquare(int startPosition, int endPosition, int piece, int[] board) {
        ArrayList<Integer> moveList = new ArrayList<>();
        if (piece % 8 == Knight)
            for (int i : Constants.Directions.get(piece % 8)) {
                if (IsCorrect(endPosition, i) && board[i + endPosition] == piece && startPosition != i + endPosition) {
                    moveList.add(endPosition + i);
                }
            }
        else if (piece % 8 != King) {
            for (int i : Constants.Directions.get(piece % 8)) {
                int pos = endPosition += i;
                while (IsCorrect(pos-i, i)) {
                    if (board[pos] == piece) {
                        moveList.add(pos);
                        break;
                    }
                    if(board[pos] != 0)
                        break;
                    pos += i;
                }
            }
        }
        return moveList;
    }

    public static ArrayList<Integer> canMoveToSquare(int startPosition, int piece, int[] board) {
        ArrayList<Integer> moveList = new ArrayList<>();
        if (piece % 8 == Knight)
            for (int i : Constants.Directions.get(piece % 8)) {
                if (IsCorrect(startPosition, i) && board[i + startPosition] == piece ) {
                    moveList.add(startPosition + i);
                }
            }
        else if (piece % 8 != King) {
            for (int i : Constants.Directions.get(piece % 8)) {
                int pos = startPosition;
                while (IsCorrect(pos, i)) {
                    pos += i;
                    if (board[pos] == 0)
                        continue;
                    if (board[pos] == piece) {
                        moveList.add(pos);
                        break;
                    }
                    if(board[pos] != 0)
                        break;
                }
            }
        }
        return moveList;
    }

    private static boolean IsCorrect(int position, int checkingDir) {
        int checkingRow = ((position) / 8) + (int) Math.round((double) checkingDir / 8);
        int help = Math.abs(checkingDir % 8) > 4 ? (checkingDir > 0 ? checkingDir % 8 - 8 : 8 + checkingDir % 8)
                : checkingDir % 8;
        int checkingColumn = position % 8 + help;

        return checkingRow < 8 && checkingRow >= 0 && checkingColumn < 8 && checkingColumn >= 0;
    }

    public static HashMap<Integer, Integer> makeMove(Move move, HashMap<Integer, Integer> boardMap, boolean[] castles){
        if(move.movedPiece == King && Math.abs(move.startField - move.endField)==2){
            //Changing rooks placement in castling
            boardMap.put((move.startField/8)*8 + move.startField%8 + (move.endField - move.startField)/2, boardMap.get((move.startField/8)*8 + ((move.endField % 8)/4) * 7));
            boardMap.remove((move.startField/8)*8 + ((move.endField % 8)/4) * 7);

            for (int i = move.startField/8==0 ? 0 : 2; i<castles.length; i++){
                castles[i] = false;
            }
        }else if(move.movedPiece == Pawn && move.startField/8 == move.takenPieceField/8 ){
            //Removing the pawn which was taken end passant
            boardMap.remove((move.startField/8)*8 + (move.endField % 8));
        }
        boardMap.put(move.endField, move.promotePiece==0? move.movedPiece : move.promotePiece + move.movedPiece - Pawn);
        boardMap.remove(move.startField);
        return boardMap;



    }

    public static HashMap<Integer, Integer> unMakeMove(Move move, HashMap<Integer, Integer> boardMap, boolean[] castles){
        if(move.movedPiece == King && Math.abs(move.startField - move.endField)==2){
            boardMap.put((move.startField/8)*8 + ((move.endField % 8)/4) * 7, boardMap.get((move.startField/8)*8 + move.startField%8 + (move.endField - move.startField)/2));
            boardMap.remove((move.startField/8)*8 + move.startField%8 + (move.endField - move.startField)/2);
            for (int i = move.startField/8==0 ? 0 : 2; i<castles.length; i++){
                castles[i] = true;
            }
        }
        boardMap.put(move.startField, move.movedPiece);
        boardMap.remove(move.endField);
        boardMap.put(move.takenPieceField, move.takenPiece);
        return boardMap;
    }
}