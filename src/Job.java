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
	public Job(int jobID, String studentID, String title,
			   String applicationName, String description,
			   TaskSize taskSize, LocalDateTime submissionTime) {

		if (jobID<=0)
			throw new IllegalArgumentException(
				"Job ID must be positive."
			);

		if (studentID==null || studentID.trim().isEmpty()
				|| title==null || title.trim().isEmpty()
				|| applicationName==null
				|| applicationName.trim().isEmpty()
				|| description==null
				|| description.trim().isEmpty())

			throw new IllegalArgumentException(
				"All job details are required."
			);

		if (taskSize==null || submissionTime==null)
			throw new IllegalArgumentException(
				"Task size and submission time are required."
			);

		this.jobID=jobID;
		this.studentID=studentID;
		this.title=title;
		this.applicationName=applicationName;
		this.description=description;
		this.taskSize=taskSize;
		this.submissionTime=submissionTime;

		status=JobStatus.QUEUED;
		executedMinutes=0;
		startTime=null;
		finishedTime=null;
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
		return status==JobStatus.COMPLETED
			|| status==JobStatus.CANCELLED;
	}

	//actual queue wait, -1 if the job never started
	public long getQueueWaitMinutes() {
		if (startTime==null)
			return -1;

		return Duration.between(
			submissionTime, startTime
		).toMinutes();
	}

	//only a queued job can start
	public void start(LocalDateTime currentTime) {
		if (status!=JobStatus.QUEUED)
			throw new IllegalStateException(
				"Only queued jobs can start."
			);

		if (currentTime==null)
			throw new IllegalArgumentException(
				"Start time is required."
			);

		if (currentTime.isBefore(submissionTime))
			throw new IllegalArgumentException(
				"A job cannot start before it was submitted."
			);

		startTime=currentTime;
		status=JobStatus.RUNNING;
	}

	//executes exactly one more minute
	public void advanceMinute(LocalDateTime currentTime) {
		if (status!=JobStatus.RUNNING)
			throw new IllegalStateException(
				"Only running jobs can advance."
			);

		if (currentTime==null)
			throw new IllegalArgumentException(
				"Current time is required."
			);

		//prevents skipped or repeated execution minutes
		LocalDateTime expectedTime=
			startTime.plusMinutes(executedMinutes+1L);

		if (!currentTime.equals(expectedTime))
			throw new IllegalArgumentException(
				"Advance exactly one execution minute at a time."
			);

		executedMinutes++;

		if (executedMinutes==getDurationMinutes()) {
			status=JobStatus.COMPLETED;
			finishedTime=currentTime;
		}
	}

	//cancels the job and preserves its execution history
	public void cancel(LocalDateTime currentTime,
					   String cancelledBy, String reason) {

		if (isFinished())
			throw new IllegalStateException(
				"A finished job cannot be cancelled."
			);

		if (cancelledBy==null || cancelledBy.trim().isEmpty()
				|| reason==null || reason.trim().isEmpty())

			throw new IllegalArgumentException(
				"Who cancelled the job and a reason are required."
			);

		if (currentTime==null)
			throw new IllegalArgumentException(
				"Cancellation time is required."
			);

		if (currentTime.isBefore(submissionTime))
			throw new IllegalArgumentException(
				"Cancellation cannot be before submission."
			);

		//all running minutes must be processed before cancellation
		if (status==JobStatus.RUNNING
				&& !currentTime.equals(
					startTime.plusMinutes(executedMinutes)))

			throw new IllegalArgumentException(
				"Process runtime up to the cancellation time first."
			);

		this.cancelledBy=cancelledBy.trim();
		cancellationReason=reason.trim();
		finishedTime=currentTime;
		status=JobStatus.CANCELLED;
	}
}
