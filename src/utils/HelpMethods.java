package utils;

import GameStates.GameResults;
import GameStates.Move;
import GameStates.Playing;
import utils.Constants.Pieces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.*;
import static utils.Constants.Letters.ALPHABET;
import static utils.Constants.Pieces.*;

public class HelpMethods {



    public static String gameResultToChessNotation(GameResults result, boolean whitesMove){
        if(result == GameResults.NONE) return "";
        if(result == GameResults.MATE) return whitesMove ? "0-1" : "1-0";
        return "1/2-1/2";
    }

    public static String moveToChessNotation(Move move, int[] board){
        String note = "";
        if(move.movedPiece == King && move.endField - move.startField == 2){
            note = "O-O";
        }
        else if(move.movedPiece == King && move.endField - move.startField == -2){
            note = "O-O-O";
        }else if(move.movedPiece%8 != 2){
            note+= toUpperCase(HelpMethods.intToCharPiece(move.movedPiece));
        }else if(move.movedPiece == Pawn){
            for (Map.Entry<Integer, Integer> set :
                    Playing.ActivePieces.entrySet()) {
                if(set.getValue()!= Pawn && move.takenPiece==0) continue;
                note += ALPHABET[move.startField%8];
                break;
            }
        }

        if (move.movedPiece%8 != Pawn && move.movedPiece%8 != King) {
            ArrayList<Integer> fromWhereCouldMove = Piece.canMoveToSquare(move.startField, move.endField, move.movedPiece, board);
            if(fromWhereCouldMove.size()>1) {
                boolean row = false;
                boolean col = false;
                for (int i : fromWhereCouldMove) {
                    if (i % 8 == move.startField % 8) {
                        note += i / 8;
                        row = true;
                    }else if(i/8 == move.startField/8){
                        note += ALPHABET[i%8];
                        break;
                    }
                }
                if (!row && col == true) {
                    note += ALPHABET[move.startField % 8];
                }
            }
        }

        if(move.takenPiece != 0){
            if(move.movedPiece%8 == Pawn){
                note+= ALPHABET[move.startField%8];
            }
            note += "x";

        }
            note+= fieldNumberToChessNotation(move.endField);
        if(move.promotePiece!=0){
            note += "=" + toUpperCase(HelpMethods.intToCharPiece(move.promotePiece));
        }
        if(move.gaveCheck){
            note += "+";
        }
        return note;
    }

