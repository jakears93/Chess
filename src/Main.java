
import javafx.scene.*;
import javafx.scene.image.ImageView;
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
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;


public class Main extends Application {
	//Constants
	private final int screenWidth = 600;
	private final int screenHeight = 600;
	final double unit = ((screenWidth-100)/8);
	final short WHITE = -1;
	final short BLACK = 1;
	
	//Game-play variables
	int selectedLocations;
	static int highlightedTiles[];
	static Board board;
	Rectangle playArea[];
	Paint colours[];
	boolean turnCompleted;
	boolean gameOver;
	
	//JavaFX GUI objects
	StackPane rootStackPane;
	ObservableList<Node> objectList;
	Scene scene;
	Text title;
	GridPane gridPane;
	Map<Integer, ImageView> icons;
	
	
	public static void main(String[] args) {
		System.out.println("Welcome to Chess");
		//Start main program GUI
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {	
		
		//Set window title
		primaryStage.setTitle("Chess by Jacob Arsenault");
		
		//Start new game
		setupBoard();
		setupWindow(primaryStage);	
	}
	
	//TODO clean up select piece function
	public void selectPiece(int selectedTileIndex) {
		//If game-over, close window
		if(gameOver) {
			System.exit(0);
		}
		
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
			playArea[selectedTileIndex].setFill(Color.TOMATO);
			
			//Calculate Valid Moves
			board.calculatePieceMoves(selectedTileIndex);
			
			//Highlight valid moves light blue
			for(int i=0; i<board.gameBoard[selectedTileIndex].moves.size(); i++) {
				playArea[board.gameBoard[selectedTileIndex].moves.get(i)].setFill(Color.TURQUOISE);
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
					double xTranslate = unit*(-3.5+xRaw);
					double yTranslate = unit*(-3.5+yRaw);
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
				endGame(board.getWinner());
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
				playArea[highlightedTiles[i]].setFill(colours[highlightedTiles[i]]);
			}
			highlightedTiles[i] = -1;
		}
	}

	public void endGame(int winner) {
		String win = "";
		if(winner == WHITE) {
			win = "WINNER\nWHITE";
		}
		else if (winner == BLACK){
			win = "WINNER\nBLACK";
		}
		Text gameWinner = new Text();
		//gameWinner.setFont(new Font(40));
		gameWinner.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		gameWinner.setFill(Color.BLUE);
		gameWinner.setTextAlignment(TextAlignment.CENTER);
		gameWinner.setText(win);
		//list.remove(gridPane);
		objectList.add(gameWinner);
		System.out.println(win);
		gameOver = true;
	}

	//Setup all GUI components and ready game
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
		
		//Setup playing area
		playArea = new Rectangle[64];
		colours = new Paint[64];
		gridPane = new GridPane();
		int setupColour = WHITE;
		for(int y=0; y<8; y++) {
			for(int x=0; x<8; x++) {
				final int index = (8*y)+x;
				playArea[index] = new Rectangle();
				playArea[index].setWidth((screenWidth-100)/8);
				playArea[index].setHeight((screenHeight-100)/8);			
				
				if(setupColour==BLACK) {
					playArea[index].setFill(Color.CHOCOLATE);
					colours[index] = Color.CHOCOLATE;
				}
				else {
					playArea[index].setFill(Color.BLANCHEDALMOND);
					colours[index] = Color.BLANCHEDALMOND;
				}
				setupColour *= -1;
				gridPane.add(playArea[index],x,y,1,1);
				
				//TODO remove, onClick now on icons
				playArea[index].setOnMouseClicked( event -> {
					selectPiece(index);
				});
				
			}
			setupColour *= -1;
		}
		
		//Add title and play area to displayed objects list
		objectList.add(title);
		objectList.add(gridPane);
		
		//Populate Board with icons
		//icon positions are tied to the position of the gridpane object (play area), position is translated from center of the play area
		icons = new HashMap<Integer, ImageView>();
		for(int i=0; i<64; i++) {
			if(board.gameBoard[i] != null) {
				icons.put(i, new ImageView());
				icons.get(i).setImage(board.gameBoard[i].icon);
				icons.get(i).setFitHeight((screenWidth-100)/8);
				icons.get(i).setFitWidth((screenWidth-100)/8);
				int yRaw = (board.gameBoard[i].getPos())/8;
				int xRaw = (board.gameBoard[i].getPos())%8;
				double xTranslate = unit*(-3.5+xRaw);
				double yTranslate = unit*(-3.5+yRaw);
				icons.get(i).setTranslateX(xTranslate);
				icons.get(i).setTranslateY(yTranslate);
				icons.get(i).setMouseTransparent(true);
				
				objectList.add(icons.get(i));
			}
		}
		
		//Set positioning of play area and 
		gridPane.setAlignment(Pos.CENTER);
		StackPane.setAlignment(title, Pos.TOP_CENTER);
		
		//Add main layout to scene
		scene = new Scene(rootStackPane, screenWidth, screenHeight);
		scene.setFill(Color.INDIGO);
		
		//Display scene
		primaryStage.setScene(scene);
		primaryStage.show();
		
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

