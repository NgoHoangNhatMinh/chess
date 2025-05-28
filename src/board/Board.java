package board;

import utils.Move;
import bitboard.Bitboard;
import java.util.Scanner;
import java.util.ArrayList;

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
        isWhite = true;
        whiteCastle = false;
        blackCastle = false;
        // Whitea and black en passant are false
        isGameOver = false;
    }

    public void run() {
        while (!isGameOver) {
            System.out.println(bitboard);

            // Inputting move
            Scanner scanner = new Scanner(System.in);
            System.out.println((isWhite ? "White" : "Black") + " to move: ");

            String moveString = scanner.nextLine();
            Move move = new Move(moveString, isWhite);

            System.out.println(move);

            // If legal move, board make move
            if (isLegalMove(move)) {
                makeMove(move);
            } else {
                // else
                System.out.println("This is not a legal move");
            }

            isGameOver = true;
        }
    }

    public boolean isLegalMove(Move move) {
        ArrayList<Move> legalMoves = generateLegalMoves();

        for (Move m : legalMoves) {
            if (m.equals(move))
                return true;
        }
        return false;
    }

    public ArrayList<Move> generateLegalMoves() {
        ArrayList<Move> moves = new ArrayList<Move>();
        return moves;
    }

    public void makeMove(Move move) {
        int from = move.from;
        int to = move.to;
        // char piece = move.piece;

        // remove piece from source
        // bitboard[piece] &= ~(1L << from);
    }
}