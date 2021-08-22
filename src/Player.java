import java.io.Serializable;
import java.net.Socket;

public class Player implements Serializable {

	private static final long serialVersionUID = 1L;
	//Constants
	final short WHITE = -1;
	final short BLACK = 1;
	
	//Fields
	private String username;
	private int pieceColour;
	private Socket connection;
	private String identifier;
	private int mmr;
	private int playerType; //0 = human, 1 = ai, 2 = online.
	
	Player(String username){
		this.username = username;
	}
	Player() {this.username = "Username";}
	
	Player(String username, Socket connection, String identifier){
		this.username = username;
		this.connection = connection;
		this.identifier = identifier;
	}

	public int[] getNextMove(Board board){
		return new int[2];
	}
	
	//Getters and Setters
	public String getUsername() {
		return this.username;
	}
	public int getPieceColour() {
		return this.pieceColour;
	}
	public Socket getConnection() {
		return this.connection;
	}
	
	public void setConnection(Socket connection) {
		this.connection = connection;
	}
	public void setPieceColour(int colour) {
		this.pieceColour = colour;
	}
	public void setPlayerType(int type) { this.playerType = type; }
	public void setUsername(String name) { this.username = name; }
	
}
