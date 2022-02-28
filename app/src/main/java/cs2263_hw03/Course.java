/**
 * MIT License
 *
 * Copyright (c) 2022 Tyson Cox
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

    /** The full names of the departments */
    public static final String[] departmentNames = { "Computer Science", "Mathematics", "Chemistry", "Physics", "Biology", "Electrical Engineering" };
    /** The short department codes */
    public static final String[] departments = { "CS", "MATH", "CHEM", "PHYS", "BIOL", "EE" };

    /**
     * Utility method to map a department name to a department code
     * @param input The department name
     * @return the department code, if able to be found. Null otherwise
     */
    public static String getDeptFromName(String input) {
        for(int i = 0; i < departmentNames.length; i++) {
            if (departmentNames[i].equals(input)) {
                return departments[i];
            }
        }

        return null;
    }

    /** Create course with no data */
    public Course() {
        name = "";
        number = 0;
        credits = 0;
        department = "";
    }

    /** Create course filled with data */
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
