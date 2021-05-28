import java.util.List;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {
	//Constants
	final short WHITE=-1;
	final short BLACK=1;	
	
	//Board status variables
	public int playerTurn;
	public Piece gameBoard[];
	List<Piece> graveyard;
	public boolean hasNextTurn, hasCheck, hasCheckmate, isDraw;
	private int winner;
	int underCheck;
	
	//Set up GameBoard for start of game positions
	public void initBoard() throws FileNotFoundException {
		
		//Initialize board status variables
		this.playerTurn = WHITE;
		graveyard = new ArrayList<Piece>();
		hasNextTurn = true;
		hasCheck = false;
		hasCheckmate = false;
		isDraw = false;
		
		
		this.gameBoard = new Piece[64];
		int pos = 0;
		
		this.gameBoard[pos] = new Rook(BLACK, pos, true);
		pos++;
		this.gameBoard[pos] = new Knight(BLACK, pos, true);
		pos++;
		this.gameBoard[pos] = new Bishop(BLACK, pos, true);
		pos++;
		this.gameBoard[pos] = new Queen(BLACK, pos, true);
		pos++;
		this.gameBoard[pos] = new King(BLACK, pos, true);
		pos++;
		this.gameBoard[pos] = new Bishop(BLACK, pos, true);
		pos++;
		this.gameBoard[pos] = new Knight(BLACK, pos, true);
		pos++;
		this.gameBoard[pos] = new Rook(BLACK, pos, true);
		pos++;
		for(; pos<16; pos++) {
			this.gameBoard[pos] = new Pawn(BLACK, pos, true);
		}
		
		for(; pos<48; pos++) {
			this.gameBoard[pos] = null;
		}
		
		for(; pos<56; pos++) {
			this.gameBoard[pos] = new Pawn(WHITE, pos, true);
		}
		this.gameBoard[pos] = new Rook(WHITE, pos, true);
		pos++;
		this.gameBoard[pos] = new Knight(WHITE, pos, true);
		pos++;
		this.gameBoard[pos] = new Bishop(WHITE, pos, true);
		pos++;
		this.gameBoard[pos] = new Queen(WHITE, pos, true);
		pos++;
		this.gameBoard[pos] = new King(WHITE, pos, true);
		pos++;
		this.gameBoard[pos] = new Bishop(WHITE, pos, true);
		pos++;
		this.gameBoard[pos] = new Knight(WHITE, pos, true);
		pos++;
		this.gameBoard[pos] = new Rook(WHITE, pos, true);
	}

	//Calculate players moves for list of pieces
	public void calculateTeamMoves(int team) {
		for(int i=0; i<this.gameBoard.length; i++) {
			//Populate the valid moves list for each piece.
			if(team == this.gameBoard[i].getColour()) {
				this.gameBoard[i].calculateMoves(this);
			}
		}
	}
	
	//Calculate players moves for individual piece
	public void calculatePieceMoves(int pos) {
		gameBoard[pos].calculateMoves(this);
	}
	
	//Move piece by passing in a source piece and a destination
	public void performTurn(int pos, int dest) {
		//Set piece location on object
		this.gameBoard[pos].movePiece(dest);
		//Move captured piece to graveyard
		if(this.gameBoard[dest] != null) {
			graveyard.add(this.gameBoard[dest]);
		}
		//Move piece to destination
		this.gameBoard[dest] = null;
		this.gameBoard[dest] = this.gameBoard[pos];
		this.gameBoard[pos] = null;
		
		//Set Flag for next turn by inverting turn variable
		this.playerTurn *= -1;
		
		
		//TEMP
		//TODO remove and put in proper place
		this.findCheck();
	}
	
	//Find check/ checkmate on board
	//TODO
	private void findCheck() {
		//Set check and checkmate accordingly
		//Temp to find if king captured
		//System.out.println("Graveyard List");
		for(int i=0; i<this.graveyard.size(); i++) {
			//System.out.println(this.graveyard.get(i).getColour()+": "+this.graveyard.get(i).getType());
			if(this.graveyard.get(i).getType() == 6) {
				this.hasCheckmate = true;
				this.setWinner(this.graveyard.get(i).getColour()*-1);
			}
		}
	}
	
	//Set winner of game
	public void setWinner(int colour) {
		this.winner = colour;
	}
	
	//Return value of winner
	public int getWinner() {
		return this.winner;
	}
}
