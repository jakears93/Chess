import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
	//Constants
	public final static int PORT = 40051;
	public final static String HOSTNAME = "192.168.50.92";
	
	final int WHITE = -1;
	final int BLACK = 1;
	final int SRC = 0;
	final int DEST = 1;
	
	//Socket Objects
	BufferedReader in;;
	PrintWriter out;
	Socket socket;
	BufferedReader terminalInput;
	
	//Game-play objects
	Player player;
	Board board;
	int playerTurn = WHITE;
	int[] lastTurn;
	boolean isActiveGame;
	
	GameClient(Player player){
		this.player = player;
	}
	
	public void connect() {
		boolean continueLoop = true;
		
		//Initialization
		in = null;
		out = null; 
		socket = null;
		terminalInput=new BufferedReader(new InputStreamReader(System.in)); 
		try {
		    	socket = new Socket(HOSTNAME, PORT); 
		    	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		   		out = new PrintWriter(socket.getOutputStream(), true);            
		} catch (IOException ex) {
		    		System.err.println(ex);
		} finally {
			try {
				System.out.println("Closing Client");
				in.close();
				out.close();
				terminalInput.close();
		        socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
		    }
		}
	}
	
	public void syncServerBoard(int sourceLocation, int destinationLocation) {
		//Store last turn
		lastTurn[SRC] = sourceLocation;
		lastTurn[DEST] = destinationLocation;
		
		//Update server board
		this.board.performTurn(lastTurn[SRC], lastTurn[DEST]);
	}
		
	public void switchTurn() {
		playerTurn *= -1;
	}
	
}
