import bitboard.Bitboard;
import static utils.BitUtils.*;

class Main {
    public static void main(String... args) {
        Bitboard bitboard = new Bitboard();
        bitboard.init();
        // System.out.println(bitboard);
        // print(Bitboard.H);
        for (int i = 0; i < 64; i++) {
            print(Bitboard.generateKingAttack(i));
        }
        return;
    }
}