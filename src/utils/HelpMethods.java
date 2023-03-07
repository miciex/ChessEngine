package utils;

import GameStates.Move;
import GameStates.Playing;
import utils.Constants.Pieces;

import java.util.HashMap;

import static java.lang.Character.*;
import static utils.Constants.Letters.ALPHABET;

public class HelpMethods {

    public static String moveToChessNotation(Move move){
        String note = "";
        if(move.movedPiece%8 != 2){
            note+= toUpperCase(HelpMethods.intToCharPiece(move.movedPiece));
        }

        if(move.takenPiece != 0){
            note += fieldNumberToChessNotation(move.startField);
            note += "x";
        }
            note+= fieldNumberToChessNotation(move.endField);

        return note;
    }

    public static String fieldNumberToChessNotation(int fieldNumber){
        String note = Character.toString(ALPHABET[fieldNumber%8]) + Integer.toString(8-fieldNumber/8);
        return note;
    }

    public static int[] FenToIntArray(String fen, int arrayLength){
        Playing.ActivePieces.clear();
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
            Playing.ActivePieces.put(num, arr[num]);
            num++;

        }
        return arr;
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

    public static Character intToPieceWithoutColor(int p){
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

    public static boolean isWhite(int p)
    {
        if(p != 0)
            return p<16 ? true : false;
        else
            return false;
    }

    public static int findKing(boolean white)
    {
        int position = -1;

        for(int i : Playing.ActivePieces.keySet())
        {
            if (Playing.ActivePieces.get(i) % 8 == Pieces.King && HelpMethods.isWhite(Playing.ActivePieces.get(i)) == white)
                return i;
        }

        return position;
    }
}
