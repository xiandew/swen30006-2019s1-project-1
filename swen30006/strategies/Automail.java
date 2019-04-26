package strategies;

import java.util.ArrayList;

import automail.IMailDelivery;
import automail.Robot;
import automail.RobotTeam;

public class Automail {
	      
    public Robot[] robots;
    public ArrayList<RobotTeam> robotTeams;
    public IMailPool mailPool;
    
    public Automail(IMailPool mailPool, IMailDelivery delivery, int numRobots) {
    	// Swap between simple provided strategies and your strategies here
    	    	
    	/** Initialize the MailPool */
    	
    	this.mailPool = mailPool;
    	
    	/** Initialize robots */
    	robots = new Robot[numRobots];
    	for (int i = 0; i < numRobots; i++) robots[i] = new Robot(delivery, mailPool);
    	
    	/** Initialize robotTeams */
    	this.robotTeams = new ArrayList<>();
    }
    
}
