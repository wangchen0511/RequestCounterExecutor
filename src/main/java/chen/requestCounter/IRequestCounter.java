package chen.requestCounter;

public interface IRequestCounter {
	public long getLastSecs(int secs);
	
	public long getLastSec();
	
	public long getLastMins(int mins);
	
	public long getLastMin();
	
	public long getLastHours(int hours);
	
	public long getLastHour();
}
