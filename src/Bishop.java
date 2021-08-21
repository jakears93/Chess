import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.image.Image;

public class Bishop extends Piece{
	Bishop(int colour, int pos, boolean isActive) throws FileNotFoundException{
		this.setType(BISHOP);
		this.setColour(colour);
		this.setPos(pos);
		this.setActive(isActive);
		this.setName("Bishop");
		this.moves = new ArrayList<Integer>();
		
		if(this.getColour()==-1) {
			//this.icon = new Image(new FileInputStream("D:\\Desktop\\ChessPieces\\whiteBishop.png"));
			this.icon = new Image(new FileInputStream(System.getProperty("user.dir")+"/ChessPieces/whiteBishop.png"));
		}
		else {
			this.icon = new Image(new FileInputStream(System.getProperty("user.dir")+"/ChessPieces/blackBishop.png"));
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
		
		//Bishop down/left
		while(canContinue) {
			yPos += 1;
			xPos -= 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Bishop down/right
		while(canContinue) {
			yPos += 1;
			xPos += 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Bishop up/left
		while(canContinue) {
			yPos -= 1;
			xPos -= 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Bishop up/right
		while(canContinue) {
			yPos -= 1;
			xPos += 1;
			canContinue = addMove(board, xPos, yPos);
		}
	}
}
