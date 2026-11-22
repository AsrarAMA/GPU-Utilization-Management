package gpu;
import java.util.*;
public class Student extends User {
	private String email, phone;
	
	private TimeAllowance allowance;
	
	private ArrayList<String> jobIds;
	
	public Student(String id, String name, String email, String phone, String password) {
		super(id, name, password);
		
		this.email = email;
		this.phone = phone;
		
		allowance = new TimeAllowance(100);
		
		jobIds = new ArrayList<>();
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
