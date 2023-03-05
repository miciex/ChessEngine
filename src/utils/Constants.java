package utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Constants {

    public static class Field{
        public static int FIELD_SIZE = 64;
    }

    public static class Pieces{
        public static int None = 0;
        public static int King = 1;
        public static int Pawn = 2;
        public static int Rook = 3;
        public static int Knight = 4;
        public static int Bishop = 5;
        public static int Queen = 6;

        public static int White = 8;
        public static int Black = 16;

        public static char[] CHAR_PIECES= new char[]{'K', 'Q', 'B', 'N', 'R','P'};
    }

    public static HashMap<Character, ArrayList<Integer>> Directions = new HashMap<>() {{
        put('q', new ArrayList<Integer>(Arrays.asList(8,-8,1,-1,7,-7,9,-9)));
        put('r', new ArrayList<Integer>(Arrays.asList(1,-1,8,-8)));
        put('b', new ArrayList<Integer>(Arrays.asList(7,-7,9,-9)));
        put('p', new ArrayList<Integer>(Arrays.asList(8)));
        put('k', new ArrayList<Integer>(Arrays.asList(1,-1,8,-8,7,-7,9,-9)));
        put('n', new ArrayList<Integer>(Arrays.asList(15,-15,17,-17,6,-6,10,-10)));
    }};

    public static ArrayList<Integer> diagonalMoves = new ArrayList<>() {{
        add(7);
        add(-7);
        add(9);
        add(-9);
    }};

    public static class Boards{
        public static String classicBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    }
}
