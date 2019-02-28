package lab4;

import java.util.concurrent.atomic.DoubleAdder;

// ===========================================================================
class Acumula {
	// ===========================================================================
	double  suma;

	// -------------------------------------------------------------------------
	Acumula() {
		suma=0;
	}

	// -------------------------------------------------------------------------
	synchronized void acumulaDato( double dato ) {
		suma+=dato;
	}

	// -------------------------------------------------------------------------
	synchronized double dameDato() {
		return suma;
		// ...
	}
}

// ===========================================================================
class MiHebraMultAcumulaciones1a extends Thread {
	// ===========================================================================

	private int miId, numThreads;
	private long numRectangulos;
	private Acumula acumula;
	private double baseRectangulo;

	// -------------------------------------------------------------------------
	MiHebraMultAcumulaciones1a(int miId, int numThreads, long numRectangulos, 
			Acumula a ) {
		this.miId=miId;
		this.numThreads = numThreads;
		this.numRectangulos = numRectangulos;
		this.acumula = a;
		
		this.baseRectangulo = 1.0 / ( ( double ) numRectangulos );
	}
	
	public void run() {
		double x;
		for(int i=miId; i<numRectangulos; i+=numThreads) {
			x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
			acumula.acumulaDato(f( x ));
		}
	}

	private double f(double x) {
		return ( 4.0/( 1.0 + x*x ) );
	}
}

class MiHebraUnaAcumulacion1 extends Thread {
	// ===========================================================================

	private int miId, numThreads;
	private long numRectangulos;
	private Acumula acumula;
	private double baseRectangulo, acumulaLocal;

	// -------------------------------------------------------------------------
	MiHebraUnaAcumulacion1(int miId, int numThreads, long numRectangulos, 
			Acumula a ) {
		this.miId=miId;
		this.numThreads = numThreads;
		this.numRectangulos = numRectangulos;
		this.acumula = a;
		acumulaLocal=0.0;
		this.baseRectangulo = 1.0 / ( ( double ) numRectangulos );
	}
	
	public void run() {
		for(int i=miId; i<numRectangulos; i+=numThreads) {
			acumulaLocal += f(baseRectangulo * ( ( ( double ) i ) + 0.5 ));
			
		}
		acumula.acumulaDato(acumulaLocal);
	}

	private double f(double x) {
		return ( 4.0/( 1.0 + x*x ) );
	}
	
	
}

class MiHebraMultAcumulacionesAtomica extends Thread {
	// ===========================================================================

	private int miId, numThreads;
	private long numRectangulos;
	private DoubleAdder acumula;
	private double baseRectangulo;

	// -------------------------------------------------------------------------
	MiHebraMultAcumulacionesAtomica(int miId, int numThreads, long numRectangulos, 
			DoubleAdder a ) {
		this.miId=miId;
		this.numThreads = numThreads;
		this.numRectangulos = numRectangulos;
		this.acumula = a;
		
		this.baseRectangulo = 1.0 / ( ( double ) numRectangulos );
	}
	
	public void run() {
		double x;
		for(int i=miId; i<numRectangulos; i+=numThreads) {
			x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
			acumula.add(f( x ));
		}
	}

	private double f(double x) {
		return ( 4.0/( 1.0 + x*x ) );
	}
}

class MiHebraUnaAcumulacionAtomica extends Thread {
		// ===========================================================================

	private int miId, numThreads;
	private long numRectangulos;
	private DoubleAdder acumula;
	private double baseRectangulo, acumulaLocal;

	// -------------------------------------------------------------------------
	MiHebraUnaAcumulacionAtomica(int miId, int numThreads, long numRectangulos, 
			DoubleAdder a ) {
		this.miId=miId;
		this.numThreads = numThreads;
		this.numRectangulos = numRectangulos;
		this.acumula = a;
		acumulaLocal=0.0;
		this.baseRectangulo = 1.0 / ( ( double ) numRectangulos );
	}
	
	public void run() {
		for(int i=miId; i<numRectangulos; i+=numThreads) {
			acumulaLocal += f(baseRectangulo * ( ( ( double ) i ) + 0.5 ));
			
		}
		acumula.add(acumulaLocal);
	}

	private double f(double x) {
		return ( 4.0/( 1.0 + x*x ) );
	}
}


// ===========================================================================
class EjemploNumeroPI1a {
	// ===========================================================================

	// -------------------------------------------------------------------------
	public static void main( String args[] ) {
		long                        numRectangulos;
		int                         numHebras;
		MiHebraMultAcumulaciones1a  vt[];
		MiHebraUnaAcumulacion1  vt2[];
		MiHebraMultAcumulacionesAtomica  vt3[];
		MiHebraUnaAcumulacionAtomica  vt4[];
		System.out.println(23342134);
		// Comprobacion de los argumentos de entrada.
		if( args.length != 2 ) {
			System.out.println( "ERROR: numero de argumentos incorrecto.");
			System.out.println( "Uso: java programa <numHebras> <numRectangulos>" );
			System.exit( -1 );
		}
		try {
			numHebras      = Integer.parseInt( args[ 0 ] );
			numRectangulos = Long.parseLong( args[ 1 ] );
		} catch( NumberFormatException ex ) {
			numHebras      = -1;
			numRectangulos = -1;
			System.out.println( "ERROR: Numeros de entrada incorrectos." );
			System.exit( -1 );
		}

		System.out.println();
		System.out.println( "Calculo del numero PI mediante integracion." );
		vt = new MiHebraMultAcumulaciones1a[numHebras];
		vt2 = new MiHebraUnaAcumulacion1[numHebras];
		vt3 = new MiHebraMultAcumulacionesAtomica[numHebras];
		vt4 = new MiHebraUnaAcumulacionAtomica[numHebras];
		//
		// Calculo del numero PI de forma secuencial.
		//
		metodoSecuencial(numRectangulos);
		//
		// Calculo del numero PI de forma paralela: 
		// Multiples acumulaciones por hebra.
		//
		metodoParalelo(numRectangulos, numHebras, vt);
		metodoParalelo2(numRectangulos, numHebras, vt2);
		metodoParaleloAtomico(numRectangulos, numHebras, vt3);
		metodoParalelo2Atomico(numRectangulos, numHebras, vt4);

		System.out.println();
		System.out.println( "Fin de programa." );
	}

