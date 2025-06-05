import java.util.ArrayList;

public class Perft {
    public static void main(String[] args) {
        Board board = new Board();
        board.init();
        for (int depth = 1; depth <= 5; depth++) {
            int nodes = perft(board, depth);
            System.out.println("Perft at depth " + depth + ": " + nodes);
        }
    }

    public static int perft(Board board, int depth) {
        if (depth == 0) {
            return 1; // Base case: reached the leaf node
        }

        int totalNodes = 0;
        ArrayList<Move> legalMoves = board.generateLegalMoves();

        for (Move move : legalMoves) {
            board.makeMove(move);
            totalNodes += perft(board, depth - 1);
            board.undoMove();
        }

        return totalNodes;
    }
}
