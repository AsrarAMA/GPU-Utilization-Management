package gpu;

import java.time.LocalDateTime;

public class SchedulerTest {

	public static void main(String[] args) {

		//test 1: the demo scenario
		TestAllowance allow=new TestAllowance();

		LocalDateTime time=
			LocalDateTime.of(2026, 11, 17, 10, 0);

		Scheduler s=new Scheduler(time, allow);

		//submit six jobs
		s.submitJob(
			"1000123", "Job1", "Python", "test", TaskSize.SMALL
		);

		s.submitJob(
			"1000123", "Job2", "Python", "test", TaskSize.MEDIUM
		);

		s.submitJob(
			"1000123", "Job3", "Python", "test", TaskSize.LARGE
		);

		s.submitJob(
			"1000123", "Job4", "Python", "test", TaskSize.VERY_LARGE
		);

		s.submitJob(
			"1000123", "Job5", "Python", "test", TaskSize.SMALL
		);

		s.submitJob(
			"1000123", "Job6", "Python", "test", TaskSize.SMALL
		);

		System.out.println(
			"Free GPUs: "+s.getFreeGPUCount()+" (expected 0)"
		);

		System.out.println(
			"Waiting jobs: "+s.getWaitingJobs().size()
			+" (expected 2)"
		);

		System.out.println(
			"Reserved: "+allow.reserved
			+", available: "+allow.getAvailable()
			+" (expected 120, 80)"
		);

		System.out.println(
			"Job 5 predicted start: "+s.getPredictedStart(5)
			+" (expected 10:05)"
		);

		System.out.println(
			"Job 6 predicted wait: "+s.getPredictedWaitMinutes(6)
			+" minutes (expected 10)"
		);

		//advance five minutes
		for (int i=1; i<=5; i++)
			s.advanceMinute();

		System.out.println(
			"After 5 minutes, job 1: "
			+s.findJobByID(1).getStatus()
			+", job 5: "+s.findJobByID(5).getStatus()
			+" (expected COMPLETED, RUNNING)"
		);

		System.out.println(
			"Job 5 is on GPU "+s.findGPUForJob(5).getGpuID()
			+" (expected 1)"
		);

		System.out.println(
			"Used: "+allow.used+", reserved: "+allow.reserved
			+" (expected 5, 115)"
		);

		//advance to minute eight
		for (int i=1; i<=3; i++)
			s.advanceMinute();

		//cancel job 6 while it is still queued
		s.cancelJob(
			6, "1000123", "Submitted by mistake"
		);

		System.out.println(
			"Job 6: "+s.findJobByID(6).getStatus()
			+", executed "+s.findJobByID(6).getExecutedMinutes()
			+" minutes (expected CANCELLED, 0)"
		);

		System.out.println(
			"Used: "+allow.used+", reserved: "+allow.reserved
			+" (expected 5, 110)"
		);

		//cancel running job 3 after eight execution minutes
		s.cancelJob(
			3, "1000123", "No longer needed"
		);

		System.out.println(
			"Job 3: "+s.findJobByID(3).getStatus()
			+", executed "+s.findJobByID(3).getExecutedMinutes()
			+" minutes (expected CANCELLED, 8)"
		);

		System.out.println(
			"Used: "+allow.used+", reserved: "+allow.reserved
			+" (expected 13, 80)"
		);

		//a finished job cannot be cancelled
		try {
			s.cancelJob(1, "1000123", "test");
			System.out.println("ERROR: finished job cancelled");
		}
		catch (IllegalStateException e) {
			System.out.println(
				"Cancelling a finished job is blocked"
			);
		}

		//test 2: exact balance accepted, excess rejected
		TestAllowance allow2=new TestAllowance();
		allow2.allocated=30;

		Scheduler s2=new Scheduler(time, allow2);

		s2.submitJob(
			"1000123", "Exact", "Python", "test", TaskSize.LARGE
		);

		System.out.println(
			"Exact balance job accepted, available: "
			+allow2.getAvailable()+" (expected 0)"
		);

		try {
			s2.submitJob(
				"1000123", "Too much", "Python",
				"test", TaskSize.SMALL
			);

			System.out.println("ERROR: job accepted");
		}
		catch (IllegalStateException e) {
			System.out.println(
				"Job rejected: "+e.getMessage()
			);
		}

		System.out.println(
			"Jobs in the system: "+s2.getJobs().size()
			+" (expected 1)"
		);
	}
}
