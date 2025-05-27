package board;

import utils.Move;
import bitboard.Bitboard;

public class Board {
    private Bitboard bitboard;
    private boolean isWhite;
    private boolean whiteCastle;
    private boolean blackCastle;
    private boolean[] whiteEnPassant = new boolean[8];
    private boolean[] blackEnPassant = new boolean[8];
    private boolean isGameOver;

    public void init() {
        bitboard = new Bitboard();
        bitboard.init();
    }

    public void run() {
        while (!isGameOver) {
            System.out.println(bitboard);

            if (isWhite) {
                Move move = new Move("a1a2"); 
                makeMove(move);
            } else {
                Move move = new Move("a1a2"); 
                makeMove(move);
            }

            isGameOver = true;
        }
    }

    public void makeMove(Move move) {
        int from = move.from;
        int to = move.to;
        char piece = move.piece;

        // remove piece from source
        bitboard.removePiece(piece, from, isWhite);
    }
}