import java.util.ArrayList;

public class Move {

    protected Location source;
    protected Location target;
    protected int lineNumber;
    protected Board board;
    protected String line;

    public Move (String line, Board board, int lineNumber) throws Exception {
        this.line = line;
        String[] moveAsString = line.split(",");
        if (moveAsString.length != 4 ) {
            throw new Exception("line " + lineNumber + " illegal move: " + line);
        }

        try {
            source = new Location(Integer.parseInt(moveAsString[0]), Integer.parseInt(moveAsString[1]));
            target = new Location(Integer.parseInt(moveAsString[2]), Integer.parseInt(moveAsString[3]));
        } catch (Exception e) {
            throw new Exception("line " + lineNumber + " illegal move: " + line);
        }
        this.lineNumber = lineNumber;
        this.board = board;
    }

    protected boolean isLegalPotentialMove(boolean isCounterCapture) {
        return (isMoveInBounds() &&
                doesSourceTileContainsLegalPiece(isCounterCapture) &&
                isTargetTileIsLegal());
    }

    public boolean isLegalMove() throws Exception {
            return true;
    }

    protected boolean isMoveInBounds() {
        int[] boardDimensions = board.getBoardDimensions();
        return source.x() >= 0 &&
                source.y() >= 0 &&
                source.x() < boardDimensions[0] &&
                source.y() < boardDimensions[1] &&
                target.x() >= 0 &&
                target.y() >= 0 &&
                target.x() < boardDimensions[0] &&
                target.y() < boardDimensions[1];
    }

    protected boolean doesSourceTileContainsLegalPiece(boolean isCounterCapture) {
        Color tileColor = board.getTile(source.x(), source.y()).getTileColor();
        Color pieceInTile = board.getTile(source.x(), source.y()).getPieceColor();
        Color pieceToCheck = isCounterCapture ? board.getInvertedTurn() : board.getTurn();
        return tileColor == Color.BLACK && pieceInTile == pieceToCheck;
    }

    protected boolean isTargetTileIsLegal() {
        Color pieceInTile = board.getTile(target.x(), target.y()).getPieceColor();
        Color tileColor = board.getTile(target.x(), target.y()).getTileColor();
        return pieceInTile == Color.EMPTY && tileColor == Color.BLACK;
    }

    protected boolean isKnownCaptureMove() {
        return board.isKnownCaptureMove(this);
    }

    public static String[] diagonalMove(Color turn, int x, int y, int moveType) {
        if (turn == Color.WHITE) return WhiteMove.diagonalMove(x, y, moveType);
        else return BlackMove.diagonalMove(x, y, moveType);
    }

    public void performMove() throws Exception {
        Tile sourceTile = board.getTile(source.x(), source.y());
        board.setTile(source.x(), source.y(), Color.EMPTY);
        board.setTile(target.x(), target.y(), board.getTurn());
        Tile targetTile = board.getTile(target.x(), target.y());
        board.updatePieceLocation(sourceTile, targetTile);
//        System.out.println("");
//        System.out.println(lineNumber + " - " + line + " - " + board.getTurn());
//        System.out.println(" 0 1 2 3 4 5 6 7");

        if(isCaptureMove(false)) {
            Tile capturedPieceTile = getCapturedPieceTile();
            board.subtractOpponentPiece();
            board.removePieceFromBoard(capturedPieceTile);
            board.removePieceFromLivingPiecesList(capturedPieceTile);
            board.removeMoveFromCaptureList(this);
        }
//        board.printBoard();
        board.updateKnownCapturesLists(this);
    }

    protected Tile getCapturedPieceTile() {
        int newX = (source.x() + target.x()) / 2;
        int newY = (source.y() + target.y()) / 2;

        return board.getTile(newX, newY);
    }

    protected boolean isCaptureMove(boolean isCounterCapture){ return true; }

    public ArrayList<Move> filterAvailableCaptureMove(ArrayList<Move> potentialMoves, boolean isCounterCapture ){
        ArrayList<Move> availableMoves = new ArrayList<>();
        for (Move move : potentialMoves) {
            if (move.isLegalPotentialMove(isCounterCapture)) {
                availableMoves.add(move);
            }
        }
        return availableMoves;
    }

    public ArrayList<Move> filterRemovableCaptureMove(ArrayList<Move> potentialMoves, boolean isCounterCapture ){
        ArrayList<Move> availableMoves = new ArrayList<>();
        for (Move move : potentialMoves) {
            if (move.isLegalPotentialMove(isCounterCapture)) {
                availableMoves.add(move);
            }
        }
        return availableMoves;
    }

