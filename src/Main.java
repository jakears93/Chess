import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;


public class Main extends Application {
	//Constants
	static public String DIR_PREFIX;
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	private final double screenWidth = screen.getWidth()/2;
	private final double screenHeight = screen.getHeight()/1.5;
	final double xUnit = ((screenWidth/1.5)/8);
	final double yUnit = ((screenWidth/1.5)/8);

	final short WHITE = -1;
	final short BLACK = 1;
	short gameMode;


	
	//Local game board
	static Board board;
	
	//Game-play variables
	boolean isLoggedIn = false;
	Player p1, p2;
	int selectedLocations;
	static int[] highlightedTiles;
	boolean turnCompleted;
	boolean gameOver;
	
	//Game board Variables
	Rectangle[] playAreaRectangles;
	Paint[] colours;
	
	//JavaFX GUI objects
	StackPane rootStackPane;
	ObservableList<Node> objectList;
	Scene scene;
	Text title;
	GridPane playAreaGrid;
	Map<Integer, ImageView> icons;
	Stage primaryStage;
	
	//game server
	static GameClient client;
	
	public static void main(String[] args) {
		//Get OS
		String OS= System.getProperty("os.name");
		if(OS.startsWith("Windows")){
			DIR_PREFIX = System.getProperty("user.dir")+"\\ChessPieces\\";
		}
		else if(OS.startsWith("Linux")){
			DIR_PREFIX = System.getProperty("user.dir")+"/ChessPieces/";
		}
		else{
			System.out.println("Incompatible OS. Exiting Now.");
			return;
		}
		//Start main program GUI
		launch(args);
	}

	@Override
	public void start(Stage stage){
		
		//Enable stage access globally
		primaryStage = stage;
		
		//Set window title
		primaryStage.setTitle("Chess by Jacob Arsenault");
		
		//Set up the JavaFX window
		setupWindow(primaryStage);
		
		//Show Main Menu
		p1 = new Player("Player 1");
		p1.setPlayerType(0);  //set player 1 as a human
		showMainMenu();
	}
	
	public void selectPiece(int selectedTileIndex) {
		//Increment selected locations
		selectedLocations++;
		
		//Check whether first or second position selected
		if(selectedLocations==1) {
			//Check if tile is occupied, if not, exit handler
			if(board.gameBoard[selectedTileIndex] == null) {
				selectedLocations --;
				return;
			}
			
			//Check if it is the correct players turn, if not, exit handler
			if(board.gameBoard[selectedTileIndex].getColour() != board.playerTurn) {
				selectedLocations --;
				return;
			}
			//Save what tile is clicked, highlight light-red
			highlightedTiles[0] = selectedTileIndex;
			playAreaRectangles[selectedTileIndex].setFill(Color.TOMATO);
			
			//Calculate Valid Moves
			board.calculatePieceMoves(selectedTileIndex);
			
			//Highlight valid moves light blue
			for(int i=0; i<board.gameBoard[selectedTileIndex].moves.size(); i++) {
				playAreaRectangles[board.gameBoard[selectedTileIndex].moves.get(i)].setFill(Color.TURQUOISE);
				highlightedTiles[i+1] = board.gameBoard[selectedTileIndex].moves.get(i);
			}
		}

		else if(selectedLocations == 2) {
			//TODO Check if move puts own king in check

			//Iterate through all moves of first selected piece (the first highlighted tile location)
			for(int i=0; i<board.gameBoard[highlightedTiles[0]].moves.size(); i++) {
				//Check is selected tile index is in move list for first selected piece
				if(selectedTileIndex == board.gameBoard[highlightedTiles[0]].moves.get(i)) {
					movePiece(highlightedTiles[0], selectedTileIndex);
					
					//Exit for-loop
					break;
				}
			}
			
			//Erase Selections
			selectedLocations = 0;
			//Remove highlighting of selected tiles and available moves
			clearHighlights();
		}
		
		//Clean up highlighted tiles on end of turn
		if(turnCompleted) {
			//Reset flag
			turnCompleted = false;
			
			//Check if game is over, exit application
			if(board.hasCheckmate) {
				finalizeGame(board.getWinner());
			}
			
			//If board has check, highlight tile of King piece under check
//			if(board.hasCheck) {
//				//TODO
//			}

			//Check if Local Game
			if(gameMode > 0){
				//check if AI or Online
				//Online
				if(gameMode >1){
					//TODO
				}
				//AI
				else{
					if(board.playerTurn == p2.getPieceColour()){
						System.out.println("AI's turn");
						int[] move;
						move = p2.getNextMove(board);
						movePiece(move[0], move[1]);
					}
				}
			}
		}
		
	}

