/*
Filename: IK.java

Description:Java program to determine the positions robot's arms and
the angles of the robot's joints.

Names:
Jeroen Serdijn 10203249 serdijn
Michiel Folkers 10001820 mfolkers

Date: 21 June, 2012
*/

import java.lang.*;
import java.util.Vector;

public class IK 
{

	//Class containing the D-H representation of the robot arm
	private static RobotJoints robotJoints;
	
	/* Calculate roll, pitch, and yaw for the gripper of the robot arm plus
   	the value of the gripper itself.*/

	private static void handJointCalculation(GripperPosition pos,
                                             JointValues j) 
	{
		j.roll = 0; //Default value of the roll is always 0
		j.pitch = -90; /*Default value of the pitch is always -90,
		since the gripper is always faced downwards*/
		j.grip = pos.grip; /*Default value of the gripper, obtained
		from GripperPoition*/
	}

/* Calculate the wrist coordinates from the hand coordinates.
If the robot's last link has some length and the tip of the robot grabs
the piece. At what height is the start of the robot's last link?*/

private static Point wristCoordinatesCalculation(GripperPosition pos) 
{
	double x = pos.coords.x; //Obtain the grippercoordinates
	double y = pos.coords.y;
	double z = pos.coords.z;

	Point c = new Point(x,y,z); //convert 3 doubles to string
	return(c); //return string
}

/* Calculate the arm joints from the (x,y,z) coordinates of the wrist (the last
link).*/

private static void armJointCalculation(Point wristCoords,
              JointValues j) 
{
	//Obtain wristCoordinates and define the lenth of the arms as l1 and l2
	double x = wristCoords.x; 
	double y = wristCoords.y;
	double z = wristCoords.z;
	double l1 = 253.5;
	double l2 = 253.5;
	
	j.zed = 364 + z; /*the height of the zed is the height of the
	wristCoordinates plus 364*/

	//calculations in order to obtain the values of both c2 and s2
	double c2 = (x*x + y*y - l1*l1 - l2*l2) / (2*l1*l2);
    	double s2 = Math.sqrt( 1 - c2 * c2 );

	/*after obtaining the correct values of c2 and s2, we can calculate the
	Jointvalues for the shoulder and elbow*/
	j.shoulder = Math.toDegrees( ( Math.atan2(y,x) - 
	( Math.atan2( 253.5 * s2,  253.5  + 253.5 * c2 ) ) ) );

	/*using the Jointvalues of the shoulder and the elbow, we can calculate
	the  values of the correct Jointvalue of the yaw*/
	j.elbow = Math.toDegrees(Math.atan2(c2,s2)); 
	j.yaw = j.elbow / -2 - j.shoulder;
}

//Calculate the appropriate values for all joints for position pos
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

	// initialize the D-H representation
	robotJoints = new RobotJoints();
  
	for (int i =0; i < p.size(); i++) 
	{
      		GripperPosition pos = (GripperPosition) p.elementAt(i);
		/* To prevent errors, the arm will switch position when the
		next move is out of range. We improved the code to prevent
		unnecessary armswitches */
      		
		if (pos.coords.x < 0 )
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
	System.out.println ("**** THIS IS THE MOST AWESOME IK MODULE \n");

	// read the gripper positions as produced by PP.java
    	GripperPosition.read(p);
	inverseKinematics(p, j);

    	for (int i =0; i < j.size(); i++)
        	System.out.println((JointValues) j.get(i));
		JointValues.write(j);
}
}
