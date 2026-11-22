package gpu;
import java.io.Serializable;
public class TimeAllowance implements Serializable {
	private int allocated, used, reserved;
	
	public TimeAllowance(int allocated) {
		this.allocated = allocated;
		this.used = 0;
		this.reserved = 0;
	}
	
	public int getAllocated() {return allocated;}
	
	public int getUsed() {return used;}
	
	public int getReserved() {return reserved;}
	
	public int getAvailable() {return allocated - used - reserved;}
	
	public void grantMinutes(int minutes) {
		if (minutes > 0) allocated +=minutes;}
	
	public boolean reserveMinutes(int minutes) {
		if (minutes <= 0 || minutes > getAvailable()) 
			return false;
		reserved += minutes;
		return true;
	}
	
	public void releaseReservation(int minutes) {
		if (minutes >= 0 && minutes <= reserved)
			reserved -= minutes;
	}
	
	public void chargeReservedMinutes(int reservedMinutes, int actualMinutes) {
		if (reservedMinutes < 0 || reservedMinutes > reserved || actualMinutes < 0 || actualMinutes > reservedMinutes) 
			throw new IllegalArgumentException("Invalid minutes.");
		
		reserved -= reservedMinutes;
		used += actualMinutes;
	}
}
