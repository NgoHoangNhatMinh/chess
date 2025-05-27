package board;

import bitboard.MagicBitboards;
import utils.BitUtils;

enum PE {
    WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK
}

public class Board {
    char[] GRAPHIC = {'♙', '♘', '♗', '♖', '♕', '♔', '♟', '♞', '♝', '♜', '♛', '♚', '.'}; 
    long[] pieces = new long[13];
    long whiteOccupany, blackOccupancy, occupany, unoccupancy;

    public void init() {
        pieces[0] = 0x00FF000000000000L;
        pieces[1] = 0x4200000000000000L;
        pieces[2] = 0x2400000000000000L;
        pieces[3] = 0x8100000000000000L;
        pieces[4] = 0x0800000000000000L;
        pieces[5] = 0x1000000000000000L;

        pieces[6] = 0x000000000000FF00L;
        pieces[7] = 0x0000000000000042L;
        pieces[8] = 0x0000000000000024L;
        pieces[9] = 0x0000000000000081L;
        pieces[10] = 0x0000000000000008L;    
        pieces[11] = 0x0000000000000010L;
        updateOccupancy();
    }

    private void updateOccupancy() {
        whiteOccupany = pieces[0] | pieces[1] | pieces[2] | pieces[3] | pieces[4] | pieces[5];
        blackOccupancy = pieces[6] | pieces[7] | pieces[8] | pieces[9] | pieces[10] | pieces[11];
        occupany = whiteOccupany | blackOccupancy;
        unoccupancy = ~occupany;
        pieces[12] = unoccupancy;
    }
    
    @Override
    public String toString() {
        String board = "";
        for (int i = 0; i < 8; i++) {
            board += (8 - i) + "   ";
            for (int j = 0; j < 8; j++) {
                int offset = i * 8 + j;
                // System.out.println(1L << offset);
                for (int k = 0; k < 13; k++) {
                    if ((pieces[k] & (1L << offset)) != 0) {
                        board += GRAPHIC[k] + " ";
                        break;
                    }
                }
                
            }
            board += "\n";
        }
        board += "\n    a b c d e f g h ";
        return board;
    }
}