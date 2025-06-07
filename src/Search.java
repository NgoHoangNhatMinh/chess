import java.util.ArrayList;
import java.util.Comparator;

public class Search {
    public static int negaMax(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || board.isGameOver()) {
            return Evaluate.evaluate(board);
        }

        int maxEval = Integer.MIN_VALUE;
        ArrayList<Move> legalMoves = board.generateLegalMoves();
        legalMoves.sort(new MoveComparator(board));
        for (Move move : legalMoves) {
            board.makeMove(move);
            int eval = -negaMax(board, depth - 1, -beta, -alpha);
            board.undoMove();

            maxEval = Math.max(maxEval, eval);
            alpha = Math.max(maxEval, alpha);
            if (alpha >= beta)
                break;
        }
        return maxEval;
    }

    public static class MoveComparator implements Comparator<Move> {
        private Board board;

        public MoveComparator(Board board) {
            this.board = board;
        }

        @Override
        public int compare(Move m1, Move m2) {
            // Prioritize capture
            if (m1.isCapture() && !m2.isCapture())
                return -1;
            if (!m1.isCapture() && m2.isCapture())
                return 1;

            // Prioritize promotion
            if (m1.isPromotion() && !m2.isPromotion())
                return -1;
            if (!m1.isPromotion() && m2.isPromotion())
                return 1;

            // Prioritize checks
            if (givesCheck(m1) && !givesCheck(m2))
                return -1;
            if (!givesCheck(m1) && givesCheck(m2))
                return 1;

            return 0;
        }

        private boolean givesCheck(Move move) {
            board.makeMove(move);
            boolean isCheck = board.isOpponentKingInCheck();
            board.undoMove();
            return isCheck;
        }
    }
}
