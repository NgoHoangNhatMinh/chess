package bitboard;

import java.util.ArrayList;

import move.Move;

enum PE {
    WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK
}

public class Bitboard {
    public static final long H = 0x8080808080808080L;
    public static final long A = 0x0101010101010101L;
    public static final long GH = 0xC0C0C0C0C0C0C0C0L;
    public static final long AB = 0x0303030303030303L;
    public static final char[] GRAPHIC = { '♙', '♘', '♗', '♖', '♕', '♔', '♟', '♞', '♝', '♜', '♛', '♚', '.' };
    public static final String[] PIECES_STRINGS = { "WP", "WN", "WB", "WR", "WQ", "WK", "BP", "BN", "BB", "BR", "BQ",
            "BK" };
    public static final long RANK_4 = 0x000000FF00000000L;
    public static final long RANK_5 = 0x00000000FF000000L;

    static long[] knightAttacks = new long[64];
    static long[] kingAttacks = new long[64];
    static long[] whitePawnAttacks = new long[64];
    static long[] blackPawnAttacks = new long[64];

    long[] pieces = new long[13];
    long whiteOccupancy, blackOccupancy, occupancy, emptyOccupancy;

    public boolean canShortCastleWhite;
    public boolean canLongCastleWhite;
    public boolean canShortCastleBlack;
    public boolean canLongCastleBlack;

    public int enPassantSquare;

    private boolean isGameOver;

    static {
        initAttacks();
        MagicBitboards.init();
    }

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

        canShortCastleWhite = true;
        canLongCastleWhite = true;
        canShortCastleBlack = true;
        canLongCastleBlack = true;

