package utils;

import Board.Board;
import GameStates.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utils.Constants.Pieces.*;
import static utils.HelpMethods.findKing;

public class CheckGameResults {

    public static boolean isMate(Board board){

        for(Map.Entry<Integer, Integer> entry : board.position.entrySet()){
            if(entry.getValue() < 16 == board.whiteToMove) {
                ArrayList<Integer> squares = Piece.PossibleMoves(board, entry.getKey());
                squares = Piece.deleteImpossibleMoves(board, squares, entry.getKey());
                if (squares.size() > 0)
                    return false;
            }
        }
        if(Piece.isChecked(board) == -1)
            return false;
      return true;
    }

    public static boolean isStalemate(Board board){

        for(Map.Entry<Integer, Integer> entry : board.position.entrySet()){
            if(entry.getValue() > 16 && !board.whiteToMove || entry.getValue() < 16 && board.whiteToMove)
                if(Piece.deleteImpossibleMoves(board,Piece.PossibleMoves(board,entry.getKey()), entry.getKey()).size() > 0) return false;
        }
        if(Piece.isChecked(board) != -1) return false;
        return true;
    }

    public static boolean isThreefold(Board board){
        if(board.positions.size() < 5) return false;
        HashMap<Integer, Integer> currentPos = board.position;

        int repetitions = 0;

        f: for(HashMap<Integer, Integer> position : board.positions){
            for(Map.Entry<Integer, Integer> entry : position.entrySet()){
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

    public static boolean insufficientMaterial(Board board){
        if(board.position.size()>4) return false;
        if(board.position.size() == 2) return true;
        if(board.position.containsValue(Pawn | White) || board.position.containsValue(Pawn | Black) ) return false;
        int blackKnights = 0;
        int whiteKnights = 0;
        ArrayList<Integer> blackBishops = new ArrayList<>();
        ArrayList<Integer> whiteBishops = new ArrayList<>();
            for(Map.Entry<Integer, Integer> entry : board.position.entrySet()){
                switch (entry.getValue()){
                    case Knight | White: whiteKnights++; break;
                    case Knight | Black: blackKnights++; break;
                    case Bishop | White: whiteBishops.add(entry.getKey()); break;
                    case Bishop | Black: blackBishops.add(entry.getKey()); break;
                    default: return false;
                }
            }

            if(board.position.size() == 3 || whiteKnights == 2 || blackKnights == 2 ) return false;

            if(whiteBishops.size() == blackBishops.size()||blackKnights==whiteKnights )return true;
            if(blackBishops.size()==2 && (blackBishops.get(0) % 2 == (blackBishops.get(0) / 8) % 2) == (blackBishops.get(1) % 2 == (blackBishops.get(1) / 8) % 2)) return true;
            if(whiteBishops.size()==2 && (whiteBishops.get(0) % 2 == (whiteBishops.get(0) / 8) % 2) == (whiteBishops.get(1) % 2 == (whiteBishops.get(1) / 8) % 2)) return true;
            return false;
    }



}
