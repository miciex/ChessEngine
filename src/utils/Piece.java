package utils;

import Board.Board;
import GameStates.Move;
import GameStates.Playing;
import ui.BoardOverlay;

import java.lang.reflect.Array;
import java.util.*;

import static utils.Constants.Pieces.*;

public class Piece {

    public static ArrayList<Move> generateMoves(Board board, boolean capturesOnly) {
        ArrayList<Move> moves = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : board.position.entrySet()) {
            if (entry.getValue() > 16 != board.whiteToMove)
                if(capturesOnly)
                    moves.addAll(calcCaptures(board, entry.getKey()));
                else
                    moves.addAll(Piece.calcMoves(board, entry.getKey()));
        }
        return moves;
    }

    public static ArrayList<Move> calcMoves(Board board, int activeField) {
        ArrayList<Integer> endingSquares = deleteImpossibleMoves(board, PossibleMoves(board, activeField), activeField);
        ArrayList<Move> moves = new ArrayList<>();

        for (int i = 0; i< endingSquares.size(); i++) {
            int endSquare = endingSquares.get(i);
            if (board.position.get(activeField) % 8 == Pawn && (endSquare / 8 == 7 || endSquare / 8 == 0)) {
                for (int j =0 ; j<PROMOTE_PIECES.length; j++) {
                    int piece = PROMOTE_PIECES[j];
                    moves.add(new Move(board.position, activeField, endSquare, piece));
                }
            } else
                moves.add(new Move(board.position, activeField, endSquare));
        }
//        for(Move move : moves){
//                move.gaveCheck = isChecked(activePieces, move.movedPiece > 16, move, possibleCastles)!=-1;
//        }
        return moves;
    }

    public static ArrayList<Move> calcCaptures(Board board, int activeField){
        ArrayList<Integer> allEndingSquares = PossibleMoves(board, activeField);
        ArrayList<Integer> captureEndingSquares = new ArrayList<>();

        ArrayList<Move> moves = new ArrayList<>();

        for(int i = 0; i< allEndingSquares.size(); i++){
            if(board.position.containsKey(allEndingSquares.get(i)) && board.position.get(allEndingSquares.get(i)) < 16 != board.whiteToMove){
                captureEndingSquares.add(allEndingSquares.get(i));
            }
        }

        captureEndingSquares = deleteImpossibleMoves(board, captureEndingSquares, activeField);

        for (int i = 0; i< captureEndingSquares.size(); i++) {
            int endSquare = captureEndingSquares.get(i);
            if (board.position.get(activeField) % 8 == Pawn && (endSquare / 8 == 7 || endSquare / 8 == 0)) {
                for (int j =0 ; j<PROMOTE_PIECES.length; j++) {

                    int piece = PROMOTE_PIECES[j];
                    moves.add(new Move(board.position, activeField, endSquare, piece));
                }
            }
            else
                moves.add(new Move(board.position, activeField, endSquare));
        }
//        for(Move move : moves){
//                move.gaveCheck = isChecked(activePieces, move.movedPiece > 16, move, possibleCastles)!=-1;
//        }
        return moves;
    }



    public static ArrayList<Integer> PossibleMoves(Board board,int position) {
        switch (board.position.get(position) % 8) {
            case Pawn:
                return PossiblePawnMoves(board, position);
            case King:
                return allPossibleKingMoves(board, position);
            case Knight:
                return specialPossibleMoves(board, position, Knight);
        }

        ArrayList<Integer> moves = new ArrayList<>();

        for (int i : Constants.Directions.get(board.position.get(position) % 8)) {
            int pos = position;
            while (IsCorrect(pos, i)) {
                pos += i;
                if (!board.position.containsKey(pos)) {
                    moves.add(pos);
                } else if (board.position.get(pos) < 16 != board.position.get(position) < 16) {
                    moves.add(pos);
                    break;
                } else
                    break;
            }
        }

        return moves;
    }

    public static ArrayList<Integer> addCastlingMoves(Board board,int position) {
        ArrayList<Integer> moves = new ArrayList<>();

        if (position == 4 && board.availableCastles[0] == 0 && board.position.containsKey(0) && board.position.get(0) % 8 == Rook && isCastlingPossible(board, position, -1))
            moves.add(2);
        if (position == 4 && board.availableCastles[1] == 0 && board.position.containsKey(7) && board.position.get(7) % 8 == Rook && isCastlingPossible(board, position, 1))
            moves.add(6);
        if (position == 60 && board.availableCastles[2] == 0 && board.position.containsKey(56) && board.position.get(56) % 8 == Rook && isCastlingPossible(board, position, -1))
            moves.add(58);
        if (position == 60 && board.availableCastles[3] == 0 && board.position.containsKey(63) && board.position.get(63) % 8 == Rook && isCastlingPossible(board, position, 1))
            moves.add(62);

        return moves;
    }

    private static boolean isCastlingPossible(Board board,int position, int dir) {
        int row = (int) Math.ceil((double) (position + 1) / 8);
        int checkingRow = row, checkingPosition = position + dir;
        int checkingColumn = (checkingPosition) % 8;

        while (checkingRow == row) {
            if ((checkingColumn == 0 || checkingColumn == 7) && board.position.containsKey(checkingPosition)) {
                if (board.position.get(checkingPosition) % 8 != Constants.Pieces.Rook
                        || HelpMethods.isWhite(board.position.get(checkingPosition)) != board.whiteToMove)
                    return false;
                else
                    return true;
            }

            if (board.position.containsKey(checkingPosition))
                return false;

            checkingPosition += dir;
            checkingColumn = checkingPosition % 8;
            checkingRow = (int) Math.ceil((double) (checkingPosition + 1) / 8);
        }

        return true;
    }

    public static ArrayList<Integer> deleteImpossibleMoves(Board board, ArrayList<Integer> moves,int activeField) {
        ArrayList<Integer> possibleMoves = new ArrayList<>();

        int multiplier = board.whiteToMove ? -1 : 1;

        for (int i : moves) {
            Move move = new Move(board.position, activeField, i);
            HashMap<Integer, Integer> copy = (HashMap<Integer, Integer>) board.position.clone();
            board.position = makeMove(board, move);

            if (isChecked(board) == -1) {
                if (copy.get(activeField) % 8 == King && Math.abs(i - activeField) == 2) {
                    if (isChecked(board, activeField) == -1 && isChecked(board,activeField + (i - activeField) / 2) == -1 && isChecked(board, i) == -1)
                        if (!(board.position.containsKey(activeField + (8 * multiplier)) && HelpMethods.isWhite(board.position.get(activeField + (8 * multiplier))) != board.whiteToMove && board.position.get(activeField + (8 * multiplier)) % 8 == Pawn))
                            possibleMoves.add(i);
                } else
                    possibleMoves.add(i);
            }
            board.position = unMakeMove(board, move);
        }

        return possibleMoves;
    }

    public static int isChecked(Board board) {
        int position = HelpMethods.findKing(board);
        int king = board.position.get(position);

        for(int i : PIECES_ARRAY){
            if(isPieceAttackingTarget(board, i, position, king < 16))
                return 1;
        }

        return -1;
    }

    public static int isChecked(Board board, int position) {
        //int positionChecking = -1;
        int checkingPosition;

        for(int i : PIECES_ARRAY){
            if(isPieceAttackingTarget(board, i, position, board.whiteToMove))
                return 1;
        }

//        for (int i : Constants.Directions.get(2137)) {
//            checkingPosition = position + i;
//
//            if (Constants.Directions.get(2137).indexOf(i) > 7 && IsCorrect(position, i)) {
//                if (board.position.containsKey(checkingPosition)
//                        && HelpMethods.isWhite(board.position.get(checkingPosition)) != board.whiteToMove
//                        && board.position.get(checkingPosition) % 8 == Knight) {
//                    return checkingPosition;
//                } else {
//                    continue;
//                }
//            }
//
//            while (checkingPosition >= 0 && checkingPosition < Constants.Field.FIELD_SIZE
//                    && IsCorrect(position, i)) {
//
//                if (board.position.containsKey(checkingPosition) && HelpMethods.isWhite(board.position.get(checkingPosition)) == board.whiteToMove)
//                    break;
//
//                if (board.position.containsKey(checkingPosition)) {
//                    if (HelpMethods.isWhite(board.position.get(checkingPosition)) != board.whiteToMove) {
//                        if (PossibleMoves(board,position).contains(position)) {
//                            return checkingPosition;
//                        } else
//                            break;
//                    }
//                }
//
//                checkingPosition += i;
//            }
//        }

        return -1;
    }

    private static boolean isLongRangePieceAttackingTarget(Board board, int piece, int targetSquare, boolean isTargetWhite){
        ArrayList<Integer> directions = getPieceDirections(piece);
        int checkingPosition;
        for(int i = 0; i<directions.size(); i++){
            checkingPosition = targetSquare;
            while(IsCorrect(checkingPosition, directions.get(i))){
                checkingPosition += directions.get(i);
                if(!board.position.containsKey(checkingPosition)) continue;
                int foundPiece = board.position.get(checkingPosition);
                if(foundPiece < 16 == isTargetWhite) break;
                if((foundPiece%8 == piece%8 || foundPiece%8 == Queen))
                    return true;
            }
        }
        return false;
    }

    private static boolean isSpecialPieceAttackingTarget(Board board, int piece, int targetSquare, boolean isTargetWhite){
        ArrayList<Integer> directions = getPieceDirections(piece);
        int checkingPosition;
        for(int i = 0; i<directions.size(); i++){
            checkingPosition = targetSquare + directions.get(i);
            if(!IsCorrect(targetSquare, i)) continue;
            if(!board.position.containsKey(checkingPosition)) continue;
            int foundPiece = board.position.get(checkingPosition);
            if(foundPiece%8 == Knight && foundPiece < 16 != isTargetWhite)
                return true;
        }
        return false;
    }

    private static boolean isPawnAttackingTarget(Board board, int targetSquare, boolean isTargetWhite){
        ArrayList<Integer> directions = getPieceDirections(Pawn);
        int checkingPosition,m = isTargetWhite ? -1 : 1;
        for(int i = 2; i<directions.size(); i++){
            checkingPosition = targetSquare + directions.get(i) * m;
            if(!IsCorrect(targetSquare, i)) continue;
            if(!board.position.containsKey(checkingPosition)) continue;
            int piece = board.position.get(checkingPosition);
            if(piece%8 == Pawn && piece < 16 != isTargetWhite)
                return true;
        }
        return false;
    }

    private static boolean isPieceAttackingTarget(Board board, int piece, int targetSquare, boolean isTargetWhite){
        switch (piece%8){
            case Rook:
            case Bishop:
            case Queen:
                return isLongRangePieceAttackingTarget(board, piece, targetSquare, isTargetWhite);
            case King:
            case Knight:
                return isSpecialPieceAttackingTarget(board, piece, targetSquare, isTargetWhite);
            case Pawn:
                return isPawnAttackingTarget(board, targetSquare, isTargetWhite);
            default: return false;
        }
    }

    private static ArrayList<Integer> getPieceDirections(int piece){
            return Constants.Directions.get(piece%8);
    }

    private static ArrayList<Integer> PossiblePawnMoves(Board board, int position) {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(board.position.get(position));
        int mulptiplier = isWhite ? -1 : 1;
        ArrayList<Integer> directions = Constants.Directions.get(Constants.Pieces.Pawn);
        for (int i = 0; i < directions.size(); i++) {
            if (!IsCorrect(position, mulptiplier * directions.get(i))) continue;
            int pos = mulptiplier * directions.get(i) + position;
            if (i < 2 && !board.position.containsKey(pos)) {
                if (i == 0)
                    moves.add(pos);
                else if ((int) (3.5 - (float) mulptiplier * 2.5) == position / 8 && !board.position.containsKey(pos - 8 * mulptiplier)) {
                    moves.add(pos);
                }
            } else if (i > 1 && board.position.containsKey(pos) && (board.position.get(pos) < 16 != isWhite)) {
                moves.add(pos);
            } else if (i > 1 && board.getLastMove().movedPiece % 8 == Pawn && Math.abs((board.getLastMove().startField / 8) - (board.getLastMove().endField / 8)) == 2 && pos == board.getLastMove().endField + 8 * mulptiplier) {
                moves.add(pos);
            }
        }

        return moves;
    }

    private static ArrayList<Integer> specialPossibleMoves(Board board, int position, int piece) {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(board.position.get(position));

        int checkingPosition;

        for (int i : Constants.Directions.get(piece)) {
            checkingPosition = position + i;

            if (IsCorrect(position, i)) {
                if (board.position.containsKey(checkingPosition) && HelpMethods.isWhite(board.position.get(checkingPosition)) == isWhite)
                    continue;

                moves.add(checkingPosition);
            }
        }
        return moves;
    }

    private static ArrayList<Integer> allPossibleKingMoves(Board board, int position) {
        ArrayList<Integer> moves = specialPossibleMoves(board ,position, King);
        moves.addAll(addCastlingMoves(board, position));
        return moves;
    }

    public static ArrayList<Integer> canMoveToSquare(Board board, int startPosition, int endPosition, int piece) {
        ArrayList<Integer> moveList = new ArrayList<>();
        if (piece % 8 == Knight)
            for (int i : Constants.Directions.get(piece % 8)) {
                if (IsCorrect(endPosition, i) && board.visualBoard[i + endPosition] == piece && startPosition != i + endPosition) {
                    moveList.add(endPosition + i);
                }
            }
        else if (piece % 8 != King) {
            for (int i : Constants.Directions.get(piece % 8)) {
                int pos = endPosition += i;
                while (IsCorrect(pos - i, i)) {
                    if (board.visualBoard[pos] == piece) {
                        moveList.add(pos);
                        break;
                    }
                    if (board.visualBoard[pos] != 0)
                        break;
                    pos += i;
                }
            }
        }
        return moveList;
    }

    public static ArrayList<Integer> canMoveToSquare(Board board, int startPosition, int piece) {
        ArrayList<Integer> moveList = new ArrayList<>();
        if (piece % 8 == Knight)
            for (int i : Constants.Directions.get(piece % 8)) {
                if (IsCorrect(startPosition, i) && board.visualBoard[i + startPosition] == piece) {
                    moveList.add(startPosition + i);
                }
            }
        else if (piece % 8 != King) {
            for (int i : Constants.Directions.get(piece % 8)) {
                int pos = startPosition;
                while (IsCorrect(pos, i)) {
                    pos += i;
                    if (board.visualBoard[pos] == 0)
                        continue;
                    if (board.visualBoard[pos] == piece) {
                        moveList.add(pos);
                        break;
                    }
                    if (board.visualBoard[pos] != 0)
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

    public static HashMap<Integer, Integer> makeMove(Board board,Move move) {
        HashMap<Integer, Integer> boardMap = (HashMap<Integer, Integer>) board.position.clone();
        if (move.movedPiece % 8 == King && Math.abs(move.startField - move.endField) == 2) {
            //Changing rooks placement in castling
            boardMap.put((move.startField / 8) * 8 + move.startField % 8 + (move.endField - move.startField) / 2, boardMap.get((move.startField / 8) * 8 + ((move.endField % 8) / 4) * 7));
            boardMap.remove((move.startField / 8) * 8 + ((move.endField % 8) / 4) * 7);
        } else if (move.movedPiece % 8 == Pawn && move.takenPiece % 8 == Pawn && move.endField != move.takenPieceField) {
            //Removing the pawn which was taken end passant
            boardMap.remove(move.takenPieceField);
        }
        boardMap.put(move.endField, move.promotePiece == 0 ? move.movedPiece : move.promotePiece + move.movedPiece - Pawn);
        boardMap.remove(move.startField);
        return boardMap;
    }

    public static HashMap<Integer, Integer> unMakeMove(Board board, Move move) {
        HashMap<Integer, Integer> boardMap = (HashMap<Integer, Integer>) board.position.clone();
        if (move.movedPiece % 8 == King && Math.abs(move.startField - move.endField) == 2) {
            boardMap.put((move.startField / 8) * 8 + ((move.endField % 8) / 4) * 7, boardMap.get(move.startField + (move.endField - move.startField) / 2));
            boardMap.remove(move.startField + (move.endField - move.startField) / 2);
        }
        boardMap.put(move.startField, move.movedPiece);
        boardMap.remove(move.endField);
        if (move.takenPiece > 0)
            boardMap.put(move.takenPieceField, move.takenPiece);
        return boardMap;
    }

    public static int[] setCastles(Board board) {
        if (board.moves.size() == 0) return board.availableCastles;
        Move lastMove = board.moves.get(board.moves.size() - 1);
        if (lastMove.movedPiece % 8 == King) {
            for (int i = lastMove.movedPiece > 16 ? 0 : 2; i - (lastMove.movedPiece > 16 ? 0 : 2) < 2; i++) {
                if (board.availableCastles[i] == 0)
                    board.availableCastles[i] = board.moves.size();

            }
            return board.availableCastles;
        }
        if (lastMove.movedPiece == Rook) {
            for (int i = 0; i < board.availableCastles.length; i++) {
                if (board.availableCastles[i] == 0 && lastMove.startField == (7 * (i % 2)) + (i / 2) * 56) {
                    board.availableCastles[i] = board.moves.size();
                    return board.availableCastles;
                }
            }
        }


        return board.availableCastles;
    }

    public static int[] unsetCastles(Board board) {
        for (int i = 0; i < board.availableCastles.length; i++) {
            if (board.availableCastles[i] > board.moves.size())
                board.availableCastles[i] = 0;
        }
        return board.availableCastles;
    }

    public static boolean isEndgame(Board board) {
        if (!board.position.containsValue(Queen + White) && !board.position.containsValue(Queen + Black))
            return true;

        if (board.position.containsValue(Queen + White)) {
            boolean minorPieces = false;

            for (int j : board.position.values()) {
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

        if (board.position.containsValue(Queen + Black)) {
            boolean minorPieces = false;

            for (int j : board.position.values()) {
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
}