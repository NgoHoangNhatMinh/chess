package board;

import utils.Move;
import bitboard.Bitboard;
import java.util.Scanner;
import java.util.ArrayList;
import static utils.OtherUtils.*;

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
        clearScreen();
        Scanner scanner = new Scanner(System.in);
        while (!isGameOver) {
            System.out.println(bitboard);

            // Inputting move
            System.out.println((isWhite ? "White" : "Black") + " to move: ");

            String moveString = scanner.nextLine();
            Move move = new Move(moveString, isWhite);

            clearScreen();
            System.out.println(move);

            // If legal move, board make move
            if (isLegalMove(move)) {
                makeMove(move);
            } else {
                System.out.println("This is not a legal move\n");
                System.out.println("These are the legal moves\n");
                for (Move m : generateLegalMoves()) {
                    System.out.println(m);
                }
            }
        }
        scanner.close();
    }

    public Board copy() {
        Board newBoard = new Board();
        newBoard.bitboard = bitboard.copy();
        newBoard.isWhite = isWhite;
        newBoard.whiteCastle = whiteCastle;
        newBoard.blackCastle = blackCastle;
        newBoard.isGameOver = isGameOver;
        newBoard.whiteEnPassant = whiteEnPassant.clone();
        newBoard.blackEnPassant = this.blackEnPassant.clone();
        return newBoard;
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
        // pseudoMoves have not accounted for king being checked after the moves are
        // made
        ArrayList<Move> pseudoMoves = new ArrayList<Move>();

        pseudoMoves.addAll(bitboard.generatePawnMoves(isWhite));
        pseudoMoves.addAll(bitboard.generateKnightMoves(isWhite));
        pseudoMoves.addAll(bitboard.generateBishopMoves(isWhite));
        pseudoMoves.addAll(bitboard.generateRookMoves(isWhite));
        pseudoMoves.addAll(bitboard.generateQueenMoves(isWhite));
        pseudoMoves.addAll(bitboard.generateKingMoves(isWhite));
        pseudoMoves.addAll(bitboard.generateCastlingMoves(isWhite));

        ArrayList<Move> legalMoves = new ArrayList<Move>();

        for (Move move : pseudoMoves) {
            Board boardCopy = this.copy();
            boardCopy.makeMove(move);
            if (!boardCopy.bitboard.isKingInCheck(isWhite))
                legalMoves.add(move);
        }

        return legalMoves;
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