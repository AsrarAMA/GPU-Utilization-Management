package gpu;
import java.io.*; //for LocalDateTime to store both date and time
import java.time.*; //class for simulating an displaying date and time that advances with each minute
//no code needed from members
import java.time.format.DateTimeFormatter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
public class SimuClock implements Serializable { //implements interface that saves the time and date and restores when application is closed
    private LocalDateTime t; //stores the current date +time
    private int mins; //class attributes to show how many mins elapsed
    public SimuClock() {
    	t=null;
    	mins=0;
    } //default constructor
	public void setT(LocalDateTime t) {
		if(t!=null && this.t==null) {
			this.t=t; //making sure the initial time is set once only
		}
	}
	public int getMins() { //needed for dashboard to calculate GPU usage
		return mins;
	} //getter for the minute attribute (scheduler class)
	public LocalDateTime getT() { //needed for member 2
		return t; //getter for the time attribute (current simulation time)
	}
	public void advMin() { //needed for member 2
		if (t!=null) {
			t=t.plusMinutes(1); //incrementing by 1 min
			mins++; //adding to the mins elapsed
		}
	}
   public void syncT(LocalDateTime schedulerTime ){ //schedulerTime would recieve member 2's class Scheduler current sim time
	  if (t!=null && schedulerTime!=null) {
		LocalDateTime startT=t.minusMinutes(mins); //if the elapsed mins nor the original time isnt 0; recover original minutes
		mins=(int) Duration.between(startT, schedulerTime).toMinutes(); //calculate how many mins have elapsed
		t=schedulerTime; //updating time to synch with Scheduler class
	  }
   }
   
   
   public String toString() { //overriding toString Method to print relevant information
	   if (t==null) {
		   return "Simulation Clock was not started";
	   }
	   return "Current Simulation Time:"+t+"\nMinutes Elapsed: "+mins;
   }
}
//class responsible for saving and loading the app data so nothing is lost upon closing app
//to preserve jobs, cancellations,requests, GPU assignments,even simulation clock
class AppManage {
  public static boolean saved(Serializable data) { //will be used for both members
	  //static to call AppManage directly, true if all data successfully saved
   try { //for writing objects into a file
	   ObjectOutputStream out_file= new ObjectOutputStream(new FileOutputStream("project_gpu.dat"));
       out_file.writeObject(data); //we use these instead of FileReader and PrintWriter because these are for texts
       //ie just elapsed mins, not entire simulation clock
       out_file.close();
       System.out.println("Data was preserved successfully");
       return true;
   } catch (IOException e1) {
	   System.out.println("Error in saving data:");
	   e1.printStackTrace(); //to print exact trail 
	   return false;
   }
  } //for saving the data in a file
  
  //for loading the data from said file
  public static Object load() { //also used by both
	  try {
		  ObjectInputStream in_file= new ObjectInputStream(new FileInputStream("project_gpu.dat")); //opening and reading from file the objects
	      Object data=in_file.readObject(); //restores the objects
	      in_file.close();
	      System.out.println("Data was loaded successfully");
	      return data;
	  } catch (FileNotFoundException e) {
		System.out.println("No saved data was found.");
		return null;
	  }
	  
	  catch (IOException |ClassNotFoundException e) {
		  System.out.println("Error in loading data:");
		  e.printStackTrace(); //to print exact trail 
		  return null;
	  }
	  
  }
  //we use Serializable to preserve objects for later
}

