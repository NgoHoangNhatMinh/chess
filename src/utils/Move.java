package utils;

import java.util.HashMap;
import bitboard.*;

public class Move {
    HashMap<String, Integer> piecesMap = new HashMap<String, Integer>();
    public int from;
    public int to;
    public int piece;
    public boolean isWhite;

    public Move(String move, boolean isWhite) {
        piecesMap.put("WP", 0);
        piecesMap.put("WN", 1);
        piecesMap.put("WB", 2);
        piecesMap.put("WR", 3);
        piecesMap.put("WQ", 4);
        piecesMap.put("WK", 5);
        piecesMap.put("BP", 6);
        piecesMap.put("BN", 7);
        piecesMap.put("BB", 8);
        piecesMap.put("BR", 9);
        piecesMap.put("BQ", 10);
        piecesMap.put("BK", 11);

        if (move.length() == 4) {
            piece = isWhite ? piecesMap.get("WP") : piecesMap.get("BP");
            from = toNum(move.substring(0, 2));
            to = toNum(move.substring(2, 4));
        } else {
            if (isWhite) {
                switch (move.toUpperCase().charAt(0)) {
                    case 'N':
                        piece = piecesMap.get("WN");
                        break;
                    case 'B':
                        piece = piecesMap.get("WB");
                        break;
                    case 'R':
                        piece = piecesMap.get("WR");
                        break;
                    case 'Q':
                        piece = piecesMap.get("WQ");
                        break;
                    case 'K':
                        piece = piecesMap.get("WK");
                        break;
                    default:
                        break;
                }
            } else {
                switch (move.toUpperCase().charAt(0)) {
                    case 'N':
                        piece = piecesMap.get("BN");
                        break;
                    case 'B':
                        piece = piecesMap.get("BB");
                        break;
                    case 'R':
                        piece = piecesMap.get("BR");
                        break;
                    case 'Q':
                        piece = piecesMap.get("BQ");
                        break;
                    case 'K':
                        piece = piecesMap.get("BK");
                        break;
                    default:
                        break;
                }
            }
            from = toNum(move.substring(1, 3));
            to = toNum(move.substring(3, 5));
        }

        this.isWhite = isWhite;

    }

    public Move(int from, int to, char piece) {
        this.from = from;
        this.to = to;
        this.piece = piece;
    }

    public static int toNum(String s) {
        char l = s.toLowerCase().charAt(0);
        char n = s.charAt(1);
        return (56 - (int) n) * 8 + (int) l - 97;
    }

    public static String toSquare(int s) {
        char row = (char) ('1' + (7 - s / 8));
        char col = (char) ('a' + s % 8);
        return "" + col + row;
    }

    @Override
    public String toString() {
        return Bitboard.PIECES_STRINGS[piece] + " from " + toSquare(from) + " to " + toSquare(to);
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }
}
