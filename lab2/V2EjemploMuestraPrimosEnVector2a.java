package lab2;

import java.util.concurrent.atomic.AtomicInteger;

// ===========================================================================
public class V2EjemploMuestraPrimosEnVector2a {
// ===========================================================================

	// -------------------------------------------------------------------------
	public static void main(String args[]) {
		int numHebras;
		long t1, t2;
		double tt, ttc, ttb;
//    long    vectorNumeros[] = {
//                200000033L, 200000039L, 200000051L, 200000069L, 
//                200000081L, 200000083L, 200000089L, 200000093L, 
//                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
//                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
//                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
//                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
//                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
//                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
//                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
//            };
		long vectorNumeros[] = { 200000033L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 200000039L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
				200000051L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 200000069L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 200000081L, 4L, 4L, 4L,
				4L, 4L, 4L, 4L, 200000083L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 200000089L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
				200000093L, 4L, 4L, 4L, 4L, 4L, 4L, 4L };

		// Comprobacion y extraccion de los argumentos de entrada.

		if (args.length != 1) {
			System.err.println("Uso: java programa <numHebras>");
			System.exit(-1);
		}
		try {
			numHebras = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			numHebras = -1;
			System.out.println("ERROR: Argumentos numericos incorrectos.");
			System.exit(-1);
		}

		//
		// Implementacion secuencial.
		//
		System.out.println("");
		System.out.println("-----------------------Implementacion secuencial-------------------");
		t1 = System.nanoTime();
		for (int i = 0; i < vectorNumeros.length; i++) {
			if (esPrimo(vectorNumeros[i])) {
				System.out.println("  Encontrado primo: " + vectorNumeros[i]);
			}
		}
		t2 = System.nanoTime();
		tt = ((double) (t2 - t1)) / 1.0e9;
		System.out.println("Tiempo secuencial (seg.):                    " + tt);

		// Implementacion paralela ciclica.

		System.out.println("-----------------------Implementacion ciclica-------------------");
		t1 = System.nanoTime();
		Thread hebrasC[] = new V2HebraCircular[numHebras];

		for (int i = 0; i < numHebras; i++) {
			hebrasC[i] = new V2HebraCircular(i, numHebras, vectorNumeros);
			hebrasC[i].start();
		}

		for (int i = 0; i < numHebras; i++) {
			try {
				hebrasC[i].join();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		t2 = System.nanoTime();
		ttc = ((double) (t2 - t1)) / 1.0e9;
		System.out.println("Tiempo circular (seg.):                    " + ttc);
		System.out.println("Speed-up (seg.):                    " + tt / ttc);

		// Implementacion paralela bloques.

		System.out.println("-----------------------Implementacion bloques-------------------");
		t1 = System.nanoTime();
		Thread hebrasB[] = new V2HebraBloques[numHebras];

		for (int i = 0; i < numHebras; i++) {
			hebrasB[i] = new V2HebraBloques(i, numHebras, vectorNumeros);
			hebrasB[i].start();
		}

		for (int i = 0; i < numHebras; i++) {
			try {
				hebrasB[i].join();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		t2 = System.nanoTime();
		ttb = ((double) (t2 - t1)) / 1.0e9;

		System.out.println("Tiempo bloques (seg.):                    " + ttb);
		System.out.println("Speed-up (seg.):                    " + tt / ttb);

		// Implementacion paralela dinamica.

		System.out.println("-----------------------Implementacion dinamica-------------------");
		t1 = System.nanoTime();
		Thread hebrasD[] = new V2HebraDinamica[numHebras];

		for (int i = 0; i < numHebras; i++) {
			hebrasB[i] = new V2HebraBloques(i, numHebras, vectorNumeros);
			hebrasB[i].start();
		}

		for (int i = 0; i < numHebras; i++) {
			try {
				hebrasB[i].join();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		t2 = System.nanoTime();
		ttb = ((double) (t2 - t1)) / 1.0e9;

		System.out.println("Tiempo bloques (seg.):                    " + ttb);
		System.out.println("Speed-up (seg.):                    " + tt / ttb);

	}

	// -------------------------------------------------------------------------
	static boolean esPrimo(long num) {
		boolean primo;
		if (num < 2) {
			primo = false;
		} else {
			primo = true;
			long i = 2;
			while ((i < num) && (primo)) {
				primo = (num % i != 0);
				i++;
			}
		}
		return (primo);
	}

}

class V2HebraCircular extends Thread {

	int id;
	int numHebras;
	long numeros[];

	public V2HebraCircular(int id, int numHebras, long vectorNumeros[]) {
		this.id = id;
		this.numHebras = numHebras;
		this.numeros = vectorNumeros;

	}

	public void run() {
		for (int i = id; i < numeros.length; i += numHebras) {
			if (esPrimo(numeros[i]))
				System.out.println("  Encontrado primo: " + numeros[i]);
		}
	}

	static boolean esPrimo(long num) {
		boolean primo;
		if (num < 2) {
			primo = false;
		} else {
			primo = true;
			long i = 2;
			while ((i < num) && (primo)) {
				primo = (num % i != 0);
				i++;
			}
		}
		return (primo);
	}
}

class V2HebraBloques extends Thread {

	int id;
	int numHebras;
	long numeros[];

	public V2HebraBloques(int id, int numHebras, long vectorNumeros[]) {
		this.id = id;
		this.numHebras = numHebras;
		this.numeros = vectorNumeros;
	}

	public void run() {
		int tam = (numeros.length + numHebras - 1) / numHebras;
		int inicio = id * tam;
		int fin = Math.min(numeros.length, inicio + tam);

		for (int i = inicio; i < fin; i++) {
			if (esPrimo(numeros[i]))
				System.out.println("  Encontrado primo: " + numeros[i]);
		}
	}

	static boolean esPrimo(long num) {
		boolean primo;
		if (num < 2) {
			primo = false;
		} else {
			primo = true;
			long i = 2;
			while ((i < num) && (primo)) {
				primo = (num % i != 0);
				i++;
			}
		}
		return (primo);
	}
}

class V2HebraDinamica extends Thread {

	static AtomicInteger indice = new AtomicInteger();
	int id;
	int numHebras;
	long numeros[];

	public V2HebraDinamica(int id, int numHebras, long vectorNumeros[]) {
		this.id = id;
		this.numHebras = numHebras;
		this.numeros = vectorNumeros;
	}

	public void run() {

		for (int i= indice.getAndIncrement(); i < numeros.length; i= indice.getAndIncrement()) {
			if (esPrimo(numeros[i]))
				System.out.println("  Encontrado primo: " + numeros[i]);
		}
	}

	static boolean esPrimo(long num) {
		boolean primo;
		if (num < 2) {
			primo = false;
		} else {
			primo = true;
			long i = 2;
			while ((i < num) && (primo)) {
				primo = (num % i != 0);
				i++;
			}
		}
		return (primo);
	}
}
