public class Context {

    private Color color;

    public Context (Color color) {
        this.color = color;
    }

    public Move createRelevantMove(String line, Board board, int lineNumber) throws Exception {
        if (color == Color.WHITE) {
            return new WhiteMove(line, board, lineNumber);
        }
        else {
            return new BlackMove(line, board, lineNumber);
        }
    }

    public Move createCounterMove(String line, Board board, int lineNumber) throws Exception {
        if (color == Color.WHITE) {
            return new BlackMove(line, board, lineNumber);
        }
        else {
            return new WhiteMove(line, board, lineNumber);
        }
    }

    public String[] diagonalMove(int x, int y, int moveType) {
        if (color == Color.WHITE) {
            return WhiteMove.diagonalMove(x, y, moveType);
        }
        else {
            return BlackMove.diagonalMove(x, y, moveType);
        }
    }

    public String[] diagonalMoveIntoSource(int x, int y, int moveType) {
        if (color == Color.WHITE) {
            return WhiteMove.diagonalMoveIntoSource(x, y, moveType);
        }
        else {
            return BlackMove.diagonalMoveIntoSource(x, y, moveType);
        }
    }

    public String[] diagonalCounterMoveIntoSource(int x, int y, int moveType) {
        if (color == Color.WHITE) {
            return WhiteMove.diagonalCounterMoveIntoSource(x, y, moveType);
        }
        else {
            return BlackMove.diagonalCounterMoveIntoSource(x, y, moveType);
        }
    }

    public String[] counterCaptureXMoveOfMiddle(int x, int y, int moveType) {
        if (color == Color.WHITE) {
            return WhiteMove.counterCaptureXMoveOfMiddle(x, y, moveType);
        }
        else {
            return BlackMove.counterCaptureXMoveOfMiddle(x, y, moveType);
        }
    }

    public String[] captureXMoveOfMiddle(int x, int y, int moveType) {
        if (color == Color.WHITE) {
            return WhiteMove.captureXMoveOfMiddle(x, y, moveType);
        }
        else {
            return BlackMove.captureXMoveOfMiddle(x, y, moveType);
        }
    }
}
