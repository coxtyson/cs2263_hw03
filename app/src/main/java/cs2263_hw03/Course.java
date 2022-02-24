package cs2263_hw03;

/**
 * Stores information about a specific course
 * @author Tyson Cox
 */
public class Course {
    private String name;
    private int number;
    private int credits;
    private String department;

    public static final String[] departments = { "CS", "MATH", "CHEM", "PHYS", "BIOL", "EE" };

    public Course() {
        name = "";
        number = 0;
        credits = 0;
        department = "";
    }

    public Course(String name, String department, int number, int credits) {
        this.name = name;
        this.department = department;
        this.number = number;
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
