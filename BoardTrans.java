/*
Filename: IK.java

Description:Java program to determine the positions robot's arms and
the angles of the robot's joints.

Names:
Jeroen Serdijn 10203249 serdijn
Michiel Folkers 10001820 mfolkers

Date: 21 June, 2012
*/

import java.io.*;
import java.lang.*;

class BoardTrans
{
  	/*BoardTrans takes one optional argument, specifying the position on the
	field it should use. It defaults to b7.*/

	public static void main(String[] args)
  	{
    		String position;
		try 
		{ 
			position=args[0]; 
		}
    		catch(ArrayIndexOutOfBoundsException e) 
		{ 
			position="b7"; 
		}

    		StudentBoardTrans boardTrans = new StudentBoardTrans(position);

		// set up the board in starting position
    		boardTrans.board.setup();

   		// draw the board state
    		boardTrans.board.print();
    
    		/*You are now asked to access some data in the board structure.
     		Please print them and check your answers with the chess_board
		editor and the chess_piece editor from SCIL. */

    		try 
		{  
      			System.out.println("The dimensions of the squares on the
			board are " + boardTrans.getX() + " by "
			+ boardTrans.getY() + "mm");
  
      			System.out.println("The x,y coordinates of the board are
			" + boardTrans.getCoordX() + "," +
			boardTrans.getCoordY() );
 			
			System.out.println("The height of the piece at " +
			boardTrans.pos + " is " + boardTrans.getHeight() + "
			mm");
    
      			System.out.println("The color of the piece at " +
			boardTrans.pos + " is " + boardTrans.getSide() );
    		} 
		catch (Exception e) 
		{
      			System.out.println(e);
      			System.exit(1);
    		}
        
    		StudentBoardTrans.BoardLocation location =
		boardTrans.boardLocation;
    		BoardLocation realLocation = new BoardLocation(boardTrans.pos);

    		System.out.println("You think position " + boardTrans.pos + " is
		at (" + location.column + "," + location.row + "), the correct
		answer is (" + realLocation.column + "," + realLocation.row +
		")");

    		/* In order to be able to plan a path to certain position on the
		board you have to know where this position is in Cartesian (i.d.
		real world) coordinates: (x,y,z) in mm relative to the origin.
		Look at the picture of the chess board in the practical manual.
		Finish the method toCartesian() in StudentBoardTrans started
		below.*/

    		Point cartesian = new Point();
    		cartesian=boardTrans.toCartesian(location.column, location.row);

    		System.out.println("You think " + boardTrans.pos + " is at " +
		cartesian + ", the correct answer is " +
                boardTrans.board.toCartesian(boardTrans.pos));

    		// Let's turn the board 45 degrees
    		boardTrans.board.theta=45;

    		// recalculate cartesian
    		cartesian=boardTrans.toCartesian(location.column, location.row);

    		System.out.println("You think " + boardTrans.pos + " is at " +
		cartesian + ", the correct answer is "
		+ boardTrans.board.toCartesian(boardTrans.pos));

    		// Let's move the position of the board and turn it again
    		boardTrans.board.coords.x=100;
    		boardTrans.board.coords.y=200;
    		boardTrans.board.theta=-60;

    		// recalculate cartesian
    		cartesian=boardTrans.toCartesian(location.column, location.row);
		System.out.println("You think " + boardTrans.pos + " is at " +
		cartesian + ", the correct answer is " +
                boardTrans.board.toCartesian(boardTrans.pos));
	}
}

class StudentBoardTrans
{
	public ChessBoard board; // our board
  	public String position, pos; // the position we're going to examine
  	public BoardLocation boardLocation;
	private Point loc;

  	public StudentBoardTrans(String position)
  	{
    		board = new ChessBoard();
    		pos = position;
    		boardLocation = new BoardLocation(position);
  	}
	
	public StudentBoardTrans(ChessBoard board) 
	{
		this.board = board;
	}

	public StudentBoardTrans(ChessBoard board, String position) 
	{
		this.setPosition(position);
		this.board = board;
	}

	Point getPositionCoords() 
	{
		return toCartesian(this.pos);
	}

	//GET & SET position
	String getPosition() 
	{
		return this.pos;
	}
	void setPosition(String position) 
	{
		this.pos = position;
		boardLocation = new BoardLocation(position);
	}

	//GET & SET theta
	double getTheta() 
	{
		return this.board.theta;
	}
	void setTheta(double theta) 
	{
		this.board.theta = theta;
	}

	public double getX() 
	{
		return this.board.delta_x;
	}
	public double getY() 
	{
		return this.board.delta_y;
	}
	public double getHeight() 
	{
		double height;
		try 
		{
			height = this.board.getHeight(this.pos);
		} 
		catch(ChessBoard.NoPieceAtPositionException e)
		{
			height = 0.0;
		}
		return height;
	}

	double getSafePieceHeight() 
	{
		return getSafePieceHeight(this.pos);
	}
	
	double getSafePieceHeight(String pos) 
	{
		double height = 0.0;
		try 
		{ 
			/*Gripping height is two thirds of the piece to be
			gripped*/
			height = this.board.board_thickness +
			this.board.getHeight(pos) - 	
			(this.board.getHeight(pos) / 3);
		} 
		catch (ChessBoard.NoPieceAtPositionException e) 
		{
			System.out.println(e);
			System.exit(1);
		}
		return height;
	}
	
	double getCoordX() 
	{
		return this.board.coords.x;
	}

	double getCoordY() 
	{
		return this.board.coords.y;
	}

	double getCoordZ() 
	{
		return this.board.board_thickness;
	}

	Point getCoords() 
	{
		return this.board.coords;
	}

	String getSide() 
	{
		String side;
		try 
		{
			side = this.board.getSide(this.pos);
		} 
		catch(ChessBoard.NoPieceAtPositionException e) 
		{
			side = "none";
		}
		return side;
	}
  
	public Point toCartesian(String pos)
	{
		return toCartesian(boardLocation.getCol(pos.charAt(0)),
		boardLocation.getRow(pos.charAt(1)));
	}
	public Point toCartesian(int column, int row)
  	{

		double x = this.board.sur_x + ((7-column) * this.board.delta_x)
		+ (this.board.delta_x / 2); 
		double y = this.board.sur_y + ((7-row) * this.board.delta_y) +
		(this.board.delta_y /2);

		double[][] m = 
		{	
			{ 
				x 
			},
			{ 
				y  
			}
			,
			{ 
				0.0 
			}
		}
		;
		double t = Math.toRadians(this.board.theta);
		double[][] r_z = 
		{ 
			{ 
				Math.cos(t), -Math.sin(t), 0.0 
			}
			,
			{ Math.sin(t), Math.cos(t), 0.0 
			}
			,
			{
				 0.0, 0.0, 1.0
			} 
		}
		;
		Matrix xyz = new Matrix(m);
		Matrix rot = new Matrix(r_z);
		Matrix rotated_xyz = rot.times(xyz);

    	Point xyz_point = new Point();
	xyz_point.x = (-1) * rotated_xyz.data[0][0] + this.getCoordX();
	xyz_point.y = rotated_xyz.data[1][0]+this.getCoordY();
	xyz_point.z = rotated_xyz.data[2][0]+this.getCoordZ();
	return(xyz_point);
  	}

  	class BoardLocation
	{
    		public int row;
    		public int column;
      
		public BoardLocation() 
		{
		}

	public BoardLocation(int column, int row) 
	{
    		this.row = row;
		this.column = column;
	}
	public BoardLocation(String loc) 
	{
		this.column = this.getCol(loc.charAt(0));
		this.row = this.getRow(loc.charAt(1));
	}

	public int getCol(char col) 
	{
		String columns = "abcdefgh";
		return columns.indexOf(col);
  	}
	public int getRow(char row) 
	{
		return Integer.parseInt(String.valueOf(row))-1;
	}
	public String getPos(int column, int row) 
	{
		String columns = "abcdefgh";
		return (columns.charAt(column) +""+ row);
	}
	}
}