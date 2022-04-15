package application;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Scanner;

public class Program {
    
    public static void main(String[] args) {
    
        Scanner sc = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        
        while (true) {
            UserInterface.printBoard(chessMatch.getPieces());
            System.out.println();

            System.out.print("Source: ");
            ChessPosition source = UserInterface.readChessPosition(sc);
            System.out.println();
    
            System.out.print("Target: ");
            ChessPosition target = UserInterface.readChessPosition(sc);
            
            ChessPiece capturedPiece = chessMatch.performChessMovie(source, target);

        }
    }
    
}