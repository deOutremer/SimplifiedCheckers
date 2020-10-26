import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

enum Color {
    WHITE,
    BLACK,
    EMPTY
}

public class Board {

    private static final int[] BOARD_DIM = {8, 8};  // Dimensions of teh board
    private static int whiteCount;                  // pieces left for first player
    private static int blackCount;                  // pieces left for second player
    private static Color turn;
    private static HashMap<String, Move> whiteCaptureList; // A list of the current known capture move
    private static HashMap<String, Move> blackCaptureList; // A list of the current known capture move
    private static Tile[][] board;
    private static HashSet<Tile> livingPieces; // A list of all the tiles currently containing pieces

    public Board() {
        whiteCount = 12;
        blackCount = 12;
        turn = Color.WHITE;
        whiteCaptureList = new HashMap<>();
        blackCaptureList = new HashMap<>();
        livingPieces = new HashSet<>();
        board = new Tile[BOARD_DIM[0]][BOARD_DIM[1]];

        for (int y = 0; y < BOARD_DIM[1]; y++ ) {
            for (int x = 0; x < BOARD_DIM[0]; x++ ) {
                setTile(x,y);
            }
            //System.out.println(" -" + y);
        }
    }

    /* This constructor is for testing purposes
    * It is used to set the game with an initial state different than the game's initial state
    * */
    public Board (String pathToReadyBoard) throws Exception {
        whiteCount = 12;
        blackCount = 12;
        turn = Color.EMPTY;
        whiteCaptureList = new HashMap<>();
        blackCaptureList = new HashMap<>();
        livingPieces = new HashSet<>();
        board = new Tile[BOARD_DIM[0]][BOARD_DIM[1]];
        BufferedReader br = new BufferedReader(new FileReader(pathToReadyBoard));
        String line;
        String[] brokenLine = new String[BOARD_DIM[0]];
        Tile tmpTile = new Tile(Color.EMPTY,Color.EMPTY,new Location(-1,-1));
        for (int y = 0; y < BOARD_DIM[1]; y++ ) {
            line = br.readLine();
            brokenLine = line.split(" ");
            for (int x = 0; x < BOARD_DIM[0]; x++ ) {
                switch (brokenLine[x]) {
                    case "O":
                        tmpTile = new Tile(Color.WHITE,Color.EMPTY,new Location(x,y));
                        break;
                    case "W":
                        tmpTile = new Tile(Color.BLACK,Color.WHITE,new Location(x,y));
                        break;
                    case "*":
                        tmpTile = new Tile(Color.BLACK,Color.EMPTY,new Location(x,y));
                        break;
                    case "B":
                        tmpTile = new Tile(Color.BLACK,Color.BLACK,new Location(x,y));
                        break;
                }
                board[x][y] = tmpTile;
//                if(tmpTile.getPieceColor() == Color.BLACK) System.out.print(" B");
//                else if(tmpTile.getPieceColor() == Color.WHITE) System.out.print(" W");
//                else if(tmpTile.getTileColor() == Color.BLACK) System.out.print(" *");
//                else if(tmpTile.getTileColor() == Color.WHITE) System.out.print(" O");
            }
//            System.out.println(" -" + y);
        }
        line = br.readLine();
        blackCaptureList.put(line, new BlackMove(line, this, -1));
        line = br.readLine();
        blackCaptureList.put(line, new BlackMove(line, this, -1));
    }

