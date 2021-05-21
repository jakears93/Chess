
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
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;


public class Main extends Application {
	//Constants
	
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	private double screenWidth = screen.getWidth()/2;
	private double screenHeight = screen.getHeight()/1.5;
	final double xUnit = ((screenWidth/1.5)/8);
	final double yUnit = ((screenWidth/1.5)/8);

	final short WHITE = -1;
	final short BLACK = 1;
	
	//Game-play variables
	int selectedLocations;
	static int highlightedTiles[];
	static Board board;
	Rectangle playAreaRectangles[];
	Paint colours[];
	boolean turnCompleted;
	boolean gameOver;
	
	//JavaFX GUI objects
	StackPane rootStackPane;
	ObservableList<Node> objectList;
	Scene scene;
	Text title;
	GridPane playAreaGrid;
	Map<Integer, ImageView> icons;
	Stage primaryStage;
	
	
	public static void main(String[] args) {
		System.out.println("Welcome to Chess");
		//Start main program GUI
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		//Enable stage access globally
		primaryStage = stage;
		
		//Set window title
		primaryStage.setTitle("Chess by Jacob Arsenault");
		
		//Setup the JavaFX window
		setupWindow(primaryStage);
		
		//Show Main Menu
		showMainMenu();
	}
	
	//TODO clean up select piece function
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
					
					//Check if new tile location is occupied by opponents piece
					//If occupied, remove icon from drawing list as well as icon map
					if(icons.containsKey(selectedTileIndex)) {
						objectList.remove(icons.get(selectedTileIndex));
						icons.remove(selectedTileIndex);
					}
					
					//Create new icon of same type as selected piece, but with new position index
					icons.put(selectedTileIndex, icons.get(highlightedTiles[0]));
					
					//Remove old selected piece icon from drawing list
					objectList.remove(icons.get(highlightedTiles[0]));
					
					//Remove old selected piece's icon location from icon map
					icons.remove(highlightedTiles[0]);
					
					//Move icon to new location on play area
					int yRaw = selectedTileIndex/8;
					int xRaw = selectedTileIndex%8;
					double xTranslate = xUnit*(-5+xRaw);
					double yTranslate = yUnit*(-3.5+yRaw);
					icons.get(selectedTileIndex).setTranslateX(xTranslate);
					icons.get(selectedTileIndex).setTranslateY(yTranslate);
					
					//Add icon back onto drawing list
					objectList.add(icons.get(selectedTileIndex));
					
					//Move Piece internally and switch turns
					board.performTurn(highlightedTiles[0], board.gameBoard[highlightedTiles[0]].moves.get(i));
					
					//Mark turn as complete
					turnCompleted = true;
					
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
			if(board.hasCheck) {
				//TODO
			}
			
		}
		
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
		Button startGame = new Button();
		startGame.setMaxHeight(80);
		startGame.setMaxWidth(200);
		startGame.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
		startGame.setText("Start Game");
		startGame.setOnMouseClicked(event -> {
			cleanup();
			startGame();
		});
		
		Button settings = new Button();
		settings.setMaxHeight(80);
		settings.setMaxWidth(200);
		settings.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
		settings.setText("Settings");
		settings.setOnMouseClicked(event -> {
			//TODO create settings page
		});
	
		//Add grid layout to root element
		objectList.add(startGame);
		objectList.add(settings);
		StackPane.setAlignment(startGame, Pos.BOTTOM_LEFT);
		StackPane.setAlignment(settings, Pos.BOTTOM_RIGHT);
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
			startGame();
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
	public void setupPlayArea(Stage primaryStage) {
		
		//Set Match title
		//TODO
		title.setText("Player vs. Player");
		
		//Setup playing area
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
				
				//TODO remove, onClick now on icons
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
			
	}
	
	//Clear the board
	public void cleanup() {
		objectList.clear();
		if(icons != null) {
			icons.clear();
		}
		objectList.add(title);
		//setupWindow(primaryStage);
	}
	
	//Start a fresh game
	public void startGame() {
		setupBoard();	
		setupPlayArea(primaryStage);
	}
	
	//Setup internal board for fresh game
	public void setupBoard() {
		//Clear Values for the clicked index
		highlightedTiles = new int[64];
		for(int i=0; i<64; i++) {
			highlightedTiles[i] = -1;
		}
		
		//Reset selected pieces
		selectedLocations = 0;
		
		//Initialize Board
		board = new Board();
		try {
			board.initBoard();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Ready first turn
		turnCompleted = false;
		gameOver = false;
	}
}

