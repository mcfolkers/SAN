
// Jeroen Serdijn & Michiel Folkers

import java.io.*;
import java.lang.*;
import java.util.Vector;

public class PP 
{
	private static double SAFE_HEIGHT=200; //default variables
	private static double LOW_HEIGHT=40;
	private static double OPEN_GRIP=30;
	private static double CLOSED_GRIP=0;

	public static void main(String[] args)
	{
		Vector<GripperPosition> p = new Vector<GripperPosition>();
		ChessBoard b;
		String computerFrom, computerTo;

		System.out.println("**** THIS IS THE STUDENT PP MODULE IN JAVA"); 
		System.out.println("**** The computer move was "+ args[0]); 

		/* Read possibly changed board position */
		if(args.length > 1)
		{
			double x=Double.parseDouble(args[1]),
            y=Double.parseDouble(args[2]),
            theta=Double.parseDouble(args[3]);
      
			Point boardPosition=new Point(x,y,0);
			System.out.println("**** Chessboard is at (x,y,z,theta): ("
            + x + ", " + y + ", 0, " + theta + ")");

			b = new ChessBoard(boardPosition, theta);
		}
		else
		b = new ChessBoard();

		/* Read the board state*/
		b.read();
		/* print the board state*/
		System.out.println("**** The board before the move was:");       
		b.print();
   
		computerFrom = args[0].substring(0,2);
		computerTo = args[0].substring(2,4);

		/* plan a path for the move */
		if (b.hasPiece(computerTo)) moveToGarbage(computerTo, b, p);
		if(!lowPath(computerFrom, computerTo, b, p))
			highPath(computerFrom, computerTo, b, p);

		/* move the computer piece */
		try 
		{
			b.movePiece(computerFrom, computerTo);
		} 
		catch (ChessBoard.NoPieceAtPositionException e) 
		{
				System.out.println(e);
				System.exit(1);
		}

    System.out.println("**** The board after the move was:");
    /* print the board state*/
    b.print();
    
    /* after done write the gripper positions */
    GripperPosition.write(p);
	}
static double getGripHeight(String pos, ChessBoard b) {

	double height = 0.0;
	try { //Gripping height is two thirds of the piece to be gripped
		height = b.getHeight(pos) - (b.getHeight(pos) / 3);
	} catch (ChessBoard.NoPieceAtPositionException e) {
		System.out.println(e);
		System.exit(1);
	}
	return height;
}
private static void highPath(String from, String to, 
ChessBoard b, Vector<GripperPosition> p) 
{
	System.out.println("**** In high path");

	StudentBoardTrans studentBoardTrans = new StudentBoardTrans(b, from);
	
	Point temp = studentBoardTrans.getPositionCoords();
	
	//The safest height for placing a gripping is always grip/piece height and height of surface
	double safePieceHeight = studentBoardTrans.getSafePieceHeight();
	
	//1 -- FROM -- UP-HIGH -- OPEN
	temp.z = SAFE_HEIGHT;
	p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	//2 -- FROM -- SEMI-LOW -- OPEN
	temp.z = LOW_HEIGHT + safePieceHeight;
	p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	//3 -- FROM -- GRIP-LOW -- OPEN
	temp.z = safePieceHeight;
	p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	//4 -- FROM -- GRIP-LOW -- CLOSED
	p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));
	//5 -- FROM -- UP-HIGH -- CLOSED
	temp.z = SAFE_HEIGHT;
	p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));
	
	//6 -- TO -- UP-HIGH -- CLOSED
	studentBoardTrans.setPosition(to);
	temp = studentBoardTrans.getPositionCoords();
	temp.z = SAFE_HEIGHT;
	p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));
	//7 -- TO -- SEMI-LOW -- CLOSED
	temp.z = LOW_HEIGHT + safePieceHeight;
	p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));
	//8 -- TO -- GRIP-LOW -- CLOSED
	temp.z = safePieceHeight;
	p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));
	//9 -- TO -- GRIP-LOW -- OPEN
	p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	//10 -- TO -- UP-HIGH -- OPEN
	temp.z = SAFE_HEIGHT;
	p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));   
}
  
  
static GripperPosition openGrip(Point point, double angle) 
{
  return (new GripperPosition(point, angle, OPEN_GRIP));
}

