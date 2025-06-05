import java.util.ArrayList;

public class Engine {
    public static Move generateBestMove(ArrayList<Move> legalMoves) {
        return generateRandomMove(legalMoves);
    }

    private static Move generateRandomMove(ArrayList<Move> legalMoves) {
        if (legalMoves.isEmpty()) {
            return null; // No legal moves available
        }
        int randomIndex = (int) (Math.random() * legalMoves.size());
        return legalMoves.get(randomIndex);
    }
}