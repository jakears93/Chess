import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameClient {
	//Constants
	public final static int SERVER_PORT = 40051;
	public final static String SERVER_IP = "localhost";
	final short WHITE = -1;
	final short BLACK = 1;
	
	//Socket Objects
	Socket connection;
	static InputStream serverDataIn;
	OutputStream serverDataOut;
	static ExecutorService updatePool;
	
	//Game-play objects
	static Board board;
	static Player player;
	public static int playerTurn = 0;
	static int[] lastTurn = {-1,-1,-1,-1};
	static boolean isActiveGame;
	boolean hasTurn;
	
	GameClient(Player player, int gameMode){
		GameClient.player = player;
		if(gameMode == 0) {
			this.connect();
		}
		
		//Initialize Board
		GameClient.board = new Board();
		try {
			GameClient.board.initBoard();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		updatePool = Executors.newFixedThreadPool(1);
	}
	
	public boolean connect() {
		//Initialization connection to server
		connection = null;
		try {
				System.out.println("Connecting to game server");
		    	connection = new Socket(SERVER_IP, SERVER_PORT);
		    	ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
				oos.writeObject(GameClient.player);    
				oos.flush();
				GameClient.player.setConnection(connection);
				serverDataIn = connection.getInputStream();
				serverDataOut = connection.getOutputStream();
				Callable<Void> updateTask = new UpdateTask();
				updatePool.submit(updateTask);	
				return true;
		} catch (Exception ex) {
					System.out.println("Connecting to game server: FAILED");
		    		System.err.println(ex);
		    		return false;
		} 
	}

	public void sendTurnToServer(int gameMode) {
		if(gameMode == 1) {
			//Do nothing if local game
			return;
		}
		try {
			serverDataOut.write(lastTurn[0]);
			serverDataOut.write(lastTurn[1]);
			serverDataOut.write(lastTurn[2]);
			serverDataOut.write(lastTurn[3]);
			serverDataOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static class UpdateTask implements Callable<Void>{
		@Override
		public Void call() throws Exception {
			do {
				//Read player turn / turn data
				try {
					int index = 0;
					serverDataIn.wait();
					playerTurn = serverDataIn.read();
					isActiveGame = true;
					if(playerTurn == player.getPieceColour()) {
						serverDataIn.wait();
						while(serverDataIn.available() != 0) {
			    			lastTurn[index] = serverDataIn.read();
			    			index++;
			    		}
						//Update local board
						if(lastTurn[0] != -1) {
							board.performTurn(lastTurn[0], lastTurn[1]);
							if(lastTurn[2] != -1) {
								board.switchTurn();
								board.performTurn(lastTurn[2], lastTurn[3]);
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while(isActiveGame);
				
			return null;
		}
	}	
	
}
