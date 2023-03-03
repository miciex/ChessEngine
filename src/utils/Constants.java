package utils;

public class Constants {

    public static class Field{
        public static int FIELD_SIZE = 32;
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
    }

    public static class Boards{
        public static String classicBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    }
}
