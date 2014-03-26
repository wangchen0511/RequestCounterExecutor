package chen.requestCounter;


public class CircularBuffer implements ICircularBuffer{

	private final long[] buffer;
	private final int size;
	private int currentLength;
	private int head = 0;
	private int tail = -1;
	private long lastCounter = 0;
	
	public CircularBuffer(int size){
		this.size = size;
		this.buffer = new long[size];
	}
	
	public synchronized void push(long val){
		tail = (tail == size - 1) ? 0 : tail + 1;
		buffer[tail] = val - this.lastCounter;
		this.lastCounter = val;
		if(currentLength == size){
			head = (head == size - 1) ? 0 : head + 1;
		}else{
			currentLength++;
		}
	}
	
	public synchronized long read(int length){
		int localTail = this.tail;
		long sum = 0;
		length = (currentLength >= length) ? length : currentLength;
		for(int i = 0; i < length; i++){
			sum += this.buffer[localTail];
			localTail = (localTail == 0) ? size - 1 : localTail - 1;
		}
		return sum;
	}
	
}
