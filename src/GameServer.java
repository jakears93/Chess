import java.util.concurrent.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class GameServer {
	public final static int PORT = 40051;
	private final static int POOL_SIZE = 5;
	static List<Player> queue;
	static ExecutorService gamePool;
	static boolean serverIsActive = true;
	final static short WHITE = -1;
	final static short BLACK = 1;
	
//Main Function
	public static void main(String args[]){
		//Initialize Game Pool
		gamePool = Executors.newFixedThreadPool(POOL_SIZE);
		
		//Start MatchMaker
		ExecutorService matchMaker = Executors.newFixedThreadPool(1);
		Callable<Void> matchMakerTask = new MatchMaker();
		matchMaker.submit(matchMakerTask);	
		
		queue = new ArrayList<Player>();
		while(true){
			try (ServerSocket server = new ServerSocket(PORT)){
				Socket connection = server.accept();
				InetAddress inet = connection.getInetAddress();
				String hostname = inet.getHostName();
				int clientPort = connection.getPort();
				String identifier = "Client "+hostname+" on port "+clientPort;
				
				//Add player to queue
				ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
				Player player = (Player) ois.readObject();
				player.setConnection(connection);
				queue.add(player);
			} catch (IOException ex){
				System.out.println("Couldn't start server.");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}//End of Main

	private static class Game implements Callable<Void>{
		private Player p1;
		private Player p2;
		private OutputStream p1Out, p2Out;
		private InputStream p1In, p2In;
		
		private Board board;
	
		Game(Player p1, Player p2){
			this.p1 = p1;
			this.p2 = p2;
			try {
				p1Out = p1.getConnection().getOutputStream();
				p2Out = p2.getConnection().getOutputStream();
				
				p1In = p1.getConnection().getInputStream();
				p2In = p2.getConnection().getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override 
		public Void call() throws Exception{
			//Hold turn information
			int[] turn = {-1,-1,-1,-1};
			//Set Piece Colours
			Random randNum = new Random();
			if(randNum.nextBoolean()) {
				p1.setPieceColour(WHITE);
				p2.setPieceColour(BLACK);
			}
			else {
				p1.setPieceColour(BLACK);
				p2.setPieceColour(WHITE);
			}
		    board = new Board();
		    board.initBoard();
			
		    while(board.hasCheckmate == false) {
		    	//Send turn info to players
		    	p1Out.write(board.playerTurn);
		    	p2Out.write(board.playerTurn);
		    	p1Out.flush();
		    	p2Out.flush();
		    	
		    	//Send last turn to new player, turn has already been switched on board so send on match
		    	if(board.playerTurn == p1.getPieceColour()) {
		    		for(int i=0; i<4; i++) {
		    			p1Out.write(turn[i]);
		    		}
		    		p1Out.flush();
		    	}
		    	else {
		    		for(int i=0; i<4; i++) {
		    			p2Out.write(turn[i]);
		    		}
		    		p2Out.flush();
		    	}
		    
		    	//Wait for turn from player
		    	int index = 0;
		    	if(board.playerTurn == p1.getPieceColour()) {
		    		p1In.wait();
		    		while(p1In.available() != 0) {
		    			turn[index] = p1In.read();
		    			index++;
		    		}
		    	}
		    	else {
		    		p2In.wait();
		    		while(p2In.available() != 0) {
		    			turn[index] = p2In.read();
		    			index++;
		    		}
		    	}
		    	
		    	//Perform turn on server board
		    	board.performTurn(turn[0], turn[1]);
		    	//Perform second piece movement (castling only)
		    	if(turn[2] != -1) {
		    		board.switchTurn();
			    	board.performTurn(turn[2], turn[3]);
		    	}
		    		    	
		    }
			
		    //TODO Game over, now modify mmr and close game
		    System.out.println("Game Over");
		    try {
			    p1In.close();
			    p1Out.close();
			    p2In.close();
			    p2Out.close();
			    p1.getConnection().close();
			    p2.getConnection().close();
		    }
		    catch (Exception e) {
		    	
		    }
			return null;
		}
	}
	
	private static class MatchMaker implements Callable<Void>{
		//TODO implement matchmaking based on skill
		@Override
		public Void call() throws Exception {
			while(serverIsActive) {
				if(queue.size()>=2) {
					Player p1 = queue.get(0);
					Player p2 = queue.get(1);
					Callable<Void> game = new Game(p1,p2);
					gamePool.submit(game);
					
					//Remove the two players from the queue
					queue.remove(0);
					queue.remove(0);
				}
			}
			return null;
		}
		
	}
}
