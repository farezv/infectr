import java.lang.Integer;
import java.util.*;

public class Infectr {

	private static int CURRENT_VERSION = 1;

	/* In production, these data structures would be in a database */
	private static HashMap<Integer, User> users;
	private static ArrayList<Integer> infectedUsers;
	private static HashMap<Integer, Group> groups;		

	public static void main(String[] args) {
		// Only proceed if an arg is provided
		if(args.length != 0) {
			int firstArg = 0;
			try {
				firstArg = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			// Only proceed if the arg is valid
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

			/* I would setup a proper testing module (InfectrTest.java) in a production codebase, 
			but I'm just going to call methods at the end of this main method to keep it simple */	
			
			/* Test Case 1: Infecting single user in disconnected graph */	
			System.out.println("\n** Test Case 1 ** Single user in disconnected graph");
			infectAll(5); // only infects 5
			System.out.println("Infected " + infectedUsers.size() + " users : " + String.valueOf(infectedUsers));
			disinfectUser(5); // test case cleanup

			/* Test Case 2: Infecting single user in connected graph
			This is testing the total_infection scenario */	
			System.out.println("\n** Test Case 2 ** Total infection in connected graph");
			createCoachedByRelations();
			createCoachesRelations();
			// printRelations(); // uncomment to visually check relation graph			
			infectAll(7); // infects users starting from 7
			System.out.println("Infected " + infectedUsers.size() + " users : " + String.valueOf(infectedUsers));

			/* Test Case 3: Limited Infection Scenario */
			System.out.println("\n** Test Case 3 ** Limited infection using Groups: \n");
			printGroups();

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

	/* Infects exactly number of users specified, fails otherwise */
	public static void infectExactly(int numUsers) {
		// Proceed only if num of users we want to infect is less than total user base, and they're not all infected
		if(numUsers < users.size() && users.size() != infectedUsers.size()) {
			// infect exactly
		}
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

	/* Helper method that rolls back a user to the current version */
	public static void disinfectUser(int uid) {
		if(!users.isEmpty()) {
			User user = users.get(uid);
			if(user != null && user.isInfected(CURRENT_VERSION + 1)) {
				// Disinfect the specifed user
				user.setVersion(CURRENT_VERSION);
				infectedUsers.remove(Integer.valueOf(uid));
			}
		}
	}

	/* Print methods used for debugging/testing. Wouldn't typically exist in production */
	public static void printUsers() {
		for (User u: users.values()) {
			System.out.println(u.getName() + " " + String.valueOf(u.getUid()) + " on version " + String.valueOf(u.getVersion()));
		}
	}

	public static void printRelations() {
		for (User u: users.values()) {
			System.out.println(String.valueOf(u.getUid()) + " coaches " + String.valueOf(u.getStudents()) + " student of " + String.valueOf(u.getCoaches()));
		}
	}

	public static void printGroups() {
		for (Group g: groups.values()) {
			System.out.println(g.getName() + " : " + g.getGroupUsers());
		}
	}
}
