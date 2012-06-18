/*
 * PP.java
 * Assignment for the Path planning part of the ZSB lab course.
 *
 * This you will work on writing a function called highPath() to move a
 * chesspiece across the board at a safe height. By raising the gripper 20 cm
 * above the board before moving it over the board you don't risk hitting any
 * other pieces on the board. This means you don't have to do any pathplanning
 * yet.
 *
 * Input of this program is a commandline argument, specifying the computer 
 * (white) move. Your job is to find the correct sequence of GripperPositions
 * (stored in Vector p) to pick up the correct white piece and deposit it at
 * its desired new location. Read file
 * /opt/stud/robotics/hints/HIGHPATH_POSITIONS to see what intermediate
 * positions you should calculate.
 *
 * To run your program, fire up playchess or one of its derviates endgame* and
 * the umirtxsimulator. In the simulator you can see the effect of your path
 * planning although the board itself is not simulated. When you think you've
 * solved this assignment ask one of the lab assistents to verify it and let
 * it run on the real robot arm.
 * 
 * You can also compare your solution with the standard PP solution outside
 * playchess by running in a shell:
 * java PPstandard e2e4
 * cat positions.txt
 * java PP e2e4
 * cat positions.txt
 *
 *
 * 
 * Nikos Massios, Matthijs Spaan <mtjspaan@science.uva.nl>
 * $Id: Week2.java,v a4f44ea5d321 2008/06/16 09:18:44 obooij $
 */

import java.io.*;
import java.lang.*;
import java.util.Vector;

public class PP {
  private static double SAFE_HEIGHT=200;
  private static double LOW_HEIGHT=40;
  private static double OPEN_GRIP=30;
  private static double CLOSED_GRIP=0;

  public static void main(String[] args){
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
    highPath(computerFrom, computerTo, b, p);

    /* move the computer piece */
    try {
      b.movePiece(computerFrom, computerTo);
    } catch (ChessBoard.NoPieceAtPositionException e) {
      System.out.println(e);
      System.exit(1);
    }

    System.out.println("**** The board after the move was:");       
    /* print the board state*/
    b.print();
    
    /* after done write the gripper positions */
    GripperPosition.write(p);
  }

  private static void highPath(String from, String to, 
           ChessBoard b, Vector<GripperPosition> p) {

    System.out.println("**** In high path"); 

    // ???? Write this function

    // Use the boardLocation and toCartesian methods you wrote:
    StudentBoardTrans studentBoardTrans = new StudentBoardTrans(from);
	
	
	//fromColumn = studentBoardTrans.boardLocation.column;
    //fromRow = studentBoardTrans.boardLocation.row;
	//fromX = studentBoardTrans.boardLocation.x ; //x,y,z oproepen, moet nog een return voor komen
	//fromY = studentBoardTrans.board.y;
	//fromZ = studentBoardTrans.board.z;
	//Hiermee zou ik de board column en row moeten krijgen, de juiste positie van het stuk dat we gaan verplaatsen
    //p.add(toSafeHeight(b.toCartesian(from), b.theta));

 //p.add(new GripperPosition(studentBoardTrans.toCartesian(from), b.theta, SAFE_HEIGHT));
Point temp = studentBoardTrans.toCartesian(studentBoardTrans.boardLocation.column,studentBoardTrans.boardLocation.row);
//1
temp.z = SAFE_HEIGHT;
 p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));

//2
temp.z = LOW_HEIGHT;
 p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));

//3
try {
temp.z = b.getHeight(from) /2;
} catch(ChessBoard.NoPieceAtPositionException e) {}
 p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));

//4
try{
temp.z = b.getHeight(from) /2;
} catch(ChessBoard.NoPieceAtPositionException e) {}
 p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));

//5
temp.z = SAFE_HEIGHT;
 p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));

//6
studentBoardTrans = new StudentBoardTrans(to);
temp = studentBoardTrans.toCartesian(studentBoardTrans.boardLocation.column,studentBoardTrans.boardLocation.row);
temp.z = SAFE_HEIGHT;
 p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));

//7
try{
temp.z = LOW_HEIGHT + (b.getHeight(to) / 2);
} catch(ChessBoard.NoPieceAtPositionException e) {}
 p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));

//8
try{
temp.z = (LOW_HEIGHT /2) + (b.getHeight(to) /2);
} catch(ChessBoard.NoPieceAtPositionException e) {}
 p.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));

//9
try{
temp.z = b.getHeight(to) /2;
} catch(ChessBoard.NoPieceAtPositionException e) {}
 p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));

//10
temp.z = SAFE_HEIGHT;
 p.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));




















