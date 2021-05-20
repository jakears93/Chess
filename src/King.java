import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.image.Image;

public class King extends Piece{
	King(int colour, int pos, boolean isActive) throws FileNotFoundException{
		this.setType(KING);
		this.setColour(colour);
		this.setPos(pos);
		this.setActive(isActive);
		this.setName("King");
		this.moves = new ArrayList<Integer>();
		
		if(this.getColour()==-1) {
			this.icon = new Image(new FileInputStream("D:\\Desktop\\ChessPieces\\whiteKing.png"));
		}
		else {
			this.icon = new Image(new FileInputStream("D:\\Desktop\\ChessPieces\\blackKing.png"));
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
		
		//King moves down 1
		yPos += 1;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//King moves down 1, left 1
		xPos -= 1;
		yPos += 1;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//King moves down 1, right 1
		xPos += 1;
		yPos += 1;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//King moves left 1
		xPos -= 1;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//King moves right 1
		xPos += 1;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//King moves up 1, left 1
		xPos -= 1;
		yPos -= 1;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//King moves up 1, right 1
		xPos += 1;
		yPos -= 1;
		addMove(board, xPos, yPos);
		
		//Reset x and y
		yPos = pos/8;
		xPos = pos%8;
		
		//King moves up 1
		yPos -= 1;
		addMove(board, xPos, yPos);
	}
}
