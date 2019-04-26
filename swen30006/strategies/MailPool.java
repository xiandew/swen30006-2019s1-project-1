package strategies;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.RobotTeam;
import exceptions.ItemTooHeavyException;

public class MailPool implements IMailPool {

	private class Item {
		int priority;
		int destination;
		MailItem mailItem;
		// Use stable sort to keep arrival time relative positions
		
		public Item(MailItem mailItem) {
			priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
			destination = mailItem.getDestFloor();
			this.mailItem = mailItem;
		}
	}
	
	public class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item i1, Item i2) {
			int order = 0;
			if (i1.priority < i2.priority) {
				order = 1;
			} else if (i1.priority > i2.priority) {
				order = -1;
			} else if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}
	
	private LinkedList<Item> pool;
	private LinkedList<Robot> robots;
	private RobotTeam robotTeam;

	public MailPool(int nrobots){
		// Start empty
		pool = new LinkedList<Item>();
		robots = new LinkedList<Robot>();
		robotTeam = new RobotTeam();
	}

	public void addToPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		pool.add(item);
		pool.sort(new ItemComparator());
	}
	
	@Override
	public void step() throws ItemTooHeavyException {
		boolean teamRequired = false;
		try{
			ListIterator<Robot> i = robots.listIterator();
			while (i.hasNext()) loadRobot(i);
		} catch (Exception e) {
			teamRequired = true;
        }
		if (teamRequired) {
			try {
				loadRobotTeam();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadRobotTeam() throws Exception {
		Robot frontRobot = robots.getFirst();
		/** if the front robot has got a MailItem in its hand */
		if (!frontRobot.isEmpty()) {
			frontRobot.dispatch();
			robots.removeLast();
		}
		
		MailItem currMailItem = pool.getFirst().mailItem;
		/** if the mailItem is way too heavy */
		if (currMailItem.getWeight() > Robot.TRIPLE_MAX_WEIGHT) {
			throw new ItemTooHeavyException();
		}
		
		boolean enoughWaitingRobots = false;
		
		/** add robots to a team */
		robotTeam.addMember(frontRobot);
		for (Robot robot : robots) {
			robotTeam.addMember(robot);
			if (robotTeam.getWeightCapacity() >= currMailItem.getWeight()) {
				enoughWaitingRobots = true;
				break;
			}
		}
		
		/** if there are enough free robots to carry the front mailItem */
		if (enoughWaitingRobots) {
			/** remove the front mailItem */
			pool.removeFirst();
			
			/** add the front mailItem to team's hands. 
			 * When carrying as a team, each robot will not carry in their tube.
			 */
			robotTeam.addToHands(currMailItem);
			
			robotTeam.dispatch();
			robotTeam.removeFromPool(pool);
		}
	}
	
	private void loadRobot(ListIterator<Robot> i) throws ItemTooHeavyException {
		Robot robot = i.next();
		assert(robot.isEmpty());
		// System.out.printf("P: %3d%n", pool.size());
		ListIterator<Item> j = pool.listIterator();
		if (pool.size() > 0) {
			try {
			robot.addToHand(j.next().mailItem); // hand first as we want higher priority delivered first
			j.remove();
			if (pool.size() > 0) {
				robot.addToTube(j.next().mailItem);
				j.remove();
			}
			robot.dispatch(); // send the robot off if it has any items to deliver
			i.remove();       // remove from mailPool queue
			} catch (Exception e) { 
	            throw e; 
	        } 
		}
	}

	@Override
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}

}
