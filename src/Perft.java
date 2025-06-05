import java.util.ArrayList;

public class Perft {
    public static void main(String[] args) {
        Board board = new Board();
        board.init();
        int maxDepth = 5;
        if (args.length > 0) {
            try {
                maxDepth = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid depth argument, using default depth of 5.");
            }
        }
        long start = System.currentTimeMillis();
        System.out.println("Starting perft with depth: " + maxDepth);
        for (int depth = 1; depth <= maxDepth; depth++) {
            int nodes = perft(board, depth);
            System.out.println("Perft at depth " + depth + ": " + nodes);
        }
        long end = System.currentTimeMillis();
        System.out.println("Total time taken: " + (end - start) + " ms");
        System.out.println("Perft completed.");
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
