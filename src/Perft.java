import java.util.ArrayList;

public class Perft {
    public static void main(String[] args) {
        Board board = new Board();
        int maxDepth = 5;
        if (args.length > 0) {
            try {
                maxDepth = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid depth argument, using default depth of 5.");
            }
        }

        // TEST VARIOUS POSITIONS
        String[] testPositions = {
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", // Starting position
                "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", // Kiwipete position
                "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1",
                "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
                "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",
                "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10 ",
        };
        // STARTING POSITION
        for (String fen : testPositions) {
            board.init(fen);
            long start = System.currentTimeMillis();
            System.out.println("Testing " + fen + "\nUp to  depth: " + maxDepth);
            for (int depth = 1; depth <= maxDepth; depth++) {
                int nodes = perft(board, depth);
                System.out.println("Perft at depth " + depth + ": " + nodes);
            }
            long end = System.currentTimeMillis();
            System.out.println("Total time taken: " + (end - start) + " ms\n");
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
