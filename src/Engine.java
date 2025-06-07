import java.util.ArrayList;

public class Engine {
    public static final int CHECKMATE_SCORE = 100_000;
    public static final int STALEMATE_SCORE = 0;

    public static Move generateBestMove(String fen, int depth) {
        Board board = new Board();
        board.init(fen);
        return generateMaximizeMaterialMove(board, depth);
    }

    public static Move generateMaximizeMaterialMove(Board board, int depth) {
        ArrayList<Move> legalMoves = board.generateLegalMoves();
        if (legalMoves.isEmpty())
            return null;

        Move bestMove = null;
        int bestEval = board.isWhiteToMove() ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : legalMoves) {
            board.makeMove(move);
            int eval = minimax(board, depth - 1, !board.isWhiteToMove());
            board.undoMove();

            if ((board.isWhiteToMove() && eval > bestEval) || (!board.isWhiteToMove() && eval < bestEval)) {
                bestEval = eval;
                bestMove = move;
            }
        }
        return bestMove;
    }

    public static int minimax(Board board, int depth, boolean maximizingPlayer) {
        if (depth == 0) {
            return evaluateMaterial(board);
        } else if (board.isGameOver()) {
            if (board.isCheckmate()) {
                return maximizingPlayer ? -CHECKMATE_SCORE : CHECKMATE_SCORE;
            } else if (board.isStalemate()) {
                return STALEMATE_SCORE;
            }
        }
        ArrayList<Move> moves = board.generateLegalMoves();

        int optimalEval = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Move move : moves) {
            board.makeMove(move);
            int eval = minimax(board, depth - 1, !maximizingPlayer);
            board.undoMove();
            optimalEval = maximizingPlayer ? Math.max(optimalEval, eval) : Math.min(optimalEval, eval);
        }
        return optimalEval;
    }

    public static int evaluateMaterial(Board board) {
        int[] pieceValues = { 100, 320, 330, 500, 900, 20000, -100, -320, -330, -500, -900, -20000 };
        int score = 0;
        for (int i = 0; i < 12; i++) {
            score += Long.bitCount(board.getBitboard().getPieces()[i]) * pieceValues[i];
        }
        return score;
    }
}