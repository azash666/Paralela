package lab7;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;



class MiRun implements Runnable{
	
	LinkedBlockingQueue<Tarea> cola;
	PuebloMaximaMinima maxMin;
	String fecha;
	
	public MiRun(LinkedBlockingQueue<Tarea> colaTareas,PuebloMaximaMinima maxMin, String fecha){
		this.cola = colaTareas;
		this.maxMin = maxMin;
		this.fecha = fecha;
	}
	
	public void run(){
		try {
			Tarea t = cola.take();
			while(t.esVeneno == false){
				Ejercicio.ProcesaPueblo(fecha, t.codigo, maxMin, false);
				t = cola.take();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}


class Tarea{
	
	boolean esVeneno;
	int codigo;
	
	
	
	public Tarea(int cod, boolean venenoso){
		this.esVeneno = venenoso;
		this.codigo = cod;
	}
	
	
}


class ThreadPoolTarea implements Runnable{
	
	int codPueblo;
	String fecha;
	PuebloMaximaMinima MaxMin;
	
	public ThreadPoolTarea(int codPueblo, String fecha, PuebloMaximaMinima MaxMin){
		this.codPueblo= codPueblo;
		this.fecha=fecha;
		this.MaxMin=MaxMin;
	}
	
	public void run(){
		
		Ejercicio.ProcesaPueblo(fecha, codPueblo, MaxMin, false);
		
	}
}

class ThreadPoolTareaCallable implements Callable{
	
	int codPueblo;
	String fecha;
	
	public ThreadPoolTareaCallable(int codPueblo, String fecha){
		this.codPueblo= codPueblo;
		this.fecha=fecha;
	}

	@Override
	public PuebloMaximaMinima call() throws Exception {
		// TODO Auto-generated method stub
		PuebloMaximaMinima maxMin = new PuebloMaximaMinima();
		Ejercicio.ProcesaPueblo(fecha, codPueblo, maxMin, false);
		return maxMin;
	}
}




class Ejercicio {
  public static void main(String[] args) {
    int                numHebras, codProvincia, desp;
    long               t1, t2, tt[];
    double             ts, tp, ttpA, ttpB;
    PuebloMaximaMinima MaxMin;
    LinkedBlockingQueue<Tarea> colaTareas = null;
    
    
    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 3 ) {
      System.out.println( "ERROR: numero de argumentos incorrecto.");
      System.out.println( "Uso: java programa <numHebras> <provincia> <desplazamiento>" );
      System.exit( -1 );
    }
    try {
      numHebras    = Integer.parseInt( args[ 0 ] );
      codProvincia = Integer.parseInt( args[ 1 ] );
      desp         = Integer.parseInt( args[ 2 ] );
    } catch( NumberFormatException ex ) {
      numHebras    = -1;
      codProvincia = -1;
      desp         = -1;
      System.out.println( "ERROR: Numero de entrada incorrecto." );
      System.exit( -1 );
    }
    if (numHebras <= 0) {
      System.out.println( "ERROR: El numero de Hebras debe ser un numero entero mayor que 0." );
      System.exit( -1 );
    }
    if ((codProvincia < 1) || (codProvincia > 50)) {
      System.out.println( "ERROR: El codigo de la provincia debe ser un numero entero " +
                          "comprendido entre 1 y 50." );
      System.exit( -1 );
    }
    if ((desp < 0) || (desp > 6)) {
      System.out.println( "ERROR: El desplazamiento debe ser un numero entero comprendido " +
                          "entre 0 y 6." );
      System.exit( -1 );
    }
    
    System.out.println();
    System.out.println( "Obtiene el pueblo de una provincia con mayor diferencia " + 
                        "de temperatura." );
    
    // Seleccion del dia elegido
    String fecha;
    Calendar c = Calendar.getInstance();
    Integer dia, mes, anyo;
    
    c.add(Calendar.DAY_OF_MONTH, desp);
    dia = c.get(Calendar.DATE);
    mes = c.get(Calendar.MONTH) + 1;
    anyo = c.get(Calendar.YEAR);
    
    fecha = String.format("%02d", anyo) + "-" + String.format("%02d", mes) + "-" +
            String.format("%02d", dia);
    System.out.println(fecha);
    
    //
    // Implementacion secuencial sin temporizar.
    //
    /*
    MaxMin = new PuebloMaximaMinima();
    obtenMayorDiferencia_SecuencialAFichero (fecha, codProvincia, MaxMin);
    System.out.println( "  Pueblo: " + MaxMin.damePueblo() + " , Maxima = " +
                        MaxMin.dameTemperaturaMaxima() + " , Minima = " +
                        MaxMin.dameTemperaturaMinima() );
    */
    //
    // Implementacion secuencial.
    //
    System.out.println();
    t1 = System.nanoTime();
    MaxMin = new PuebloMaximaMinima();
    obtenMayorDiferencia_SecuencialDeFichero (fecha, codProvincia, MaxMin);
    t2 = System.nanoTime();
    ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion secuencial.                           " );
    System.out.println( " Tiempo(s): " + ts );
    System.out.println( "  Pueblo: " + MaxMin.damePueblo() + " , Maxima = " +
                           MaxMin.dameTemperaturaMaxima() + " , Minima = " +
                           MaxMin.dameTemperaturaMinima() );

  
  
	 //
	 // Implementacion paralela..
	 //
	 System.out.println();
	 t1 = System.nanoTime();
	 Thread hebras[] = new Thread[numHebras];
	 colaTareas = new LinkedBlockingQueue<Tarea>();
	 MaxMin = new PuebloMaximaMinima();
	 obtenMayorDiferencia_SecuencialDeFicheroParalelo(colaTareas, numHebras);
	 for(int i=0; i<numHebras; i++){
		 hebras[i] = new Thread(new MiRun(colaTareas, MaxMin,fecha));
		 hebras[i].start();
	 }
	 for(int i=0; i<numHebras; i++){
		 try {
			hebras[i].join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 t2 = System.nanoTime();
	 tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
	 System.out.println( "\nImplementacion paralela.Ejercicio1" );
	    System.out.println( " Tiempo(s): " + tp );
	    System.out.println( " Speed-up: " + ts/tp );
	    System.out.println( "  Pueblo: " + MaxMin.damePueblo() + " , Maxima = " +
	                           MaxMin.dameTemperaturaMaxima() + " , Minima = " +
	                           MaxMin.dameTemperaturaMinima() );
  
  
  
	    //
		 // Implementacion con ThreadPool awaitTermination..
		 //
	  	 System.out.println();
		 t1 = System.nanoTime();
		 MaxMin = new PuebloMaximaMinima();
		 obtenMayorDiferencia_SecuencialDeFicheroThreadPool(numHebras, fecha, MaxMin);
		
		 
		 t2 = System.nanoTime();
		 ttpA = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
		 System.out.println( "\nImplementacion con threadPool isTerminated().Ejercicio  2" ); 
		 System.out.println( " Tiempo(s): " + ttpA );
		 System.out.println( " Speed-up: " + ts/ttpA );
		 System.out.println( "  Pueblo: " + MaxMin.damePueblo() + " , Maxima = " +
		                           MaxMin.dameTemperaturaMaxima() + " , Minima = " +
		                           MaxMin.dameTemperaturaMinima() );  
	    
	    
	    
	        
  	 //
	 // Implementacion con ThreadPool awaitTermination..
	 //
  	 System.out.println();
	 t1 = System.nanoTime();
	 MaxMin = new PuebloMaximaMinima();
	 
	 obtenMayorDiferencia_SecuencialDeFicheroThreadPoolAwait(numHebras, fecha, MaxMin);
	
	 t2 = System.nanoTime();
	 ttpB = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
	 System.out.println( "\nImplementacion con threadPool await. Ejercicio 3" );
	 System.out.println( " Tiempo(s): " + ttpB );
	 System.out.println( " Speed-up: " + ts/ttpB );
	 System.out.println( "  Pueblo: " + MaxMin.damePueblo() + " , Maxima = " +
	                           MaxMin.dameTemperaturaMaxima() + " , Minima = " +
	                           MaxMin.dameTemperaturaMinima() );
	 

     
  	 //
	 // Implementacion con ThreadPool Callable.
	 //
  	 System.out.println();
	 t1 = System.nanoTime();
	 MaxMin = new PuebloMaximaMinima();
	 
	 obtenMayorDiferencia_SecuencialDeFicheroThreadPoolCallable(numHebras, fecha, MaxMin);
	
	 t2 = System.nanoTime();
	 ttpB = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
	 System.out.println( "\nImplementacion con threadPool callable. Ejercicio 4" );
	 System.out.println( " Tiempo(s): " + ttpB );
	 System.out.println( " Speed-up: " + ts/ttpB );
	 System.out.println( "  Pueblo: " + MaxMin.damePueblo() + " , Maxima = " +
	                           MaxMin.dameTemperaturaMaxima() + " , Minima = " +
	                           MaxMin.dameTemperaturaMinima() );
	 
	 
}
  
  
  
  
  // --------------------------------------------------------------------------
  public static void obtenMayorDiferencia_SecuencialAFichero (String fecha, int codProvincia,
                                                              PuebloMaximaMinima MaxMin) {
    FileWriter  fichero = null;
    PrintWriter pw      = null;
    
    // Verifica todas los codigos de pueblos y escribe el fichero  "codPueblos.txt"
    try
    {
      // Apertura del fichero y creacion de FileWriter para poder
      // hacer una escritura comoda (disponer del metodo println()).
      fichero = new FileWriter("codPueblos.txt");
      pw = new PrintWriter(fichero);
    
      for (int i=codProvincia*1000; i<(codProvincia+1)*1000; i++){
        if (ProcesaPueblo(fecha, i, MaxMin, false) == true) {
          pw.println(i);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        // Se aprovecha el finally para asegurar el cierre del fichero,
        // tanto si todo va bien como si salta una excepcion.
        if (null != fichero)
          fichero.close();
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
  }

  // --------------------------------------------------------------------------
 

  public static void obtenMayorDiferencia_SecuencialDeFichero (String fecha, int codProvincia,
		  PuebloMaximaMinima MaxMin) {
	  File           archivo = null;
	  FileReader     fr      = null;
	  BufferedReader br      = null;

	  // Procesa el fichero "codPueblos.txt"
	  try
	  {
		  // Apertura del fichero y creacion de BufferedReader para poder
		  // hacer una lectura comoda (disponer del metodo readLine()).
		  archivo = new File ("codPueblos.txt");
		  fr = new FileReader (archivo);
		  br = new BufferedReader(fr);

		  String linea;
		  while( ( linea = br.readLine() ) != null ) {
			  int codPueblo = Integer.parseInt(linea);
			  ProcesaPueblo(fecha, codPueblo, MaxMin, false);
		  }

	  } catch (Exception e) {
		  e.printStackTrace();
	  }finally{
		  // Se aprovecha el finally para asegurar el cierre del fichero,
		  // tanto si todo va bien como si salta una excepcion.
		  try{
			  if( null != fr ){
				  fr.close();
			  }
		  }catch (Exception e2){
			  e2.printStackTrace();
		  }
	  }
  }
  
  
  
  public static void obtenMayorDiferencia_SecuencialDeFicheroParalelo (LinkedBlockingQueue<Tarea> cola, int numHebras) {
    File           archivo = null;
    FileReader     fr      = null;
    BufferedReader br      = null;

    // Procesa el fichero "codPueblos.txt"
    try
    {
      // Apertura del fichero y creacion de BufferedReader para poder
      // hacer una lectura comoda (disponer del metodo readLine()).
      archivo = new File ("codPueblos.txt");
      fr = new FileReader (archivo);
      br = new BufferedReader(fr);

      String linea;
      while( ( linea = br.readLine() ) != null ) {
    	  int codPueblo = Integer.parseInt(linea);
    	  Tarea t = new Tarea(codPueblo, false);
    	  cola.put(t);
    
      }
      for(int i=0; i<numHebras; i++){
    	  Tarea t = new Tarea(0, true);
    	  cola.put(t);
      }
    	  
    } catch (Exception e) {
      e.printStackTrace();
    }finally{
      // Se aprovecha el finally para asegurar el cierre del fichero,
      // tanto si todo va bien como si salta una excepcion.
      try{
        if( null != fr ){
          fr.close();
        }
      }catch (Exception e2){
        e2.printStackTrace();
      }
    }
  }
 
  
  public static void obtenMayorDiferencia_SecuencialDeFicheroThreadPool (int numHebras,
		  											String fecha, PuebloMaximaMinima MaxMin ) {
	    File           archivo = null;
	    FileReader     fr      = null;
	    BufferedReader br      = null;
	    ExecutorService threadPool = null;
	    
		
	    // Procesa el fichero "codPueblos.txt"
	    try
	    {
	      // Apertura del fichero y creacion de BufferedReader para poder
	      // hacer una lectura comoda (disponer del metodo readLine()).
	      archivo = new File ("codPueblos.txt");
	      fr = new FileReader (archivo);
	      br = new BufferedReader(fr);
	      threadPool = Executors.newFixedThreadPool(numHebras);

	      String linea;
	      while( ( linea = br.readLine() ) != null ) {
	    	  int codPueblo = Integer.parseInt(linea);
	    	  ThreadPoolTarea t = new ThreadPoolTarea(codPueblo, fecha, MaxMin);
	    	  threadPool.execute(t);
	      }
	     threadPool.shutdown();
	     while(!threadPool.isTerminated()){}
	    	  
	    } catch (Exception e) {
	      e.printStackTrace();
	    }finally{
	      // Se aprovecha el finally para asegurar el cierre del fichero,
	      // tanto si todo va bien como si salta una excepcion.
	      try{
	        if( null != fr ){
	          fr.close();
	        }
	      }catch (Exception e2){
	        e2.printStackTrace();
	      }
	    }
	  }
	 
  public static void obtenMayorDiferencia_SecuencialDeFicheroThreadPoolAwait (int numHebras,
		  String fecha, PuebloMaximaMinima MaxMin ) {
	  File           archivo = null;
	  FileReader     fr      = null;
	  BufferedReader br      = null;
	  ExecutorService threadPool = null;


	  // Procesa el fichero "codPueblos.txt"
	  try
	  {
		  // Apertura del fichero y creacion de BufferedReader para poder
		  // hacer una lectura comoda (disponer del metodo readLine()).
		  archivo = new File ("codPueblos.txt");
		  fr = new FileReader (archivo);
		  br = new BufferedReader(fr);
		  threadPool = Executors.newFixedThreadPool(numHebras);

		  String linea;
		  while( ( linea = br.readLine() ) != null ) {
			  int codPueblo = Integer.parseInt(linea);
			  ThreadPoolTarea t = new ThreadPoolTarea(codPueblo, fecha, MaxMin);
			  threadPool.execute(t);
		  }
		  threadPool.shutdown();
		  while(!threadPool.awaitTermination(1L, TimeUnit.MILLISECONDS)){}

	  } catch (Exception e) {
		  e.printStackTrace();
	  }finally{
		  // Se aprovecha el finally para asegurar el cierre del fichero,
		  // tanto si todo va bien como si salta una excepcion.
		  try{
			  if( null != fr ){
				  fr.close();
			  }
		  }catch (Exception e2){
			  e2.printStackTrace();
		  }
	  }
  }

  public static void obtenMayorDiferencia_SecuencialDeFicheroThreadPoolCallable (int numHebras,
		  String fecha, PuebloMaximaMinima MaxMin ) {
	  File           archivo = null;
	  FileReader     fr      = null;
	  BufferedReader br      = null;
	  ExecutorService threadPool = null;


	  // Procesa el fichero "codPueblos.txt"
	  try
	  {
		  // Apertura del fichero y creacion de BufferedReader para poder
		  // hacer una lectura comoda (disponer del metodo readLine()).
		  archivo = new File ("codPueblos.txt");
		  fr = new FileReader (archivo);
		  br = new BufferedReader(fr);
		  threadPool = Executors.newFixedThreadPool(numHebras);
		  ArrayList<Future<PuebloMaximaMinima>> alf = new ArrayList<Future<PuebloMaximaMinima>>();
		  String linea;
		  while( ( linea = br.readLine() ) != null ) {
			  int codPueblo = Integer.parseInt(linea);
			  ThreadPoolTareaCallable tarea = new ThreadPoolTareaCallable(codPueblo, fecha);
			  alf.add(threadPool.submit(tarea));
		  }
		  threadPool.shutdown();
		  for(Future<PuebloMaximaMinima> maxMinFuture: alf){
			  PuebloMaximaMinima maxMinLocal = maxMinFuture.get();
			  MaxMin.actualizaMaxMin (maxMinLocal.poblacion, maxMinLocal.codigo, maxMinLocal.max, maxMinLocal.min);
		  }

	  } catch (Exception e) {
		  e.printStackTrace();
	  }finally{
		  // Se aprovecha el finally para asegurar el cierre del fichero,
		  // tanto si todo va bien como si salta una excepcion.
		  try{
			  if( null != fr ){
				  fr.close();
			  }
		  }catch (Exception e2){
			  e2.printStackTrace();
		  }
	  }
  }

  
  
  // --------------------------------------------------------------------------
  public static boolean ProcesaPueblo (String fecha, int codPueblo, PuebloMaximaMinima MaxMin,
                                       boolean imprime) {
    URL            url;
    InputStream    is = null;
    BufferedReader br;
    String         line, poblacion = new String (), provincia = new String ();
    int            state, num[]=new int[2];
    boolean        res = false;
    
    // Procesamiento de la informacion XML asociada a codPueblo
    // Actualizacion de MaxMin de acuerdo a los valores obtenidos
    try {
      String urlStr = "http://www.aemet.es/xml/municipios/localidad_" +
                      String.format("%05d",codPueblo)+ ".xml";
      url = new URL(urlStr);
      is  = url.openStream();  // throws an IOException
      br  = new BufferedReader(new InputStreamReader(is));
      if (imprime) System.out.println(urlStr);

      state = 0;      
      while (((line = br.readLine()) != null) && (state < 6)) {
        //        System.out.println (line);
        if ((state == 0) && (line.contains ("nombre"))) {
          poblacion=line.split(">")[1].split("<")[0].split("/")[0];
          state++;
        } else if ((state == 1) && (line.contains ("provincia"))) {
          provincia=line.split(">")[1].split("<")[0].split("/")[0];
          state++;
        } else if ((state == 2) && (line.contains (fecha))) {
          state++;
        } else if ((state == 3) && (line.contains ("temperatura"))) {
          state++;
        } else if ((state > 3) && ((line.contains ("maxima")) || (line.contains ("minima")))) {
          num[state-4] = Integer.parseInt (line.split(">")[1].split("<")[0]);
          state++;
        }
      }
      // System.out.println("(" + codPueblo + ") " + poblacion + "(" + provincia + ") => " +
      //                    "(" + num[0] + " , " + num[1] + ")");
      MaxMin.actualizaMaxMin (poblacion, codPueblo, num[0], num[1]);
      res = true;
    } catch (MalformedURLException mue) {
      mue.printStackTrace();
    } catch (IOException ioe) {
      //      ioe.printStackTrace();
    } finally {
      try {
        if (is != null) is.close();
      } catch (IOException ioe) {
        // nothing to see here
      }
    }
    return res;
  }
}

// ============================================================================
class PuebloMaximaMinima {
// ============================================================================
  String poblacion;
  int    codigo, max, min;
  
  
  // --------------------------------------------------------------------------
  public PuebloMaximaMinima() {
    poblacion = null;
    codigo    = -1;
    max       = -1;
    min       = -1;
  }
  
  // --------------------------------------------------------------------------
  public synchronized void actualizaMaxMin( String poblacion, int codigo, int max, int min ) {
    if ((this.poblacion == null) || ((this.max-this.min) < (max-min)) ||
        (((this.max-this.min) == (max-min)) && (this.min > min)) ||
        (((this.max-this.min) == (max-min)) && (this.min == min) && (this.codigo > codigo))
        ) {
      //      (((this.max-this.min) == (max-min)) && (this.max < max))) {
      this.poblacion = poblacion;
      this.codigo = codigo;
      this.max = max;
      this.min = min;
    }
  }
  
  // --------------------------------------------------------------------------
  public synchronized String damePueblo() {
    return this.poblacion + "(" + this.codigo + ")";
  }
  
  // --------------------------------------------------------------------------
  public synchronized int dameCodigo() {
    return this.codigo;
  }
  
  // --------------------------------------------------------------------------
  public synchronized int dameTemperaturaMaxima() {
    return this.max;
  }
  
  // --------------------------------------------------------------------------
  public synchronized int dameTemperaturaMinima() {
    return this.min;
  }
}


