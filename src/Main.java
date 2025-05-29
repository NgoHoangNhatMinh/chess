import board.*;

import static utils.BitUtils.print;

import bitboard.MagicBitboards;

class Main {
    public static void main(String... args) {
        // Board board = new Board();
        // board.init();
        // board.run();
        // TEST
        for (int i = 0; i < 64; i++) {
            System.out.println(i);
            print(MagicBitboards.bishopRelevantOccupancy[i]);
        }
        return;
    }
}