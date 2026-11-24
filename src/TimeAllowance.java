package gpu;
import java.io.Serializable;
public class TimeAllowance implements Serializable {
	private int allocated, used, reserved; // Total minutes allocated to the student 
										   //and Minutes already used and  Minutes reserved for submitted jobs
	
	public TimeAllowance(int allocated) {
		this.allocated = allocated;
		this.used = 0;
		this.reserved = 0;
	}
	
	public int getAllocated() {return allocated;}
	
	public int getUsed() {return used;}
	
	public int getReserved() {return reserved;}
	
	public int getAvailable() {return allocated - used - reserved;}     // Calculates the remaining available minutes
	
	public void grantMinutes(int minutes) {     // Gives additional minutes to the student
		if (minutes > 0) allocated +=minutes;}         // Only positive amounts are accepted
	
	public boolean reserveMinutes(int minutes) {     // Reserves minutes before a job starts
		if (minutes <= 0 || minutes > getAvailable())  // Checks that the requested amount is valid
			return false;
		reserved += minutes;   // Adds the minutes to the reserved amount
		return true;
	}
	
	public void releaseReservation(int minutes) {     // Releases reserved minutes
		if (minutes >= 0 && minutes <= reserved)     // Checks that the amount is valid
			reserved -= minutes;
	}
	
	// Releases the reservation and charges actual runtime
	public void chargeReservedMinutes(int reservedMinutes, int actualMinutes) {
		// Validates both amounts
		if (reservedMinutes < 0 || reservedMinutes > reserved || actualMinutes < 0 || actualMinutes > reservedMinutes) 
			throw new IllegalArgumentException("Invalid minutes.");
		
		reserved -= reservedMinutes;  // Removes the full reservation
		used += actualMinutes;        // Charges only the actual minutes used
	}
}