class Dashboard extends JFrame implements ActionListener{
   private Main.AppState data; //shared data between members
   private boolean isadmin;
   private String studID;
   private JLabel clock, usage;
   private JTextArea gpu ,queue,balance,job;//4 gpus, jobs waiting for assignment, status of time (used and available), job status
   private JButton advButton, refButton, saveButton,setClock;
   private JButton submit, cancel; //for students
   private JButton grant, request; //for admins
   private Color navy=new Color(24,47,76);
   private Color white=Color.white;
   private Color teal= new Color(0,135,145);
   private Color light= new Color(240,244,248); //KU adjacent schema color
   private DateTimeFormatter format=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
   private JSpinner dateselection; //for admin to select date and time
   public Dashboard(Main.AppState data, boolean isadmin, String studID) {
      this.data=data; //
      this.isadmin=isadmin;
      this.studID=studID;
      setTitle("GPU Usage Management Dashboard");
      setSize(1000,700); //width and height
	  setMinimumSize(new Dimension(850,600));
      setLayout(new BorderLayout(10,10)); //rows and columns
      getContentPane().setBackground(light); //our gui background color
	  setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //X button pushed then closes
      JPanel topPanel=new JPanel(new BorderLayout(10,10));
	  topPanel.setBackground(navy); //color and size for top panel
      topPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	  JLabel tit1=new JLabel("GPU Usage System");
	  tit1.setFont(new Font("Arial", Font.BOLD, 23));
	  tit1.setForeground(white);
	  topPanel.add(tit1, BorderLayout.NORTH);
	  JLabel access= new JLabel( isadmin? "Admin Dashboard":"Student Dashboard"); //ternary operator to decide which access view to grant
	  access.setFont(new Font("Arial", Font.BOLD, 13));
	  access.setForeground(white);
	  topPanel.add(access, BorderLayout.CENTER);
	  JPanel panel= new JPanel (new GridLayout(1,2,15,0)); // 1 row and 2 columns
	  panel.setOpaque(false);
	  clock= new JLabel("Simulation Clock; not initialzed");
      usage=new JLabel("GPU Usage; Not Started");
	  clock.setForeground(white);
	  usage.setForeground(white);
	  clock.setFont(new Font("Arial", Font.BOLD, 13));
      usage.setFont(new Font("Arial", Font.BOLD, 13));
	  panel.add(clock);
	  panel.add(usage);
	  topPanel.add(panel,BorderLayout.SOUTH);
	  add(topPanel, BorderLayout.NORTH);
	  //middle panel area:
      JPanel middle= new JPanel (new GridLayout(2,2,12,12)); // 2 row and 2 columns
	  middle.setBackground(light);
	  middle.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
	  gpu= new JTextArea();
      queue=new JTextArea();
      balance=new JTextArea();
      job=new JTextArea();
      gpu.setEditable(false);
      queue.setEditable(false);
      balance.setEditable(false);
      job.setEditable(false); //users cannot edit these features
      middle.add(makePanel("GPU STATUS:",gpu));
	  middle.add(makePanel("QUEUE:",queue));
	  middle.add(makePanel("JOB RECORD:",job));
	  middle.add(makePanel("BALANCE FOR STUDENT:",balance));
	  add(middle, BorderLayout.CENTER);
	  
	  //Buttons, bottom secton;
	  JPanel bottom= new JPanel (new FlowLayout(FlowLayout.CENTER,10,10)); // 2 row and 2 columns
	  bottom.setBackground(light);
	  bottom.setBorder(BorderFactory.createEmptyBorder(5,10,15,10));
      refButton= new JButton("Refresh:");
      saveButton=new JButton("Save the data:");
      styleButton(refButton); //buttons common for all
	  styleButton(saveButton);
	  saveButton.setBackground(navy);
      refButton.addActionListener(this);
	  bottom.add(refButton);
	  bottom.add(saveButton);
      saveButton.addActionListener(this);

      if (isadmin){
		advButton=new JButton("Advance Minutes");
		setClock=new JButton("Setting initial time"); //buttons only for admin access
	    request=new JButton("Access request");
		grant=new JButton("Grant Minutes");
		styleButton(advButton);
	    styleButton(setClock);
		styleButton(request);
		styleButton(grant);
		advButton.setBackground(navy);
		setClock.setBackground(navy);
		request.setBackground(navy);
		grant.setBackground(navy);
		advButton.addActionListener(this);
		setClock.addActionListener(this);
		request.addActionListener(this);
		grant.addActionListener(this);
		dateselection= new JSpinner(new SpinnerDateModel());
        dateselection.setEditor(new JSpinner.DateEditor(dateselection, "dd/MM/yyyy HH:mm"));
        bottom.add(dateselection);
		bottom.add(setClock);
		bottom.add(advButton);
        bottom.add(request);
		bottom.add(grant);
	  }
	  else{
		 submit=new JButton("Submit job:");
		 cancel=new JButton("Cancel job:");
		 styleButton(submit);
		 styleButton(cancel);
		 cancel.setBackground(navy);
		 submit.addActionListener(this);
		 cancel.addActionListener(this);
		 bottom.add(submit);
		 bottom.add(cancel);
	  }
	  add(bottom, BorderLayout.SOUTH);
	  addWindowListener(new WindowAdapter(){ //to handle window page changes
		public void windowClosing (WindowEvent w){
			AppManage.saved(data); //preserving the data
		}
		});
       refreshDashboard();
	   setLocationRelativeTo(null);
	   setVisible(true);
	  }
      private JPanel makePanel(String tit, JTextArea a){
        JPanel pa=new JPanel (new BorderLayout());
		pa.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(teal),tit));
		a.setFont(new Font("Arial",Font.PLAIN, 13));
		pa.add(new JScrollPane (a));
		return pa; //for info panel
	  }	
	  private void styleButton(JButton bu){
		bu.setBackground(teal);
		bu.setForeground(white);
		bu.setFont(new Font("Arial",Font.PLAIN, 12));
		bu.setFocusPainted(false);}
	 public void refreshDashboard(){ //updating all the info in dashboard
       Scheduler s1=data.scheduler;
		 String g="",q="",b="",j="";
		 if (s1==null){
			clock.setText("simulation not initialized");
			usage.setText("Usage; N/A");
			gpu.setText("No GPU Activity");
			queue.setText("Empty Queue");
            job.setText("No jobs");
		 } else{
			data.clock.syncT(s1.getCurrentTime());
			int passed=data.clock.getMins();
			long busy=0;
			clock.setText("Time:"+s1.getCurrentTime());
			for (GPU y:s1.getGPUs()){ //array loop
                busy+=y.getBusyMinutes();
				g+="GPU"+y.getGpuID()+":"+(y.isFree()?"Available":"Busy")+"\n";//ternary to check status of GPU
			}
			gpu.setText(g);
			usage.setText(passed==0? "Usage; N/A":String.format("Usage:%.2f%%",100.0*busy/(4*passed)));
			for (Job y:s1.getWaitingJobs()){
				if (isadmin || y.getStudentID().equals(studID)){
					q+="Job"+y.getJobID()+"| Wait:"+s1.getPredictedWaitMinutes(y.getJobID())+
					"min \n Start:"+s1.getPredictedStart(y.getJobID())+"| Finish:"+s1.getPredictedFinish(y.getJobID())+"\n";}
	
				}
				queue.setText(q.isEmpty()?"Queue is Empty":q);	
			for (Job y:s1.getJobs()){
				if (isadmin|| y.getStudentID().equals(studID)){
					j+="Job"+y.getJobID()+"|"+y.getStatus()+"|"+y.getExecutedMinutes()+"/"+y.getDurationMinutes()+"min \n";}

				}
			job.setText(j.isEmpty()?"No jobs":j);
			}
			for (Student x:data.students){
				if (isadmin || x.getId().equals(studID)){
					TimeAllowance a1=x.getAllowance();
					b+=x.getName()+"|Allocated:"+a1.getAllocated()+"| Used:"+a1.getUsed()+"|Reserved:"+a1.getReserved()+"| Available:"+a1.getAvailable()+"\n";
				}
		 }
		 balance.setText(b.isEmpty()?"No student":b);
		 if (isadmin){
			setClock.setEnabled(s1==null);
			dateselection.setEnabled(s1==null);
			advButton.setEnabled(s1!=null);
		 }
		 else{
			submit.setEnabled(s1!=null);
			cancel.setEnabled(s1!=null);
		 }
      
        }
		public void actionPerformed (ActionEvent e1){
			try{
				if (e1.getSource()==refButton){
					refreshDashboard();
					return;
				}
				if (e1.getSource()==saveButton){
					JOptionPane.showMessageDialog(this, AppManage.saved(data)?"Saved":"Save was not successfull");
					return;
				}
				if (isadmin && e1.getSource()==setClock){
					Date d= (Date) dateselection.getValue();
					data.initializeSimulation(LocalDateTime.ofInstant(d.toInstant(),ZoneId.systemDefault()));

				}else if (isadmin && e1.getSource()==advButton){
					data.scheduler.advanceMinute();
				}else if (isadmin && e1.getSource()==grant){
					Student s=data.findStudent(ask("Student ID:"));
					if (s== null) throw new IllegalArgumentException ("Student wasnt found");
					String inp=ask("Extra Minutes:");
					if (inp==null) return;
					int minute=Integer.parseInt(inp);
					if (minute<=0) throw new IllegalArgumentException ("Minutes not valid");
					data.administrator.grantMinutes(s,minute);
				} else if (isadmin && e1.getSource()==request){
					for (AccessRequest re:data.requests){
						if (re.getStatus()!=AccessRequest.Status.PENDING) continue;
						int acc= JOptionPane.showConfirmDialog(this, "Approve"+re.getName()+"?","Access Request:",JOptionPane.YES_NO_CANCEL_OPTION);
						if (acc== JOptionPane.YES_OPTION){ // granting access
							data.students.add(data.administrator.approveRequest(re));}
						else if (acc==JOptionPane.NO_OPTION) {
							data.administrator.rejectRequest(re); }//not granting access
			      
                        else {
						 break;}
						}
					} else if (!isadmin && e1.getSource()==submit){
						String title=ask("Job Title:"); //student Job application
						if (title==null) return;
						String app=ask("Application:");
						if(app==null) return;
						String details=ask("Details:");
						if(details==null) return;
						TaskSize task=(TaskSize) JOptionPane.showInputDialog(this, "Task Size:","Submit Job",JOptionPane.QUESTION_MESSAGE,null, TaskSize.values(),TaskSize.SMALL);
						if (task ==null) return;
						data.scheduler.submitJob(studID, title, app, details, task);
					} else if (!isadmin && e1.getSource()==cancel){
						String inp=ask("Job ID:"); //student Job application
						if (inp==null) return;
						int id=Integer.parseInt(inp);
						Job job=data.scheduler.findJobByID(id);
						if(job==null || !job.getStudentID().equals(studID)) throw new IllegalArgumentException("Invalid ID");
						String why=ask("Cancellation Justification:");
						if(why==null) return;
				
						data.scheduler.cancelJob(id, studID, why);
					}
					refreshDashboard();
					AppManage.saved(data);
				} catch (RuntimeException e){
					JOptionPane.showMessageDialog(this, e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE );
				}
			}
			private String ask(String messg){ //method used above to get input via JOption from user
				return JOptionPane.showInputDialog(this, messg);
			}

		  
		}
   
	 


