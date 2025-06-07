public class Search {
    public static int negaMax(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || board.isGameOver()) {
            return Evaluate.evaluateMaterial(board);
        }

        int maxEval = Integer.MIN_VALUE;
        for (Move move : board.generateLegalMoves()) {
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
}
