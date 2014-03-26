package chen.requestCounter;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class RequestCounterExecutor {

	private static final Map<AtomicLong, List<ICircularBuffer>> map = new HashMap<AtomicLong, List<ICircularBuffer>>();
	private static final AtomicReference<Map<AtomicLong, List<ICircularBuffer>>> ref = new AtomicReference<Map<AtomicLong, List<ICircularBuffer>>>(map);
	private static final ScheduledExecutorService exec = Executors.newScheduledThreadPool(6);
	private final boolean enableSec;
	private final boolean enableMin;
	private final boolean enableHour;
	
	public RequestCounterExecutor(boolean enableSec, boolean enableMin, boolean enableHour){
		this.enableHour = enableHour;
		this.enableMin = enableMin;
		this.enableSec = enableSec;
	}
	
	public void start(){
		if(enableSec){
			exec.scheduleAtFixedRate(new UpdateTask(0), 0, 1, TimeUnit.SECONDS);
		}
		if(enableMin){
			exec.scheduleAtFixedRate(new UpdateTask(1), 0, 1, TimeUnit.MINUTES);
		}
		if(enableHour){
			exec.scheduleAtFixedRate(new UpdateTask(2), 0, 1, TimeUnit.HOURS);
		}
	}
	
	public void shutdown(){
		exec.shutdown();
	}
	
	
	static class UpdateTask implements Runnable{
		private int flag;
		
		public UpdateTask(int flag){
			this.flag = flag;
		}
		
		@Override
		public void run() {
			Map<AtomicLong, List<ICircularBuffer>> tempMap = ref.get();
			Set<AtomicLong> keys = tempMap.keySet();
			for(AtomicLong counter : keys){
				tempMap.get(counter).get(flag).push(counter.get());
			}
		}
		
	}
	
	
	public void add(AtomicLong counter, int secBufferSize, int minBufferSize, int hourBufferSize){
		if(secBufferSize > 0 && !enableSec){
			throw new InvalidParameterException("no sec counter here, but with secBufferSize > 0");
		}
		if(minBufferSize > 0 && !enableMin){
			throw new InvalidParameterException("no min counter here, but with minBufferSize > 0");
		}
		if(hourBufferSize > 0 && !enableHour){
			throw new InvalidParameterException("no hour counter here, but with hourBufferSize > 0");
		}
		Map<AtomicLong, List<ICircularBuffer>> tempMap = new HashMap<AtomicLong, List<ICircularBuffer>>();
		tempMap.putAll(ref.get());
		List<ICircularBuffer> list = new ArrayList<ICircularBuffer>();
		list.add(CircularBuffers.getCircularBuffer(secBufferSize));
		list.add(CircularBuffers.getCircularBuffer(minBufferSize));
		list.add(CircularBuffers.getCircularBuffer(hourBufferSize));
		tempMap.put(counter, list);
		ref.set(tempMap);
	}
	
	public IRequestCounter getRequestCounter(final AtomicLong counter){
		return new IRequestCounter(){

			@Override
			public long getLastSecs(int secs) {
				return ref.get().get(counter).get(0).read(secs);
			}

			@Override
			public long getLastSec() {
				return getLastSecs(1);
			}

			@Override
			public long getLastMins(int mins) {
				return ref.get().get(counter).get(1).read(mins);
			}

			@Override
			public long getLastMin() {
				return getLastMins(1);
			}

			@Override
			public long getLastHours(int hours) {
				return ref.get().get(counter).get(2).read(hours);
			}

			@Override
			public long getLastHour() {
				return getLastHours(1);
			}
			
		};
	}
	
}
