package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
    
    private int turn;
    private Color currentPlayer;
    private Board board;
    
    public ChessMatch() {
        this.turn = 1;
        this.currentPlayer = Color.CYAN;
        this.board = new Board(8, 8);
        this.initialSetup();
    }
    
    public int getTurn() {
        return this.turn;
    }
    
    public Color getCurrentPlayer() {
        return this.currentPlayer;
    }
    
    private void nextTurn() {
        this.turn++;
        this.currentPlayer = currentPlayer == (Color.CYAN) ? Color.YELLOW : Color.CYAN;
    }
    
    public ChessPiece[][] getPieces() {
        
        ChessPiece[][] mat = new ChessPiece[this.board.getRows()][this.board.getColumns()];
        for (int i = 0; i < this.board.getRows(); i++) {
            for (int j = 0; j < this.board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) this.board.piece(i, j);
            }
        }
        return mat;
    }
    
    private void validateSourcePosition(Position position) {
        if (!board.thereIsAPiece(position)) {
            throw new ChessException("There is no piece on source position!");
        }
        if (this.currentPlayer != ((ChessPiece) (this.board.piece(position))).getColor()) {
            throw new ChessException("The chosen piece is not yours!");
        }
        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible moves for the chosen piece!");
        }
    }
    
    private void validateTargetPosition(Position source, Position target) {
        if (!this.board.piece(source).possibleMove(target)) {
            throw new ChessException("The chosen piece can't move to target position!");
        }
    }
    
    private Piece makeMove(Position source, Position target) {
        Piece p = this.board.removePiece(source);
        Piece capturedPiece = this.board.removePiece(target);
        this.board.placePiece(p, target);
        return capturedPiece;
    }
    
    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return this.board.piece(position).possibleMoves();
    }
    
    public ChessPiece performChessMovie(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        this.validateSourcePosition(source);
        this.validateTargetPosition(source, target);
        Piece capturedPiece = this.makeMove(source, target);
        this.nextTurn();
        return (ChessPiece) capturedPiece;
    }
    
    private void placeNewPiece(char column, int row, ChessPiece piece) {
        this.board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }
    
    private void initialSetup() {
        
        this.placeNewPiece('c', 1, new Rook(board, Color.CYAN));
        this.placeNewPiece('c', 2, new Rook(board, Color.CYAN));
        this.placeNewPiece('d', 2, new Rook(board, Color.CYAN));
        this.placeNewPiece('e', 2, new Rook(board, Color.CYAN));
        this.placeNewPiece('e', 1, new Rook(board, Color.CYAN));
        this.placeNewPiece('d', 1, new King(board, Color.CYAN));
        
        this.placeNewPiece('c', 8, new Rook(board, Color.YELLOW));
        this.placeNewPiece('d', 7, new Rook(board, Color.YELLOW));
        this.placeNewPiece('c', 7, new Rook(board, Color.YELLOW));
        this.placeNewPiece('e', 7, new Rook(board, Color.YELLOW));
        this.placeNewPiece('e', 8, new Rook(board, Color.YELLOW));
        this.placeNewPiece('d', 8, new King(board, Color.YELLOW));
        
    }
}