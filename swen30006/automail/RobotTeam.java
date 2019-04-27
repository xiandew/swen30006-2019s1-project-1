package automail;

import java.util.ArrayList;

public class RobotTeam {
	private ArrayList<Robot> members = null;
	private MailItem deliveryItem = null;
	private int current_floor;
	private int destination_floor;
	private int lastMoveTime;
	
	public RobotTeam() {
		members = new ArrayList<>();
		current_floor = Building.MAILROOM_LOCATION;
	}
	
	public void step() {
		if (current_floor == destination_floor) {
			members.get(0).delivery.deliver(deliveryItem);
			for (Robot member : members) {
				member.unregisterTeam();
			}
		} else {
			moveTowards(destination_floor);
		}
	}
	
	/**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    private void moveTowards(int destination) {
    	/** wait two time steps before moving */
    	if (Clock.Time() <= lastMoveTime + 2) {
    		return;
    	}
    	lastMoveTime = Clock.Time();
    	
    	/** update the current floor of the team */
        for (Robot member : members) {
			member.moveTowards(destination_floor);
		}
        current_floor = members.get(0).getCurrentFloor();
    }
	
	public void addToHands(MailItem mailItem) {
		deliveryItem = mailItem;
	}
	
	public void dispatch() {
		for (Robot member : members) {
			member.registerTeam(this);
		}
		setRoute();
		lastMoveTime = Clock.Time();
		System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), this, deliveryItem.toString());
	}
	
	/**
     * Sets the route for the team
     */
    private void setRoute() {
        /** Set the destination floor */
        destination_floor = deliveryItem.getDestFloor();
    }
	
	public String toString() {
		String team_id = " ";
		for (Robot member : members) {
			team_id += String.format("%s ", member.id);
		}
		return String.format("Team(%s)", team_id);
	}
	
	public MailItem getDeliveryItem() {
		return deliveryItem;
	}

	public int getWeightCapacity() {
		assert (members.size() <= 3);
		return
			members.size() == 1 ? Robot.INDIVIDUAL_MAX_WEIGHT :
			members.size() == 2 ? Robot.PAIR_MAX_WEIGHT :
			members.size() == 3 ? Robot.TRIPLE_MAX_WEIGHT : -1;
	}
	
	public ArrayList<Robot> getMemebers() {
		return members;
	}
	
	public void addMember(Robot robot) {
		members.add(robot);
	}
	
	public boolean hasMember(Robot robot) {
		return members.contains(robot);
	}
}
