import java.util.*;

public class Perft {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";

    // Known perft node counts for each FEN and depth
    static Map<String, Map<Integer, Long>> knownPerft = new LinkedHashMap<>();
    static {
        // 1. Starting position
        Map<Integer, Long> start = new HashMap<>();
        start.put(1, 20L);
        start.put(2, 400L);
        start.put(3, 8902L);
        start.put(4, 197281L);
        start.put(5, 4865609L);
        start.put(6, 119060324L);
        start.put(7, 3195901860L);
        knownPerft.put("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", start);

        // 2. Kiwipete
        Map<Integer, Long> kiwipete = new HashMap<>();
        kiwipete.put(1, 48L);
        kiwipete.put(2, 2039L);
        kiwipete.put(3, 97862L);
        kiwipete.put(4, 4085603L);
        kiwipete.put(5, 193690690L);
        kiwipete.put(6, 8031647685L);
        kiwipete.put(7, 34661140543L);
        knownPerft.put("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", kiwipete);

        // 3. Position 3
        Map<Integer, Long> pos3 = new HashMap<>();
        pos3.put(1, 14L);
        pos3.put(2, 191L);
        pos3.put(3, 2812L);
        pos3.put(4, 43238L);
        pos3.put(5, 674624L);
        pos3.put(6, 11030083L);
        pos3.put(7, 178633661L);
        knownPerft.put("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", pos3);

        // 4. Position 4
        Map<Integer, Long> pos4 = new HashMap<>();
        pos4.put(1, 6L);
        pos4.put(2, 264L);
        pos4.put(3, 9467L);
        pos4.put(4, 422333L);
        pos4.put(5, 15833292L);
        pos4.put(6, 706045033L);
        pos4.put(7, 23355840693L);
        knownPerft.put("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1", pos4);

        // 5. Position 5
        Map<Integer, Long> pos5 = new HashMap<>();
        pos5.put(1, 44L);
        pos5.put(2, 1486L);
        pos5.put(3, 62379L);
        pos5.put(4, 2103487L);
        pos5.put(5, 89941194L);
        pos5.put(6, 3243631744L);
        pos5.put(7, 119265043504L);
        knownPerft.put("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8", pos5);

        // 6. Position 6
        Map<Integer, Long> pos6 = new HashMap<>();
        pos6.put(1, 46L);
        pos6.put(2, 2079L);
        pos6.put(3, 89890L);
        pos6.put(4, 3894594L);
        pos6.put(5, 164075551L);
        pos6.put(6, 6923051137L);
        pos6.put(7, 287188994746L);
        knownPerft.put("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10", pos6);
    }

    public static void main(String[] args) {
        Board board = new Board();
        int maxDepth = 5;
        if (args.length > 0) {
            try {
                maxDepth = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid depth argument, using default depth of 7.");
            }
        }

        System.out.println("\n");
        String[] testPositions = knownPerft.keySet().toArray(new String[0]);
        for (String fen : testPositions) {
            board.init(fen);
            long start = System.currentTimeMillis();
            System.out.println("Testing: " + fen + "\n");
            board.printBoard();
            for (int depth = 1; depth <= maxDepth; depth++) {
                long nodes = perft(board, depth);
                Long expected = knownPerft.get(fen).get(depth);
                if (expected != null) {
                    if (nodes == expected) {
                        System.out
                                .println(ANSI_GREEN + "Perft at depth " + depth + ": " + nodes + " (OK)" + ANSI_RESET);
                    } else {
                        System.out.println(ANSI_RED + "Perft at depth " + depth + ": " + nodes +
                                " (Expected: " + expected + ")" + ANSI_RESET);
                    }
                } else {
                    System.out.println("Perft at depth " + depth + ": " + nodes);
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("Total time taken: " + (end - start) + " ms\n");
        }
    }

    public static long perft(Board board, int depth) {
        if (depth == 0)
            return 1;
        long totalNodes = 0;
        ArrayList<Move> legalMoves = board.generateLegalMoves();
        for (Move move : legalMoves) {
            board.makeMove(move);
            totalNodes += perft(board, depth - 1);
            board.undoMove();
        }
        return totalNodes;
    }
}
