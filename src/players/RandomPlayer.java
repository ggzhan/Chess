package players;

import java.util.ArrayList;
import java.util.Random;
import chess.Board;
import chess.Move;

public class RandomPlayer extends Player
{

    public RandomPlayer(int col, String n, Board b) {
        super(col, n, b);

    }

    @Override
    public void makeMove(Board b) {
        ArrayList<Move> legalMoves = legalMoves(b);
        if (legalMoves.size() == 0) {
            System.out.println("Player " + name + " has no more legal moves and loses.");
            return;
        }
        Move randomMove = legalMoves.get(new Random().nextInt(legalMoves.size()));
        String origin = indexToStr(randomMove.originSquare.index);
        String target = indexToStr(randomMove.targetSquare.index);
        String victim;
        if (randomMove.taking) {
            victim = " taking " + randomMove.targetPiece.name;
        } else {
            victim = "";
        }
        System.out.println(randomMove.agent.name + " from " + origin + " to " + target + victim);
        b.applyMove(randomMove);

    }

    public String indexToStr(int i) {
        int x = (int)(i % 8);
        int y = (int)(i / 8);
        return "" + (x + 1) + "/" + (y + 1);

    }

}
