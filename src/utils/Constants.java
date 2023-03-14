package utils;

import jdk.jshell.spi.SPIResolutionException;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Constants {

    public static class Game_Info {
        public final static int GAME_WIDTH = 1200;
        public final static int GAME_HEIGHT = 700;
    }

    public static class Letters{
        public static final char[] ALPHABET = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    }

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

        public static final int[] PROMOTE_PIECES = new int[]{Rook, Knight, Bishop, Queen};

        public static final char[] CHAR_PIECES= new char[]{'K', 'Q', 'B', 'N', 'R','P'};
    }

    public static final HashMap<Integer, ArrayList<Integer>> Directions = new HashMap<>() {{
        put(Pieces.Queen, new ArrayList<Integer>(Arrays.asList(8,-8,1,-1,7,-7,9,-9)));
        put(Pieces.Rook, new ArrayList<Integer>(Arrays.asList(1,-1,8,-8)));
        put(Pieces.Bishop, new ArrayList<Integer>(Arrays.asList(7,-7,9,-9)));
        put(Pieces.Pawn, new ArrayList<Integer>(Arrays.asList(8,16,7,9)));
        put(Pieces.King, new ArrayList<Integer>(Arrays.asList(1,-1,8,-8,7,-7,9,-9)));
        put(Pieces.Knight, new ArrayList<Integer>(Arrays.asList(15,-15,17,-17,6,-6,10,-10)));
        put(2137, new ArrayList<Integer>(Arrays.asList(1,-1,8,-8,7,-7,9,-9,15,-15,17,-17,6,-6,10,-10)));
    }};

    public static class Boards{
        public final static String classicBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        public final static class TestBoards{
            public final static String promotionTestingBoard = "rnb2bnr/pppPkppp/4p3/8/3P1q2/8/PPP2PPP/RNBQKBNR";
            public final static String testBoard1 = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
            public final static String testBoard2 = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2";
            public final static String testBoard3 = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8";
            public final static String testBoard4 = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1";
            public final static String testBoard5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R";
            public final static String testBoard7 = "r1Qq1k1r/pp2bppp/n1p5/8/2B5/8/PPP1NnPP/RNBQK2R";
            public final static String testBoard6 = "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1";
        }
    }

    public static String PromotionPieces[] = {"Queen", "Rook", "Knight", "Bishop"};
    public static int PromotionPiecesInts[] = {Pieces.Queen, Pieces.Rook, Pieces.Knight, Pieces.Bishop};

    public static class BoardInfo{
        public static final int BOARD_HEIGHT = 8;
        public static final int BOARD_WIDTH = 8;
    }

    public static class Colors{
        public final static String BLACK = "Black";
        public final static String WHITE = "White";
        public final static String ACTIVE = "Active";
        public static final HashMap<String, Color> basic = new HashMap<>(){{put("White", Color.white);put("Black",new Color(133, 80, 27));put("WhiteActive", new Color(155,155,155,100)); put("BlackActive", new Color(155,155,155,155));}};
    }
}
