package bitboard;

import java.util.ArrayList;
import utils.Move;
import static utils.BitUtils.*;

enum PE {
    WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK
}

public class Bitboard {
    public static final long H = 0x8080808080808080L;
    public static final long A = 0x0101010101010101L;
    public static final long GH = 0xC0C0C0C0C0C0C0C0L;
    public static final long AB = 0x0303030303030303L;
    public static final char[] GRAPHIC = { '♙', '♘', '♗', '♖', '♕', '♔', '♟', '♞', '♝', '♜', '♛', '♚', '.' };
    public static final String[] PIECES_STRINGS = { "WP", "WN", "WB", "WR", "WQ", "WK", "BP", "BN", "BB", "BQ", "BK" };
    public static final long RANK_4 = 0x000000FF00000000L;
    public static final long RANK_5 = 0x00000000FF000000L;

    long[] pieces = new long[13];
    long whiteOccupancy, blackOccupancy, occupancy, emptyOccupancy;
    long[] knightAttacks = new long[64];
    long[] kingAttacks = new long[64];
    long[] whitePawnAttacks = new long[64];
    long[] blackPawnAttacks = new long[64];
    boolean isWhite = true;

    public void init() {
        pieces[0] = 0x00FF000000000000L;
        pieces[1] = 0x4200000000000000L;
        pieces[2] = 0x2400000000000000L;
        pieces[3] = 0x8100000000000000L;
        pieces[4] = 0x0800000000000000L;
        pieces[5] = 0x1000000000000000L;

        pieces[6] = 0x000000000000FF00L;
        pieces[7] = 0x0000000000000042L;
        pieces[8] = 0x0000000000000024L;
        pieces[9] = 0x0000000000000081L;
        pieces[10] = 0x0000000000000008L;
        pieces[11] = 0x0000000000000010L;
        updateOccupancy();

        initAttacks();
    }

    public void updateOccupancy() {
        whiteOccupancy = pieces[0] | pieces[1] | pieces[2] | pieces[3] | pieces[4] | pieces[5];
        blackOccupancy = pieces[6] | pieces[7] | pieces[8] | pieces[9] | pieces[10] | pieces[11];
        occupancy = whiteOccupancy | blackOccupancy;
        emptyOccupancy = ~occupancy;
        pieces[12] = emptyOccupancy;
    }

    // Generate the loopup attacks table for knight and king
    private void initAttacks() {
        for (int i = 0; i < 64; i++) {
            knightAttacks[i] = generateKnightAttack(i);
            kingAttacks[i] = generateKingAttack(i);
            whitePawnAttacks[i] = generateWhitePawnAttack(i);
            blackPawnAttacks[i] = generateBlackPawnAttack(i);
        }
    }

    // Generate the possible knight attacks on 1 of the 64 squares
    public static long generateKnightAttack(int square) {
        long init = 1L << square;
        long knightAttack = 0L;

        // Check if the attack doesn't wrap around to the opposite side of the board
        knightAttack |= (init >>> 6) & ~AB;
        knightAttack |= (init >>> 10) & ~GH;
        knightAttack |= (init >>> 17) & ~H;
        knightAttack |= (init >>> 15) & ~A;

        knightAttack |= (init << 6) & ~GH;
        knightAttack |= (init << 10) & ~AB;
        knightAttack |= (init << 17) & ~A;
        knightAttack |= (init << 15) & ~H;

        return knightAttack;
    }

    // Generate the possible king attacks on 1 of the 64 squares
    public static long generateKingAttack(int square) {
        long init = 1L << square;
        long kingAttack = 0L;

        // Check if the attack doesn't wrap around to the opposite side of the board
        kingAttack |= (init << 1) & ~A;
        kingAttack |= (init << 9) & ~A;
        kingAttack |= (init >>> 7) & ~A;

        kingAttack |= (init >>> 1) & ~H;
        kingAttack |= (init >>> 9) & ~H;
        kingAttack |= (init << 7) & ~H;

        kingAttack |= (init >>> 8) | (init << 8);

        return kingAttack;
    }

    public static long generateWhitePawnAttack(int square) {
        long init = 1L << square;
        long pawnAttack = 0L;

        pawnAttack |= (init >>> 7) & ~A;
        pawnAttack |= (init >>> 9) & ~H;

        return pawnAttack;
    }

    public static long generateBlackPawnAttack(int square) {
        long init = 1L << square;
        long pawnAttack = 0L;

        pawnAttack |= (init << 7) & ~H;
        pawnAttack |= (init << 9) & ~A;

        return pawnAttack;
    }

