package utils;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Constants {

    public static class Field{
        public static final int FIELD_SIZE = 64;
        public static final int CIRCLE_SIZE = 32;
    }

    public static class Pieces{
        public static final int None = 0;
        public static final int King = 1;
        public static final int Pawn = 2;
        public static final int Rook = 3;
        public static final int Knight = 4;
        public static final int Bishop = 5;
        public static final int Queen = 6;

        public static final int White = 8;
        public static final int Black = 16;

        public static final char[] CHAR_PIECES= new char[]{'K', 'Q', 'B', 'N', 'R','P'};
    }

    public static final HashMap<Character, ArrayList<Integer>> Directions = new HashMap<>() {{
        put('q', new ArrayList<Integer>(Arrays.asList(8,-8,1,-1,7,-7,9,-9)));
        put('r', new ArrayList<Integer>(Arrays.asList(1,-1,8,-8)));
        put('b', new ArrayList<Integer>(Arrays.asList(7,-7,9,-9)));
        put('p', new ArrayList<Integer>(Arrays.asList(8,7,9)));
        put('k', new ArrayList<Integer>(Arrays.asList(1,-1,8,-8,7,-7,9,-9)));
        put('n', new ArrayList<Integer>(Arrays.asList(15,-15,17,-17,6,-6,10,-10)));
    }};

    public static final ArrayList<Integer> diagonalMoves = new ArrayList<>() {{
        add(7);
        add(-7);
        add(9);
        add(-9);
    }};

    public static class Boards{
        public final static String classicBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    }

    public static class Colors{
        public final static String BLACK = "Black";
        public final static String WHITE = "White";
        public final static String ACTIVE = "Active";
        public static final HashMap<String, Color> basic = new HashMap<>(){{put("White", Color.white);put("Black",new Color(133, 80, 27));put("WhiteActive", new Color(155,155,155,100)); put("BlackActive", new Color(155,155,155,155));}};
    }
}
