package gpu;
import java.util.*;

public class Administrator extends User {

    public Administrator(String id, String name,String password) {
    	super(id, name, password);} // Calls the constructor of the parent class (User)

    @Override
    public String getRole() {return "Administrator";} // Returns the administrator's role

    public Student approveRequest(AccessRequest request) { // Approves a student's registration request
       if (request.getStatus()!=AccessRequest.Status.PENDING){
           throw new IllegalStateException("Request already processed");}
       request.approve(); // Changes the request status to APPROVED

       Student student = new Student( // Creates a new Student using the request information
        		request.getStudentId(),
        		request.getName(),
        		request.getEmail(),
        		request.getPhone(),
        		request.getPassword()
        		);

        return student;} // Returns the newly created studen

    public void rejectRequest(AccessRequest request) {request.reject();} // Rejects a student's registration request

    public Student findStudent(String id, ArrayList<Student> students) {  // Searches for a student using their ID
    	 // Loops through all students
    	for (Student student : students) {
    		if (student.getId().equalsIgnoreCase(id)) 
                return student;   
        }

        return null;  // Student not found
    }
    // Gives additional GPU minutes to a student
    public void grantMinutes(Student student, int minutes) {
    	student.getAllowance().grantMinutes(minutes);} // Accesses the student's allowance and increases allocated minutes
}
