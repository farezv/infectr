import java.lang.Integer;
import java.util.*;

public class Infectr {

	private static int CURRENT_VERSION = 1;

	// In production, users would be in a database and deserialized into a User class upon request
	private static HashMap<Integer, User> users;
	private static ArrayList<Integer> infected;		

	public static void main(String[] args) {
		if(args.length != 0) {
			int firstArg = 0;
			try {
				firstArg = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println(e.getMessage());
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
			infected = new ArrayList<Integer>();

			infectAll(5); // only infects 5
			printUsers();
			createCoachedByRelations();
			createCoachesRelations();
			printRelations();			
			infectAll(7); // infects users starting from 7
			System.out.println("Infected: " + String.valueOf(infected));

		} else System.out.println("Please enter the number of users you'd like to create");
	}

	public static void createUser(String name, int uid) {
		User u = new User(name, uid, CURRENT_VERSION);
		users.put(uid, u);
	}

	public static void printUsers() {
		for (User u: users.values()) {
			System.out.println(u.getName() + " " + String.valueOf(u.getUid()) + " on version " + String.valueOf(u.getVersion()));
		}
	}

	/* Recursively infects users */
	public static void infectAll(int uid) {
		if(!users.isEmpty()) {
			User user = users.get(uid);
			if(user != null && !user.isInfected(CURRENT_VERSION + 1)) {
				// Infect the specifed user
				user.setVersion(CURRENT_VERSION + 1);
				infected.add(uid);
				// Infect its coaches and students
				for(int coachId: user.getCoaches()) {
					infectAll(coachId);
				}
				for(int studentId: user.getStudents()) {
					infectAll(studentId);
				}
			}	
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

	public static void createCoachesRelations() {
		for(User student: users.values()) {
			for(int coachId: student.getCoaches()) {
				User coach = users.get(coachId);
				if(coach != null) {
					coach.addStudent(student.getUid());
				}
			}
		}
	}

	// Generates random user id from list of current users, that don't match the given uid
	public static int randomUidGenerator(int uid) {
		Random numGen = new Random();
		int randomUid = numGen.nextInt(users.size());
		// Just in case a user's own uid is randomly generated
		while(randomUid == uid) {
			randomUid = numGen.nextInt(users.size());
		}
		return randomUid;
	}

	public static void createCoachedByRelations() {
		for(int i = 0; i < users.size(); i++) {
			addRandomCoach(i);
		}
	}

	public static void printRelations() {
		for (User u: users.values()) {
			System.out.println(String.valueOf(u.getUid()) + " coaches " + String.valueOf(u.getStudents()) + " student of " + String.valueOf(u.getCoaches()));
		}
	}
}