static GripperPosition closeGrip(Point point, double angle) 
{
	return (new GripperPosition(point, angle, CLOSED_GRIP));
}

static GripperPosition toSafeHeight(Point point, double angle) 
{
	return (new GripperPosition(point, angle, SAFE_HEIGHT));
}

static GripperPosition toLowHeight(Point point, double angle) 
{
	return (new GripperPosition(point, angle, LOW_HEIGHT));
}

static GripperPosition toPosition(Point point, double angle) 
{
	return (new GripperPosition(point, angle, SAFE_HEIGHT));
}

private static void moveToGarbage(String to, ChessBoard b, Vector<GripperPosition> g) 
{
	System.out.println("**** In movoToGarbage");
	StudentBoardTrans studentBoardTrans = new StudentBoardTrans(b, to);
	
	Point garbage = studentBoardTrans.toCartesian("h4");
	garbage.x += 100;

	Point temp = studentBoardTrans.getPositionCoords();
	
	double safePieceHeight = studentBoardTrans.getSafePieceHeight();
	
	//1 -- TO -- UP-HIGH -- OPEN
	temp.z = SAFE_HEIGHT;
	g.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	//2 -- TO -- SEMI-LOW -- OPEN
	temp.z = LOW_HEIGHT + safePieceHeight;
	g.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	//3 -- TO -- GRIP-LOW -- OPEN
	temp.z = safePieceHeight;
	g.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	//4 -- TO -- GRIP-LOW -- CLOSED
	g.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));
	//5 -- TO -- UP-HIGH -- CLOSED
	temp.z = SAFE_HEIGHT;
	g.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));
	
	//6 -- GARBAGE -- UP-HIGH -- CLOSED
	garbage.z = SAFE_HEIGHT;
	g.add(new GripperPosition(garbage, b.theta,CLOSED_GRIP ));
	//7 -- GARBAGE -- SEMI-LOW -- CLOSED
	garbage.z = LOW_HEIGHT + safePieceHeight;
	g.add(new GripperPosition(garbage, b.theta,CLOSED_GRIP ));
	//8 -- GARBAGE -- GRIP-LOW -- OPEN
	//garbage.z = safePieceHeight;
	//g.add(new GripperPosition(garbage, b.theta,CLOSED_GRIP ));
	//9 -- GARBAGE -- SEMI-LOW -- OPEN
	g.add(new GripperPosition(garbage, b.theta,OPEN_GRIP ));
	//10 -- GARBAGE -- UP-HIGH -- OPEN
	garbage.z = SAFE_HEIGHT;
	g.add(new GripperPosition(garbage, b.theta,OPEN_GRIP ));
}


