package gpu;

import java.time.LocalDateTime;

public class JobTest {

	public static void main(String[] args) {

		//use a fixed simulated time
		LocalDateTime time=
			LocalDateTime.of(2026, 11, 17, 10, 0);

		//test 1: run a small job until completion
		Job firstJob=new Job(
			1, "1000123", "Test job", "Python",
			"Test completion", TaskSize.SMALL, time
		);

		System.out.println(
			"Initial status: "+firstJob.getStatus()
		);

		//start after two minutes of waiting
		firstJob.start(time.plusMinutes(2));

		System.out.println(
			"Queue wait: "+firstJob.getQueueWaitMinutes()
		);

		//a small job requires five execution minutes
		for (int minute=1; minute<=5; minute++) {
			firstJob.advanceMinute(
				time.plusMinutes(2+minute)
			);
		}

		System.out.println(
			"Final status: "+firstJob.getStatus()
		);

		System.out.println(
			"Executed: "+firstJob.getExecutedMinutes()
		);

		System.out.println(
			"Remaining: "+firstJob.getRemainingMinutes()
		);

		//test 2: cancel a running job after two minutes
		Job secondJob=new Job(
			2, "1000123", "Another job", "Java",
			"Test running cancellation", TaskSize.MEDIUM, time
		);

		secondJob.start(time);
		secondJob.advanceMinute(time.plusMinutes(1));
		secondJob.advanceMinute(time.plusMinutes(2));

		secondJob.cancel(
			time.plusMinutes(2),
			"1000123",
			"No longer needed"
		);

		System.out.println(
			"Running cancellation: "+secondJob.getStatus()
		);

		System.out.println(
			"Executed before cancellation: "
			+secondJob.getExecutedMinutes()
		);

		//test 3: cancel a job before it starts
		Job thirdJob=new Job(
			3, "1000123", "Waiting job", "Python",
			"Test queued cancellation", TaskSize.LARGE, time
		);

		thirdJob.cancel(
			time.plusMinutes(1),
			"1000123",
			"Submitted by mistake"
		);

		System.out.println(
			"Queued cancellation: "+thirdJob.getStatus()
		);

		System.out.println(
			"Queued job executed: "+thirdJob.getExecutedMinutes()
		);

		//test 4: a completed job cannot restart
		try {
			firstJob.start(time.plusMinutes(10));
			System.out.println("ERROR: Completed job restarted.");
		}
		catch (IllegalStateException exception) {
			System.out.println("Restart correctly blocked.");
		}
	}
}
