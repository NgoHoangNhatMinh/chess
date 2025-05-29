package bitboard;

public class MagicBitboards {
    public static long[] bishopRelevantOccupancy = new long[64];
    public static long[] rookRelevantOccupancy = new long[64];
    public static long[][] rookAttacks;
    public static long[][] bishopAttacks;
    public static long[] rookMagic;
    public static long[] bishopMagic;

    static {
        for (int i = 0; i < 64; i++) {
            bishopRelevantOccupancy[i] = generateBishopRelevantOccupancy(i);
            rookRelevantOccupancy[i] = generateRookRelevantOccupancy(i);
        }
    }

    private static long generateRookRelevantOccupancy(int i) {
        long relevantOccupancy = 0L;
        int r = i / 8;
        int c = i % 8;
        for (int j = c + 1; j < 7; j++)
            relevantOccupancy |= 1L << (8 * r + j);
        for (int j = c - 1; j > 0; j--)
            relevantOccupancy |= 1L << (8 * r + j);
        for (int j = r + 1; j < 7; j++)
            relevantOccupancy |= 1L << (8 * j + c);
        for (int j = r - 1; j > 0; j--)
            relevantOccupancy |= 1L << (8 * j + c);
        return relevantOccupancy;
    }

    private static long[] generateRookBlockers() {
        long[] blockers = new long[1];
        return blockers;
    }

    public static int rookAttacks(int square) {
        return 0;
    }

    private static long generateBishopRelevantOccupancy(int i) {
        long relevantOccupancy = 0L;
        int r = i / 8;
        int c = i % 8;
        for (int j = r + 1, k = c + 1; j < 7 & k < 7; j++, k++)
            relevantOccupancy |= 1l << (8 * j + k);
        for (int j = r + 1, k = c - 1; j < 7 & k > 0; j++, k--)
            relevantOccupancy |= 1l << (8 * j + k);
        for (int j = r - 1, k = c + 1; j > 0 & k < 7; j--, k++)
            relevantOccupancy |= 1l << (8 * j + k);
        for (int j = r - 1, k = c - 1; j > 0 & k > 0; j--, k--)
            relevantOccupancy |= 1l << (8 * j + k);
        return relevantOccupancy;
    }

    private static long[] generateBishopBlockers() {
        long[] blockers = new long[1];
        return blockers;
    }
}