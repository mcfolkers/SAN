
public class ChessBoard {

	public class NoPieceAtPositionException extends Exception {

	}

	public double theta;
	public Point coords;
	public double delta_x;
	public double delta_y;
	public double board_thickness;
	public double sur_x;
	public double sur_y;
	public ChessPiece aliveChessPieces;

	public ChessBoard(Point boardPosition, double theta) {
		this.coords = boardPosition;
		this.theta = theta;
	}

	public ChessBoard() {
		// TODO Auto-generated constructor stub
	}

	public void read() {
		// TODO Auto-generated method stub
		
	}

	public void print() {
		// TODO Auto-generated method stub
		
	}

	public void movePiece(String computerFrom, String computerTo) throws NoPieceAtPositionException {
		// TODO Auto-generated method stub
		
	}

	public void setup() {
		// TODO Auto-generated method stub
		
	}

	public String toCartesian(String pos) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getHeight(String pos) throws ChessBoard.NoPieceAtPositionException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getSide(String pos) throws ChessBoard.NoPieceAtPositionException {
		// TODO Auto-generated method stub
		return null;
	}

}
