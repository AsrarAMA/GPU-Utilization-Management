package gpu;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;

public class Scheduler implements Serializable {

	private static final long serialVersionUID=1L;

	//attributes
	private ArrayList<GPU> gpus;
	private ArrayList<Job> jobs; //includes finished jobs
	private ArrayList<Job> waitingQueue; //one queue for all GPUs
	private AllowanceService allowanceService;
	private LocalDateTime initialTime, currentTime;
	private int nextJobID;

	//constructor, creates the four GPUs
	public Scheduler(LocalDateTime initialTime,
					 AllowanceService allowanceService) {

		if (initialTime==null || allowanceService==null)
			throw new IllegalArgumentException(
				"Initial time and allowance service are required."
			);

		this.initialTime=initialTime;
		this.currentTime=initialTime;
		this.allowanceService=allowanceService;

		nextJobID=1;

		gpus=new ArrayList<GPU>();
		jobs=new ArrayList<Job>();
		waitingQueue=new ArrayList<Job>();

		for (int id=1; id<=4; id++)
			gpus.add(new GPU(id));
	}

	//accepts a job from a student
	public Job submitJob(String studentID, String title,
						 String applicationName, String description,
						 TaskSize taskSize) {

		//invalid details must not reserve minutes
		Job job=new Job(
			nextJobID, studentID, title, applicationName,
			description, taskSize, currentTime
		);

		//Member 1 checks approval and reserves the minutes
		allowanceService.reserveMinutes(
			studentID, job.getDurationMinutes()
		);

		nextJobID++;
		jobs.add(job);

		//appended in submission order, with increasing job IDs
		waitingQueue.add(job);

		dispatchJobs();
		return job;
	}

	//assigns waiting jobs to free GPUs in GPU ID order
	private void dispatchJobs() {
		for (GPU gpu : gpus) {
			if (waitingQueue.size()>0 && gpu.isFree()) {

				//remove only after successful assignment
				Job next=waitingQueue.get(0);
				gpu.assignJob(next, currentTime);
				waitingQueue.remove(0);
			}
		}
	}

	//moves the clock exactly one minute
	public void advanceMinute() {
		currentTime=currentTime.plusMinutes(1);

		//advance existing running jobs before assigning new ones
		for (GPU gpu : gpus) {
			Job done=gpu.advanceMinute(currentTime);

			if (done!=null)
				settleAllowance(done);
		}

		dispatchJobs();
	}

	//trusted operation; the UI must check who may cancel
	public Job cancelJob(int jobID, String cancelledBy, String reason) {
		Job job=findJobByID(jobID);

		if (job==null)
			throw new IllegalArgumentException(
				"Job ID was not found."
			);

		if (job.isFinished())
			throw new IllegalStateException(
				"A finished job cannot be cancelled."
			);

		if (job.getStatus()==JobStatus.QUEUED) {
			job.cancel(currentTime, cancelledBy, reason);
			waitingQueue.remove(job);
		}
		else {
			GPU gpu=findGPUForJob(jobID);

			if (gpu==null)
				throw new IllegalStateException(
					"Running job has no GPU assignment."
				);

			gpu.cancelCurrentJob(
				currentTime, cancelledBy, reason
			);
		}

		settleAllowance(job);
		dispatchJobs();

		return job;
	}

	//releases the full reservation and charges actual runtime
	private void settleAllowance(Job job) {
		allowanceService.settleMinutes(
			job.getStudentID(),
			job.getDurationMinutes(),
			job.getExecutedMinutes()
		);
	}

	//finds a job, including finished jobs
	public Job findJobByID(int jobID) {
		for (Job job : jobs) {
			if (job.getJobID()==jobID)
				return job;
		}

		return null;
	}

	//finds the GPU currently running this job
	public GPU findGPUForJob(int jobID) {
		for (GPU gpu : gpus) {
			Job job=gpu.getCurrentJob();

			if (job!=null && job.getJobID()==jobID)
				return gpu;
		}

		return null;
	}

	//getters
	public LocalDateTime getInitialTime() {
		return initialTime;
	}

	public LocalDateTime getCurrentTime() {
		return currentTime;
	}

	//copies protect list membership; contained objects stay shared
	public ArrayList<GPU> getGPUs() {
		return new ArrayList<GPU>(gpus);
	}

	public ArrayList<Job> getJobs() {
		return new ArrayList<Job>(jobs);
	}

	public ArrayList<Job> getWaitingJobs() {
		return new ArrayList<Job>(waitingQueue);
	}

	//returns one student's jobs
	public ArrayList<Job> getJobsForStudent(String studentID) {
		if (studentID==null || studentID.trim().isEmpty())
			throw new IllegalArgumentException(
				"Student ID is required."
			);

		ArrayList<Job> studentJobs=new ArrayList<Job>();

		for (Job job : jobs) {
			if (job.getStudentID().equals(studentID))
				studentJobs.add(job);
		}

		return studentJobs;
	}

	//counts jobs with the selected status
	public int countJobs(JobStatus status) {
		if (status==null)
			throw new IllegalArgumentException(
				"Job status is required."
			);

		int count=0;

		for (Job job : jobs) {
			if (job.getStatus()==status)
				count++;
		}

		return count;
	}

	//counts the free GPUs
	public int getFreeGPUCount() {
		int count=0;

		for (GPU gpu : gpus) {
			if (gpu.isFree())
				count++;
		}

		return count;
	}

	//predicts a start using copied GPU availability times
	public LocalDateTime getPredictedStart(int jobID) {
		Job target=findJobByID(jobID);

		if (target==null)
			throw new IllegalArgumentException(
				"Job ID was not found."
			);

		if (target.isFinished())
			throw new IllegalStateException(
				"Finished jobs have no prediction."
			);

		if (target.getStatus()==JobStatus.RUNNING)
			return target.getStartTime();

		LocalDateTime[] freeAt=new LocalDateTime[gpus.size()];

		for (int i=0; i<gpus.size(); i++) {
			freeAt[i]=currentTime.plusMinutes(
				gpus.get(i).getRemainingMinutes()
			);
		}

		//simulate the queue without changing real jobs
		for (Job job : waitingQueue) {
			int first=0;

			for (int i=1; i<freeAt.length; i++) {
				if (freeAt[i].isBefore(freeAt[first]))
					first=i;
			}

			//equal times keep the lower GPU ID
			if (job==target)
				return freeAt[first];

			freeAt[first]=freeAt[first].plusMinutes(
				job.getDurationMinutes()
			);
		}

		throw new IllegalStateException(
			"Queued job was not found in the waiting queue."
		);
	}

	//predicts when an active job will finish
	public LocalDateTime getPredictedFinish(int jobID) {
		LocalDateTime start=getPredictedStart(jobID);
		Job job=findJobByID(jobID);

		if (job.getStatus()==JobStatus.RUNNING)
			return currentTime.plusMinutes(
				job.getRemainingMinutes()
			);

		return start.plusMinutes(job.getDurationMinutes());
	}

	//remaining wait, zero if already running
	public long getPredictedWaitMinutes(int jobID) {
		LocalDateTime start=getPredictedStart(jobID);

		if (findJobByID(jobID).getStatus()==JobStatus.RUNNING)
			return 0;

		return Duration.between(currentTime, start).toMinutes();
	}
}
