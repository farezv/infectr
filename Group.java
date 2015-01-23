import java.lang.Integer;
import java.util.*;

/* A group is created with at least one coach & one student
This restriction can of course be changed as needed */
public class Group {

	private String name;
	private ArrayList<Integer> users;	
	private int headCoachId;	
	
	/* A group can contain any number of users and is created from the first coach's id */
	public Group(int coachId) {
		this.name = "Group " + String.valueOf(coachId);
		this.users = new ArrayList<Integer>();
		this.headCoachId = coachId;
	}

	/* Getters */
	public String getName() {
		return this.name;
	}

	public int getHeadCoachId() {
		return this.headCoachId;
	}

	public ArrayList<Integer> getGroupUsers() {
		return this.users;
	}

	public int getGroupSize() {
		return this.users.size();
	}

	/* Helpers */
	public void addUser(int uid) {
		this.users.add(uid);
	}
}