/*
	p.add(toPosition(b.toCartesian(from), b.theta));
	p.add(openGrip(b.toCartesian(from), b.theta));
	p.add(toLowHeight(b.toCartesian(from), b.theta));
	p.add(closeGrip(b.toCartesian(from), b.theta));
	p.add(toSafeHeight(b.toCartesian(from), b.theta));
	p.add(toPosition(b.toCartesian(to), b.theta));
	p.add(toLowHeight(b.toCartesian(to), b.theta));
	p.add(openGrip(b.toCartesian(to), b.theta));
	p.add(toSafeHeight(b.toCartesian(to), b.theta));
    */


    
     //Point tempPoint;
    // GripperPosition temp;
     //tempPoint = new Point(x-coordinate, y-coordinate, z-coordinate);
     //temp = new GripperPosition(tempPoint, angle, CLOSED_GRIP/OPEN_GRIP);
     
	 //Now you only have to add it at the end of Vector p.
     
  }
  
  
  static GripperPosition openGrip(Point point, double angle) {
	  return (new GripperPosition(point, angle, OPEN_GRIP));
  }
  static GripperPosition closeGrip(Point point, double angle) {
	  return (new GripperPosition(point, angle, CLOSED_GRIP));
  }
  static GripperPosition toSafeHeight(Point point, double angle) {
	  return (new GripperPosition(point, angle, SAFE_HEIGHT));
  }
  static GripperPosition toLowHeight(Point point, double angle) {
	  return (new GripperPosition(point, angle, LOW_HEIGHT));
  }
  static GripperPosition toPosition(Point point, double angle) {
	  return (new GripperPosition(point, angle, SAFE_HEIGHT));
  }

  private static void moveToGarbage(String to, ChessBoard b, Vector<GripperPosition> g) {

    /* When you're done with highPath(), incorporate this function.
     * It should remove a checked piece from the board.
     * In main() you have to detect if the computer move checks a white
     * piece, and if so call this function to remove the white piece from
     * the board first.
     */
StudentBoardTrans studentBoardTrans = new StudentBoardTrans("h4");
	  Point garbage = studentBoardTrans.toCartesian(studentBoardTrans.boardLocation.column,studentBoardTrans.boardLocation.row);
		garbage.x += 50;
studentBoardTrans = new StudentBoardTrans(to);
    System.out.println("**** In movoToGarbage"); 
    //highPath(to, "offboard", b, g)
    //lowPath()
	
	Point temp = studentBoardTrans.toCartesian(studentBoardTrans.boardLocation.column,studentBoardTrans.boardLocation.row);
//1
temp.z = SAFE_HEIGHT;
 g.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));

//2
temp.z = LOW_HEIGHT;
 g.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));

//3
try {
temp.z = b.getHeight(to) /2;
} catch(ChessBoard.NoPieceAtPositionException e) {}
 g.add(new GripperPosition(temp, b.theta,OPEN_GRIP ));

//4
try{
temp.z = b.getHeight(to) /2;
} catch(ChessBoard.NoPieceAtPositionException e) {}
 g.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));

//5
temp.z = SAFE_HEIGHT;
 g.add(new GripperPosition(temp, b.theta,CLOSED_GRIP ));

//6
 g.add(new GripperPosition(garbage, b.theta,CLOSED_GRIP ));

//7
try{
temp.z = LOW_HEIGHT + (b.getHeight(to) / 2);
} catch(ChessBoard.NoPieceAtPositionException e) {}
 g.add(new GripperPosition(garbage, b.theta,CLOSED_GRIP ));

//8
try{
temp.z = (LOW_HEIGHT /2) + (b.getHeight(to) /2);
} catch(ChessBoard.NoPieceAtPositionException e) {}
 g.add(new GripperPosition(garbage, b.theta,CLOSED_GRIP ));

//9
try{
temp.z = b.getHeight(to) /2;
} catch(ChessBoard.NoPieceAtPositionException e) {}
 g.add(new GripperPosition(garbage, b.theta,OPEN_GRIP ));

//10
temp.z = SAFE_HEIGHT;
 g.add(new GripperPosition(garbage, b.theta,OPEN_GRIP ));
    


	    //p.add(toSafeHeight(b.toCartesian(to), b.theta));
	    //path plan
	/*	p.add(toPosition(b.toCartesian(to), b.theta));
		p.add(openGrip(b.toCartesian(to), b.theta));
		//p.add(toLowHeight(b.toCartesian(from), b.theta));
		p.add(closeGrip(b.toCartesian(to), b.theta));
		//p.add(toSafeHeight(b.toCartesian(to), b.theta));
		//path plan
		p.add(toPosition(garbage, b.theta));
		//p.add(toLowHeight(garbage, b.theta));
		p.add(openGrip(garbage, b.theta));
		//p.add(toSafeHeight(garbage, b.theta));

	
*/	
  }
  
  private static void lowPath(String from, String to, 
          ChessBoard b, Vector<GripperPosition> p) {
	  /*
	    //p.add(toSafeHeight(b.toCartesian(from), b.theta));
	    //path plan
		p.add(toPosition(b.toCartesian(from), b.theta));
		p.add(openGrip(b.toCartesian(from), b.theta));
		//p.add(toLowHeight(b.toCartesian(from), b.theta));
		p.add(closeGrip(b.toCartesian(from), b.theta));
		//p.add(toSafeHeight(b.toCartesian(from), b.theta));
		//path plan
		p.add(toPosition(b.toCartesian(to), b.theta));
		//p.add(toLowHeight(b.toCartesian(to), b.theta));
		p.add(openGrip(b.toCartesian(to), b.theta));
		//p.add(toSafeHeight(b.toCartesian(to), b.theta));
	*/
  }
}