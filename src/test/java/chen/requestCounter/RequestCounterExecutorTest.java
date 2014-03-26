package chen.requestCounter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.testng.annotations.Test;

/**
 * 
 * @author adam701
 *
 */


public class RequestCounterExecutorTest {

	@Test(description="Test get thread This Hour")
	public void testThreadGetThisHour() throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException{
		final AtomicLong counter = new AtomicLong();
		RequestCounterExecutor exec1 = new RequestCounterExecutor(true, true, true);
		exec1.add(counter, 300, 60, 24);
		exec1.start();
		
		//ScheduledExecutorService exec = Executors.newScheduledThreadPool(100);
		System.out.println("For our test! ");
		ExecutorService exec = Executors.newCachedThreadPool();
		final CountDownLatch startLatch = new CountDownLatch(101);
		final CountDownLatch doneLatch = new CountDownLatch(101);
		long startTime, endTime;
		class Task implements Runnable{
			@Override
			public void run() {
				Random rand = new Random();
				// TODO Auto-generated method stub
				try {
					startLatch.countDown();
					startLatch.await();
					for(int i = 0; i < 10; i++){
						counter.incrementAndGet();
						Thread.sleep(1000);
						//Thread.sleep(rand.nextInt(1000) + 1);
					}
					doneLatch.countDown();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for(int i = 0; i < 100; i++){
			exec.execute(new Task());
		}
		startLatch.countDown();
		startTime = System.nanoTime();
		doneLatch.countDown();
		doneLatch.await();
		endTime = System.nanoTime();
		
		System.out.println("Total time " + TimeUnit.NANOSECONDS.toSeconds(endTime - startTime));
		System.out.println("Last 60 Secs " + exec1.getRequestCounter(counter).getLastSecs(300));
		System.out.println("Last 1 Min " + exec1.getRequestCounter(counter).getLastMins(1));
		System.out.println("Last 1 Hour " + exec1.getRequestCounter(counter).getLastHours(1));
		System.out.println("Total" + counter.get());
		exec.shutdown();
		exec1.shutdown();
	}
	
}
