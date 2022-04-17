package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

public abstract class ChessPiece extends Piece {
    
    private Color color;
    private int moveCount;
    
    public ChessPiece(Board board, Color color) {
        super(board);
        this.color = color;
    }
    
    protected boolean isThereOpponentPiece(Position position) {
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p != null && p.getColor() != color;
    }
    
    public int getMoveCount() {
        return this.moveCount;
    }
    
    protected void increaseMoveCount() {
        this.moveCount++;
    }
    
    protected void decreaseMoveCount() {
        this.moveCount++;
    }
    
    public Color getColor() {
        return color;
    }
    
    public ChessPosition getChessPosition() {
        return ChessPosition.fromPosition(position);
    }
}