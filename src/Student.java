package gpu;
import java.util.*;
public class Student extends User {
	private String email, phone; // Student's additional information
	
	private TimeAllowance allowance;     // Stores the student's GPU time allowance
	
	private ArrayList<String> jobIds;     // Stores the IDs of the student's jobs
	
	public Student(String id, String name, String email, String phone, String password) {  // creates a student

		super(id, name, password);         // Initializes inherited User attributes
		
		this.email = email;
		this.phone = phone;
		
		allowance = new TimeAllowance(100);      // Gives every new student 100 GPU minutes
		
		jobIds = new ArrayList<>();    // Creates an empty list of job IDs
	}
	
	public String getPhone() {return phone;}
	public void setPhone(String phone) {this.phone = phone;}
	
	public String getEmail() {return email;}
	public void setEmail(String email) {this.email = email;}
	
	public TimeAllowance getAllowance() {return allowance;}
	
	public void addJodId(String jobId) {jobIds.add(jobId);}
	
	public ArrayList<String> getJobIds() {return new ArrayList<>(jobIds);}
	
	@Override
	public String getRole() { return "Student";}

}
