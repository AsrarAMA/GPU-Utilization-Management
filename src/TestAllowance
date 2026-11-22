package gpu;

//only for testing, acts like Member 1's code for one student
public class TestAllowance implements AllowanceService {

	private static final long serialVersionUID=1L;

	int allocated=200, used=0, reserved=0;

	//reserves minutes if enough are available
	public void reserveMinutes(String studentID, int minutes) {
		if (minutes<=0 || minutes>getAvailable())
			throw new IllegalStateException(
				"Not enough available minutes or invalid duration."
			);

		reserved+=minutes;
	}

	//releases the reservation and charges actual runtime
	public void settleMinutes(String studentID,
							  int reservedMinutes,
							  int executedMinutes) {

		if (reservedMinutes<0 || reservedMinutes>reserved
				|| executedMinutes<0
				|| executedMinutes>reservedMinutes)

			throw new IllegalArgumentException(
				"Invalid minutes."
			);

		reserved-=reservedMinutes;
		used+=executedMinutes;
	}

	public int getAvailable() {
		return allocated-used-reserved;
	}
}