        updateOccupancy();
    }

    public void init(String fen) {

    }

    public void updateOccupancy() {
        whiteOccupancy = pieces[0] | pieces[1] | pieces[2] | pieces[3] | pieces[4] | pieces[5];
        blackOccupancy = pieces[6] | pieces[7] | pieces[8] | pieces[9] | pieces[10] | pieces[11];
        occupancy = whiteOccupancy | blackOccupancy;
        emptyOccupancy = ~occupancy;
        pieces[12] = emptyOccupancy;
    }

    // Generate the loopup attacks table for knight and king
    private static void initAttacks() {
        for (int i = 0; i < 64; i++) {
            knightAttacks[i] = generateKnightAttack(i);
            kingAttacks[i] = generateKingAttack(i);
            whitePawnAttacks[i] = generateWhitePawnAttack(i);
            blackPawnAttacks[i] = generateBlackPawnAttack(i);
        }
    }

    public Bitboard copy() {
        Bitboard newBB = new Bitboard();
        for (int i = 0; i < pieces.length; i++) {
            newBB.pieces[i] = pieces[i];
        }
        return newBB;
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

    public void shortCastle(boolean isWhite) {
        int king = isWhite ? 5 : 11;
        int rook = isWhite ? 3 : 9;
        int kingFrom = isWhite ? 60 : 4;
        int kingTo = isWhite ? 62 : 6;
        int rookFrom = isWhite ? 63 : 7;
        int rookTo = isWhite ? 61 : 5;

        pieces[king] &= ~(1L << kingFrom);
        pieces[rook] &= ~(1L << rookFrom);

        pieces[king] |= (1L << kingTo);
        pieces[rook] |= (1L << rookTo);

        disableCastle(isWhite);

        updateOccupancy();
    }

    public void longCastle(boolean isWhite) {
        int king = isWhite ? 5 : 11;
        int rook = isWhite ? 3 : 9;
        int kingFrom = isWhite ? 60 : 4;
        int kingTo = isWhite ? 58 : 2;
        int rookFrom = isWhite ? 56 : 0;
        int rookTo = isWhite ? 59 : 3;

        pieces[king] &= ~(1L << kingFrom);
        pieces[rook] &= ~(1L << rookFrom);

        pieces[king] |= (1L << kingTo);
        pieces[rook] |= (1L << rookTo);

        disableCastle(isWhite);

        updateOccupancy();
    }

    public void disableCastle(boolean isWhite) {
        if (isWhite) {
            canShortCastleWhite = false;
            canLongCastleWhite = false;
        } else {
            canShortCastleBlack = false;
            canLongCastleBlack = false;
        }
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
        int currPiece = isWhite ? 0 : 6;
        long singlePushs = isWhite ? wSinglePushTargets() : bSinglePushTargets();
        long doublePushs = isWhite ? wDblPushTargets() : bDblPushTargets();

        while (singlePushs != 0) {
            int to = Long.numberOfTrailingZeros(singlePushs);
            if (isWhite) {
                if (to / 8 == 0)
                    for (int i = 1; i < 5; i++)
                        possibleMoves.add(new Move(to + 8, to, currPiece, i));
                else
                    possibleMoves.add(new Move(to + 8, to, currPiece));
            } else {
                if (to / 8 == 7)
                    for (int i = 7; i < 11; i++)
                        possibleMoves.add(new Move(to - 8, to, currPiece, i));
                else
                    possibleMoves.add(new Move(to - 8, to, currPiece));
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

        long pawns = pieces[currPiece];
        long opponentOccupancy = isWhite ? blackOccupancy : whiteOccupancy;

        while (pawns != 0) {
            int from = Long.numberOfTrailingZeros(pawns);

            if (enPassantSquare != -1) {
                if (isWhite && (from / 8 == 3) && Math.abs(enPassantSquare % 8 - from % 8) == 1) {
                    possibleMoves.add(new Move(from, enPassantSquare, 0, true));
                }
                if (!isWhite && (from / 8 == 4) && Math.abs(enPassantSquare % 8 - from % 8) == 1) {
                    possibleMoves.add(new Move(from, enPassantSquare, 6, true));
                }
            }

            long possible = isWhite ? whitePawnAttacks[from] : blackPawnAttacks[from];
            possible &= opponentOccupancy;

            while (possible != 0) {
                int to = Long.numberOfTrailingZeros(possible);
                if (isWhite) {
                    if (to / 8 == 0)
                        for (int i = 1; i < 5; i++)
                            possibleMoves.add(new Move(from, to, currPiece, i));
                    else
                        possibleMoves.add(new Move(from, to, currPiece));
                } else {
                    if (to / 8 == 7)
                        for (int i = 7; i < 11; i++)
                            possibleMoves.add(new Move(from, to, currPiece, i));
                    else
                        possibleMoves.add(new Move(from, to, currPiece));
                }
                // possibleMoves.add(new Move(from, to, currPiece));
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

    public ArrayList<Move> generateRookMoves(boolean isWhite) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        int currPiece = isWhite ? 3 : 9;
        long rooks = pieces[currPiece];
        long ownOccupancy = isWhite ? whiteOccupancy : blackOccupancy;

        while (rooks != 0) {
            int from = Long.numberOfTrailingZeros(rooks);

            long mask = MagicBitboards.rookRelevantOccupancy[from];
            long blockers = occupancy & mask;
            // Compute index for precomputed attack table:
            // https://www.chessprogramming.org/Magic_Bitboards
            // int index = (int) ((blockers * MagicBitboards.rookMagic[from]) >>> (64
            // - Long.bitCount(MagicBitboards.rookRelevantOccupancy[from])));
            // long attacks = MagicBitboards.rookAttacks[from][index];

            // compute attacks manually by scanning the board
            long attacks = MagicBitboards.computeRookAttacks(from, blockers);
            long possible = attacks & ~ownOccupancy;

            while (possible != 0) {
                int to = Long.numberOfTrailingZeros(possible);
                possibleMoves.add(new Move(from, to, currPiece));
                possible &= possible - 1;
            }
            rooks &= rooks - 1;
        }

        return possibleMoves;
    }

    public ArrayList<Move> generateBishopMoves(boolean isWhite) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        int currPiece = isWhite ? 2 : 8;
        long bishops = pieces[currPiece];
        long ownOccupancy = isWhite ? whiteOccupancy : blackOccupancy;

        while (bishops != 0) {
            int from = Long.numberOfTrailingZeros(bishops);

            long mask = MagicBitboards.bishopRelevantOccupancy[from];
            long blockers = occupancy & mask;
            // Compute index for precomputed attack table:
            // https://www.chessprogramming.org/Magic_Bitboards
            // int index = (int) ((blockers * MagicBitboards.bishopMagic[from]) >>> (64
            // - Long.bitCount(MagicBitboards.bishopRelevantOccupancy[from])));
            // long attacks = MagicBitboards.bishopAttacks[from][index];

            long attacks = MagicBitboards.computeBishopAttacks(from, blockers);
            long possible = attacks & ~ownOccupancy;

            while (possible != 0) {
                int to = Long.numberOfTrailingZeros(possible);
                possibleMoves.add(new Move(from, to, currPiece));
                possible &= possible - 1;
            }
            bishops &= bishops - 1;
        }

        return possibleMoves;
    }

    public ArrayList<Move> generateQueenMoves(boolean isWhite) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        int currPiece = isWhite ? 4 : 10;
        long queens = pieces[currPiece];
        long ownOccupancy = isWhite ? whiteOccupancy : blackOccupancy;

        while (queens != 0) {
            int from = Long.numberOfTrailingZeros(queens);

            long mask = MagicBitboards.rookRelevantOccupancy[from] | MagicBitboards.bishopRelevantOccupancy[from];
            long blockers = occupancy & mask;
            // Compute index for precomputed attack table:
            // https://www.chessprogramming.org/Magic_Bitboards
            // int index = (int) ((blockers * MagicBitboards.queenMagic[from]) >>> (64
            // - Long.bitCount(MagicBitboards.queenRelevantOccupancy[from])));
            // long attacks = MagicBitboards.queenAttacks[from][index];

            long attacks = MagicBitboards.computeRookAttacks(from, blockers)
                    | MagicBitboards.computeBishopAttacks(from, blockers);
            long possible = attacks & ~ownOccupancy;

            while (possible != 0) {
                int to = Long.numberOfTrailingZeros(possible);
                possibleMoves.add(new Move(from, to, currPiece));
                possible &= possible - 1;
            }
            queens &= queens - 1;
        }

        return possibleMoves;
    }

    public ArrayList<Move> generateCastlingMoves(boolean isWhite) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        boolean canShortCastle = isWhite ? canShortCastleWhite : canShortCastleBlack;
        boolean canLongCastle = isWhite ? canLongCastleWhite : canLongCastleBlack;

        if (canShortCastle) {
            boolean empty = isWhite
                    ? ((occupancy & ((1L << 61) | (1L << 62))) == 0)
                    : ((occupancy & ((1L << 5) | (1L << 6))) == 0);
            boolean safe = !isKingInCheck(isWhite)
                    && !isKingInCheck(isWhite, isWhite ? 61 : 5)
                    && !isKingInCheck(isWhite, isWhite ? 62 : 6);
            if (empty && safe) {
                possibleMoves.add(new Move("0-0", isWhite));
            }
        }
        if (canLongCastle) {
            boolean empty = isWhite
                    ? ((occupancy & ((1L << 59) | (1L << 58))) == 0)
                    : ((occupancy & ((1L << 3) | (1L << 2))) == 0);
            boolean safe = !isKingInCheck(isWhite)
                    && !isKingInCheck(isWhite, isWhite ? 59 : 3)
                    && !isKingInCheck(isWhite, isWhite ? 58 : 2);
            if (empty && safe) {
                possibleMoves.add(new Move("0-0-0", isWhite));
            }
        }
        return possibleMoves;
    }

    public boolean isKingInCheck(boolean isWhite) {
        int piece = isWhite ? 5 : 11;
        long king = pieces[piece];
        int from = Long.numberOfTrailingZeros(king);

        // Check all opponent pieces if they can attack king square
        long opponentPawns = isWhite ? pieces[6] : pieces[0];
        long opponentKnights = isWhite ? pieces[7] : pieces[1];
        long opponentBishops = isWhite ? pieces[8] : pieces[2];
        long opponentRooks = isWhite ? pieces[9] : pieces[3];
        long opponentQueens = isWhite ? pieces[10] : pieces[4];
        long opponentKing = isWhite ? pieces[11] : pieces[5];

        // putting the piece at the king square and attack out to opponent pieces
        long pawnAttacks = isWhite ? whitePawnAttacks[from] : blackPawnAttacks[from];
        if ((pawnAttacks & opponentPawns) != 0)
            return true;

        if ((knightAttacks[from] & opponentKnights) != 0)
            return true;

        // Check for bishop and queen's diagional
        long bishopBlockers = occupancy & MagicBitboards.bishopRelevantOccupancy[from];
        long bishopAttacks = MagicBitboards.computeBishopAttacks(from, bishopBlockers);
        if ((bishopAttacks & (opponentBishops | opponentQueens)) != 0)
            return true;

        // Check for rook and queen's vertical/horizontal movements
        long rookBlockers = occupancy & MagicBitboards.rookRelevantOccupancy[from];
        long rookAttacks = MagicBitboards.computeRookAttacks(from, rookBlockers);
        if ((rookAttacks & (opponentRooks | opponentQueens)) != 0)
            return true;

        if ((kingAttacks[from] & opponentKing) != 0)
            return true;

        return false;
    }

    public boolean isKingInCheck(boolean isWhite, int from) {
        int king = isWhite ? 5 : 11;
        long original = pieces[king];
        pieces[king] = 1L << from;
        boolean isChecked = isKingInCheck(isWhite);
        pieces[king] = original;
        return isChecked;
    }

    public boolean isGameOver() {
        return isGameOver;
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
