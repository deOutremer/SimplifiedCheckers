

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    /* When the game encounters empty lines, it will ignore them
    * If illegal characters are in a line, the user will get alerted*/
    public static void main(String[] args) {

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            Board board = new Board();
            int lineNumber = 1;
            String line = getLine(br);
            Context context;
            boolean wasIllegalMove = false;
            /* A line is being read and processed */
            while (null != line) {
                /* We create a relevant move (black or white depending on the turn) */
                context = new Context(board.getTurn());
                Move move = context.createRelevantMove(line, board, lineNumber);

                /* An illegal move will stop the game */
                if (move.isLegalMove()) {
                    move.performMove();
                }
                else {
                    wasIllegalMove = true;
                    break;
                }
                line = getLine(br);
                if (line != null && !isNextMovePartOfMultiCapture(move, line, board, lineNumber + 1)) {
                    board.switchTurn();
                }
                lineNumber ++;
            }

            /* Once the game stopped, we analyze why */
            if (!wasIllegalMove) {
                board.switchTurn();
                if (board.noMoreMoves()) {
                    System.out.println(board.getWinner());
                } else {
                    System.out.println("incomplete game");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static String getLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        while (line != null && line.isEmpty()) {
            line = br.readLine();
        }
        return line;
    }

    public static boolean isNextMovePartOfMultiCapture(Move move, String nextLine, Board board, int lineNumber) throws Exception {

        Move nextMove = new Move(nextLine, board, lineNumber);

        return move.target.x() == nextMove.source.x() &&
                move.target.y() == nextMove.source.y();
    }
}
