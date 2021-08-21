import java.util.List;

import javafx.scene.image.Image;

class Piece {

	//Constants
	final short WHITE=-1;
	final short BLACK=1;
	final short PAWN = 1;
	final short ROOK = 2;
	final short BISHOP = 3;
	final short KNIGHT = 4;
	final short QUEEN = 5;
	final short KING = 6;

	//Fields
	private int pieceType;
	private int colour;
	private int pos;
	private boolean isActive;
	private String name;
	public List<Integer> moves;
	public Image icon;
	
	//Methods
	public void clearMoves() {
		this.moves.clear();
	}
	
	public void calculateMoves(Board board) {
		//Implemented by child classes
	}
	
	public void movePiece(int dest) {
		this.setPos(dest);
	}
	
	public int calculatePosFromXY(int x, int y) {
		return ((y*8)+x);
	}
	
	public boolean addMove(Board board, int xPos, int yPos) {
		boolean canContinue = false;
		//Variable to hold move position
		int movePos;
		
		//First check space is inbounds of board, then check if space is not occupied by same colour
		if(yPos >=0 && yPos <8) {
			if(xPos >=0 && xPos<8) {
				movePos = calculatePosFromXY(xPos, yPos);
				if(board.gameBoard[movePos] == null) {
					this.moves.add(movePos);
					//Space is empty, can check next space in same direction now
					canContinue = true;
				}
				else if(board.gameBoard[movePos].getColour() != this.getColour()) {
					this.moves.add(movePos);
					//Space is captured, cannot move further.
				}
			}
		}
		return canContinue;
	}
	//Setters
	public void setType(int type) {
		this.pieceType = type;
	}
	public void setColour(int colour) {
		this.colour = colour;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setMoves(List<Integer> moves){
		this.moves = moves;
	}
	//Getters
	public int getType() {
		return this.pieceType;
	}
	public int getColour() {
		return this.colour;
	}
	public int getPos() {
		return this.pos;
	}
	public boolean getIsActive() {
		return this.isActive;
	}
	public String getName() {
		return this.name;
	}
	public List<Integer> getMoves(){
		return this.moves;
	}
}
