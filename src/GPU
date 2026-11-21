package gpu;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GPU implements Serializable {

	private static final long serialVersionUID=1L;

	//attributes
	private int gpuID;
	private Job currentJob; //null when the GPU is free
	private long busyMinutes; //includes runtime before cancellation

	//creates a free GPU
	public GPU(int gpuID) {
		if (gpuID<1 || gpuID>4)
			throw new IllegalArgumentException(
				"GPU ID must be between 1 and 4."
			);

		this.gpuID=gpuID;
		currentJob=null;
		busyMinutes=0;
	}

	//getters
	public int getGpuID() {
		return gpuID;
	}

	public Job getCurrentJob() {
		return currentJob;
	}

	public long getBusyMinutes() {
		return busyMinutes;
	}

	//true if the GPU has no job
	public boolean isFree() {
		return currentJob==null;
	}

	//minutes left for the current job
	public int getRemainingMinutes() {
		if (isFree())
			return 0;
		else
			return currentJob.getRemainingMinutes();
	}

	//starts a queued job on this GPU
	public void assignJob(Job job, LocalDateTime currentTime) {
		if (!isFree())
			throw new IllegalStateException(
				"This GPU already has a job."
			);

		if (job==null)
			throw new IllegalArgumentException(
				"A job is required."
			);

		job.start(currentTime);
		currentJob=job;
	}

	//runs one minute and returns a newly completed job
	public Job advanceMinute(LocalDateTime currentTime) {
		if (currentTime==null)
			throw new IllegalArgumentException(
				"Current time is required."
			);

		if (isFree())
			return null;

		currentJob.advanceMinute(currentTime);
		busyMinutes++;

		if (currentJob.getStatus()==JobStatus.COMPLETED) {
			Job done=currentJob;
			currentJob=null;
			return done;
		}

		return null;
	}

	//cancels the current job and frees the GPU
	public Job cancelCurrentJob(LocalDateTime currentTime,
								String cancelledBy,
								String reason) {

		if (isFree())
			throw new IllegalStateException(
				"This GPU has no job to cancel."
			);

		currentJob.cancel(currentTime, cancelledBy, reason);

		Job cancelled=currentJob;
		currentJob=null;

		return cancelled;
	}
}
