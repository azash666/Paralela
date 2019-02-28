package lab6;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;

// ============================================================================
class Ejercicio {
	// ============================================================================

	// -------------------------------------------------------------------------
	public static void main( String args[] ) {
		long                     t1, t2;
		double                   tt,ttc;
		int                      numHebras;
		String                   nombreFichero, palabraActual;
		Vector<String>           vectorLineas;
		HashMap<String,Integer>  hmCuentaPalabrasmap;
		Hashtable<String, Integer>  hmCuentaPalabras;
		Map<String,Integer> hmCuentaPalabrasSincronizado; 

		// Comprobacion y extraccion de los argumentos de entrada.
		if( args.length != 2 ) {
			System.err.println( "Uso: java programa <numHebras> <fichero>" );
			System.exit( -1 );
		}
		try {
			numHebras     = Integer.parseInt( args[ 0 ] );
			nombreFichero = args[ 1 ];
		} catch( NumberFormatException ex ) {
			numHebras = -1;
			nombreFichero = "";
			System.out.println( "ERROR: Argumentos numericos incorrectos." );
			System.exit( -1 );
		}

		// Lectura y carga de lineas en "vectorLineas".
		vectorLineas = readFile( nombreFichero );
		System.out.println( "Numero de lineas leidas: " + vectorLineas.size() );
		System.out.println();

		//
		// Implementacion secuencial sin temporizar.
		//
		//    hmCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
		//    for( int i = 0; i < vectorLineas.size(); i++ ) {
		//      // Procesa la linea "i".
		//      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
		//      for( int j = 0; j < palabras.length; j++ ) {
		//        // Procesa cada palabra de la linea "i", si es distinta de blancos.
		//        palabraActual = palabras[ j ].trim();
		//        if( palabraActual.length() > 0 ) {
		//          contabilizaPalabra( hmCuentaPalabras, palabraActual );
		//        }
		//      }
		//    }

		//
		// Implementacion secuencial.
		//
		t1 = System.nanoTime();
		hmCuentaPalabrasmap = new HashMap<String,Integer>( 1000, 0.75F );
		for( int i = 0; i < vectorLineas.size(); i++ ) {
			// Procesa la linea "i".
			String[] palabras = vectorLineas.get( i ).split( "\\W+" );
			for( int j = 0; j < palabras.length; j++ ) {
				// Procesa cada palabra de la linea "i", si es distinta de blancos.
				palabraActual = palabras[ j ].trim();
				if( palabraActual.length() > 0 ) {
					contabilizaPalabra( hmCuentaPalabrasmap, palabraActual );
				}
			}
		}
		t2 = System.nanoTime();
		tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. secuencial: " );
		imprimePalabraMasUsadaYVeces( hmCuentaPalabrasmap );
		System.out.println( " Tiempo(s): " + tt );
		System.out.println( "Num. elems. tabla hash: " + hmCuentaPalabrasmap.size() );
		System.out.println();


		System.out.println( "Fin de programa." );

		//---------- Implementacion paralela con HashMap ------------------

		t1 = System.nanoTime();
		hmCuentaPalabrasmap =  new HashMap<String,Integer>( 1000, 0.75F );
		hmCuentaPalabrasSincronizado = Collections.synchronizedMap(hmCuentaPalabrasmap);

		Thread hebras[] = new MiHebra_1[numHebras];

		for(int i=0; i<numHebras; i++){

			hebras[i] = new MiHebra_1(i,numHebras, vectorLineas, hmCuentaPalabrasSincronizado);
			hebras[i].start();

		}

		for(int i=0; i<numHebras; i++){
			try{
				hebras[i].join();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}

		t2 = System.nanoTime();
		ttc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela ciclica HashMap: " );
		imprimePalabraMasUsadaYVeces( hmCuentaPalabrasSincronizado );
		System.out.println( " Tiempo(s): " + ttc );
		System.out.println( "Num. elems. tabla hash: " + hmCuentaPalabrasSincronizado.size() );
		System.out.println();

		//---------- Implementacion paralela con Hashtable ------------------

		t1 = System.nanoTime();
		hmCuentaPalabras = new Hashtable<String,Integer>( 1000, 0.75F );
		hebras = new MiHebra_2[numHebras];

		for(int i=0; i<numHebras; i++){

			hebras[i] = new MiHebra_2(i,numHebras, vectorLineas, hmCuentaPalabras);
			hebras[i].start();

		}

		for(int i=0; i<numHebras; i++){
			try{
				hebras[i].join();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}

		t2 = System.nanoTime();
		ttc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela ciclica HashTable: " );
		imprimePalabraMasUsadaYVeces( hmCuentaPalabras );
		System.out.println( " Tiempo(s): " + ttc );
		System.out.println( "Num. elems. tabla hash: " + hmCuentaPalabras.size() );
		System.out.println();

		// Implementacion con ConcurrentHashMap.
		//
		t1 = System.nanoTime();
		ConcurrentHashMap<String, Integer> chmCuentaPalabras = new ConcurrentHashMap<String,Integer>( 1000, 0.75F );
		Thread[] hilos = new Thread[numHebras];
		for(int i=0; i<numHebras; i++) {
			hilos[i] = new MiHebra_5(numHebras, i, chmCuentaPalabras, vectorLineas);
			hilos[i].start();
		}
		for(int i=0; i<numHebras; i++) {
			try {
				hilos[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t2 = System.nanoTime();
		tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela ejercicio 3: " );
		imprimePalabraMasUsadaYVeces( chmCuentaPalabras );
		System.out.println( " Tiempo(s): " + tt );
		System.out.println( "Num. elems. tabla hash concurrente: " + chmCuentaPalabras.size() );
		System.out.println();


		//---------- Implementacion paralela 4 ------------------

		t1 = System.nanoTime();
		ConcurrentHashMap<String, Integer> chmCuentaPalabrasmap = new ConcurrentHashMap<String,Integer>( 1000, 0.75F );
		hebras = new MiHebra_4[numHebras];

		for(int i=0; i<numHebras; i++){

			hebras[i] = new MiHebra_4(i,numHebras, vectorLineas, chmCuentaPalabrasmap);
			hebras[i].start();

		}

		for(int i=0; i<numHebras; i++){
			try{
				hebras[i].join();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}

		t2 = System.nanoTime();
		ttc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela ejercicio 4: " );
		imprimePalabraMasUsadaYVeces( chmCuentaPalabrasmap );
		System.out.println( " Tiempo(s): " + ttc );
		System.out.println( "Num. elems. tabla hash: " + chmCuentaPalabrasmap.size() );
		System.out.println();

		// Implementacion paralela 5.
		//
		t1 = System.nanoTime();
		chmCuentaPalabras = new ConcurrentHashMap<String,Integer>( 1000, 0.75F );
		hilos = new Thread[numHebras];
		for(int i=0; i<numHebras; i++) {
			hilos[i] = new MiHebra_5(numHebras, i, chmCuentaPalabras, vectorLineas);
			hilos[i].start();
		}
		for(int i=0; i<numHebras; i++) {
			try {
				hilos[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t2 = System.nanoTime();
		tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		System.out.print( "Implemen. paralela ejercicio 5: " );
		imprimePalabraMasUsadaYVeces( chmCuentaPalabras );
		System.out.println( " Tiempo(s): " + tt );
		System.out.println( "Num. elems. tabla hash concurrente: " + chmCuentaPalabras.size() );
		System.out.println();

		// Implementacion paralela 6.
				//
				t1 = System.nanoTime();
				chmCuentaPalabras = new ConcurrentHashMap<String,Integer>( 1000, 0.75F , 256);
				hilos = new Thread[numHebras];
				for(int i=0; i<numHebras; i++) {
					hilos[i] = new MiHebra_6(numHebras, i, chmCuentaPalabras, vectorLineas);
					hilos[i].start();
				}
				for(int i=0; i<numHebras; i++) {
					try {
						hilos[i].join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				t2 = System.nanoTime();
				tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
				System.out.print( "Implemen. paralela ejercicio 6: " );
				imprimePalabraMasUsadaYVeces( chmCuentaPalabras );
				System.out.println( " Tiempo(s): " + tt );
				System.out.println( "Num. elems. tabla hash concurrente: " + chmCuentaPalabras.size() );
				System.out.println();

				// Implementacion paralela 7.
				/*
				t1 = System.nanoTime();
				Map<String, Integer> stCuentaPalabras = vectorLineas.parallelStream()
						.filter(s -> s!=null)
						.map(s->s.split("\\W+"))
						.flatMap(Arrays::stream)
						.map(String::trim)
						.filter(s->(s.length()>0))
						.collect(groupingBy(s->s,counting()));
				
				t2 = System.nanoTime();
				tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
				System.out.print( "Implemen. paralela ejercicio 5: " );
				imprimePalabraMasUsadaYVeces( stCuentaPalabras );
				System.out.println( " Tiempo(s): " + tt );
				System.out.println( "Num. elems. tabla hash concurrente: " + stCuentaPalabras.size() );
				System.out.println();
				*/

				System.out.println( "Fin de programa." );


	}


	// -------------------------------------------------------------------------
	public static Vector<String> readFile( String fileName ) {
		BufferedReader br; 
		String         linea;
		Vector<String> data = new Vector<String>();

		try {
			br = new BufferedReader( new FileReader( fileName ) );
			while( ( linea = br.readLine() ) != null ) {
				//// System.out.println( "Leida linea: " + linea );
				data.add( linea );
			}
			br.close(); 
		} catch( FileNotFoundException ex ) {
			ex.printStackTrace();
		} catch( IOException ex ) {
			ex.printStackTrace();
		}
		return data;
	}

	// -------------------------------------------------------------------------
	public static synchronized void contabilizaPalabra( 
			Map<String,Integer> cuentaPalabras,
			String palabra ) {
		Integer numVeces = cuentaPalabras.get( palabra );
		if( numVeces != null ) {
			cuentaPalabras.put( palabra, numVeces+1 );
		} else {
			cuentaPalabras.put( palabra, 1 );
		}
	}   

	public static void contabilizaPalabra5( ConcurrentHashMap<String, Integer> chmCuentaPalabras, String palabra ) {
		Integer variable = chmCuentaPalabras.putIfAbsent(palabra, 1);
		if(variable != null) {
			boolean modif;
			do {
				AtomicInteger numVeces= new AtomicInteger(chmCuentaPalabras.get( palabra ));
				modif = chmCuentaPalabras.replace( palabra, numVeces.get(), numVeces.incrementAndGet() );
			}while(!modif);
		}
		
	}

	// --------------------------------------------------------------------------
	static void imprimePalabraMasUsadaYVeces(
			Map<String,Integer> cuentaPalabras ) {
		Vector<Map.Entry> lista = 
				new Vector<Map.Entry>( cuentaPalabras.entrySet() );

		String palabraMasUsada = "";
		int    numVecesPalabraMasUsada = 0;
		// Calcula la palabra mas usada.
		for( int i = 0; i < lista.size(); i++ ) {
			String palabra = ( String ) lista.get( i ).getKey();
			int numVeces = ( Integer ) lista.get( i ).getValue();
			if( i == 0 ) {
				palabraMasUsada = palabra;
				numVecesPalabraMasUsada = numVeces;
			} else if( numVecesPalabraMasUsada < numVeces ) {
				palabraMasUsada = palabra;
				numVecesPalabraMasUsada = numVeces;
			}
		}
		// Imprime resultado.
		System.out.print( "( Palabra: '" + palabraMasUsada + "' " + 
				"veces: " + numVecesPalabraMasUsada + " )" );
	}

	
	
	// --------------------------------------------------------------------------
	static void printCuentaPalabrasOrdenadas(
			HashMap<String,Integer> cuentaPalabras ) {
		int             i, numVeces;
		List<Map.Entry> list = new Vector<Map.Entry>( cuentaPalabras.entrySet() );

		// Ordena por valor.
		Collections.sort( 
				list,
				new Comparator<Map.Entry>() {
					public int compare( Map.Entry e1, Map.Entry e2 ) {
						Integer i1 = ( Integer ) e1.getValue();
						Integer i2 = ( Integer ) e2.getValue();
						return i2.compareTo( i1 );
					}
				}
				);
		// Muestra contenido.
		i = 1;
		System.out.println( "Veces Palabra" );
		System.out.println( "-----------------" );
		for( Map.Entry e : list ) {
			numVeces = ( ( Integer ) e.getValue () ).intValue();
			System.out.println( i + " " + e.getKey() + " " + numVeces );
			i++;
		}
		System.out.println( "-----------------" );
	}
}

/********************************************************************************************/
/**						HEBRAS															   **/
/********************************************************************************************/


class MiHebra_1 extends Thread {

	int id;
	int numHebras;
	Vector<String> lineas;
	Map<String, Integer> almacenPalabras; 

	public MiHebra_1(int id,  int numHebras, Vector<String> vectorLineas, Map<String, Integer> hmCuentaPalabras){

		this.id=id;
		this.numHebras = numHebras;
		this.lineas = vectorLineas;
		this.almacenPalabras = hmCuentaPalabras;
	}


	public void run(){

		String palabraActual;

		for(int i=id; i<lineas.size(); i+=numHebras){
			String[] palabras = lineas.get( i ).split( "\\W+" );
			for( int j = 0; j < palabras.length; j++ ) {
				palabraActual = palabras[ j ].trim();
				if( palabraActual.length() > 0 ) {
					Ejercicio.contabilizaPalabra( almacenPalabras, palabraActual );
				}
			}
		}
	}
}

class MiHebra_2 extends Thread {
	
	int id;
	int numHebras;
	Vector<String> lineas;
	Hashtable<String, Integer> almacenPalabras; 
	
	public MiHebra_2(int id,  int numHebras, Vector<String> vectorLineas, Hashtable<String, Integer> hmCuentaPalabras){
		
		this.id=id;
		this.numHebras = numHebras;
		this.lineas = vectorLineas;
		this.almacenPalabras = hmCuentaPalabras;
	}
	
	
	public void run(){
		
		String palabraActual;
		
		for(int i=id; i<lineas.size(); i+=numHebras){
			String[] palabras = lineas.get( i ).split( "\\W+" );
		    for( int j = 0; j < palabras.length; j++ ) {
		    	palabraActual = palabras[ j ].trim();
		        if( palabraActual.length() > 0 ) {
		        	Ejercicio.contabilizaPalabra( almacenPalabras, palabraActual );
		        }
		    }
		 }
	}
}

class MiHebra_3 extends Thread{
	private int miId;
	private ConcurrentHashMap<String, Integer> chmCuentaPalabras;
	private Vector<String> vectorLineas;
	private int numHebras;
	private String palabraActual;
	public MiHebra_3(int numHebras, int i, ConcurrentHashMap<String, Integer> chmCuentaPalabras, Vector<String> vectorLineas) {
		// TODO Auto-generated constructor stub
		this.numHebras = numHebras;
		this.miId = i;
		this.chmCuentaPalabras = chmCuentaPalabras;
		this.vectorLineas = vectorLineas;
	}
	public void run(){
		for(int i = miId; i < vectorLineas.size(); i+=numHebras ) {
			String[] palabras = vectorLineas.get( i ).split( "\\W+" );
			for( int j = 0; j < palabras.length; j++ ) {
				palabraActual = palabras[ j ].trim();
				if( palabraActual.length() > 0 ) {
					synchronized (chmCuentaPalabras) {
						Integer numVeces = chmCuentaPalabras.get( palabraActual );
						if( numVeces != null ) {
							chmCuentaPalabras.put( palabraActual, numVeces+1 );
						} else {
							chmCuentaPalabras.put( palabraActual, 1 );
						}
					}
				}
			}
		}
	}
}

class MiHebra_4 extends Thread {
	
	int id;
	int numHebras;
	Vector<String> lineas;
	ConcurrentHashMap<String, Integer> almacenPalabras; 
	
	public MiHebra_4(int id,  int numHebras, Vector<String> vectorLineas, ConcurrentHashMap<String, Integer> hmCuentaPalabras){
		
		this.id=id;
		this.numHebras = numHebras;
		this.lineas = vectorLineas;
		this.almacenPalabras = hmCuentaPalabras;
	}
	
	
	public void run(){
		
		String palabraActual;
		
		for(int i=id; i<lineas.size(); i+=numHebras){
			String[] palabras = lineas.get( i ).split( "\\W+" );
		    for( int j = 0; j < palabras.length; j++ ) {
		    	palabraActual = palabras[ j ].trim();
		        if( palabraActual.length() > 0 ) {
		        	Ejercicio.contabilizaPalabra( almacenPalabras, palabraActual );
		        }
		    }
		 }
	}
}

class MiHebra_5 extends Thread{
	private int miId;
	private ConcurrentHashMap<String, Integer> chmCuentaPalabras;
	private Vector<String> vectorLineas;
	private int numHebras;
	private String palabraActual;
	public MiHebra_5(int numHebras, int i, ConcurrentHashMap<String, Integer> chmCuentaPalabras, Vector<String> vectorLineas) {
		// TODO Auto-generated constructor stub
		this.numHebras = numHebras;
		this.miId = i;
		this.chmCuentaPalabras = chmCuentaPalabras;
		this.vectorLineas = vectorLineas;
	}
	public void run(){
		for(int i = miId; i < vectorLineas.size(); i+=numHebras ) {
			String[] palabras = vectorLineas.get( i ).split( "\\W+" );
			for( int j = 0; j < palabras.length; j++ ) {
				palabraActual = palabras[ j ].trim();
				if( palabraActual.length() > 0 ) {
					Ejercicio.contabilizaPalabra5(chmCuentaPalabras, palabraActual);
				}
			}
		}
	}
}

class MiHebra_6 extends Thread{
	private int miId;
	private ConcurrentHashMap<String, Integer> chmCuentaPalabras;
	private Vector<String> vectorLineas;
	private int numHebras;
	private String palabraActual;
	public MiHebra_6(int numHebras, int i, ConcurrentHashMap<String, Integer> chmCuentaPalabras, Vector<String> vectorLineas) {
		// TODO Auto-generated constructor stub
		this.numHebras = numHebras;
		this.miId = i;
		this.chmCuentaPalabras = chmCuentaPalabras;
		this.vectorLineas = vectorLineas;
	}
	public void run(){
		for(int i = miId; i < vectorLineas.size(); i+=numHebras ) {
			String[] palabras = vectorLineas.get( i ).split( "\\W+" );
			for( int j = 0; j < palabras.length; j++ ) {
				palabraActual = palabras[ j ].trim();
				if( palabraActual.length() > 0 ) {
					Ejercicio.contabilizaPalabra5(chmCuentaPalabras, palabraActual);
				}
			}
		}
	}
}