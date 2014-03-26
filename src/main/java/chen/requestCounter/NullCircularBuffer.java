package chen.requestCounter;

public class NullCircularBuffer implements ICircularBuffer{

	@Override
	public void push(long val) {
	}

	@Override
	public long read(int length) {
		return -9999;
	}

}