    /* We use the coordinates to calculate:
     * The color of the tile
     * The color of the piece on the tile (might also be empty
     */
    private void setTile (int x, int y) {
        Location locationOnBoard = new Location (x, y);
        Tile currentTile = new Tile(Color.EMPTY, Color.EMPTY, locationOnBoard);
        Color tmp;
        if ((x % 2 == 0) && (y % 2 == 0)) {
            currentTile = new Tile (Color.WHITE,Color.EMPTY, locationOnBoard);
        }
        else if (((x % 2 == 0) && (y % 2 != 0))) {
            if (y * 2 == BOARD_DIM[1] || (y + 1) * 2 == BOARD_DIM[1]) {
                tmp = Color.EMPTY;
            }
            else {
                if (whiteCount > 0) {
                    tmp = Color.WHITE;
                    whiteCount--;
                } else {
                    tmp = Color.BLACK;
                    blackCount--;
                }
            }
            currentTile = new Tile(Color.BLACK, tmp, locationOnBoard);
            if (tmp != Color.EMPTY) livingPieces.add(currentTile);
        }
        else if (((x % 2 != 0) && (y % 2 == 0))) {
            if (y * 2 == BOARD_DIM[1] || (y + 1) * 2 == BOARD_DIM[1]) {
                tmp = Color.EMPTY;
            }
            else {
                if (whiteCount > 0) {
                    tmp = Color.WHITE;
                    whiteCount--;
                } else {
                    tmp = Color.BLACK;
                    blackCount--;
                }
            }
            currentTile = new Tile(Color.BLACK, tmp, locationOnBoard);
            if (tmp != Color.EMPTY) livingPieces.add(currentTile);
        }
        else if (((x % 2 != 0) && (y % 2 != 0))) {
            currentTile = new Tile (Color.WHITE,Color.EMPTY, locationOnBoard);
        }
        board[x][y] = currentTile;
//        if(currentTile.getPieceColor() == Color.BLACK) System.out.print(" B");
//        else if(currentTile.getPieceColor() == Color.WHITE) System.out.print(" W");
//        else if(currentTile.getTileColor() == Color.BLACK) System.out.print(" *");
//        else if(currentTile.getTileColor() == Color.WHITE) System.out.print(" O");

    }

    public Tile getTile (int x, int y) {
        return board[x][y];
    }

    public void setTile (int x, int y, Color piece) {
        board[x][y].setPieceColor(piece);
    }

    public int[] getBoardDimensions() {
        return BOARD_DIM;
    }

    public Color getTurn() {
        return turn;
    }

    public void switchTurn() {
        turn = getInvertedTurn();
    }

    /* Checks if the given move is known */
    public boolean isKnownCaptureMove(Move move) {
        boolean contains;
        if (turn == Color.WHITE) {
            contains = whiteCaptureList.containsKey(move.line);
            if (contains) whiteCaptureList.remove(move.line);
        }
        else {
            contains = blackCaptureList.containsKey(move.line);
            if (contains) blackCaptureList.remove(move.line);
        }
        return contains;
    }

    public Color getInvertedTurn() {
        if (turn == Color.BLACK) return Color.WHITE;
        else return Color.BLACK;
    }

    public void subtractOpponentPiece() {
        if(turn == Color.BLACK) whiteCount--;
        else if (turn == Color.WHITE) blackCount--;
    }

    public void removeMoveFromCaptureList(Move move) {
        if(turn == Color.BLACK) blackCaptureList.remove(move);
        else if (turn == Color.WHITE) whiteCaptureList.remove(move);;
    }

    public void updateKnownCapturesLists(Move move) throws Exception {
        /* check if there are capture moves available at this time
        * 1. check if there are moves that start from target of current move - moves to be done by current player
        * 2. check if there are moves that capture the target of current move - moves to be made by opponent
        * 3. remove moves that captured the source of current move (the piece moved, there is nothing more to capture
        * 4. remove moves that captured from the source (and were not performed)
        * */
        ArrayList<Move> newAvailableCapturesByPlayer = move.availableCaptureMoves();
        newAvailableCapturesByPlayer.addAll(move.availableMultiCaptureMoves());
        for (Move newCaptureMove: newAvailableCapturesByPlayer ) {
            if (turn == Color.BLACK) blackCaptureList.put(newCaptureMove.line,newCaptureMove);
            else if (turn == Color.WHITE) whiteCaptureList.put(newCaptureMove.line,newCaptureMove);
        }

        ArrayList<Move> newAvailableCapturesByOpponent = move.availableCounterCaptureMoves();
        for (Move newCaptureMove: newAvailableCapturesByOpponent ) {
            if (turn == Color.BLACK) whiteCaptureList.put(newCaptureMove.line,newCaptureMove);
            else if (turn == Color.WHITE) blackCaptureList.put(newCaptureMove.line,newCaptureMove);
        }

        ArrayList<Move> captureMovesToRemoveByPlayer = move.removableCaptureMoves();
        for (Move removableCaptureMove: captureMovesToRemoveByPlayer ) {
            if (turn == Color.BLACK) blackCaptureList.remove(removableCaptureMove.line);
            else if (turn == Color.WHITE) whiteCaptureList.remove(removableCaptureMove.line);
        }

        ArrayList<Move> captureMovesToRemoveByOpponent = move.removableCounterCaptureMoves();
        for (Move removableCaptureMove: captureMovesToRemoveByOpponent ) {
            if (turn == Color.BLACK) whiteCaptureList.remove(removableCaptureMove.line);
            else if (turn == Color.WHITE) blackCaptureList.remove(removableCaptureMove.line);
        }
        //System.out.println("");
    }

