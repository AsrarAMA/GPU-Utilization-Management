import java.util.*;

public class Administrator extends User {

    public Administrator(String id, String name,String password) {
    	super(id, name, password);}

    @Override
    public String getRole() {return "Administrator";}

    public Student approveRequest(AccessRequest request) {

        request.approve();

        Student student = new Student(
        		request.getStudentId(),
        		request.getName(),
        		request.getEmail(),
        		request.getPhone(),
        		request.getPassword()
        		);

        return student;}

    public void rejectRequest(AccessRequest request) {request.reject();}

    public Student findStudent(String id, ArrayList<Student> students) {
    	
    	for (Student student : students) {
    		if (student.getId().equalsIgnoreCase(id)) 
                return student;   
        }

        return null;
    }

    public void grantMinutes(Student student, int minutes) {
    	student.getAllowance().grantMinutes(minutes);}
}
