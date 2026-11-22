public class AccountsTest {

	public static void main(String[] args) {

		Main.AppState data=new Main.AppState();

		data.register(
			"1000123", "Demo Student",
			"student@example.com", "0500000001", "Demo123!"
		);

		check(
			data.requests.size()==1,
			"Registration creates a request"
		);

		AccessRequest request=data.requests.get(0);

		check(
			request.getStatus()==AccessRequest.Status.PENDING,
			"New request is pending"
		);

		check(
			data.findStudent("1000123")==null,
			"Pending request is not an approved account"
		);

		//duplicate registration must be rejected
		try {
			data.register(
				"1000123", "Other",
				"other@example.com", "0500000002", "Demo123!"
			);

			throw new AssertionError(
				"Duplicate request was accepted."
			);
		}
		catch (IllegalArgumentException e) {
			System.out.println("PASS: Duplicate request blocked");
		}

		//approve and add the account to the shared list
		Student student=data.administrator.approveRequest(request);
		data.students.add(student);

		check(
			request.getStatus()==AccessRequest.Status.APPROVED,
			"Request approved"
		);

		check(
			data.findStudent("1000123")==student,
			"Approved student found"
		);

		check(
			student.getAllowance().getAvailable()==100,
			"Initial allowance is 100"
		);

		check(
			student.checkPassword("Demo123!"),
			"Correct password accepted"
		);

		check(
			!student.checkPassword("wrong"),
			"Wrong password rejected"
		);

		//the same request cannot be approved twice
		try {
			data.administrator.approveRequest(request);

			throw new AssertionError(
				"Repeated approval was accepted."
			);
		}
		catch (IllegalStateException e) {
			System.out.println("PASS: Repeated approval blocked");
		}

		data.administrator.grantMinutes(student, 50);

		check(
			student.getAllowance().getAvailable()==150,
			"Grant adds minutes"
		);

		//test a rejected request
		data.register(
			"1000456", "Other Student",
			"other@example.com", "0500000002", "Demo123!"
		);

		AccessRequest rejected=data.requests.get(1);
		data.administrator.rejectRequest(rejected);

		check(
			rejected.getStatus()==AccessRequest.Status.REJECTED,
			"Request rejected"
		);

		check(
			data.findStudent("1000456")==null,
			"Rejected request creates no account"
		);

		try {
			data.administrator.approveRequest(rejected);

			throw new AssertionError(
				"Rejected request was approved."
			);
		}
		catch (IllegalStateException e) {
			System.out.println(
				"PASS: Rejected request cannot be approved"
			);
		}

		//use a separate state for invalid registration
		Main.AppState empty=new Main.AppState();

		try {
			empty.register(
				"1000999", "   ",
				"test@example.com", "0500000003", "Demo123!"
			);

			throw new AssertionError(
				"Blank name was accepted. Check register() validation."
			);
		}
		catch (IllegalArgumentException e) {
			System.out.println(
				"PASS: Blank registration field rejected"
			);
		}

		System.out.println("AccountsTest finished successfully.");
	}

	//prints success or stops on an incorrect result
	private static void check(boolean condition, String message) {
		if (!condition)
			throw new AssertionError(message);

		System.out.println("PASS: "+message);
	}
}
