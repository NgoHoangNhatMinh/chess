public class Evaluate {
    public static final int CHECKMATE_SCORE = 100_000;
    public static final int STALEMATE_SCORE = 0;
    public static final int[] PIECE_VALUES = { 100, 320, 330, 500, 900 };

    public static int evaluateMaterial(Board board) {
        int who2Move = board.isWhiteToMove() ? 1 : -1;
        if (board.isCheckmate())
            return -who2Move * CHECKMATE_SCORE;
        if (board.isStalemate())
            return STALEMATE_SCORE;

        int score = 0;
        for (int i = 0; i < 5; i++) {
            long numWhitePieces = Long.bitCount(board.getBitboard().getPieces()[i]);
            long numBlackPieces = Long.bitCount(board.getBitboard().getPieces()[i + 6]);
            score += who2Move * (numWhitePieces - numBlackPieces) * PIECE_VALUES[i];
        }
        return score;
    }
}
