package boardgame;

public class Board {

    private int rows;
    private int columns;
    private Piece[][] pieces;

    public Board(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            throw new BoardException("Error creating board: there must be at least 1 row and 1 column!");
        }
        this.rows = rows;
        this.columns = columns;
        this.pieces = new Piece[rows][columns];
    }

    public Piece piece(int rows, int columns) {
        if (!positionExists(rows, columns)) {
            throw new BoardException("Position not on the board!");
        }
        return pieces[rows][columns];
    }

    public Piece piece(Position position) {
        if (!positionExists(position)) {
            throw new BoardException("Position not on the board!");
        }
        return pieces[position.getRow()][position.getColumn()];
    }

    public void placePiece(Piece piece, Position position) {
        if(thereIsAPieces(position)){
            throw new BoardException("There is already a piece on position " + position);
        }
        this.pieces[position.getRow()][position.getColumn()]=piece;
    piece.position =position;
}

    private boolean positionExists(int row, int column) {
        return (row >= 0 && row < rows && column >= 0 && column < columns);
    }

    public boolean positionExists(Position positions) {
        return positionExists(positions.getRow(), positions.getColumn());
    }

    public boolean thereIsAPieces(Position position) {
        if (!positionExists(position)) {
            throw new BoardException("Position not on the board!");
        }
        return this.piece(position) != null;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

}