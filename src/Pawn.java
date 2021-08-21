import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.image.Image;

public class Pawn extends Piece{
	private boolean isFirstMove = true;
	
	Pawn(int colour, int pos, boolean isActive) throws FileNotFoundException{
		this.setType(PAWN);
		this.setColour(colour);
		this.setPos(pos);
		this.setActive(isActive);
		this.setName("Pawn");
		this.moves = new ArrayList<Integer>();
		
		if(this.getColour()==-1) {
			this.icon = new Image(new FileInputStream(System.getProperty("user.dir")+"/ChessPieces/whitePawn.png"));
		}
		else {
			this.icon = new Image(new FileInputStream(System.getProperty("user.dir")+"/ChessPieces/blackPawn.png"));
		}
	}
	
	//Methods
	public void calculateMoves(Board board) {
		int pos = this.getPos();
		//Convert pos to x,y coords
		int yPos = pos/8;
		int xPos = pos%8;
		
		//Variable to hold position based on move.
		int movePos;
		
		//Clear move list of previous moves
		this.clearMoves();
		
		//Pawn moves forward 1 square, direction based on colour of piece
		//First check space is inbounds of board, then check if space is not occupied
		yPos += this.getColour();
		if(yPos >=0 && yPos <8) {
			movePos = calculatePosFromXY(xPos, yPos);
			if(board.gameBoard[movePos] == null) {
				this.moves.add(movePos);
			}
		}
		
		//Reset y
		yPos = pos/8;
		
		//Pawn moves forward 2 squares (first move only)
		//First check space is inbounds of board, then check if space is not occupied
		yPos += (this.getColour()*2);
		if(yPos >=0 && yPos <8 && isFirstMove) {
			movePos = calculatePosFromXY(xPos, yPos);
			if(board.gameBoard[movePos] == null) {
				this.moves.add(movePos);
			}
		}
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//Pawn captures left-hand piece
		//First check inbounds, then check if space is occupied by opposite colour
		yPos += this.getColour();
		xPos -=1;
		if(yPos >=0 && yPos <8) {
			if(xPos >=0 && xPos<8) {
				movePos = calculatePosFromXY(xPos, yPos);
				if(board.gameBoard[movePos] != null && board.gameBoard[movePos].getColour() != this.getColour()) {
					this.moves.add(movePos);
				}
			}
		}
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//Pawn captures right-hand piece
		//First check inbounds, then check if space is occupied by opposite colour
		yPos += this.getColour();
		xPos +=1;
		if(yPos >=0 && yPos <8) {
			if(xPos >=0 && xPos<8) {
				movePos = calculatePosFromXY(xPos, yPos);
				if(board.gameBoard[movePos] != null && board.gameBoard[movePos].getColour() != this.getColour()) {
					this.moves.add(movePos);
				}
			}
		}
	}
	
	public void movePiece(int dest) {
		super.movePiece(dest);
		this.isFirstMove = false;
	}
}