	private void movePiece(int src, int dest){
		//Check if new tile location is occupied by opponents piece
		//If occupied, remove icon from drawing list as well as icon map
		if(icons.containsKey(dest)) {
			objectList.remove(icons.get(dest));
			icons.remove(dest);
		}

		//Create new icon of same type as selected piece, but with new position index
		icons.put(dest, icons.get(src));

		//Remove old selected piece icon from drawing list
		objectList.remove(icons.get(src));

		//Remove old selected piece's icon location from icon map
		icons.remove(src);

		//Move icon to new location on play area
		int yRaw = dest/8;
		int xRaw = dest%8;
		double xTranslate = xUnit*(-5+xRaw);
		double yTranslate = yUnit*(-3.5+yRaw);
		icons.get(dest).setTranslateX(xTranslate);
		icons.get(dest).setTranslateY(yTranslate);

		//Add icon back onto drawing list
		objectList.add(icons.get(dest));

		//Move Piece internally and switch turns
		board.performTurn(src, dest);

		//Mark turn as complete
		turnCompleted = true;
	}

	private void clearHighlights() {
		//Remove highlighting of selected tiles and available moves
		for(int i=0; i<64; i++) {
			if(highlightedTiles[i] != -1) {
				playAreaRectangles[highlightedTiles[i]].setFill(colours[highlightedTiles[i]]);
			}
			highlightedTiles[i] = -1;
		}
	}

	public void finalizeGame(int winner) {
		String win = "";
		if(winner == WHITE) {
			win = "WHITE WINS";
		}
		else if (winner == BLACK){
			win = "BLACK WINS";
		}
		showEndGameMenu(win);
	}

	public void showMainMenu() {
		//Create buttons to choose either next game or return to main menu
		Button localGame = new Button();
		localGame.setMaxHeight(80);
		localGame.setMaxWidth(300);
		localGame.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
		localGame.setText("Play Local Game");
		localGame.setOnMouseClicked(event -> {
			gameMode = 0;
			cleanup();
			startGame(gameMode);
		});

		Button aiGame = new Button();
		aiGame.setMaxHeight(80);
		aiGame.setMaxWidth(300);
		aiGame.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
		aiGame.setText("Play Against Bot");
		aiGame.setOnMouseClicked(event -> {
			gameMode = 1;
			cleanup();
			startGame(gameMode);
		});

		Button signIn = new Button();
		signIn.setMaxHeight(80);
		signIn.setMaxWidth(300);
		signIn.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
		signIn.setText("Sign In");
		signIn.setOnMouseClicked(event -> {
			isLoggedIn = login();
			cleanup();
			showMainMenu();
		});
		
		Button onlineGame = new Button();
		onlineGame.setMaxHeight(80);
		onlineGame.setMaxWidth(300);
		onlineGame.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
		onlineGame.setText("Play Online Game");
		onlineGame.setOnMouseClicked(event -> {
			//TODO
//			gameMode = 2;
//			cleanup();
//			startGame(gameMode);
		});
		
		Button settings = new Button();
		settings.setMaxHeight(80);
		settings.setMaxWidth(300);
		settings.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
		settings.setText("Settings");
		settings.setOnMouseClicked(event -> {
			//TODO create settings page
		});
	
		//Add grid layout to root element
		objectList.add(localGame);
		StackPane.setAlignment(localGame, Pos.BOTTOM_LEFT);

		objectList.add(aiGame);
		StackPane.setAlignment(aiGame, Pos.CENTER_LEFT);
		
		if(isLoggedIn) {
			objectList.add(onlineGame);
			StackPane.setAlignment(onlineGame, Pos.BOTTOM_CENTER);
		}
		else {
			objectList.add(signIn);
			StackPane.setAlignment(signIn, Pos.BOTTOM_CENTER);
		}

		objectList.add(settings);
		StackPane.setAlignment(settings, Pos.BOTTOM_RIGHT);
	}
	
