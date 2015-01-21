import java.lang.Integer;
import java.util.*;

public class Infectr {

	private static int CURRENT_VERSION = 1;

	// In production, users would be in a database and deserialized into a User class upon request
	private static HashMap<Integer, User> users;		

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

			infectAll(5); // only infects 5
			printUsers();
			createRelationships();
			printRelationships();			
			infectAll(7); // infects users starting from 7
			printUsers();

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

	public static void infectAll(int uid) {
		if(!users.isEmpty()) {
			User user = users.get(uid);
			System.out.println("After getting user, before infecting it");
			if(user != null && !user.isInfected(CURRENT_VERSION + 1)) {
				// Infect the specifed user
				user.setVersion(CURRENT_VERSION + 1);
				for(int coachId: user.getCoaches()) {
					infectAll(coachId);
				}
			}	
		}
	}

	public static void addRandomCoachAndStudent(int uid) {
		if(!users.isEmpty()) {
			User user = users.get(uid);
			if(user != null) {
				user.addCoach(randomUidGenerator(uid));
				user.addStudent(randomUidGenerator(uid));
			}
		}
	}

	// Generates random user id from list of current users, that don't match the given uid
	public static int randomUidGenerator(int uid) {
		Random numGen = new Random();
		int randomUid = numGen.nextInt(users.size());
		// For good measure
		while(randomUid == uid) {
			randomUid = numGen.nextInt(users.size());
		}
		return randomUid;
	}

	public static void createRelationships() {
		for(int i = 0; i < users.size(); i++) {
			addRandomCoachAndStudent(i);
		}
	}

	public static void printRelationships() {
		for (User u: users.values()) {
			System.out.println(String.valueOf(u.getUid()) + " coaches " + String.valueOf(u.getStudents()));
		}
	}
}
