package utils;

import GameStates.Move;
import GameStates.Playing;
import ui.BoardOverlay;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static utils.Constants.Pieces.*;

public class Piece {

    public static ArrayList<Move> generateMoves(HashMap<Integer, Integer> pieces, boolean whitesMove, Move lastMove,  int[] possibleCastles){
        ArrayList<Move> moves = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry : pieces.entrySet()){
            if(entry.getValue() > 16 != whitesMove)
                moves.addAll(Piece.calcMoves(entry.getKey(), pieces, whitesMove, lastMove, possibleCastles));
        }
        return moves;
    }

    public static ArrayList<Move> calcMoves(int activeField, HashMap<Integer, Integer> activePieces, boolean whitesMove, Move lastMove, int[] possibleCastles){
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

    public static ArrayList<Integer> PossibleMoves(int position, HashMap<Integer, Integer> activePieces, Move lastMove, boolean whitesMove, int[] possibleCastles) {
        switch (activePieces.get(position) % 8) {
            case Pawn:
                return PossiblePawnMoves(position, activePieces, lastMove);
            case King:
                return allPossibleKingMoves(position, activePieces, whitesMove, possibleCastles, lastMove);
            case Knight:
                return specialPossibleMoves(position, activePieces, Knight);
        }

        ArrayList<Integer> moves = new ArrayList<>();

        for (int i : Constants.Directions.get(activePieces.get(position) % 8)) {
            int pos = position;
            while (IsCorrect(pos, i)) {
                pos += i;
                if (!activePieces.containsKey(pos)){
                    moves.add(pos);
                }
                else if (activePieces.get(pos)<16 != activePieces.get(position)<16) {
                    moves.add(pos);
                    break;
                }else
                    break;
            }
        }

        return moves;
    }

    public static ArrayList<Integer> addCastlingMoves(int position, HashMap<Integer, Integer> activePieces, boolean whitesMove, int[] possibleCastles) {
        ArrayList<Integer> moves = new ArrayList<>();

        if (position == 4 && possibleCastles[0] == 0 && activePieces.containsKey(0) && activePieces.get(0)%8 == Rook && isCastlingPossible(position, -1, activePieces, whitesMove))
            moves.add(2);
        if (position == 4 && possibleCastles[1] == 0 && activePieces.containsKey(7) && activePieces.get(7)%8 == Rook && isCastlingPossible(position, 1, activePieces, whitesMove))
            moves.add(6);
        if (position == 60 && possibleCastles[2] == 0 && activePieces.containsKey(56) && activePieces.get(56)%8 == Rook && isCastlingPossible(position, -1, activePieces, whitesMove))
            moves.add(58);
        if (position == 60 && possibleCastles[3] == 0 && activePieces.containsKey(63) && activePieces.get(63)%8 == Rook && isCastlingPossible(position, 1, activePieces, whitesMove))
            moves.add(62);

        return moves;
    }

    private static boolean isCastlingPossible(int position, int dir, HashMap<Integer, Integer> pieces, boolean whitesMove) {
        int row = (int) Math.ceil((double) (position + 1) / 8);
        int checkingRow = row, checkingPosition = position + dir;
        int checkingColumn = (checkingPosition) % 8;

        while (checkingRow == row) {
            if ((checkingColumn == 0 || checkingColumn == 7)&& pieces.containsKey(checkingPosition)) {
                if (pieces.get(checkingPosition) % 8 != Constants.Pieces.Rook
                        || HelpMethods.isWhite(pieces.get(checkingPosition)) != whitesMove)
                    return false;
                else
                    return true;
            }

            if (pieces.containsKey(checkingPosition))
                return false;

            checkingPosition += dir;
            checkingColumn = checkingPosition % 8;
            checkingRow = (int) Math.ceil((double) (checkingPosition + 1) / 8);
        }

        return true;
    }

    public static ArrayList<Integer> deleteImpossibleMoves(int activeField, ArrayList<Integer> moves, HashMap<Integer, Integer> activePieces, boolean whitesMove, Move lastMove, int[] possibleCastles) {
        ArrayList<Integer> possibleMoves = new ArrayList<>();
        HashMap<Integer, Integer> copy = (HashMap<Integer, Integer>) activePieces.clone();

        int multiplier = whitesMove ? -1 : 1;

        for (int i : moves) {
            Move move = new Move(copy, activeField, i);
            copy = makeMove(move, copy);

            if(isChecked(copy, whitesMove, lastMove, possibleCastles) == -1)
            {
                if(activePieces.get(activeField) % 8 == King && Math.abs(i - activeField) == 2)
                {
                    if (isChecked(activeField, activePieces, whitesMove, lastMove, possibleCastles) == -1 && isChecked(activeField + (i - activeField)/2, activePieces, whitesMove, lastMove, possibleCastles) == -1 && isChecked(i, activePieces, whitesMove, lastMove, possibleCastles) == -1)
                        if(!(activePieces.containsKey(activeField + (8 * multiplier)) && HelpMethods.isWhite(activePieces.get(activeField + (8 * multiplier))) != whitesMove && activePieces.get(activeField + (8 * multiplier)) % 8 == Pawn))
                            possibleMoves.add(i);
                }
                else
                    possibleMoves.add(i);
            }
            copy = unMakeMove(move, copy);
        }

        return possibleMoves;
    }

    public static int isChecked(HashMap<Integer, Integer> activePieces, boolean whitesMove, Move lastMove, int[] possibleCastles) {
        int position = HelpMethods.findKing(whitesMove, activePieces);
        int positionChecking = -1;
        int checkingPosition;

        for (int i : Constants.Directions.get(2137)) {
            checkingPosition = position + i;

            if (Constants.Directions.get(2137).indexOf(i) > 7 && IsCorrect(position, i)) {
                if (activePieces.containsKey(checkingPosition)
                        && HelpMethods.isWhite(activePieces.get(checkingPosition)) != whitesMove
                        && activePieces.get(checkingPosition) % 8 == Knight) {
                    return checkingPosition;
                } else {
                    continue;
                }
            }

            while (checkingPosition >= 0 && checkingPosition < Constants.Field.FIELD_SIZE
                    && IsCorrect(position, i)) {
                if(activePieces.containsKey(checkingPosition) && HelpMethods.isWhite(activePieces.get(checkingPosition)) == whitesMove)
                    break;

                if (activePieces.containsKey(checkingPosition)
                        && HelpMethods.isWhite(activePieces.get(checkingPosition)) != whitesMove
                        && PossibleMoves(checkingPosition, activePieces, lastMove, whitesMove, possibleCastles).contains(position)) {
                    return checkingPosition;
                }

                checkingPosition += i;
            }
        }

        return positionChecking;
    }

    public static int isChecked(int position, HashMap<Integer, Integer> activePieces, boolean whitesMove, Move lastMove, int[] possibleCastles) {
        int positionChecking = -1;
        int checkingPosition;

        for (int i : Constants.Directions.get(2137)) {
            checkingPosition = position + i;

            if (Constants.Directions.get(2137).indexOf(i) > 7 && IsCorrect(position, i)) {
                if (activePieces.containsKey(checkingPosition)
                        && HelpMethods.isWhite(activePieces.get(checkingPosition)) != whitesMove
                        && activePieces.get(checkingPosition) % 8 == Knight) {
                    return checkingPosition;
                } else {
                    continue;
                }
            }

            while (checkingPosition >= 0 && checkingPosition < Constants.Field.FIELD_SIZE
                    && IsCorrect(position, i)) {
                if(activePieces.containsKey(checkingPosition) && HelpMethods.isWhite(activePieces.get(checkingPosition)) == whitesMove)
                    break;

                if (activePieces.containsKey(checkingPosition)
                        && HelpMethods.isWhite(activePieces.get(checkingPosition)) != whitesMove
                        && PossibleMoves(checkingPosition, activePieces, lastMove, whitesMove, possibleCastles).contains(position)) {
                    return checkingPosition;
                }

                checkingPosition += i;
            }
        }

        return positionChecking;
    }

    private static ArrayList<Integer> PossiblePawnMoves(int position, HashMap<Integer, Integer> pieces, Move lastMove) {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(pieces.get(position));
        int mulptiplier = isWhite ? -1 : 1;
        ArrayList<Integer> directions = Constants.Directions.get(Constants.Pieces.Pawn);
        for(int i = 0; i<directions.size(); i++){
            if(!IsCorrect(position , mulptiplier * directions.get(i))) continue;
            int pos = mulptiplier * directions.get(i) + position;
            if(i<2 && !pieces.containsKey(pos) ){
                if(i==0)
                    moves.add(pos);
                else if((int)(3.5 - (float)mulptiplier * 2.5) == position/8 && !pieces.containsKey(pos - 8 * mulptiplier)){
                    moves.add(pos);
                }
            }
            else if(i > 1 && pieces.containsKey(pos) && (pieces.get(pos) < 16 != isWhite)){
                moves.add(pos);
            }
            else if(i > 1 && lastMove.movedPiece%8 == Pawn  && Math.abs((lastMove.startField/8) - (lastMove.endField/8))==2 && pos == lastMove.endField + 8 * mulptiplier){
                moves.add(pos);
            }
        }

        return moves;
    }

    private static ArrayList<Integer> specialPossibleMoves(int position, HashMap<Integer, Integer> pieces, int piece)
    {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(pieces.get(position));

        int checkingPosition;

        for (int i : Constants.Directions.get(piece)) {
            checkingPosition = position + i;

            if (IsCorrect(position, i)) {
                if (pieces.containsKey(checkingPosition) && HelpMethods.isWhite(pieces.get(checkingPosition)) == isWhite)
                    continue;

                moves.add(checkingPosition);
            }
        }
        return moves;
    }

    private static ArrayList<Integer> allPossibleKingMoves(int position, HashMap<Integer, Integer> activePieces, boolean whitesMove, int[] possibleCastles, Move lastMove){
        ArrayList<Integer> moves = specialPossibleMoves(position, activePieces, King);
        moves.addAll(addCastlingMoves(position, activePieces, whitesMove, possibleCastles));
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

    public static HashMap<Integer, Integer> makeMove(Move move, HashMap<Integer, Integer> boardMap){
        if(move.movedPiece%8 == King && Math.abs(move.startField - move.endField)==2){
            //Changing rooks placement in castling
            boardMap.put((move.startField/8)*8 + move.startField%8 + (move.endField - move.startField)/2, boardMap.get((move.startField/8)*8 + ((move.endField % 8)/4) * 7));
            boardMap.remove((move.startField/8)*8 + ((move.endField % 8)/4) * 7);
        }else if(move.movedPiece%8 == Pawn  && move.takenPiece%8 == Pawn && move.endField!=move.takenPieceField){
            //Removing the pawn which was taken end passant
            boardMap.remove(move.takenPieceField);
        }
        boardMap.put(move.endField, move.promotePiece==0? move.movedPiece : move.promotePiece + move.movedPiece - Pawn);
        boardMap.remove(move.startField);
        return boardMap;
    }

    public static HashMap<Integer, Integer> unMakeMove(Move move, HashMap<Integer, Integer> boardMap){

        if(move.movedPiece%8 == King && Math.abs(move.startField - move.endField)==2){
            boardMap.put((move.startField/8)*8 + ((move.endField % 8)/4) * 7, boardMap.get( move.startField + (move.endField - move.startField)/2));
            boardMap.remove(  move.startField + (move.endField - move.startField)/2);
        }
        boardMap.put(move.startField, move.movedPiece);
        boardMap.remove(move.endField);
        if(move.takenPiece>0)
            boardMap.put(move.takenPieceField, move.takenPiece);
        return boardMap;
    }

    public static int[] setCastles(int[] castles, ArrayList<Move> moves) {
        if (moves.size() == 0) return castles;
        Move lastMove = moves.get(moves.size() - 1);
        if ( lastMove.movedPiece%8==King) {
            for (int i = lastMove.movedPiece > 16 ? 0 : 2; i - (lastMove.movedPiece > 16 ? 0 : 2) < 2; i++) {
                if(castles[i]==0)
                    castles[i] = moves.size();

            }
            return castles;
        }
        if (lastMove.movedPiece == Rook) {
            for (int i = 0; i < castles.length; i++) {
                if ( castles[i] == 0 && lastMove.startField == (7 * (i % 2)) + (i / 2) * 56) {
                    castles[i] = moves.size();
                    return castles;
                }
            }
        }


        return castles;
    }

    public static int[] unsetCastles(int[] castles, ArrayList<Move> moves){
        for(int i = 0; i< castles.length; i++){
            if(castles[i]>moves.size())
                castles[i] = 0;
        }
        return castles;
    }


}