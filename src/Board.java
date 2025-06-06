
import java.util.Scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Board {
    private Bitboard bitboard;
    private boolean isWhite;
    private int enPassantSquare = -1;
    private int halfMovesTillDraw = 100;
    private int fullMoves = 1;
    private Map<Long, Integer> zobristMap = new HashMap<>();

    private boolean isWhiteBot = false;
    private boolean isBlackBot = !isWhiteBot;

    private Stack<Board> gameHistory = new Stack<>();

    public void init() {
        bitboard = new Bitboard();
        bitboard.init();
        isWhite = true;
    }

    public void init(String fen) {
        String[] parts = fen.trim().split("\\s+");
        if (parts.length > 6)
            throw new IllegalArgumentException("Invalid FEN string");

        bitboard = new Bitboard();
        bitboard.init(fen);
        isWhite = bitboard.isWhiteToMove;
        enPassantSquare = bitboard.enPassantSquare;

        // Update halfMoves and fullMoves
        String halfMoveStr = parts[4];
        String fullMoveStr = parts[5];
        try {
            halfMovesTillDraw = 100 - Integer.parseInt(halfMoveStr);
            fullMoves = Integer.parseInt(fullMoveStr);
        } catch (NumberFormatException e) {
            System.out.println(
                    "Invalid half move count and full move count in FEN, defaulting to half move count = 100 and full move count = 1");
            fullMoves = 1;
            halfMovesTillDraw = 100;
        }
    }

    public void run() {
        OtherUtils.clearScreen();
        Scanner scanner = new Scanner(System.in);
        while (!bitboard.isGameOver()) {
            System.out.println(bitboard);

            ArrayList<Move> legalMoves = generateLegalMoves();

            if (legalMoves.isEmpty()) {
                if (bitboard.isKingInCheck(isWhite))
                    System.out.println((isWhite ? "White" : "Black") + " is checkmated");
                else
                    System.out.println("The game is a draw by stalemate");
                break;
            }
            if (halfMovesTillDraw == 0) {
                System.out.println("The game is a draw by 50-move rule");
                break;
            }
            if (isThreefoldRepetition()) {
                System.out.println("The game is a draw by threefold repetition");
                break;
            }
            if (bitboard.isInsufficientMaterial()) {
                System.out.println("The game is a draw by insufficient material");
                break;
            }

            if (bitboard.isKingInCheck(isWhite)) {
                System.out.println((isWhite ? "White" : "Black") + " is in check");
            }

            // Inputting move
            System.out.println((isWhite ? "White" : "Black") + " to move: ");

            if (isWhite && isWhiteBot || !isWhite && isBlackBot) {
                // Bot move generation
                Move botMove = Engine.generateBestMove(legalMoves);
                System.out.println("Bot chose: " + botMove);
                makeMove(botMove);
                long hash = bitboard.zobristHash();
                zobristMap.put(hash, zobristMap.getOrDefault(hash, 0) + 1);
                // System.out.println("Zobrist Hash: " + hash + ", Count: " +
                // zobristMap.get(hash));
                continue;
            }

            String moveString = scanner.nextLine();
            Move move = new Move(moveString, isWhite);

            // clearScreen();
            System.out.println(move);

            Move selectedMove = null;
            for (Move m : legalMoves) {
                if (m.equals(new Move(moveString, isWhite))) {
                    selectedMove = m;
                }
            }
            // If legal move, board make move
            if (selectedMove != null) {
                makeMove(selectedMove);
                long hash = bitboard.zobristHash();
                zobristMap.put(hash, zobristMap.getOrDefault(hash, 0) + 1);
                // System.out.println("Zobrist Hash: " + hash + ", Count: " +
                // zobristMap.get(hash));
            } else {
                System.out.println("This is not a legal move\n");
            }
        }
        scanner.close();
    }

    public Board copy() {
        Board newBoard = new Board();
        newBoard.bitboard = bitboard.copy();
        newBoard.isWhite = isWhite;
        newBoard.enPassantSquare = enPassantSquare;
        newBoard.halfMovesTillDraw = halfMovesTillDraw;
        for (Long key : zobristMap.keySet()) {
            newBoard.zobristMap.put(key, zobristMap.get(key));
        }

        newBoard.isWhiteBot = isWhiteBot;
        newBoard.isBlackBot = isBlackBot;
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
        // Save current state for undo functionality
        gameHistory.push(this.copy());

        // Make move
        if (move.isShortCastling) {
            bitboard.shortCastle(isWhite);
        } else if (move.isLongCastling) {
            bitboard.longCastle(isWhite);
        } else {
            int from = move.from;
            int to = move.to;
            int piece = move.piece;

            if (piece == 0 || piece == 6)
                halfMovesTillDraw = 0;

            // remove piece from source
            bitboard.removePiece(piece, from);
            int capturedPiece = bitboard.getPieceAt(to);
            if (capturedPiece != -1) {
                bitboard.removePiece(capturedPiece, to);
                halfMovesTillDraw = 0;
            }
            if (move.isEnPassant) {
                int removedFrom = isWhite ? to + 8 : to - 8;
                bitboard.removePiece(isWhite ? 6 : 0, removedFrom);
            }
            if (move.promotionPiece != -1)
                bitboard.addPiece(move.promotionPiece, to);
            else
                bitboard.addPiece(piece, to);

            // Handle castling's rights
            if (piece == 3) {
                if (from == 63)
                    bitboard.canShortCastleWhite = false;
                else if (from == 56)
                    bitboard.canLongCastleWhite = false;
            } else if (piece == 9) {
                if (from == 7)
                    bitboard.canShortCastleBlack = false;
                else if (from == 0)
                    bitboard.canLongCastleBlack = false;
            } else if (piece == 5 || piece == 11)
                bitboard.disableCastle(isWhite);

            if (capturedPiece == 3) {
                if (to == 63)
                    bitboard.canShortCastleWhite = false;
                else if (to == 56)
                    bitboard.canLongCastleWhite = false;
            } else if (capturedPiece == 9) {
                if (to == 7)
                    bitboard.canShortCastleBlack = false;
                else if (to == 0)
                    bitboard.canLongCastleBlack = false;
            }

            // Check for en passant
            if (piece == 0 || piece == 6) {
                if (Math.abs(from - to) == 16) { // double move
                    enPassantSquare = isWhite ? from - 8 : from + 8; // square behind the double pawn push
                } else {
                    enPassantSquare = -1;
                }
            } else {
                enPassantSquare = -1;
            }
            bitboard.enPassantSquare = enPassantSquare;
        }

        halfMovesTillDraw--;
        if (!isWhite)
            fullMoves++;
        bitboard.updateOccupancy();
        switchPlayer();
    }

    public void undoMove() {
        if (gameHistory.isEmpty()) {
            System.out.println("No moves to undo");
            return;
        }
        Board previousBoard = gameHistory.pop();
        this.bitboard = previousBoard.bitboard.copy();
        this.isWhite = previousBoard.isWhite;
        this.enPassantSquare = previousBoard.enPassantSquare;
        this.halfMovesTillDraw = previousBoard.halfMovesTillDraw;
        this.zobristMap = new HashMap<>(previousBoard.zobristMap);
        this.isWhiteBot = previousBoard.isWhiteBot;
        this.isBlackBot = previousBoard.isBlackBot;
        bitboard.updateOccupancy();
        switchPlayer();
        isWhite = !isWhite; // Switch player back to the previous player
    }

    public boolean isThreefoldRepetition() {
        long hash = bitboard.zobristHash();
        return zobristMap.getOrDefault(hash, 0) >= 3;
    }

    public void switchPlayer() {
        isWhite = !isWhite;
        bitboard.switchPlayer();
    }
}
