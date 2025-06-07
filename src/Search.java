public class Search {
    public static int negaMax(Board board, int depth) {
        if (depth == 0 || board.isGameOver()) {
            return Evaluate.evaluateMaterial(board);
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
}
