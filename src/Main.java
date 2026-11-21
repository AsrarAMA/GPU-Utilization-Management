
import java.io.*;
 //for LocalDateTime to store both date and time
import java.time.*; //class for simulating an displaying date and time that advances with each minute
//no code needed from members
import java.util.*;
import javax.swing.*;
public class Main{ //class within a class
    public static class AppState implements Serializable, AllowanceService{ //stores the data shared between all classes; simulation clock, student accounts/allowance, GPU scheduler; serializble interface for saving app state, allowance service for scheduler to charge students based on minutes
      public ArrayList<Student> students=new ArrayList<>(); //dynamic array for approved students list
      public ArrayList<AccessRequest> requests=new ArrayList<>(); //dynamic array for requests
      public Administrator administrator; //object for admin
      public SimuClock clock=new SimuClock(); //instantiating for simulation clock obj
      public Scheduler scheduler; //manages 4 GPUs and jobs
      public AppState(){
        administrator=new Administrator("admin", "Administrator","gpu2026"); //default constructor; and admin credentials
      }
      public Student findStudent(String ID){ //returns Student obj
        return administrator.findStudent(ID, students); //method that searches for student via ID
      }
      //method that starts sim clock once; this time is passed to SimuClock class and Scheduler class
      public void initializeSimulation(LocalDateTime time){
        if (scheduler!=null){ //doesnt allow reinitialization
            throw new IllegalStateException("Simulation clock already started");}//ensuring only once
        clock.setT(time);
        scheduler=new Scheduler(time,this); //creating scheduler obj with this time and corresponding 4 gpus
        }
        //to check student has enough minutes for Job
        public void reserveMinutes(String studID, int min){
            Student s=findStudent(studID);
            if (s==null){
                throw new IllegalArgumentException("Student not found");
    
            }
           if(!s.getAllowance().reserveMinutes(min)){
            throw new IllegalArgumentException("No minutes available");
    
           }
        } //this method charges the minutes used by the Job
        public void settleMinutes(String studID, int reservedMinutes, int executedMinutes){
          Student s=findStudent(studID); //finds the student responsible for job via ID
          if (s==null){
                throw new IllegalArgumentException("Student not found");
            }
          s.getAllowance().chargeReservedMinutes(reservedMinutes, executedMinutes); //updates reserved and used student minutes for balance
        }
        //method responsible for student registration to use GPUs and approved by admins
        public void register(String id, String studname, String mail, String phoneno,String pass){
            if(studname==null||id==null||mail==null||phoneno==null||pass==null){//ensures each field was filled
              throw new IllegalArgumentException("Please fill all requirements:");}
            if(id.equals("admin")||findStudent(id)!=null){
                throw new IllegalArgumentException("ID already taken");
            } //doesnt allow duplicate (student or admin) ID
            for (AccessRequest re:requests){ //also looks for previous requests for Jobs to disqualify duplicate ID
               if(re.getStudentId().equals(id)){
                 throw new IllegalArgumentException("Request for registration already exists in system");
               }
            }
            //if no duplicates, creates a request so admin can reject or accept
            requests.add(new AccessRequest(id,studname,mail,phoneno,pass));       
    }
} //responsible for loading saved app data, login/registration/exit, checking credentials, opening dashboard after credentials
public static void main (String [] args){
   Object saved= AppManage.load(); //using instanceof to identify whether object (saved data) exists, if not create new app state
   AppState  data= saved instanceof AppState?(AppState) saved:new AppState();
   SwingUtilities.invokeLater(()-> { //to use swing interface
     String[] but={"Login","Registration","Exit"};
     while (true){
        int choice=JOptionPane.showOptionDialog(null,"GPU Utilization System", "Welcome!", JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE,null,but,but[0]);
        if (choice==0){ //person has clicked on login in titular page
           String id=ask("Student ID | User ID:"); //using ask method in dashboard class
           if (id==null) continue;
           String pass=ask("Password:");
           if (pass==null) continue;
           if (id.equals("admin")&& data.administrator.checkPassword(pass)){//if matches admin credentials
               new Dashboard(data,true,id); //creates dashboard obj
               break;
           }
           Student student=data.findStudent(id);
           if (student!=null && student.checkPassword(pass)){ //matches student credentials
              new Dashboard(data,false,id); //creates dashboard object
              break;
           }
           JOptionPane.showMessageDialog(null, "Account not found");

        }else if (choice==1){  //registration chosen
           String id=ask("University ID:");
           if (id== null) continue;
           String name=ask("Full name:");//full name since first-time user
           if (name== null) continue;
           String email=ask("email:");
           if (email==null) continue;
           String phoneno=ask("Phone number:");
           if (phoneno== null) continue;
           String pass=ask("Password:");
           if (pass==null) continue;
           try{ //creating registration and access
            data.register(id,name,email,phoneno,pass);
            if (AppManage.saved(data)){
              JOptionPane.showMessageDialog(null,"Request is pending for admin approval.");
            }
            else{
              JOptionPane.showMessageDialog(null,"Request failed to save.");
            }
           } catch (IllegalArgumentException e1){
            JOptionPane.showMessageDialog(null, e1.toString(), "Registration failed.",JOptionPane.ERROR_MESSAGE);
           }
           
        } else{
          AppManage.saved(data);
          break; //exit button selected
        }
     }

   });

}
private static String ask(String message){
  return JOptionPane.showInputDialog(null,message);}
  } //method used above to take input from user