	//TODO make login page
	//TODO authenticate credentials with database
	public boolean login() {
		//Populate player object
		p1.setUsername("Jacob");
		title.setText("Welcome back "+p1.getUsername()+"!");
		//Temp
		return true;
	}
	
	public void showEndGameMenu(String winner) {
		//Create text announcing winner
		Text gameWinner = new Text();
		gameWinner.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		gameWinner.setFill(Color.BLUE);
		gameWinner.setTextAlignment(TextAlignment.CENTER);
		gameWinner.setText(winner);	
		
		//Create buttons to choose either next game or return to main menu
		Button nextGame = new Button();
		nextGame.setMaxHeight(200);
		nextGame.setMaxWidth(400);
		nextGame.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		nextGame.setText("Next Game");
		nextGame.setOnMouseClicked(event -> {
			cleanup();
			startGame(gameMode);
		});
		
		Button mainMenu = new Button();
		mainMenu.setMaxHeight(200);
		mainMenu.setMaxWidth(400);
		mainMenu.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		mainMenu.setText("Main Menu");
		mainMenu.setOnMouseClicked(event -> {
			cleanup();
			showMainMenu();
		});
		
		//Create grid layout to add buttons and text to
		GridPane endGameMenu = new GridPane();
		BackgroundFill bgFill = new BackgroundFill(Color.LIGHTSTEELBLUE, null, null);
		Background endGameBackground = new Background(bgFill);
		endGameMenu.setMaxWidth(400);
		endGameMenu.setMaxHeight(500);
		endGameMenu.setBackground(endGameBackground);
		endGameMenu.setAlignment(Pos.CENTER);
		
		//Add buttons and text to grid layout
		endGameMenu.add(gameWinner, 1, 1, 1, 1);
		endGameMenu.add(mainMenu, 1, 2, 1, 1);
		endGameMenu.add(nextGame,1, 3, 1, 1);
		
		
		//Add grid layout to root element
		objectList.add(endGameMenu);
	}
	
