package automail;

import java.util.ArrayList;

public class RobotTeam {
	private ArrayList<Robot> members = null;
	
	public RobotTeam() {
		members = new ArrayList<>();
	}
	
	public void step() {
		
	}
	
	public int getWeightCapacity() throws Exception {
		if (members.size() > 3) {
			throw new Exception("Invalid number of team members");
		}
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
