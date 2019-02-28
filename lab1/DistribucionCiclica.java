//package lab1;


public class DistribucionCiclica {
	

	public static void main(String args[]) {
		int n = 0, numThreads = 0;
	
		if(args.length!=2) {
			System.out.println("Se esperaban dos argumentos numéricos");
			System.exit(-1);
		}
		try {
			n = Integer.parseInt(args[0]);
			numThreads = Integer.parseInt(args[1]);
		}catch(Exception e) {
			System.out.println("Ambos argumentos deben ser numéricos");
			System.exit(-1);
		}
		Thread[] hilos = new Thread[numThreads];
		for(int i=0; i<numThreads; i++) {
			hilos[i]= new MiHebra1(n, i, numThreads);
			hilos[i].start();
		}
		for(int i=0; i<numThreads; i++) {
			try {
				hilos[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
