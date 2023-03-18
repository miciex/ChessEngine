package utils;

import GameStates.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utils.Constants.Pieces.*;
import static utils.HelpMethods.findKing;

public class CheckGameResults {

    public static boolean isMate(HashMap<Integer, Integer> pieces, boolean whitesMove, Move lastMove, int[] possibleCastles){
        for(Map.Entry<Integer, Integer> entry : pieces.entrySet()){
            if(entry.getValue() > 16 && !whitesMove || entry.getValue() < 16 && whitesMove)
                if(Piece.deleteImpossibleMoves(entry.getKey(),Piece.PossibleMoves(entry.getKey(), pieces, lastMove, whitesMove, possibleCastles),pieces, whitesMove, lastMove, possibleCastles).size() > 0) return false;
        }
      return Piece.isChecked(pieces, whitesMove, lastMove, possibleCastles) != -1;
    }

    public static boolean isStalemate(HashMap<Integer, Integer> pieces, boolean whitesMove, Move lastMove, int[] possibleCastles){
        for(Map.Entry<Integer, Integer> entry : pieces.entrySet()){
            if(entry.getValue() > 16 && !whitesMove || entry.getValue() < 16 && whitesMove)
                if(Piece.deleteImpossibleMoves(entry.getKey(),Piece.PossibleMoves(entry.getKey(), pieces, lastMove, whitesMove, possibleCastles),pieces, whitesMove, lastMove, possibleCastles).size() > 0) return false;
        }
        return Piece.isChecked(pieces, whitesMove, lastMove, possibleCastles) == -1;
    }

    public static boolean isThreefold(ArrayList<HashMap<Integer, Integer>> boards){

        HashMap<Integer, Integer> currentPos = boards.get(boards.size()-1);

        int repetitions = 0;

        f: for(HashMap<Integer, Integer> board : boards){
            for(Map.Entry<Integer, Integer> entry : board.entrySet()){
                if(!currentPos.containsKey(entry.getKey()) || currentPos.get(entry.getKey()) != entry.getValue()) continue f;
            }
            repetitions++;
            if(repetitions == 3){
                return true;
            }
        }
        return false;
    }

    public static int draw50MoveRuleCheck(Move move, int movesAmount){
        if(move.movedPiece == Pawn || move.takenPiece != 0) return 0;
        return ++movesAmount;
    }

    public static boolean draw50MoveRule(int movesAmount){
        if(movesAmount == 100)
            return true;
        return false;
    }

    public static boolean insufficientMaterial(HashMap<Integer, Integer> pieces){
        if(pieces.size()>4) return false;
        if(pieces.size() == 2) return true;
        if(pieces.containsValue(Pawn + White) || pieces.containsValue(Pawn + Black) ) return false;
        int blackKnights = 0;
        int whiteKnights = 0;
        ArrayList<Integer> blackBishops = new ArrayList<>();
        ArrayList<Integer> whiteBishops = new ArrayList<>();
            for(Map.Entry<Integer, Integer> entry : pieces.entrySet()){
                switch (entry.getValue()){
                    case Knight + White: whiteKnights++; break;
                    case Knight + Black: blackKnights++; break;
                    case Bishop + White: whiteBishops.add(entry.getKey()); break;
                    case Bishop + Black: blackBishops.add(entry.getKey()); break;
                    default: return false;
                }
            }

            if(pieces.size() == 3 || whiteKnights == 2 || blackKnights == 2 ) return false;

            if(whiteBishops.size() == blackBishops.size()||blackKnights==whiteKnights )return true;
            if(blackBishops.size()==2 && (blackBishops.get(0) % 2 == (blackBishops.get(0) / 8) % 2) == (blackBishops.get(1) % 2 == (blackBishops.get(1) / 8) % 2)) return true;
            if(whiteBishops.size()==2 && (whiteBishops.get(0) % 2 == (whiteBishops.get(0) / 8) % 2) == (whiteBishops.get(1) % 2 == (whiteBishops.get(1) / 8) % 2)) return true;
            return false;
    }



}
