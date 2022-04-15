package application;

import boardgame.Board;
import boardgame.Position;
import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Program {
    
    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> captured = new ArrayList<>();
        
        while (true) {
            try {
                UserInterface.clearScreen();
                UserInterface.printMatch(chessMatch, captured);
                System.out.println();
                
                System.out.print("Source: ");
                ChessPosition source = UserInterface.readChessPosition(sc);
                boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                UserInterface.clearScreen();
                UserInterface.printBoard(chessMatch.getPieces(), possibleMoves);
                System.out.println();
                
                System.out.print("Target: ");
                ChessPosition target = UserInterface.readChessPosition(sc);
                
                ChessPiece capturedPiece = chessMatch.performChessMovie(source, target);
                
                if(capturedPiece != null) {
                    captured.add(capturedPiece);
                }
                
            } catch (ChessException e) {
                System.out.println(e.getMessage());
                System.out.println();
                System.out.print("Type enter to try again: ");
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                System.out.println();
                System.out.print("Type enter to try again: ");
                sc.nextLine();
            }
        }
    }
    
}