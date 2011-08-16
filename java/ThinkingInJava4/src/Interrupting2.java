import static print.Print.print;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

class BlockedMutex {
	private Lock lock = new ReentrantLock();
	public BlockedMutex() {
		// Acquire it right away, to demonstrate interruption
		// of a task blocked on a ReentrantLock:
		lock.lock();
	}
	public void f() {
		try {
			// This will never be available to a second task
			lock.lockInterruptibly(); // Special call
			print("lock acquired in f()");
		} catch(InterruptedException e) {
			print("Interrupted from lock acquisition in f()");
		}
	}
}
class Blocked2 implements Runnable {
	BlockedMutex blocked = new BlockedMutex();
	public void run() {
		print("Waiting for f() in BlockedMutex");
		blocked.f();
		print("Broken out of blocked call");
	}
}
public class Interrupting2 {
	public static void main(String[] args) throws Exception {
		// TODO why this is so slow
		ExecutorService exec = Executors.newCachedThreadPool();
		Future<?> f = exec.submit(new Blocked2());
		TimeUnit.SECONDS.sleep(1);
		System.out.println("Issuing t.interrupt()");
		f.cancel(true);
		print("Interrup sent to task.");

		/*
		Thread t = new Thread(new Blocked2());
		t.start();
		TimeUnit.SECONDS.sleep(1);
		System.out.println("Issuing t.interrupt()");
		t.interrupt();
		*/
	}
}
