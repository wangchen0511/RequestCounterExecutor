package chen.requestCounter;

public class CircularBuffers {

	public static ICircularBuffer getCircularBuffer(int size){
		if(size <= 0){
			return new NullCircularBuffer();
		}else{
			return new CircularBuffer(size);
		}
	}
		
}