    public static Move chessNotationToMove(String notation, int[] board, boolean whitesMove){
        char[] notationArr = notation.toCharArray();
        Move move = new Move();
        if(!notation.contains("=") && containsUpperCaseLetter(notation)){
            move.movedPiece = CharPieceToInt2(notationArr[0]) + (whitesMove ? White : Black);

            if(notation.contains("x")){
                move.endField = getLetterIndexInAlphabet(notationArr[notation.indexOf('x') + 1])  + (8-Character.getNumericValue(notationArr[notation.indexOf('x')+2])) * 8;
                move.takenPieceField = move.endField;
                move.takenPiece = board[move.takenPieceField];
            }
            else{
                move.endField = getLetterIndexInAlphabet(notationArr[1])  + (8-Character.getNumericValue(notationArr[2])) * 8;
            }

            ArrayList<Integer> moves = new ArrayList<>();
            if(move.movedPiece%8 == King){
                move.startField = findKing(whitesMove, Playing.ActivePieces);
            }else{
                moves = Piece.canMoveToSquare(move.endField, move.movedPiece, board);
            }
            if(moves.size()>1){
                if(isNumeric(Character.toString(notationArr[2]))){
                    move.startField = getLetterIndexInAlphabet(notationArr[1]) + (8-Character.getNumericValue(notationArr[2]))*8;
                }else
                    for(int i : moves){
                        if(isNumeric(Character.toString(notationArr[1])) &&8-i/8 == Character.getNumericValue(notationArr[1]) || !isNumeric(Character.toString(notationArr[1])) && i%8 == getLetterIndexInAlphabet(notationArr[1])){
                            move.startField = i;
                            break;
                        }
                    }}else if(moves.size() == 1)
                move.startField = moves.get(0);
        }else{
            move.movedPiece = Pawn + (whitesMove ? White : Black);
            if(notation.contains("x")){
                move.endField = getLetterIndexInAlphabet(notationArr[notation.indexOf('x') + 1])  + (8-Character.getNumericValue(notationArr[notation.indexOf('x')+2])) * 8;
                move.startField = ((whitesMove? move.endField +8 : move.endField - 8)/8)*8 + getLetterIndexInAlphabet(notationArr[notation.indexOf('x') - 1]);
                move.takenPiece = board[move.endField]!=0 ? board[move.endField] : Pawn;
                move.takenPieceField = board[move.endField]!=0 ? move.endField : (move.startField/8) * 8 + getLetterIndexInAlphabet(notationArr[0]);
            }else{
                int col = getLetterIndexInAlphabet(notationArr[0]);
                move.endField = col  + (8-Character.getNumericValue(notationArr[1])) * 8;
                for(int i = 0; i<8; i++){
                    if(board[i*8 + col]  == (Pawn + (whitesMove ? White : Black))) {
                        move.startField = i*8 + col;
                    }
                }

            }
            if(notation.contains("=")){
                move.promotePiece = CharPieceToInt2(notationArr[notation.indexOf('=')+1]) + (whitesMove?White:Black);
            }
        }

        move.gaveCheck = notation.contains("+") ? true : false;
        return move;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static boolean containsUpperCaseLetter(String str){
        for(char i : str.toCharArray()){
            if(isUpperCase(i)) return true;
        }
        return false;
    }

    public static String fieldNumberToChessNotation(int fieldNumber){
        String note = ALPHABET[fieldNumber%8] + Integer.toString(8-fieldNumber/8);
        return note;
    }

    public static int getLetterIndexInAlphabet(char letter){
        for(int i = 0; i<ALPHABET.length; i++){
            if(Character.toLowerCase(letter) == ALPHABET[i]) return i;
        }
        return -1;
    }

    public static int[] FenToIntArray(String fen, int arrayLength){
        int num = 0;
        int[] arr = new int[arrayLength];
        for(Character currChar : fen.toCharArray()){
            if(currChar=='/' ){
                continue;
            }

            if(isDigit(currChar)){
                int max = getNumericValue(currChar);
                for(int i = 0; i<max; i++){
                    arr[num] = Pieces.None;
                }
                num+=max;
                continue;
            }

            arr[num] = CharPieceToInt(currChar);
            num++;

        }
        return arr;
    }

    public static String IntArrayToFen(int[] arr) {
        StringBuilder sb = new StringBuilder();
        int emptyCount = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == Pieces.None) {
                emptyCount++;
            } else {
                if (emptyCount > 0) {
                    sb.append(emptyCount);
                    emptyCount = 0;
                }
                sb.append(intToCharPiece(arr[i]));
            }
            if (i % 8 == 7) {
                if (emptyCount > 0) {
                    sb.append(emptyCount);
                    emptyCount = 0;
                }
                if (i < arr.length - 1) {
                    sb.append('/');
                }
            }
        }
        return sb.toString();
    }

    public static HashMap<Integer, Integer> boardToMap(int[] board){
        HashMap<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i<board.length; i++){
            if(board[i] != 0)
                map.put(i, board[i]);
        }
        return map;
    }

    public static int[] mapToBoard(HashMap<Integer, Integer> map, int boardSize) {
        int[] board = new int[boardSize];
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            board[entry.getKey()] = entry.getValue();
        }
        return board;
    }

    public static int CharPieceToInt(Character p){
        switch (toLowerCase(p)){
            case 'q':
                return Pieces.Queen + addPieceColorValue(p);
            case 'k':
                return Pieces.King + addPieceColorValue(p);
            case 'p':
                return Pieces.Pawn + addPieceColorValue(p);
            case 'r':
                return Pieces.Rook + addPieceColorValue(p);
            case 'n':
                return Pieces.Knight + addPieceColorValue(p);
            case 'b':
                return Pieces.Bishop + addPieceColorValue(p);
            default: return 0;
        }
    }

    public static int CharPieceToInt2(Character p){
        switch (toLowerCase(p)){
            case 'q':
                return Pieces.Queen;
            case 'k':
                return Pieces.King;
            case 'p':
                return Pieces.Pawn;
            case 'r':
                return Pieces.Rook;
            case 'n':
                return Pieces.Knight;
            case 'b':
                return Pieces.Bishop;
            default: return 0;
        }
    }

    public static Character intToCharPiece(int p){
        switch (p%8){
            case 1:
                return p<16?'K':'k';
            case 2:
                return p<16?'P':'p';
            case 3:
                return p<16?'R':'r';
            case 4:
                return p<16?'N':'n';
            case 5:
                return p<16?'B':'b';
            case 6:
                return p<16?'Q':'q';
            default: return ' ';
        }
    }

    public static int addPieceColorValue(Character p){
        return (isUpperCase(p)?Pieces.White:Pieces.Black);
    }

    public static boolean isWhite(int p) {
        if(p != 0)
            return p<16 ? true : false;
        else
            return false;
    }

    public static int findKing(boolean white, HashMap<Integer, Integer> activePieces) {
        int position = -1;

        for(int i : activePieces.keySet())
        {
            if (activePieces.containsKey(i) && activePieces.get(i) % 8 == Pieces.King && HelpMethods.isWhite(activePieces.get(i)) == white)
                return i;
        }

        return position;
    }

    public static boolean isPromotionNeeded()
    {
        int row;

        for(int i : Playing.ActivePieces.keySet())
        {
            row = (int) Math.ceil((double) (i + 1) / 8);
            if(Playing.ActivePieces.get(i) % 8 == Pawn && (row == 1 || row == 8)) return true;
        }

        return false;
    }
}
