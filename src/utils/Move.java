package utils;

public class Move {
    public int from;
    public int to;
    public char piece;

    public Move(String move) {
        if (move.length() == 4) {
            piece = 'P'; 
            from = squareToInt(move.substring(0, 2));
            to = squareToInt(move.substring(2, 4));
        } else {
            piece = move.charAt(0);
            from = squareToInt(move.substring(1, 3));
            to = squareToInt(move.substring(3, 5));
        }
    }

    public Move(int from, int to, char piece) {
        this.from = from;
        this.to = to;
        this.piece = piece;
    }
    
    public static int squareToInt(String s) {
        char l = s.toLowerCase().charAt(0);
        char n = s.charAt(1);
        return (56 - (int) n) * 8 + (int) l - 97;
    }

    // public static String intToSquare(int s) {
    //     String square = (char) 1 + (char) 1;
    //     return (char) (s / 8) + (char) 1;
    // }
}
