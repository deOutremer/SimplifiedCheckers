import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class MoveTest {
    @Test
    public void testPerformMove() throws Exception {
        Board board = new Board("testBoard.txt");
        board.switchTurn();
        Context context = new Context(board.getTurn());
        Move  theMove = context.createRelevantMove("2,5,4,3", board, 1);
        if (theMove.isLegalMove()) {
            theMove.performMove();
        }
        ArrayList<Move> expectedPotentialMovesWhite = new ArrayList<>();
        ArrayList<Move> expectedPotentialMovesBlack = new ArrayList<>();
        ArrayList<Move> removedMoves = new ArrayList<>();
        expectedPotentialMovesWhite.add(new WhiteMove("3,2,5,4", board, 1));
        expectedPotentialMovesWhite.add(new WhiteMove("0,3,2,5", board, 1));
        expectedPotentialMovesWhite.add(new WhiteMove("5,2,3,4", board, 1));
        expectedPotentialMovesBlack.add(new BlackMove("0,7,2,5", board, 1));
        expectedPotentialMovesBlack.add(new BlackMove("4,7,2,5", board, 1));
        expectedPotentialMovesBlack.add(new BlackMove("4,3,6,1", board, 1));
        removedMoves.add(new BlackMove("2,5,4,3",board,-1));
        removedMoves.add(new BlackMove("4,5,2,3",board,-1));

        for (Move move : expectedPotentialMovesBlack) {
            Assertions.assertTrue(board.isKnownCaptureMove(move));
        }
        board.switchTurn();
        for (Move move : expectedPotentialMovesWhite) {
            Assertions.assertTrue(board.isKnownCaptureMove(move));
        }

        board.switchTurn();
        for (Move removedMove : removedMoves) {
            Assertions.assertFalse(board.isKnownCaptureMove(removedMove));
        }

    }
}
