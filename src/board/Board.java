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
        Scanner scanner = new Scanner(System.in);
        while (!isGameOver) {
            System.out.println(bitboard);

            // Inputting move
            System.out.println((isWhite ? "White" : "Black") + " to move: ");

            String moveString = scanner.nextLine();
            Move move = new Move(moveString, isWhite);

            System.out.println(move);

            // If legal move, board make move
            if (isLegalMove(move)) {
                makeMove(move);
            } else {
                // else
                System.out.println("This is not a legal move\n");
            }
        }
        scanner.close();
    }

    public boolean isLegalMove(Move move) {
        // We generate all the potential moves first and then check if our king is
        // checked
        ArrayList<Move> potentialMoves = generateLegalMoves();

        System.out.println("These are the legal moves: ");
        for (Move m : potentialMoves) {
            System.out.println(m);
        }

        // filter moves that lead to check
        ArrayList<Move> legalMoves = potentialMoves;

        for (Move m : legalMoves) {
            if (m.equals(move))
                return true;
        }
        return false;
    }

    public ArrayList<Move> generateLegalMoves() {
        ArrayList<Move> moves = new ArrayList<Move>();

        moves.addAll(bitboard.generatePawnMoves(isWhite));
        moves.addAll(bitboard.generateKnightMoves(isWhite));
        moves.addAll(bitboard.generateBishopMoves(isWhite));
        moves.addAll(bitboard.generateRookMoves(isWhite));
        moves.addAll(bitboard.generateQueenMoves(isWhite));
        moves.addAll(bitboard.generateKingMoves(isWhite));

        return moves;
    }

    public void makeMove(Move move) {
        int from = move.from;
        int to = move.to;
        int piece = move.piece;

        // remove piece from source
        bitboard.removePiece(piece, from);
        int capturedPiece = bitboard.getPieceAt(to);
        if (capturedPiece != -1) {
            bitboard.removePiece(capturedPiece, from);
        }
        bitboard.addPiece(piece, to);
        bitboard.updateOccupancy();
        // switch player
        switchPlayer();
    }

    public void switchPlayer() {
        isWhite = !isWhite;
    }
}