package gpu;
import java.io.Serializable;
//a class that stores students registration request for GPUs; uses Serializable for restoration
public class AccessRequest implements Serializable{ 
   public enum Status{
    PENDING, APPROVED, REJECTED
   } //status of request; decided by admin
   private String studentID, email,name,phone,pass;
   private Status status; //class attributes
   public AccessRequest(String studentID, String name,String email, String phone, String pass, Status status){
    this.studentID=studentID;
    this.name=name;
    this.email=email;
    this.phone=phone;
    this.pass=pass;
    this.status=status; //full constructor
   }
   //getter methods since attr are private
   public String getStudentId(){
    return studentID;
   }
   public String getName(){
    return name;
   }
   public String getEmail(){
    return email;
   }
   public String getPhone(){
    return phone;
   }
   public Status getStatus(){
    return status;
   }
   public void approve(){ status=Staus.APPROVED;} //REQUEST GOT APPROVED BY ADMIN
   public void reject(){status= Status.REJECTED;} //REJECTED BY ADMIN
}