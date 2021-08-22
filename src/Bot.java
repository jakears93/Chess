import java.util.Random;

public class Bot extends Player{

    public Bot(String username) {this.setUsername(username);}

    public int[] getNextMove(Board board){
        int[] move = new int[2];
        boolean complete = false;
        Random rnd = new Random();
        int src;

        //Calculate moves for all board pieces
        //board.calculateTeamMoves(this.getPieceColour());
        while(!complete){
            src = rnd.nextInt(64);
            if(board.gameBoard[src] != null)
            {
                if(board.gameBoard[src].getColour() == this.getPieceColour())
                {
                    board.gameBoard[src].calculateMoves(board);
                    if(board.gameBoard[src].moves.size() > 0) {
                        move[0] = src;
                        move[1] = board.gameBoard[src].moves.get(0);
                        complete = true;
                    }
                }
            }
        }
        System.out.println("Moved");
        return move;
    }
}
