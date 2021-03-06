package pieces;

import java.util.ArrayList;
import chess.Board;
import chess.Move;
import chess.Square;
import players.Player;

public class King extends Piece
{
    public static int[] kingTranslations = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(Player p, Square position) {
        super(p, position);
        name = "King";
        c = "k";
    }

    @Override
    public ArrayList<Move> legalMoves(Board b) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int i = 0; i < 8; i++) {
            int move = kingTranslations[i];
            Square newPos = b.translate(pos, move);

            if (newPos != null && !occupiedByFriend(newPos) && !moveEndangersKing(b, this, newPos)) {
                moves.add(new Move(b, this, newPos));
            }
        }

        moves.addAll(castlingMoves(b));

        return moves;
    }

    private ArrayList<Move> castlingMoves(Board b) {
        ArrayList<Move> moves = new ArrayList<Move>();
        if (hasMoved) {
            return moves;
        }

        // rooks are at +3 or -4
        Piece r1 = b.squares[pos.index - 4].visitor;
        Piece r2 = b.squares[pos.index + 3].visitor;
        if (r1 != null && !r1.hasMoved) {
            Move m = new Move(b, this, b.squares[pos.index - 2]);
            moves.add(m);
        }
        if (r2 != null && !r2.hasMoved) {
            Move m = new Move(b, this, b.squares[pos.index + 2]);
            moves.add(m);
        }
        return moves;
    }

    public boolean isInCheck(Board b) {
        return (threatByAnyRay(b) || threatByKnight(b) || threatByPawn(b) || threatByKing(b));
    }

    private boolean threatByKing(Board b) {
        return (enemyKingHere(b, -1) || enemyKingHere(b, 1) || enemyKingHere(b, -8) || enemyKingHere(b, 8)
                || enemyKingHere(b, -7) || enemyKingHere(b, 7) || enemyKingHere(b, -9) || enemyKingHere(b, 9));
    }

    private boolean enemyKingHere(Board b, int trans) {
        Square square = b.translate(pos, trans);
        if (enemyPieceAtPos(square, "King")) {
            return true;
        }
        return false;
    }

    private boolean enemyPieceAtPos(Square square, String piece) {
        if (square != null && square.visitor != null && square.visitor.name.equals(piece) && square.visitor.color != color) {
            return true;
        }
        return false;
    }

    private boolean threatByPawn(Board b) {
        // moving direction for white pawns: -8; black pawns: +8
        // dangerous direction for white king: -8, black king: +8
        int dir = 1;
        if (color == 0) {
            dir = -1;
        }
        Square square1 = b.translate(pos, 8 * dir - 1);
        Square square2 = b.translate(pos, 8 * dir + 1);
        if (enemyPieceAtPos(square1, "Pawn")) {
            return true;
        }
        if (enemyPieceAtPos(square2, "Pawn")) {
            return true;
        }
        return false;
    }

    private boolean threatByKnight(Board b) {
        for (int i = 0; i < 8; i++) {
            int move = Knight.knightTranslations[i];
            Square square = b.translate(pos, move);
            if (enemyPieceAtPos(square, "Knight")) {
                return true;
            }
        }
        return false;
    }

    private boolean threatByAnyRay(Board b) {
        return (threatByRay(b, true, 1, 0) || threatByRay(b, true, -1, 0) || threatByRay(b, true, 0, 1)
                || threatByRay(b, true, 0, 1) || threatByRay(b, false, 1, 1) || threatByRay(b, false, -1, -1)
                || threatByRay(b, false, 1, -1) || threatByRay(b, false, -1, 1));
    }

    private boolean threatByRay(Board b, boolean orth, int dx, int dy) {

        for (int i = 0; i < 7; i++) {
            int trans = dx + 8 * dy;
            Square target = b.translate(pos, trans);
            if (target == null) // out of bounds; we can stop here
            {
                return false;
            }
            if (target.visitor != null) {
                // TODO: string comparisons
                if (target.visitor.color != color && target.visitor.name.equals("Queen")) {
                    return true;
                } else if (target.visitor.color != color && orth && target.visitor.name.equals("Rook")) {
                    return true;
                } else if (target.visitor.color != color && !orth && target.visitor.name.equals("Bishop")) {
                    return true;
                } else // there is a non-ray piece in the way; we can stop here
                {
                    return false;
                }
            }
        }
        return false;
    }

}