    /* the bellow functions build a list of potential capture moves that might reflect the
     * status of the board after the last play
     * if the move actually exists on the board, it will be recorded in the relevant player's list
     * if the move was on a list, but is not relevant anymore after the last play, it will be removed */
    public ArrayList<Move> availableCaptureMoves () throws Exception {
        Context context = new Context(board.getTurn());
        String[] potentialDiagonalMoves = context.diagonalMoveIntoSource(source.x(), source.y(), 2);
        ArrayList<Move> availableCaptureMoves = new ArrayList<>();
        availableCaptureMoves.add(context.createRelevantMove(potentialDiagonalMoves[0], board, lineNumber));
        availableCaptureMoves.add(context.createRelevantMove(potentialDiagonalMoves[1], board, lineNumber));

        return filterAvailableCaptureMove(availableCaptureMoves, false);
    }

    public ArrayList<Move> availableMultiCaptureMoves () throws Exception {
        Context context = new Context(board.getTurn());
        String[] potentialDiagonalMoves = context.diagonalMove(target.x(), target.y(), 2);
        ArrayList<Move> availableCaptureMoves = new ArrayList<>();
        availableCaptureMoves.add(context.createRelevantMove(potentialDiagonalMoves[0], board, lineNumber));
        availableCaptureMoves.add(context.createRelevantMove(potentialDiagonalMoves[1], board, lineNumber));

        return filterAvailableCaptureMove(availableCaptureMoves, false);
    }

    public ArrayList<Move> availableCounterCaptureMoves () throws Exception {
        Context context = new Context(board.getTurn());
        ArrayList<Move> availableCaptureMoves = new ArrayList<>();
        String[] potentialDiagonalMovesIntoSource = context.diagonalCounterMoveIntoSource(source.x(), source.y(), 2);
        String[] potentialDiagonalMovesAroundTarget = context.counterCaptureXMoveOfMiddle(target.x(), target.y(), 1);
        availableCaptureMoves.add(context.createCounterMove(potentialDiagonalMovesAroundTarget[0], board, lineNumber));
        availableCaptureMoves.add(context.createCounterMove(potentialDiagonalMovesAroundTarget[1], board, lineNumber));
        availableCaptureMoves.add(context.createCounterMove(potentialDiagonalMovesIntoSource[0], board, lineNumber));
        availableCaptureMoves.add(context.createCounterMove(potentialDiagonalMovesIntoSource[1], board, lineNumber));

        return filterAvailableCaptureMove(availableCaptureMoves,true);
    }

    public ArrayList<Move> removableCaptureMoves () throws Exception {
        Context context = new Context(board.getTurn());
        String[] potentialDiagonalMoves = context.diagonalMove(source.x(), source.y(), 2);
        String[] potentialDiagonalMovesAroundCaptured = context.captureXMoveOfMiddle((source.x()+target.x())/2, (source.y()+target.y())/2, 1);
        ArrayList<Move> availableRemovableCaptureMoves = new ArrayList<>();
        availableRemovableCaptureMoves.add(new BlackMove(potentialDiagonalMoves[0], board, lineNumber));
        availableRemovableCaptureMoves.add(new BlackMove(potentialDiagonalMoves[1], board, lineNumber));
        availableRemovableCaptureMoves.add(new BlackMove(potentialDiagonalMovesAroundCaptured[0], board, lineNumber));
        availableRemovableCaptureMoves.add(new BlackMove(potentialDiagonalMovesAroundCaptured[1], board, lineNumber));

        return filterRemovableCaptureMove(availableRemovableCaptureMoves, false);
    }

    public ArrayList<Move> removableCounterCaptureMoves () throws Exception {
        Context context = new Context(board.getTurn());
        String[] potentialDiagonalMoves = context.counterCaptureXMoveOfMiddle(source.x(), source.y(), 1);
        ArrayList<Move> availableRemovableCaptureMoves = new ArrayList<>();
        availableRemovableCaptureMoves.add(context.createCounterMove(potentialDiagonalMoves[0], board, lineNumber));
        availableRemovableCaptureMoves.add(context.createCounterMove(potentialDiagonalMoves[1], board, lineNumber));

        return filterRemovableCaptureMove(availableRemovableCaptureMoves, true);
    }

}



class Location {
    int x;
    int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public String toString() {
        return x + "," + y;
    }
}