	public void setupWindow(Stage primaryStage) {
		//Initialize main layout and viewable object list
		rootStackPane = new StackPane();
		objectList = rootStackPane.getChildren();
		
		//Setup title string
		title = new Text();
		title.setFont(new Font(30));
		title.setX(50);
		title.setTextAlignment(TextAlignment.CENTER);
		title.setText("Welcome To Chess");
		
		//Add title to viewable list
		objectList.add(title);
		
		//Add main layout to scene
		scene = new Scene(rootStackPane, screenWidth, screenHeight);
		
		BackgroundFill mainBgFill = new BackgroundFill(Color.STEELBLUE, null, null);
		Background mainBackground = new Background(mainBgFill);
		rootStackPane.setBackground(mainBackground);
		
		//Set Title position
		StackPane.setAlignment(title, Pos.TOP_CENTER);
		
		//Display scene
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	//Setup all GUI components and ready game
	public void setupPlayArea() {

		//Choose p1 colour
		Random rnd = new Random();
		if(rnd.nextBoolean()){
			p1.setPieceColour(WHITE);
			p2.setPieceColour(BLACK);
			//Set Match title
			title.setText(p1.getUsername()+" vs. "+p2.getUsername());
		}
		else {
			p1.setPieceColour(BLACK);
			p2.setPieceColour(WHITE);
			//Set Match title
			title.setText(p2.getUsername()+" vs. "+p1.getUsername());
		}
		
		//Setup playing area
		//Initialize Board
		board = new Board();
		try {
			board.initBoard();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		playAreaRectangles = new Rectangle[64];
		colours = new Paint[64];
		playAreaGrid = new GridPane();
		int setupColour = WHITE;
		for(int y=0; y<8; y++) {
			for(int x=0; x<8; x++) {
				final int index = (8*y)+x;
				playAreaRectangles[index] = new Rectangle();
				playAreaRectangles[index].setWidth(xUnit);
				playAreaRectangles[index].setHeight(yUnit);			
				
				if(setupColour==BLACK) {
					playAreaRectangles[index].setFill(Color.CHOCOLATE);
					colours[index] = Color.CHOCOLATE;
				}
				else {
					playAreaRectangles[index].setFill(Color.BLANCHEDALMOND);
					colours[index] = Color.BLANCHEDALMOND;
				}
				setupColour *= -1;
				playAreaGrid.add(playAreaRectangles[index],x,y,1,1);
				
				playAreaRectangles[index].setOnMouseClicked( event -> {
					selectPiece(index);
				});
				
			}
			setupColour *= -1;
		}
		
		//Add play area to displayed objects list
		objectList.add(playAreaGrid);
		
		//Populate Board with icons
		//icon positions are tied to the position of the gridpane object (play area), position is translated from center of the play area
		icons = new HashMap<Integer, ImageView>();
		for(int i=0; i<64; i++) {
			if(board.gameBoard[i] != null) {
				icons.put(i, new ImageView());
				icons.get(i).setImage(board.gameBoard[i].icon);
				icons.get(i).setFitHeight(xUnit);
				icons.get(i).setFitWidth(yUnit);
				int yRaw = (board.gameBoard[i].getPos())/8;
				int xRaw = (board.gameBoard[i].getPos())%8;
				double xTranslate = xUnit*(-5+xRaw);
				double yTranslate = yUnit*(-3.5+yRaw);
				icons.get(i).setTranslateX(xTranslate);
				icons.get(i).setTranslateY(yTranslate);
				icons.get(i).setMouseTransparent(true);
				
				objectList.add(icons.get(i));
			}
		}
		
		//Set positioning of play area
		playAreaGrid.setAlignment(Pos.CENTER_LEFT);
		Insets margin = new Insets(0,0,0,xUnit/2);
		StackPane.setMargin(playAreaGrid, margin);

		//Check if Local Game
		if(gameMode > 0){
			//check if AI or Online
			//Online
			if(gameMode >1){
				//TODO
			}
			//AI
			else{
				if(board.playerTurn == p2.getPieceColour()){
					System.out.println("AI's turn");
					int[] move;
					move = p2.getNextMove(board);
					movePiece(move[0], move[1]);
				}
			}
		}
	}
	
	//Clear the board
	public void cleanup() {
		objectList.clear();
		if(icons != null) {
			icons.clear();
		}
		objectList.add(title);
	}
	
	//Start a fresh game
	public void startGame(int gameMode) {
		if(gameMode == 0) //Local
		{
			p2 = new Player("Player 2");
		}
		else if(gameMode == 1) //AI
		{
			p2 = new Bot("Bot");
		}
		else if(gameMode == 2) //Online
		{
			//TODO
		}

		setupInternalBoard();	
		setupPlayArea();
	}
	
	//Setup internal board for fresh game
	public void setupInternalBoard() {
		//Clear Values for the clicked index
		highlightedTiles = new int[64];
		for(int i=0; i<64; i++) {
			highlightedTiles[i] = -1;
		}
		
		//Reset selected pieces
		selectedLocations = 0;
		
		//Ready first turn
		turnCompleted = false;
		gameOver = false;
	}
}

