import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.image.Image;

public class Rook extends Piece{
	Rook(int colour, int pos, boolean isActive) throws FileNotFoundException{
		this.setType(ROOK);
		this.setColour(colour);
		this.setPos(pos);
		this.setActive(isActive);
		this.setName("Rook");
		this.moves = new ArrayList<Integer>();
		
		if(this.getColour()==-1) {
			this.icon = new Image(new FileInputStream(Main.DIR_PREFIX+"whiteRook.png"));
		}
		else {
			this.icon = new Image(new FileInputStream(Main.DIR_PREFIX+"blackRook.png"));
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
		
		//Rook down
		while(canContinue) {
			yPos += 1;
			canContinue = addMove(board, xPos, yPos);
		}

		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Rook up
		while(canContinue) {
			yPos -= 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Rook left
		while(canContinue) {
			xPos -= 1;
			canContinue = addMove(board, xPos, yPos);
		}
		
		//Reset x, y and canContinue flag
		yPos = pos/8;
		xPos = pos%8;
		canContinue = true;
		
		//Rook right
		while(canContinue) {
			xPos += 1;
			canContinue = addMove(board, xPos, yPos);
		}
	}
}
