package gpu;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class SaveLoadTest {

	public static void main(String[] args) throws IOException {

		Path file=Paths.get("project_gpu.dat");

		//protect an existing application save
		if (Files.exists(file))
			throw new IllegalStateException(
				"Existing save found. Choose an empty test working directory."
			);

		Main.AppState data=new Main.AppState();

		data.register(
			"1000123", "Demo Student",
			"student@example.com", "0500000001", "Demo123!"
		);

		data.students.add(
			data.administrator.approveRequest(
				data.requests.get(0)
			)
		);

		//keep a pending request in the saved state too
		data.register(
			"1000456", "Pending Student",
			"pending@example.com", "0500000002", "Demo123!"
		);

		LocalDateTime time=
			LocalDateTime.of(2026, 11, 17, 10, 0);

		data.initializeSimulation(time);

		for (int i=1; i<=5; i++) {
			data.scheduler.submitJob(
				"1000123", "Job "+i,
				"Python", "Test", TaskSize.SMALL
			);
		}

		data.scheduler.advanceMinute();
		data.scheduler.advanceMinute();

		data.clock.syncT(
			data.scheduler.getCurrentTime()
		);

		try {
			check(
				AppManage.saved(data),
				"Entire AppState saves successfully"
			);

			Object loaded=AppManage.load();

			check(
				loaded instanceof Main.AppState,
				"AppState object restored"
			);

			Main.AppState restored=(Main.AppState) loaded;

			check(
				restored.administrator.checkPassword("gpu2026"),
				"Admin credentials restored"
			);

			Student student=restored.findStudent("1000123");

			check(
				student!=null
				&& student.checkPassword("Demo123!"),
				"Student ID and password restored"
			);

			check(
				restored.requests.size()==2
				&& restored.requests.get(1).getStatus()
					==AccessRequest.Status.PENDING,
				"Pending request preserved"
			);

			check(
				restored.scheduler.countJobs(
					JobStatus.RUNNING
				)==4
				&& restored.scheduler.getWaitingJobs().size()==1,
				"Running jobs and queue preserved"
			);

			check(
				time.plusMinutes(2).equals(
					restored.scheduler.getCurrentTime()
				)
				&& time.plusMinutes(2).equals(
					restored.clock.getT()
				)
				&& restored.clock.getMins()==2,
				"Both saved times preserved without advancing"
			);

			check(
				restored.scheduler.getGPUs().get(0)
					.getRemainingMinutes()==3
				&& restored.scheduler.getGPUs().get(0)
					.getBusyMinutes()==2,
				"GPU progress preserved"
			);

			check(
				restored.scheduler.getGPUs().get(0)
					.getCurrentJob()
				==restored.scheduler.findJobByID(1),
				"Shared job reference preserved"
			);

			//continue using the restored objects
			for (int i=1; i<=3; i++)
				restored.scheduler.advanceMinute();

			restored.clock.syncT(
				restored.scheduler.getCurrentTime()
			);

			check(
				restored.scheduler.countJobs(
					JobStatus.COMPLETED
				)==4
				&& restored.scheduler.findJobByID(5)
					.getStatus()==JobStatus.RUNNING,
				"Restored simulation continues correctly"
			);

			check(
				student.getAllowance().getUsed()==20
				&& student.getAllowance().getReserved()==5,
				"Restored scheduler updates the restored real student balance"
			);

			Job next=restored.scheduler.submitJob(
				"1000123", "Next",
				"Java", "Test", TaskSize.SMALL
			);

			check(
				next.getJobID()==6,
				"Next job ID preserved"
			);

			System.out.println(
				"SaveLoadTest finished successfully."
			);
		}
		finally {
			//remove only this test's newly created save
			Files.deleteIfExists(file);
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition)
			throw new AssertionError(message);

		System.out.println("PASS: "+message);
	}
}
