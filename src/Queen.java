import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.image.Image;

public class Queen extends Piece{
	Queen(int colour, int pos, boolean isActive) throws FileNotFoundException{
		this.setType(QUEEN);
		this.setColour(colour);
		this.setPos(pos);
		this.setActive(isActive);
		this.setName("Queen");
		this.moves = new ArrayList<Integer>();
		
		if(this.getColour()==-1) {
			this.icon = new Image(new FileInputStream("D:\\Desktop\\ChessPieces\\whiteQueen.png"));
		}
		else {
			this.icon = new Image(new FileInputStream("D:\\Desktop\\ChessPieces\\blackQueen.png"));
		}
	}
	
	//Methods
	public void calculateMoves(Board board) {
		int pos = this.getPos();
		//Convert pos to x,y coords
		int yPos = pos/8;
		int xPos = pos%8;
		
		//Clear move list of previous moves
		this.clearMoves();
		
		//Flag to continue calculation of moves in a direction
		boolean canContinue = true;
		
		//Queen down
		while(canContinue) {
			yPos += 1;
			canContinue = addMove(board, xPos, yPos);
		}

		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Queen up
		while(canContinue) {
			yPos -= 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Queen left
		while(canContinue) {
			xPos -= 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Queen right
		while(canContinue) {
			xPos += 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Queen down/left
		while(canContinue) {
			yPos += 1;
			xPos -= 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Queen down/right
		while(canContinue) {
			yPos += 1;
			xPos += 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Queen up/left
		while(canContinue) {
			yPos -= 1;
			xPos -= 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Queen up/right
		while(canContinue) {
			yPos -= 1;
			xPos += 1;
			canContinue = addMove(board, xPos, yPos);
		}
	}
}
