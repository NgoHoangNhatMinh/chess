class Main {
    public static void main(String... args) {
        Board board = new Board();
        // board.init();
        board.init("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        board.run();
    }
}