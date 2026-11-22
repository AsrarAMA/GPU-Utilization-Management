public abstract class User implements Serializable {
	private String id, name, password;
	
	public User() {}
	
	public User(String id, String name, String password) {
		this.id= id;
		this.name= name;
		this.password = password;
	}
	
	public String getId() {return id;}
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	
	public void setPassword(String password) {this.password= password;}
	
	public boolean checkPassword(String password) {return this.password.equals(password);}
	
	public abstract String getRole();
	
}
