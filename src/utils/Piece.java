package utils;

import GameStates.Playing;

import java.util.ArrayList;

public class Piece
{
    public static ArrayList<Integer> PossibleMoves(int position)
    {
        switch(Playing.ActivePieces.get(position)%8)
        {
            case Constants.Pieces.Pawn: return PossiblePawnMoves(position);
            case Constants.Pieces.King: return PossibleKingMoves(position);
            case Constants.Pieces.Knight: return PossibleKnightMoves(position);
        }

        int piece = Playing.ActivePieces.get(position) % 8;
        boolean isWhite = HelpMethods.isWhite(Playing.ActivePieces.get(position));

        int row = (int)Math.ceil((double)(position + 1) / 8);
        int column = (position) % 8;

        ArrayList<Integer> moves = new ArrayList<>();
        int checkingDir, checkingPosition, checkingRow, checkingColumn;

        for(int i = 0; i < Constants.Directions.get(piece).size(); i++)
        {
            checkingDir = Constants.Directions.get(piece).get(i);
            checkingPosition = position + checkingDir;
            checkingRow = (int)Math.ceil((double)(checkingPosition + 1) / 8);
            checkingColumn = checkingPosition % 8;

            while(checkingPosition >= 0 && checkingPosition < Constants.Field.FIELD_SIZE && IsCorrect(position, checkingDir))
            {
                if(Constants.Directions.get(Constants.Pieces.Rook).contains(checkingDir) && (checkingColumn != column && checkingRow != row))
                    break;

                if(Playing.ActivePieces.containsKey(checkingPosition))
                    if((HelpMethods.isWhite(Playing.ActivePieces.get(checkingPosition)) != isWhite))
                    {
                        moves.add(checkingPosition);
                        break;
                    }
                    else
                        break;

                moves.add(checkingPosition);

                if((Constants.Directions.get(Constants.Pieces.Bishop).contains(checkingDir) && (checkingColumn == 0 || checkingColumn == 7)))
                    break;

                checkingPosition += checkingDir;
                checkingColumn = checkingPosition % 8;
                checkingRow = (int)Math.ceil((double)(checkingPosition + 1) / 8);
            }
        }

        return moves;
    }

    public static int isChecked(int position)
    {
        int positionChecking = -1;

        boolean isWhite = HelpMethods.isWhite(Playing.ActivePieces.get(position));

        int checkingDir, checkingPosition;

        for(int i = 0; i < Constants.Directions.get(Constants.Pieces.King).size(); i++)
        {
            checkingDir = Constants.Directions.get(Constants.Pieces.King).get(i);
            checkingPosition = position + checkingDir;

            while(checkingPosition >= 0 && checkingPosition < Constants.Field.FIELD_SIZE && IsCorrect(position, checkingDir))
            {
                if(i > 7)
                {
                    if(Playing.ActivePieces.containsKey(checkingPosition) && HelpMethods.isWhite(Playing.ActivePieces.get(checkingPosition)) != isWhite && Playing.ActivePieces.get(checkingPosition) % 8 == Constants.Pieces.Knight)
                    {
                        positionChecking = checkingPosition;
                        break;
                    }
                    else
                    {
                        break;
                    }
                }

                if(Playing.ActivePieces.containsKey(checkingPosition) && HelpMethods.isWhite(Playing.ActivePieces.get(checkingPosition)) && PossibleMoves(checkingPosition).contains(position))
                {
                    positionChecking = checkingPosition;
                    break;
                }

                checkingPosition += checkingDir;
            }
        }

        return positionChecking;
    }

    private static ArrayList<Integer> PossiblePawnMoves(int position)
    {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(Playing.ActivePieces.get(position));

        int checkingPosition, checkingDir;

        int row = (int)Math.ceil((double)(position + 1) / 8);

        for(int i = 0; i < Constants.Directions.get(Constants.Pieces.Pawn).size(); i++)
        {
            checkingDir = isWhite ? -Constants.Directions.get(Constants.Pieces.Pawn).get(i) : Constants.Directions.get(Constants.Pieces.Pawn).get(i);
            checkingPosition = position + checkingDir;

            if(IsCorrect(position, checkingDir))
            {
                if(i > 0)
                    if(Playing.ActivePieces.containsKey(checkingPosition) && HelpMethods.isWhite(Playing.ActivePieces.get(checkingPosition)) != isWhite)
                        moves.add(checkingPosition);
                    else
                        continue;

                if(!Playing.ActivePieces.containsKey(checkingPosition))
                    moves.add(checkingPosition);
            }
        }

        if(isWhite == false && row == 2 && !Playing.ActivePieces.containsKey(position + 8) && !Playing.ActivePieces.containsKey(position + 16))
            moves.add(position + 16);
        else if(isWhite == true && row == 7 && !Playing.ActivePieces.containsKey(position - 8) && !Playing.ActivePieces.containsKey(position - 16))
            moves.add(position -16);

        return moves;
    }

    private static ArrayList<Integer> PossibleKingMoves(int position)
    {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(Playing.ActivePieces.get(position));

        int checkingPosition, checkingDir;

        for(int i = 0; i < Constants.Directions.get(Constants.Pieces.King).size(); i++)
        {
            checkingDir = Constants.Directions.get(Constants.Pieces.King).get(i);

            checkingPosition = position + checkingDir;

            if(IsCorrect(position, checkingDir))
            {
                if(Playing.ActivePieces.containsKey(checkingPosition))
                    if(HelpMethods.isWhite(Playing.ActivePieces.get(checkingPosition)) != isWhite)
                    {
                        moves.add(checkingPosition);
                        continue;
                    }
                    else
                    {
                        continue;
                    }

                moves.add(checkingPosition);
            }
        }

        return moves;
    }

    private static ArrayList<Integer> PossibleKnightMoves(int position)
    {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.isWhite(Playing.ActivePieces.get(position));

        int checkingPosition, checkingDir;

        for(int i = 0; i < Constants.Directions.get(Constants.Pieces.Knight).size(); i++)
        {
            checkingDir = Constants.Directions.get(Constants.Pieces.Knight).get(i);
            checkingPosition = position + checkingDir;

            if(IsCorrect(position, checkingDir))
            {
                if(Playing.ActivePieces.containsKey(checkingPosition))
                    if(HelpMethods.isWhite(Playing.ActivePieces.get(checkingPosition)) != isWhite)
                    {
                        moves.add(checkingPosition);
                        continue;
                    }
                    else
                    {
                        continue;
                    }

                moves.add(checkingPosition);
            }
        }

        return moves;
    }

    private static boolean IsCorrect(int position, int checkingDir)
    {
        int checkingRow = ((position) / 8) + (int)Math.round((double)checkingDir/8);
        int help = Math.abs(checkingDir % 8) > 4 ? (checkingDir > 0 ? checkingDir % 8 - 8 : 8 + checkingDir % 8) : checkingDir % 8;
        int checkingColumn = position % 8 + help;

        return checkingRow<8 && checkingRow >= 0 && checkingColumn < 8 && checkingColumn >= 0;
    }
}
