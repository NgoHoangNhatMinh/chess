import java.util.ArrayList;

public class Engine {
    public static final int CHECKMATE_SCORE = 100_000;
    public static final int STALEMATE_SCORE = 0;
    public static final int[] PIECE_VALUES = { 100, 320, 330, 500, 900 };

    public static Move generateBestMove(String fen, int depth) {
        Board board = new Board();
        board.init(fen);
        return rootNegaMax(board, depth);
    }

    public static Move rootNegaMax(Board board, int depth) {
        ArrayList<Move> legalMoves = board.generateLegalMoves();
        if (legalMoves.isEmpty())
            return null;

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : legalMoves) {
            board.makeMove(move);
            int score = -negaMax(board, depth);
            board.undoMove();
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    public static int negaMax(Board board, int depth) {
        if (depth == 0 || board.isGameOver()) {
            return evaluateMaterial(board);
        }

        int maxEval = Integer.MIN_VALUE;
        for (Move move : board.generateLegalMoves()) {
            board.makeMove(move);
            int eval = -negaMax(board, depth - 1);
            board.undoMove();
            maxEval = Math.max(maxEval, eval);
        }
        return maxEval;
    }

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