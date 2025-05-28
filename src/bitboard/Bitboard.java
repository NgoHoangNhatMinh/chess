package bitboard;

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

    long[] pieces = new long[13];
    long whiteOccupany, blackOccupancy, occupany, unoccupancy;
    long[] knightAttacks = new long[64];
    long[] kingAttacks = new long[64];

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

    private void updateOccupancy() {
        whiteOccupany = pieces[0] | pieces[1] | pieces[2] | pieces[3] | pieces[4] | pieces[5];
        blackOccupancy = pieces[6] | pieces[7] | pieces[8] | pieces[9] | pieces[10] | pieces[11];
        occupany = whiteOccupany | blackOccupancy;
        unoccupancy = ~occupany;
        pieces[12] = unoccupancy;
    }

    private void initAttacks() {
        for (int i = 0; i < 64; i++) {
            knightAttacks[i] = generateKnightAttack(i);
            kingAttacks[i] = generateKingAttack(i);
        }
    }

    public static long generateKnightAttack(int square) {
        long init = 1L << square;
        long knightAttack = 0L;

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

    public static long generateKingAttack(int square) {
        long init = 1L << square;
        long kingAttack = 0L;

        kingAttack |= (init << 1) & ~A;
        kingAttack |= (init << 9) & ~A;
        kingAttack |= (init >>> 7) & ~A;

        kingAttack |= (init >>> 1) & ~H;
        kingAttack |= (init >>> 9) & ~H;
        kingAttack |= (init << 7) & ~H;

        kingAttack |= (init >>> 8) | (init << 8);

        return kingAttack;
    }

    public void removePiece(char piece, int from, boolean isWhite) {
        long fromBit = 1L << from;
        if (isWhite) {
            switch (piece) {
                case 'K':
                    pieces[5] &= ~fromBit;
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public String toString() {
        String board = "";
        for (int i = 0; i < 8; i++) {
            board += (8 - i) + "   ";
            for (int j = 0; j < 8; j++) {
                int offset = i * 8 + j;
                // System.out.println(1L << offset);
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
