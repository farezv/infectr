import java.lang.Integer;
import java.util.*;

/* This class is probably doing a lot more than it should
Ideally, I would have helper classes in separate modules
NOTE: I had to make all methods/fields static to be able to call them
from the main method's static context */
public class Infectr {

	private static int CURRENT_VERSION = 1;

	/* In production, these data structures would be in a database */
	private static HashMap<Integer, User> users;
	private static ArrayList<Integer> infectedUsers;
	private static HashMap<Integer, Group> groups;
	private static ArrayList<Group>	sortedGroups;	

	public static void main(String[] args) {
		// Proceed if an arg is provided
		if(args.length != 0) {
			int firstArg, secondArg;
			firstArg = secondArg = 0;
			try {
				firstArg = Integer.parseInt(args[0]);
				if(args[1] != null) secondArg = Integer.parseInt(args[1]);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			// Proceed if the arg is valid
			if(firstArg <= 0) {
				System.out.println("Please enter a number greater than zero");
				return;
			}
			
			users = new HashMap<Integer, User>(firstArg);
			for(int i = 0; i < firstArg; i++) {
				// Every 10th user is a "coach"
				if(i%10 == 0) {
					createUser("coach", i);
				} else {
					createUser("student", i);
				}
			}
			infectedUsers = new ArrayList<Integer>();
			groups = new HashMap<Integer, Group>();
			
			/* I would setup a separate testing package with InfectrTest.java in a production codebase, 
			but I'm just going to call methods here to keep it simple and self contained */	
			InfectrTest.testSingleUserInDisconnectedGraph();
			InfectrTest.testTotalInfection();

			sortedGroups = new ArrayList<Group>(groups.values());
			if(secondArg > 0 && secondArg < firstArg) {
				InfectrTest.testLimitedInfectionWithGroups(secondArg);
				InfectrTest.testExactInfectionWithGroups(secondArg);
			} else System.out.println("\nEnter a valid second argument to test Limited & Exact infection, like 'java Infectr 30 3' \nThis will try to infect approx. 3 users out of 30 (limited), and exactly 3 out of 30 respectively");

		} else System.out.println("Please enter the number of users you'd like to create, like 'java Infectr 30'");
	}

	/* Creates and adds user to a HashTable. In production, the HashTable would be in a db */
	public static void createUser(String name, int uid) {
		User u = new User(name, uid, CURRENT_VERSION);
		users.put(uid, u);
	}

	/* Recursively infects all users connected by "coaching" and "is coached by" relations */
	public static void infectAll(int uid) {
		if(!users.isEmpty()) {
			User user = users.get(uid);
			if(user != null && !user.isInfected(CURRENT_VERSION + 1)) {
				// Infect the specifed user
				user.setVersion(CURRENT_VERSION + 1);
				infectedUsers.add(uid);
				// Infect its coaches and students
				for(int studentId: user.getStudents()) {
					infectAll(studentId);
				}
				for(int coachId: user.getCoaches()) {
					infectAll(coachId);
				}
			}	
		}
	}

	/* Infects one user */
	public static void infectOneUser(int uid) {
		if(!users.isEmpty()) {
			User user = users.get(uid);
			if(user != null && !user.isInfected(CURRENT_VERSION + 1)) {
				// Infect the specifed user
				user.setVersion(CURRENT_VERSION + 1);
				infectedUsers.add(uid);
			}
		}
	}

	/* Infects one group */
	public static void infectOneGroup(Group g) {
		infectOneUser(g.getHeadCoachId());
		for(int uid: g.getGroupUsers()) {
				infectOneUser(uid);
			}
	}

	/* Infects a limited number of users in the connected graph */
	public static void infectLimited(int numUsers) {
		int sum = 0;
		for(int i = 0; i < sortedGroups.size() && sum < numUsers; i++) {
			Group g = sortedGroups.get(i);
			sum += g.getGroupSize();
			infectOneGroup(g);
			// I realize that I'm infecting before I evaluate the sum in the next loop iteration
			// This is because groups can have overlapping users so I'm being a little lenient
		}
	}

	/* Infects exactly number of users specified, fails otherwise
	NOTE: This can use the results of infectLimited but I implemented it as a standalone method */
	public static void infectExactly(int numUsers) {
		// Proceed only if num of users we want to infect is less than total user base, and they're not all infected
		if(numUsers < users.size() && users.size() != infectedUsers.size()) {
			ArrayList<Integer> exactUids = new ArrayList<Integer>();
			int sum, i; 
			sum = i = 0;
			for(i = 0; i < sortedGroups.size() && sum < numUsers; i++) {
				Group g = sortedGroups.get(i);
				sum += g.getGroupSize();
				// Build a list of potentially valid users to infect
				for(int uid : g.getGroupUsers()) {
					int coachId = g.getHeadCoachId();
					if(!exactUids.contains(coachId)) {
						exactUids.add(coachId);
					}
					if(!exactUids.contains(uid)) {
						exactUids.add(uid);
					}
				}
			}
			if(exactUids.size() == numUsers) {
				for(int uid : exactUids) {
					infectOneUser(uid);
				}
			} else System.out.println("Whoops, it's not possible to infect exactly " + numUsers + " users");
			System.out.println("Candidates: " + exactUids);
		} else System.out.println("Whoops, it's not possible to infect exactly " + numUsers + " users");
	}

	/* Adds a random coach to the specified user */
	public static void addRandomCoach(int uid) {
		if(!users.isEmpty()) {
			User user = users.get(uid);
			if(user != null) {
				user.addCoach(randomUidGenerator(uid));
			}
		}
	}

	/* Iterates through every user and adds that user to it's coaches' student list */
	public static void createCoachesRelations() {
		for(User student: users.values()) {
			for(int i = 0; i < student.getCoaches().size(); i++) {
				int coachId = student.getCoaches().get(i);
				User coach = users.get(coachId);
				if(coach != null) {
					coach.addStudent(student.getUid());
					// For every student's first coach
					if(i == 0) {
						// If a group exists, add the student
						if(groups.containsKey(coachId)) {
							addStudentToExistingGroup(coachId, student.getUid());
						} else {
						// Otherwise, create group and add student
							addStudentToNewGroup(coachId, student.getUid());
						}
					}
				}
			}
		}
	}

	/* Add student to specified coach's group */
	public static void addStudentToExistingGroup(int coachId, int studentId) {
		Group group = groups.get(coachId);
		if(group != null) group.addUser(studentId);
	}

	/* Add student to new group */
	public static void addStudentToNewGroup(int coachId, int studentId) {
		Group group = new Group(coachId);
		group.addUser(studentId);
		groups.put(coachId, group);
	}

	/* Generates random user id from list of current users, that don't match the given uid */
	public static int randomUidGenerator(int uid) {
		Random numGen = new Random();
		int randomUid = numGen.nextInt(users.size());
		// Just in case a user's own uid is randomly generated
		while(randomUid == uid) {
			randomUid = numGen.nextInt(users.size());
		}
		return randomUid;
	}

	/* Adds exactly one coach to every user
	Calling this method multiple times will add more coaches */
	public static void createCoachedByRelations() {
		for(int i = 0; i < users.size(); i++) {
			addRandomCoach(i);
		}
	}

	/* Helper method that rolls back a user to the current version
	NOTE: Does not remove from infected array */
	public static void disinfectUser(int uid) {
		if(!users.isEmpty()) {
			User user = users.get(uid);
			if(user != null && user.isInfected(CURRENT_VERSION + 1)) {
				// Disinfect the specifed user
				user.setVersion(CURRENT_VERSION);
			}
		}
	}

	/* Helper method that rolls back all infected users to current version */
	public static void disinfectAll() {
		for(int uid : infectedUsers) {
			disinfectUser(uid);
		}
		infectedUsers.clear();
	}

	/* Print methods used for debugging/testing. Wouldn't typically exist in production */
	public static void printUsers() {
		for (User u: users.values()) {
			System.out.println(u.getName() + " " + String.valueOf(u.getUid()) + " on version " + String.valueOf(u.getVersion()));
		}
	}

	/* Prints entire relation graph. This method and the terminal don't get along. */
	public static void printRelations() {
		for (User u: users.values()) {
			System.out.println(String.valueOf(u.getUid()) + " coaches " + String.valueOf(u.getStudents()) + " student of " + String.valueOf(u.getCoaches()));
		}
	}

	/* Prints unsorted groups in the order they were created */
	public static void printGroups() {
		for (Group g: groups.values()) {
			System.out.println(g.getName() + " : " + g.getGroupUsers());
		}
	}

	/* I got tired of System.outs */
	public static void print(String msg) {
		System.out.println(msg);
	}

	public static class InfectrTest {

		/* Test Case 1: Infecting single user in disconnected graph */	
		public static void testSingleUserInDisconnectedGraph() {
			System.out.println("\n** Test Case 1 ** Single user in disconnected graph");
			// Only infects user 5
			infectAll(5);
			System.out.println("Infected " + infectedUsers.size() + " users : " + String.valueOf(infectedUsers));
			// Test case cleanup
			disinfectAll();
		}

		/* Test Case 2: Infecting single user in connected graph */	
		public static void testTotalInfection() {
			System.out.println("\n** Test Case 2 ** Total infection in connected graph");
			createCoachedByRelations();
			createCoachesRelations();
			// Uncomment next line to visually check relation graph
			// printRelations(); 			
			System.out.println("Infecting users starting from 7...\n");
			infectAll(7);
			printGroups();
			System.out.println("Infected " + infectedUsers.size() + " users : " + String.valueOf(infectedUsers));
			// Test case cleanup
			disinfectAll();
		}

		/* Test Case 3: Limited Infection Scenario */
		public static void testLimitedInfectionWithGroups(int infectNum) {
			System.out.println("\n** Test Case 3 ** Limited infection using Groups sorted by size (including head coach): ");
			Collections.sort(sortedGroups);
			System.out.println(sortedGroups.get(0).getName() + " and size = " + sortedGroups.get(0).getGroupSize() + "\n ... ");
			System.out.println(sortedGroups.get(sortedGroups.size()-1).getName() + " and size = " + sortedGroups.get(sortedGroups.size()-1).getGroupSize());
			infectLimited(infectNum);
			System.out.println("Infected " + infectedUsers.size() + " users : " + String.valueOf(infectedUsers));
			// Test case cleanup
			disinfectAll();
		}

		/* Test Case 4: Exact Infection Scenario */
		public static void testExactInfectionWithGroups(int infectNum) {
			System.out.println("\n** Test Case 4 ** Exact infection using Groups sorted by size (including head coach): ");
			Collections.sort(sortedGroups);
			System.out.println(sortedGroups.get(0).getName() + " and size = " + sortedGroups.get(0).getGroupSize() + "\n ... ");
			System.out.println(sortedGroups.get(sortedGroups.size()-1).getName() + " and size = " + sortedGroups.get(sortedGroups.size()-1).getGroupSize());
			infectExactly(infectNum);
			System.out.println("Infected " + infectedUsers.size() + " users : " + String.valueOf(infectedUsers));
			// Test case cleanup
			disinfectAll();
		}
	}
}
