package gpu;

import java.io.Serializable;

//Connects the scheduler to Member 1's allowance code
public interface AllowanceService extends Serializable {

	//Checks approval and balance, then reserves the minutes
	//An exact match is accepted; rejection must change nothing
	void reserveMinutes(String studentID, int minutes);

	//Releases the full reservation and charges actual execution
	void settleMinutes(String studentID, int reservedMinutes, int executedMinutes);
}
