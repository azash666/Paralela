//package lab1;

public class MiHebra1 extends Thread {
	private int miId;
	private int n;
	private int numThreads;
	public MiHebra1(int n, int i, int numThreads) {
		this.miId = i;
		this.n=n;
		this.numThreads = numThreads;
	}
	
	@Override
	public void run() {
		for (int i=miId; i<n; i+=numThreads) {
			System.out.println(i);
		}
	}

}
