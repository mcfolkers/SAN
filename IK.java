//Jeroen Serdijn & Michiel Folkers

import java.lang.*;
import java.util.Vector;

public class IK 
{

	// Class containing the Denavit-Hartenberg representation of the robot arm
	private static RobotJoints robotJoints;
	/* Calculate roll, pitch, and yaw for the gripper of the robot arm plus
   	* the value of the gripper itself.
   	*/

	private static void handJointCalculation(GripperPosition pos,
                                             JointValues j) {

	j.roll = 0; //standaard waarde van roll is altijd gelijk aan 0
	j.pitch = -90; // standaard waarde van de pitch is altijd gelijk aan -90
 	 // standaard waarde van de jaw is altijd gelijk aan 0
	j.grip = pos.grip; // dit is de waarde van de gripper, deze is afhankelijk van of deze open of dicht is.
}

/* Calculate the wrist coordinates from the hand coordinates.
* If the robot's last link has some length and the tip of the robot grabs
* the piece. At what height is the start of the robot's last link?
*/

private static Point wristCoordinatesCalculation(GripperPosition pos) 
{
	double x = pos.coords.x;
	double y = pos.coords.y;
	double z = pos.coords.z;
		//System.out.println("**** test"
                //               + x + ", " + y + "," + z + ")");
	Point c = new Point(x,y,z); //waardes omzetten naar een string
	return(c);
}
/*
/* Calculate the arm joints from the (x,y,z) coordinates of the wrist (the
* last link).
*/

private static void armJointCalculation(Point wristCoords,
              JointValues j) 
{

	double x = wristCoords.x;
	double y = wristCoords.y;
	double l1 = 253.5;
	double l2 = 253.5;
	double z = wristCoords.z;
	
	double c2 = (x*x + y*y - l1*l1 - l2*l2) / (2*l1*l2);
    	double s2 = Math.sqrt( 1 - c2 * c2 );

    	j.zed = 364 + z; //deze waarde is hoogte wrist plus 364
	j.shoulder = Math.toDegrees( ( Math.atan2(x,y) - ( Math.atan2( 253.5 * s2,  253.5  + 253.5 * c2 ) ) ) );
	j.elbow = Math.toDegrees(Math.atan2(s2,c2)); 
	j.yaw = j.elbow / -2 - j.shoulder;
}

/* Calculate the appropriate values for all joints for position pos.
*/
  
private static JointValues jointCalculation(GripperPosition pos) 
{
	JointValues j = new JointValues();
	Point wristCoords;

	handJointCalculation(pos,j);
	wristCoords=wristCoordinatesCalculation(pos);
	armJointCalculation(wristCoords, j);

return(j);
}

private static void inverseKinematics(Vector<GripperPosition> p, Vector<JointValues> j) 
{

	// initialize the Denavit-Hartenberg representation
	robotJoints = new RobotJoints();
  
	for (int i =0; i < p.size(); i++) 
	{
      		GripperPosition pos = (GripperPosition) p.elementAt(i);
      		/* correct for errors in the arm*/
      		// if left on the board then assume left-hand configuration
      		// if right on the board then assume right-hand configuration
      		
		if (pos.coords.x < 0)
        		RobotJoints.correctCartesian(pos, 0);
      		else 
        		RobotJoints.correctCartesian(pos, 1);
      			j.addElement(jointCalculation(pos));
    	}
}

public static void main(String[] args) 
{
	Vector<GripperPosition> p = new Vector<GripperPosition>();
	Vector<JointValues> j = new Vector<JointValues>();
	System.out.println ("**** THIS IS THE STUDENT IK MODULE IN JAVA\n");

	// read the gripper positions as produced by PP.java
    	GripperPosition.read(p);
	inverseKinematics(p, j);

    	for (int i =0; i < j.size(); i++)
        	System.out.println((JointValues) j.get(i));
		JointValues.write(j);
}
}
