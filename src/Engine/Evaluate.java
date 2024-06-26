package Engine;

import Board.Board;
import GameStates.Move;
import GameStates.Playing;
import utils.Constants;
import utils.HelpMethods;

import java.util.ArrayList;
import java.util.Map;

import static utils.Constants.Pieces.*;
import static utils.Constants.Pieces.King;
import static utils.HelpMethods.getPieceValue;

public class Evaluate {

    public static int evaluate(Board board, ArrayList<Move> moves) {
        int eval = 0, multiplier = 1;

        for (Map.Entry<Integer, Integer> entry : board.position.entrySet()) {
            eval += entry.getValue() < 16 ? getPieceValue(entry.getValue()) : -getPieceValue(entry.getValue());
        }

        for(int i : board.position.keySet())
        {
            multiplier = board.position.get(i) < 16 ? 1 : -1;
            if(board.position.get(i) == Pawn && board.position.containsKey(i + 8 * -multiplier) && board.position.get(i + 8 * -multiplier) == (Pawn + (multiplier == 1 ? White : Black)))
                eval -= 50 * multiplier;
        }

        eval += evaluateBonus(board, moves);

        return eval;
    }

    private static int evaluateBonus(Board board, ArrayList<Move> moves) {
        int eval = 0;

        for (Move move : moves) {
            int multiplier = move.movedPiece < 16 ? 1 : -1;
            int moved = move.movedPiece % 8;

            if (moved == King && Math.abs(move.endField - move.startField) != 2)
                eval -= (50 * multiplier);
            else if (moved == King && Math.abs(move.endField - move.startField) == 2)
                eval += (50 * multiplier);

            if (board.moves.size() <= 10) {
                if ((moved == King && Math.abs(move.endField - move.startField) != 2) || moved == Rook || moved == Queen)
                    eval -= (50 * multiplier);

                if (board.movedPieces[move.startField] != 0)
                   eval -= (20 * multiplier);

                if (board.moves.size() - moves.size() <= 2 && moved == Knight)
                    eval -= (100 * multiplier);
            }

            if (Playing.isEndgame) {
                if (moved == King) {
                    if(multiplier == 1)
                        eval += Constants.Heatmaps.kingEndgame[0][move.endField];
                    else if(multiplier == -1)
                        eval -= Constants.Heatmaps.kingEndgame[1][move.endField];
                } else {
                    if (multiplier == 1)
                        eval += Constants.Heatmaps.Whites[moved - 1][move.endField];
                    else if (multiplier == -1)
                        eval -= Constants.Heatmaps.Blacks[moved - 1][move.endField];
                }

                eval += endgameEval(board, multiplier);
            } else if (moved != King) {
                if (multiplier == 1)
                    eval += Constants.Heatmaps.Whites[moved - 1][move.endField];
                else if (multiplier == -1)
                    eval -= Constants.Heatmaps.Blacks[moved - 1][move.endField];
            }

            eval += evaluatePawnStructure(board, move, multiplier);
        }

        return eval;
    }

    private static int evaluatePawnStructure(Board board, Move move, int multiplier)
    {
        int eval = 0, color = multiplier == 1 ? 8 : 16;

        for(int i : Constants.Engine.pawnStructureGeneralDirections)
        {
            if(board.position.containsKey(move.endField + i) && board.position.get(move.endField + i) == Pawn + color) {
                eval += 10 * multiplier;
                continue;
            }

            if(move.movedPiece % 8 == Pawn && (i / Math.abs(i) == -multiplier) && board.position.containsKey(move.endField + i) && ((board.position.get(move.endField + i) < 16) == (multiplier == 1)))
                eval += 10 * multiplier;
        }

        return eval;
    }

    private static int endgameEval(Board board, int multiplier) {
        boolean isWhite = multiplier == 1;

        int opponentKing = HelpMethods.findKing(board.position, !isWhite);
        int king = HelpMethods.findKing(board.position, isWhite);

        int eval = 0;

        int opponentKingRow = (int) Math.ceil((double) (opponentKing + 1) / 8);
        int opponentKingColumn = opponentKing % 8;

        int kingRow = (int) Math.ceil((double) (king + 1) / 8);
        int kingColumn = king % 8;

        int opponentDistanceToCentreColumn = Math.max(3 - opponentKingColumn, opponentKingColumn - 4);
        int opponentDistanceToCentreRow = Math.max(3 - opponentKingColumn, opponentKingColumn - 4);
        int opponentDistanceFromCentre = opponentDistanceToCentreColumn + opponentDistanceToCentreRow;
        eval += opponentDistanceFromCentre * 100 * multiplier;

        int distanceBetweenColumns = Math.abs(kingColumn - opponentKingColumn);
        int distanceBetweenRows = Math.abs(kingRow - opponentKingRow);
        int distanceBetweenKings = distanceBetweenColumns + distanceBetweenRows;
        eval += (14 - distanceBetweenKings) * 10 * multiplier;

        return eval;
    }
}