    public void removePiece(int piece, int from) {
        pieces[piece] &= ~(1L << from);
    }

    public void addPiece(int piece, int to) {
        pieces[piece] |= (1L << to);
    }

    public int getPieceAt(int to) {
        for (int i = 0; i < pieces.length; i++) {
            if ((pieces[i] & (1L << to)) != 0) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Move> generateKnightMoves(boolean isWhite) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        int currPiece = isWhite ? 1 : 7;
        long knights = pieces[currPiece];
        long ownOccupancy = isWhite ? whiteOccupancy : blackOccupancy;

        // Get the leading 1s in the knights biboard
        while (knights != 0) {
            int from = Long.numberOfTrailingZeros(knights);
            long possible = knightAttacks[from] & ~ownOccupancy;

            while (possible != 0) {
                int to = Long.numberOfTrailingZeros(possible);
                possibleMoves.add(new Move(from, to, currPiece));

                possible &= possible - 1;
            }

            knights &= knights - 1;
        }

        return possibleMoves;
    }

    public ArrayList<Move> generateKingMoves(boolean isWhite) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        int currPiece = isWhite ? 5 : 11;
        long kings = pieces[currPiece];
        long ownOccupancy = isWhite ? whiteOccupancy : blackOccupancy;

        while (kings != 0) {
            int from = Long.numberOfTrailingZeros(kings);
            long possible = kingAttacks[from] & ~ownOccupancy;

            while (possible != 0) {
                int to = Long.numberOfTrailingZeros(possible);
                possibleMoves.add(new Move(from, to, currPiece));

                possible &= possible - 1;
            }

            kings &= kings - 1;
        }

        return possibleMoves;
    }

    public ArrayList<Move> generatePawnMoves(boolean isWhite) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        long singlePushs = isWhite ? wSinglePushTargets() : bSinglePushTargets();
        long doublePushs = isWhite ? wDblPushTargets() : bDblPushTargets();

        while (singlePushs != 0) {
            int to = Long.numberOfTrailingZeros(singlePushs);
            if (isWhite) {
                possibleMoves.add(new Move(to + 8, to, 0));
            } else {
                possibleMoves.add(new Move(to - 8, to, 6));
            }
            singlePushs &= singlePushs - 1;
        }

        while (doublePushs != 0) {
            int to = Long.numberOfTrailingZeros(doublePushs);
            if (isWhite) {
                possibleMoves.add(new Move(to + 16, to, 0));
            } else {
                possibleMoves.add(new Move(to - 16, to, 6));
            }
            doublePushs &= doublePushs - 1;
        }

        int currPiece = isWhite ? 0 : 6;
        long pawns = pieces[currPiece];
        long opponentOccupancy = isWhite ? blackOccupancy : whiteOccupancy;

        while (pawns != 0) {
            int from = Long.numberOfTrailingZeros(pawns);
            long possible = isWhite ? whitePawnAttacks[from] : blackPawnAttacks[from];
            possible &= opponentOccupancy;

            while (possible != 0) {
                int to = Long.numberOfTrailingZeros(possible);
                possibleMoves.add(new Move(from, to, currPiece));
                possible &= possible - 1;
            }
            pawns &= pawns - 1;
        }

        return possibleMoves;
    }

    public long wSinglePushTargets() {
        return (pieces[0] >>> 8) & emptyOccupancy;
    }

    public long wDblPushTargets() {
        long singlePushs = wSinglePushTargets();
        return (singlePushs >>> 8) & emptyOccupancy & RANK_4;
    }

    public long bSinglePushTargets() {
        return (pieces[6] << 8) & emptyOccupancy;
    }

    public long bDblPushTargets() {
        long singlePushs = bSinglePushTargets();
        return (singlePushs << 8) & emptyOccupancy & RANK_5;
    }

    public void switchPlayer() {
        isWhite = !isWhite;
    }

    @Override
    public String toString() {
        String board = "";
        for (int i = 0; i < 8; i++) {
            board += (8 - i) + "   ";
            for (int j = 0; j < 8; j++) {
                int offset = i * 8 + j;
                for (int k = 0; k < 13; k++) {
                    if ((pieces[k] & (1L << offset)) != 0) {
                        board += GRAPHIC[k] + " ";
                        break;
                    }
                }
            }
            board += "\n";
        }
        board += "\n    a b c d e f g h ";
        return board;
    }
}
