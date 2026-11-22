package gpu;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Duration;

public class Job implements Serializable {

	private static final long serialVersionUID=1L;

	//attributes
	private int jobID;
	private String studentID, title, applicationName, description;
	private TaskSize taskSize;
	private LocalDateTime submissionTime;

	//these change while the job moves forward
	private JobStatus status;
	private int executedMinutes;
	private LocalDateTime startTime, finishedTime;
	private String cancelledBy, cancellationReason;

	//a new job always starts as QUEUED
	public Job(int jobID, String studentID, String title, String applicationName, String description, TaskSize taskSize, LocalDateTime submissionTime) {
		if (studentID.trim().isEmpty() || title.trim().isEmpty() || applicationName.trim().isEmpty() || description.trim().isEmpty())
			throw new IllegalArgumentException("All job details are required.");

		this.jobID=jobID;
		this.studentID=studentID;
		this.title=title;
		this.applicationName=applicationName;
		this.description=description;
		this.taskSize=taskSize;
		this.submissionTime=submissionTime;

		status=JobStatus.QUEUED;
		executedMinutes=0;
		startTime=null; //not started yet
		finishedTime=null; //not finished yet
	}

	//getters
	public int getJobID() {
		return jobID;
	}
	public String getStudentID() {
		return studentID;
	}
	public String getTitle() {
		return title;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public String getDescription() {
		return description;
	}
	public TaskSize getTaskSize() {
		return taskSize;
	}
	public int getDurationMinutes() {
		return taskSize.getDurationMinutes();
	}
	public LocalDateTime getSubmissionTime() {
		return submissionTime;
	}
	public JobStatus getStatus() {
		return status;
	}
	public int getExecutedMinutes() {
		return executedMinutes;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public LocalDateTime getFinishedTime() {
		return finishedTime;
	}
	public String getCancelledBy() {
		return cancelledBy;
	}
	public String getCancellationReason() {
		return cancellationReason;
	}

	//minutes of work left unfinished
	public int getRemainingMinutes() {
		return getDurationMinutes()-executedMinutes;
	}

	//true if the job has ended
	public boolean isFinished() {
		return status==JobStatus.COMPLETED || status==JobStatus.CANCELLED;
	}

	//actual queue wait, -1 if the job never started
	public long getQueueWaitMinutes() {
		if (startTime==null)
			return -1;
		return Duration.between(submissionTime, startTime).toMinutes();
	}

	//only a queued job can start (so a finished job can't start again)
	public void start(LocalDateTime currentTime) {
		if (status!=JobStatus.QUEUED)
			throw new IllegalStateException("Only queued jobs can start.");
		startTime=currentTime;
		status=JobStatus.RUNNING;
	}

	//executes one more minute, the job is completed when it reaches its runtime
	public void advanceMinute(LocalDateTime currentTime) {
		executedMinutes++;
		if (executedMinutes==getDurationMinutes()) {
			status=JobStatus.COMPLETED;
			finishedTime=currentTime;
		}
	}

	//cancels the job and keeps who cancelled it and why
	public void cancel(LocalDateTime currentTime, String cancelledBy, String reason) {
		if (isFinished())
			throw new IllegalStateException("A finished job cannot be cancelled.");
		if (cancelledBy.trim().isEmpty() || reason.trim().isEmpty())
			throw new IllegalArgumentException("Who cancelled the job and a reason are required.");

		this.cancelledBy=cancelledBy.trim();
		cancellationReason=reason.trim();
		finishedTime=currentTime;
		status=JobStatus.CANCELLED;
	}
}
