public class Group {

	private String name;
	private ArrayList<Integer> users;		
	
	/* A group can contain any number of users is created from the first coach's id */
	public Group(int coachId) {
		this.name = "Group " + String.valueOf(coachId);
		this.users = new ArrayList<Integer>();
	}

	public String getName() {
		return this.name;
	}

	public ArrayList<Integer> getGroupUsers() {
		return this.users;
	}

	public int getGroupSize() {
		return this.users.size();
	}

}