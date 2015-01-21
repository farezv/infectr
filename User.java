import java.util.*;

public class User {
  private int siteVersion;
  private int uid;
  private String name;
  private ArrayList<Integer> students;
  private ArrayList<Integer> coaches;

  public User(String name, int uid, int version) {
    this.name = name;
    this.uid = uid;
    this.siteVersion = version;
    this.students = new ArrayList<Integer>();
    this.coaches = new ArrayList<Integer>();
  }

  /* Getters */
  public String getName() {
    return this.name;
  }

  public int getUid() {
    return this.uid;
  }

  public int getVersion() {
    return this.siteVersion;
  }

  public ArrayList<Integer> getStudents() {
    return this.students;
  }

  public ArrayList<Integer> getCoaches() {
    return this.coaches;
  }

  /* Setters */
  public void setVersion(int newVersion) {
    this.siteVersion = newVersion;
  }

  public void addCoach(int uid) {
    if(!this.coaches.contains(uid)) this.coaches.add(uid);
  }

  public void addStudent(int uid) {
    if(!this.students.contains(uid)) this.students.add(uid);
  }

  public boolean isInfected(int infectedVersion) {
    return this.siteVersion == infectedVersion;
  }
}