static Vector<BoardLocation> getPlannedPath(String from, String to, ChessBoard b) 
{
	//start from goal node b
	BoardLocation start = new BoardLocation(to);
	BoardLocation end = new BoardLocation(from);
	BoardLocation node = new BoardLocation(end.column, end.row);
	BoardLocation bestNode = end;
	BoardLocation prevNode = bestNode;
	BoardLocation tempNode = prevNode;
	Vector<BoardLocation> n = new Vector<BoardLocation>();
	DistanceMatrix dm = new DistanceMatrix();
	//as long as the node isn't pathed to the from node
	int i = 0;
	while((bestNode.column != start.column || bestNode.row != start.row) && !dm.notPossible(to) && i < 100)
	{ i++;
		node = new BoardLocation(tempNode.column+1, tempNode.row);
		if(isValid(node.column, node.row, b)) 
		{
			bestNode = diff(node.column, start.column) + diff(node.row, start.row) >
			diff(bestNode.column, start.column) + diff(bestNode.row, start.row) ?
			bestNode : node;
		}
		node = new BoardLocation(tempNode.column-1, tempNode.row);
		if(isValid(node.column, node.row,b )) 
		{
			bestNode = diff(node.column, start.column) + diff(node.row, start.row) >
			diff(bestNode.column, start.column) + diff(bestNode.row, start.row) ?
			bestNode : node;
		}
		node = new BoardLocation(tempNode.column, tempNode.row+1);
		if(isValid(node.column, node.row, b)) 
		{
			bestNode = diff(node.column, start.column) + diff(node.row, start.row) >
			diff(bestNode.column, start.column) + diff(bestNode.row, start.row) ?
			bestNode : node;
		}
		node = new BoardLocation(tempNode.column, tempNode.row-1);
		if(isValid(node.column, node.row, b)) 
		{
			bestNode = diff(node.column, start.column) + diff(node.row, start.row) >
			diff(bestNode.column, start.column) + diff(bestNode.row, start.row) ?
			bestNode : node;
			//if ((diff(node.column, start.column) == 1 || diff(node.row, start.row) == 1) && (node.column != start.column || node.row != start.row)) bestNode
		}
		  
		if((bestNode.column != prevNode.column && prevNode.row != bestNode.row))
		{System.out.println("in condition 1");
			//new pos
			n.add(prevNode);
			prevNode = bestNode;
		}
		if(bestNode.column != start.column && bestNode.row == start.row && diff(bestNode.column, start.column)==1){System.out.println("in condition 2");
			n.add(bestNode);
		} else if (bestNode.column == start.column && bestNode.row != start.row && diff(bestNode.row, start.row)==1){System.out.println("in condition 3");
			n.add(bestNode);
		}System.out.println("END");
		//if(bestNode.column == start.column && bestNode == start.column)
		//{
		//	n.add(bestNode);
		//}
		tempNode = bestNode;
		//System.out.println(bestNode.column+","+bestNode.row);
	}
	if(bestNode.column != start.column && bestNode.row == start.row && diff(bestNode.column, start.column)==1){
			System.out.println("in condition 2");
			n.add(bestNode);
		} else if (bestNode.column == start.column && bestNode.row != start.row && diff(bestNode.row, start.row)==1){
			System.out.println("in condition 3");
			n.add(bestNode);
		}
System.out.println("IN PATH");
	return n;//positions;
}
  
// static boolean isBetter(BoardLocation a, BoardLocation b) {
  
static boolean isValid(int x, int y, ChessBoard b) 
{
	boolean valid = false;
	if (x < 8 && x > -1 && y < 8 && y > -1) 
	{
		  
		//TODO: any function to test if a chesspiece is there or not
		//something like this, if there is no piece then true.
		String columns = "abcdefgh";
		 
		valid = b.hasPiece((columns.charAt(y) +""+ x)) == false ? true : false;
		  
	}
	return valid;
}
static double diff(double a, double b) 
{
	return Math.abs(a-b);
}
private static boolean lowPath(String from, String to, 
ChessBoard b, Vector<GripperPosition> p) 
{
	System.out.println("**** In low path");
	StudentBoardTrans sbt = new StudentBoardTrans(b, from);
	
	double safePieceHeight = sbt.getSafePieceHeight();
	
	//FIND A PATH
	Point temp = sbt.toCartesian(sbt.boardLocation.column,sbt.boardLocation.row);
	Vector<BoardLocation> n = new Vector<BoardLocation>(getPlannedPath(from, to, b));
	
	//IF NO PATH THEN FAIL
	if (n.size() == 0)
		return false;

	//1 -- FROM -- SEMI-LOW -- OPEN
	temp.z = LOW_HEIGHT + safePieceHeight;
	p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	//2 -- FROM -- GRIP-LOW -- OPEN
	temp.z = safePieceHeight;
	p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	//3 -- FROM -- GRIP-LOW -- CLOSED
	p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));
	//4 -- PATH -- SEMI-LOW -- CLOSED
	for (int i = 0; i < n.size(); i++) 
	{
		temp = sbt.toCartesian(n.elementAt(i).column, n.elementAt(i).row);
		temp.z = LOW_HEIGHT + safePieceHeight;
		p.add(new GripperPosition(temp, b.theta, CLOSED_GRIP));
	}	
	//5 -- TO -- SEMI-LOW -- CLOSED
	sbt = new StudentBoardTrans(to);
	temp = sbt.toCartesian(sbt.boardLocation.column,sbt.boardLocation.row);
	temp.z = LOW_HEIGHT + safePieceHeight;
	p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));
	//6 -- TO -- GRIP-LOW -- CLOSED
	temp.z = safePieceHeight;
	p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));
	//7 -- TO -- GRIP-LOW -- OPEN
	p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	//8 -- TO -- SEMI-LOW -- OPEN	
	temp.z = LOW_HEIGHT + safePieceHeight;
	p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));
	
	//return success
	return true;
}
}