    public String getWinner() {
        if (whiteCount > blackCount) return "first";
        else if (whiteCount < blackCount )return "second";
        else return "tie";
    }

    public void updatePieceLocation(Tile sourceTile, Tile targetTile) {
        removePieceFromLivingPiecesList(sourceTile);
        addPieceFromLivingPiecesList(targetTile);
    }

    private void addPieceFromLivingPiecesList(Tile targetTile) {
        livingPieces.add(targetTile);
    }

    public void removePieceFromLivingPiecesList (Tile tile){
        livingPieces.remove(tile);
    }

    /* Checks is regular moves or capture moves are still available to the current player
    * We iterate over all the pieces the current player still has on the board and see if any still have moves to do */
    public boolean noMoreMoves() throws Exception {
        boolean result = true;
        boolean turnCondition = false;
        String firstDiagonalMoveString;
        String secondDiagonalMoveString;
        Move firstDiagonalMove;
        Move secondDiagonalMove;
        Context context = new Context(turn);
        String[] potentialDiagonalMoves = new String[2];
        for (Tile livingPiece : livingPieces ) {
            if (livingPiece.getPieceColor() == Color.BLACK && turn == Color.BLACK) {
                potentialDiagonalMoves = Move.diagonalMove(turn, livingPiece.getLocation().x(), livingPiece.getLocation().y(),1);
                turnCondition = turn != Color.BLACK;
            }
            else if (livingPiece.getPieceColor() == Color.WHITE&& turn == Color.WHITE) {
                potentialDiagonalMoves = Move.diagonalMove(turn, livingPiece.getLocation().x(), livingPiece.getLocation().y(),1);
                turnCondition = turn != Color.WHITE;
            }
            if (potentialDiagonalMoves[0] != null ) {
                firstDiagonalMoveString = potentialDiagonalMoves[0];
                secondDiagonalMoveString = potentialDiagonalMoves[1];
                firstDiagonalMove = context.createRelevantMove(firstDiagonalMoveString, this, -1);
                secondDiagonalMove = context.createRelevantMove(secondDiagonalMoveString, this, -1);

                if (firstDiagonalMove.isLegalPotentialMove(turnCondition) || secondDiagonalMove.isLegalPotentialMove(turnCondition)) {
                    result = false;
                    break;
                }
            }
        }

        if (turn == Color.WHITE) return result || whiteCaptureList.isEmpty();
        return result || blackCaptureList.isEmpty();
    }

    public void removePieceFromBoard(Tile capturedPieceTile) {
        Location tileLocation = capturedPieceTile.getLocation();
        board[tileLocation.x()][tileLocation.y()] = new Tile(Color.BLACK, Color.EMPTY, tileLocation);
    }



    public void printBoard() {
        for (int y = 0; y < BOARD_DIM[1]; y++ ) {
            for (int x = 0; x < BOARD_DIM[0]; x++ ) {
                if(board[x][y].getPieceColor() == Color.BLACK) System.out.print(" B");
                else if(board[x][y].getPieceColor() == Color.WHITE) System.out.print(" W");
                else if(board[x][y].getTileColor() == Color.BLACK) System.out.print(" *");
                else if(board[x][y].getTileColor() == Color.WHITE) System.out.print(" O");
            }
            System.out.println(" -" + y);
        }
        System.out.println("");
    }


}