	private static void metodoSecuencial(long numRectangulos) {
		double baseRectangulo;
		double x;
		double suma;
		double pi;
		long t1;
		long t2;
		double tSec;
		System.out.println();
		System.out.println( "Comienzo del calculo secuencial." );
		t1 = System.nanoTime();
		baseRectangulo = 1.0 / ( ( double ) numRectangulos );
		suma           = 0.0;
		for( long i = 0; i < numRectangulos; i++ ) {
			x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
			suma += f( x );
		}
		pi = baseRectangulo * suma;
		t2 = System.nanoTime();
		tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Version Secuencial. Numero PI: " + pi );
		System.out.println( "Tiempo transcurrido (s.):      " + tSec );

		System.out.println();
	}
	
	private static void metodoParalelo(long numRectangulos, int numHebras, MiHebraMultAcumulaciones1a  vt[]) {
		double baseRectangulo, pi;
		long t1, t2;
		double tSec;
		Acumula a = new Acumula();
		System.out.println( "\nComienzo del calculo paralelo." );
		t1 = System.nanoTime();
		baseRectangulo = 1.0 / ( ( double ) numRectangulos );
		for( int i = 0; i < numHebras; i++ ) {
			vt[i] = new MiHebraMultAcumulaciones1a(i, numHebras, numRectangulos, a);
			vt[i].start();
		}

		for( int i = 0; i < numHebras; i++ ) {
			try {
				vt[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pi = baseRectangulo * a.dameDato();
		t2 = System.nanoTime();
		tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Version Paralela. Numero PI: " + pi );
		System.out.println( "Tiempo transcurrido (s.):      " + tSec +"\n");
	}
	
	private static void metodoParalelo2(long numRectangulos, int numHebras, MiHebraUnaAcumulacion1  vt[]) {
		double baseRectangulo, pi;
		long t1, t2;
		double tSec;
		Acumula a = new Acumula();
		System.out.println( "\nComienzo del calculo paralelo 2." );
		t1 = System.nanoTime();
		baseRectangulo = 1.0 / ( ( double ) numRectangulos );
		for( int i = 0; i < numHebras; i++ ) {
			vt[i] = new MiHebraUnaAcumulacion1(i, numHebras, numRectangulos, a);
			vt[i].start();
		}

		for( int i = 0; i < numHebras; i++ ) {
			try {
				vt[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pi = baseRectangulo * a.dameDato();
		t2 = System.nanoTime();
		tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Version Paralela 2. Numero PI: " + pi );
		System.out.println( "Tiempo transcurrido (s.):      " + tSec +"\n");
	}
	
	
	private static void metodoParaleloAtomico(long numRectangulos, int numHebras, MiHebraMultAcumulacionesAtomica  vt[]) {
		double baseRectangulo, pi;
		long t1, t2;
		double tSec;
		DoubleAdder a = new DoubleAdder();
		System.out.println( "\nComienzo del calculo paralelo atomico." );
		t1 = System.nanoTime();
		baseRectangulo = 1.0 / ( ( double ) numRectangulos );
		for( int i = 0; i < numHebras; i++ ) {
			vt[i] = new MiHebraMultAcumulacionesAtomica(i, numHebras, numRectangulos, a);
			vt[i].start();
		}
		for( int i = 0; i < numHebras; i++ ) {
			try {
				vt[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pi = baseRectangulo * a.sum();
		t2 = System.nanoTime();
		tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Version Paralela atomica. Numero PI: " + pi );
		System.out.println( "Tiempo transcurrido (s.):      " + tSec +"\n");
	}
	
	
	private static void metodoParalelo2Atomico(long numRectangulos, int numHebras, MiHebraUnaAcumulacionAtomica  vt[]) {
		double baseRectangulo, pi;
		long t1, t2;
		double tSec;
		DoubleAdder a = new DoubleAdder();
		System.out.println( "\nComienzo del calculo paralelo atomico." );
		t1 = System.nanoTime();
		baseRectangulo = 1.0 / ( ( double ) numRectangulos );
		for( int i = 0; i < numHebras; i++ ) {
			vt[i] = new MiHebraUnaAcumulacionAtomica(i, numHebras, numRectangulos, a);
			vt[i].start();
		}

		for( int i = 0; i < numHebras; i++ ) {
			try {
				vt[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pi = baseRectangulo * a.sum();
		t2 = System.nanoTime();
		tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.println( "Version Paralela atomica. Numero PI: " + pi );
		System.out.println( "Tiempo transcurrido (s.):      " + tSec +"\n");
	}
	
	

	// -------------------------------------------------------------------------
	static double f( double x ) {
		return ( 4.0/( 1.0 + x*x ) );
	}
	
	
}


