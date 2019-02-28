//package lab1;

public class DistribucionBloques {
	

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
		int tam = (n+numThreads-1)/numThreads;
		
		for(int i=0; i<numThreads; i++) {
			int iniElem = i*tam;
			int finElem = Math.min(iniElem+tam, n);
			hilos[i]= new MiHebra2(i, iniElem, finElem);
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
