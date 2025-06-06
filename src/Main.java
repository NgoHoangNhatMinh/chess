class Main {
    public static void main(String... args) {
        Board board = new Board();
        board.init("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        board.run();
    }
}