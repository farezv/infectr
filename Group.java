import java.lang.Integer;
import java.util.*;

/* A group is created with at least one coach & one student
This restriction can of course be changed as needed */
public class Group implements Comparable<Group> {

	private String name;
	private ArrayList<Integer> users;	
	private int headCoachId;	
	
	/* A group can contain any number of users and is created from the first coach's id */
	public Group(int coachId) {
		this.name = "Group " + String.valueOf(coachId);
		this.users = new ArrayList<Integer>();
		this.headCoachId = coachId;
	}

	@Override
	public int compareTo(Group group) {
		if(this.getGroupSize() < group.getGroupSize()) {
			return -1;
		}
		if(this.getGroupSize() > group.getGroupSize()) {
			return 1;
		}
		return 0;
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
		// Adding 1 to include the coach :)
		return this.users.size() + 1;
	}

	/* Helpers */
	public void addUser(int uid) {
		this.users.add(uid);
	}
}