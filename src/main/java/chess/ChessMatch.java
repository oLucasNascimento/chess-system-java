package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {
    
    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;
    private boolean checkMate;
    private List<Piece> piecesOnTheBoard;
    private List<Piece> capturedPieces;
    
    public ChessMatch() {
        this.piecesOnTheBoard = new ArrayList<>();
        this.capturedPieces = new ArrayList<>();
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
    
    public boolean getCheck() {
        return this.check;
    }
    
    public boolean getCheckMate() {
        return this.checkMate;
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
        ChessPiece p = (ChessPiece) this.board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = this.board.removePiece(target);
        if (capturedPiece != null) {
            this.piecesOnTheBoard.remove(capturedPiece);
            this.capturedPieces.add(capturedPiece);
        }
        this.board.placePiece(p, target);
        
        // #SpecialMove Castling Kingside Rook
        if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
            Position targetT = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }
        
        // #SpecialMove Castling Queenside Rook
        if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }
        
        return capturedPiece;
    }
    
    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece p = (ChessPiece) this.board.removePiece(target);
        p.decreaseMoveCount();
        this.board.placePiece(p, source);
        
        if (capturedPiece != null) {
            this.board.placePiece(capturedPiece, target);
            this.capturedPieces.remove(capturedPiece);
            this.piecesOnTheBoard.add(capturedPiece);
        }
        
        // #SpecialMove Castling Kingside Rook
        if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
            Position targetT = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(targetT);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }
        
        // #SpecialMove Castling Queenside Rook
        if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(targetT);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }
        
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
        
        if (this.testCheck(this.currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check!");
        }
        
        this.check = this.testCheck(opponent(currentPlayer));
        
        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        } else {
            this.nextTurn();
        }
        return (ChessPiece) capturedPiece;
    }
    
    private Color opponent(Color color) {
        return (color == Color.CYAN) ? Color.YELLOW : Color.CYAN;
    }
    
    private ChessPiece king(Color color) {
        List<Piece> list = this.piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list) {
            if (p instanceof King) {
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There is no " + color + " king on the board!");
    }
    
    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = this.piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }
        return false;
    }
    
    private boolean testCheckMate(Color color) {
        if (!testCheck(color)) {
            return false;
        }
        List<Piece> list = this.piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            for (int i = 0; i < this.board.getRows(); i++) {
                for (int j = 0; j < this.board.getColumns(); j++) {
                    if ((mat[i][j])) {
                        Position source = ((ChessPiece) p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = this.testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if (!testCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private void placeNewPiece(char column, int row, ChessPiece piece) {
        this.board.placePiece(piece, new ChessPosition(column, row).toPosition());
        this.piecesOnTheBoard.add(piece);
    }
    
    private void initialSetup() {
        
        placeNewPiece('a', 1, new Rook(board, Color.CYAN));
        placeNewPiece('b', 1, new Knight(board, Color.CYAN));
        placeNewPiece('c', 1, new Bishop(board, Color.CYAN));
        placeNewPiece('d', 1, new Queen(board, Color.CYAN));
        placeNewPiece('e', 1, new King(board, Color.CYAN, this));
        placeNewPiece('f', 1, new Bishop(board, Color.CYAN));
        placeNewPiece('g', 1, new Knight(board, Color.CYAN));
        placeNewPiece('h', 1, new Rook(board, Color.CYAN));
        placeNewPiece('a', 2, new Pawn(board, Color.CYAN));
        placeNewPiece('b', 2, new Pawn(board, Color.CYAN));
        placeNewPiece('c', 2, new Pawn(board, Color.CYAN));
        placeNewPiece('d', 2, new Pawn(board, Color.CYAN));
        placeNewPiece('e', 2, new Pawn(board, Color.CYAN));
        placeNewPiece('f', 2, new Pawn(board, Color.CYAN));
        placeNewPiece('g', 2, new Pawn(board, Color.CYAN));
        placeNewPiece('h', 2, new Pawn(board, Color.CYAN));
        
        placeNewPiece('a', 8, new Rook(board, Color.YELLOW));
        placeNewPiece('b', 8, new Knight(board, Color.YELLOW));
        placeNewPiece('c', 8, new Bishop(board, Color.YELLOW));
        placeNewPiece('d', 8, new Queen(board, Color.YELLOW));
        placeNewPiece('e', 8, new King(board, Color.YELLOW, this));
        placeNewPiece('f', 8, new Bishop(board, Color.YELLOW));
        placeNewPiece('g', 8, new Knight(board, Color.YELLOW));
        placeNewPiece('h', 8, new Rook(board, Color.YELLOW));
        placeNewPiece('a', 7, new Pawn(board, Color.YELLOW));
        placeNewPiece('b', 7, new Pawn(board, Color.YELLOW));
        placeNewPiece('c', 7, new Pawn(board, Color.YELLOW));
        placeNewPiece('d', 7, new Pawn(board, Color.YELLOW));
        placeNewPiece('e', 7, new Pawn(board, Color.YELLOW));
        placeNewPiece('f', 7, new Pawn(board, Color.YELLOW));
        placeNewPiece('g', 7, new Pawn(board, Color.YELLOW));
        placeNewPiece('h', 7, new Pawn(board, Color.YELLOW));
    }
}