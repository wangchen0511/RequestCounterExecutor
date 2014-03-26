package chen.requestCounter;

public interface ICircularBuffer {
	public void push(long val);
	
	public long read(int length);
}
