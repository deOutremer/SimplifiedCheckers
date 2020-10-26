import java.util.ArrayList;

public class BlackMove extends Move{

    public BlackMove (String line, Board board, int lineNumber) throws Exception {
        super(line, board, lineNumber);
    }

    @Override
    public boolean isLegalMove() throws Exception {
        if (super.isMoveInBounds() &&
                super.doesSourceTileContainsLegalPiece(false) &&
                super.isTargetTileIsLegal() &&
                (isDiagonalForwardMove() ||
                        (isCaptureMove(false) && isKnownCaptureMove()))
        ) {
            return true;
        }
        System.out.println("line " + lineNumber + " illegal move: " + super.line);
        return false;
    }

    /**
     * create the string of a move.
     * moveType == 1 is regular
     * moveType == 2 is capture
     * @param x
     * @param y
     * @param moveType
     */
    public static String[] diagonalMove(int x, int y, int moveType) {
        String[] potentialMoves = new String[2];
        potentialMoves[0] = x + "," + y + "," + (x - moveType) + "," + (y - moveType);
        potentialMoves[1] = x + "," + y + "," + (x + moveType) + "," + (y - moveType);
        return potentialMoves;
    }

    public static String[] diagonalMoveIntoSource(int x, int y, int moveType) {
        String[] potentialMoves = new String[2];
        potentialMoves[0] = (x + moveType) + "," + (y + moveType) + "," + x + "," + y;
        potentialMoves[1] = (x - moveType) + "," + (y + moveType) + "," + x + "," + y;
        return potentialMoves;
    }

    public static String[] diagonalCounterMoveIntoSource(int x, int y, int moveType) {
        String[] potentialMoves = new String[2];
        potentialMoves[0] = (x - moveType) + "," + (y - moveType) + "," + x + "," + y;
        potentialMoves[1] = (x + moveType) + "," + (y - moveType) + "," + x + "," + y;
        return potentialMoves;
    }

    public static String[] counterCaptureXMoveOfMiddle(int x, int y, int moveType) {
        String[] potentialMoves = new String[2];
        potentialMoves[0] = (x - moveType) + "," + (y - moveType) + "," + (x + moveType) + "," + (y + moveType);
        potentialMoves[1] = (x + moveType) + "," + (y - moveType) + "," + (x - moveType) + "," + (y + moveType);
        return potentialMoves;
    }

    public static String[] captureXMoveOfMiddle(int x, int y, int moveType) {
        String[] potentialMoves = new String[2];
        potentialMoves[0] = (x + moveType) + "," + (y + moveType) + "," + (x - moveType) + "," + (y - moveType);
        potentialMoves[1] = (x - moveType) + "," + (y - moveType) + "," + (x + moveType) + "," + (y - moveType);
        return potentialMoves;
    }

    private boolean isDiagonalForwardMove() {
        return (source.y() == target.y() + 1) && ((source.x() == target.x() - 1) || (source.x() == target.x() + 1));
    }

    @Override
    protected boolean isCaptureMove(boolean isCounterCapture) {
        Color pieceToCheck = isCounterCapture ? board.getTurn() : board.getInvertedTurn();
        return (((source.y() == target.y() + 2) && (source.x() == target.x() - 2) &&
                board.getTile(target.x() - 1, target.y() + 1).getPieceColor() == pieceToCheck)
                ||
                ((source.y() == target.y() + 2) && (source.x() == target.x() + 2) &&
                        board.getTile(target.x() + 1, target.y() + 1).getPieceColor() == pieceToCheck));
    }

    protected boolean isKnownCaptureMove() {
        return board.isKnownCaptureMove(this);
    }

}