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
	
	Player(String username){
		this.username = username;
	}
	
	Player(String username, Socket connection, String identifier){
		this.username = username;
		this.connection = connection;
		this.identifier = identifier;
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
	
}
