import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.image.Image;

public class Knight extends Piece{
	Knight(int colour, int pos, boolean isActive) throws FileNotFoundException{
		this.setType(KNIGHT);
		this.setColour(colour);
		this.setPos(pos);
		this.setActive(isActive);
		this.setName("Knight");
		this.moves = new ArrayList<Integer>();
		
		if(this.getColour()==-1) {
			this.icon = new Image(new FileInputStream(Main.DIR_PREFIX+"whiteKnight.png"));
		}
		else {
			this.icon = new Image(new FileInputStream(Main.DIR_PREFIX+"blackKnight.png"));
		}
	}
	
	//Methods
	//Methods
	public void calculateMoves(Board board) {
		int pos = this.getPos();
		//Convert pos to x,y coords
		int yPos = pos/8;
		int xPos = pos%8;
		
		//Clear move list of previous moves
		this.clearMoves();
		
		//Knight moves down 2, left 1
		xPos -= 1;
		yPos += 2;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//Knight moves down 2, right 1
		xPos += 1;
		yPos += 2;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//Knight moves down 1, left 2
		xPos -= 2;
		yPos += 1;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//Knight moves down 1, right 2
		xPos += 2;
		yPos += 1;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//Knight moves up 2, left 1
		xPos -= 1;
		yPos -= 2;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//Knight moves up 2, right 1
		xPos += 1;
		yPos -= 2;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//Knight moves up 1, left 2
		xPos -= 2;
		yPos -= 1;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//Knight moves up 1, right 2
		xPos += 2;
		yPos -= 1;
		addMove(board, xPos, yPos);
	}
}
