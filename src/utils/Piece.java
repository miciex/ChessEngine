package utils;

import GameStates.Playing;

import java.util.ArrayList;

public class Piece
{
    public static ArrayList<Integer> PossibleMoves(int position)
    {
        System.out.println(isChecked(4));

        switch(Playing.ActivePieces.get(position))
        {
            case 'P': case 'p': return PossiblePawnMoves(position);
            case 'K': case 'k': return PossibleKingMoves(position);
            case 'N': case 'n': return PossibleKnightMoves(position);
        }

        Character piece = Playing.ActivePieces.get(position);
        boolean isWhite = HelpMethods.determineColor(piece);

        int row = (int)Math.ceil((double)(position + 1) / 8);
        int column = (position) % 8;

        piece = Character.toLowerCase(piece);

        ArrayList<Integer> moves = new ArrayList<>();
        int checkingDir, checkingPosition, checkingRow, checkingColumn;

        for(int i = 0; i < Constants.Directions.get(piece).size(); i++)
        {
            checkingDir = Constants.Directions.get(piece).get(i);
            checkingPosition = position + checkingDir;
            checkingRow = (int)Math.ceil((double)(checkingPosition + 1) / 8);
            checkingColumn = checkingPosition % 8;

            while(checkingPosition >= 0 && checkingPosition < Constants.Field.FIELD_SIZE && ((checkingColumn == column || checkingRow == row) || IsDiagonal(position, checkingPosition)))
            {
                if(Constants.diagonalMoves.contains(checkingDir))
                {
                    if((column == 0 && (checkingDir == -9 || checkingDir == 7)) || (column == 7 && (checkingDir == 9 || checkingDir == -7)))
                        break;
                }

                if(Playing.ActivePieces.containsKey(checkingPosition))
                    if((HelpMethods.determineColor(Playing.ActivePieces.get(checkingPosition)) != isWhite))
                    {
                        moves.add(checkingPosition);
                        break;
                    }
                    else
                        break;

                moves.add(checkingPosition);

                if(Constants.diagonalMoves.contains(checkingDir) && (checkingColumn == 0 || checkingColumn == 7))
                    break;

                checkingPosition += checkingDir;
                checkingColumn = checkingPosition % 8;
                checkingRow = (int)Math.ceil((double)(checkingPosition + 1) / 8);
            }
        }

        return moves;
    }

    private static int isChecked(int position)
    {
        int positionChecking = -1;

        int row = (int)Math.ceil((double)(position + 1) / 8);
        int column = (position) % 8;

        boolean isWhite = HelpMethods.determineColor(Playing.ActivePieces.get(position));

        int checkingDir, checkingPosition, checkingRow, checkingColumn;

        for(int i = 0; i < Constants.Directions.get('k').size(); i++)
        {
            checkingDir = Constants.Directions.get('k').get(i);
            checkingPosition = position += checkingDir;
            checkingRow = (int)Math.ceil((double)(checkingPosition + 1) / 8);
            checkingColumn = checkingPosition % 8;

            while(checkingPosition >= 0 && checkingPosition < Constants.Field.FIELD_SIZE && ((checkingColumn == column || checkingRow == row) || IsDiagonal(position, checkingPosition)))
            {
                if(Constants.Directions.get('r').contains(checkingDir))
                    if(Playing.ActivePieces.containsKey(checkingPosition))
                        if(HelpMethods.determineColor(Playing.ActivePieces.get(checkingPosition)) != isWhite)
                        {
                            if (Character.toLowerCase(Playing.ActivePieces.get(checkingPosition)) == 'r')
                                positionChecking = checkingPosition;
                        }
                        else
                        {
                            break;
                        }

                checkingPosition += checkingDir;
                checkingColumn = checkingPosition % 8;
                checkingRow = (int)Math.ceil((double)(checkingPosition + 1) / 8);
            }
        }

        return positionChecking;
    }

    private static ArrayList<Integer> PossiblePawnMoves(int position)
    {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.determineColor(Playing.ActivePieces.get(position));

        if(isWhite == true)
        {
            if(Playing.ActivePieces.containsKey(position - 8) == false)
            {
                moves.add(position - 8);
                if (position >= 48 && position <= 55 && !Playing.ActivePieces.containsKey(position - 16))
                    moves.add(position - 16);
            }

            if(Playing.ActivePieces.containsKey(position - 9))
                if(HelpMethods.determineColor(Playing.ActivePieces.get(position - 9)) != isWhite)
                    moves.add(position - 9);

            if(Playing.ActivePieces.containsKey(position - 7))
                if(HelpMethods.determineColor(Playing.ActivePieces.get(position - 7)) != isWhite)
                    moves.add(position - 7);
        }
        else if(isWhite == false)
        {
            if(Playing.ActivePieces.containsKey(position + 8) == false)
            {
                moves.add(position + 8);
                if (position >= 8 && position <= 15 && !Playing.ActivePieces.containsKey(position + 16))
                    moves.add(position + 16);
            }

            if(Playing.ActivePieces.containsKey(position + 9))
                if(HelpMethods.determineColor(Playing.ActivePieces.get(position + 9)) != isWhite)
                    moves.add(position + 9);

            if(Playing.ActivePieces.containsKey(position + 7))
                if(HelpMethods.determineColor(Playing.ActivePieces.get(position + 7)) != isWhite)
                    moves.add(position + 7);
        }

        return moves;
    }

    private static ArrayList<Integer> PossibleKingMoves(int position)
    {
        ArrayList<Integer> moves = new ArrayList<>();

        boolean isWhite = HelpMethods.determineColor(Playing.ActivePieces.get(position));

        int checkingPosition, checkingDir;

        for(int i = 0; i < Constants.Directions.get('k').size(); i++)
        {
            checkingDir = Constants.Directions.get('k').get(i);

            checkingPosition = position + checkingDir;

            if(IsCorrect(position, checkingDir))
            {
                if(Playing.ActivePieces.containsKey(checkingPosition))
                    if(HelpMethods.determineColor(Playing.ActivePieces.get(checkingPosition)) != isWhite)
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

        boolean isWhite = HelpMethods.determineColor(Playing.ActivePieces.get(position));

        int row = (int)Math.ceil((double)(position + 1) / 8);
        int column = (position) % 8;

        int checkingPosition, checkingDir;

        for(int i = 0; i < Constants.Directions.get('n').size(); i++)
        {
            checkingDir = Constants.Directions.get('n').get(i);
            checkingPosition = position + checkingDir;

            if(IsCorrect(position, checkingDir))
            {
                if(Playing.ActivePieces.containsKey(checkingPosition))
                    if(HelpMethods.determineColor(Playing.ActivePieces.get(checkingPosition)) != isWhite)
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

    private static boolean IsDiagonal(int position, int checkingPosition)
    {
        //return ((checkingPosition + 1 - checkingPosition + 1) & 7) == ((checkingPosition + 1 >> 3) - (position + 1 >> 3));
        return ((checkingPosition - position) % 7) == 0 || ((checkingPosition - position) % 9) == 0;
    }
}
