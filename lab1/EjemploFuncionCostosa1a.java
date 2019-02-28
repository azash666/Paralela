//package lab1;
// ============================================================================
class EjemploFuncionCostosa1a {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int     n, numHebras;
    long    t1, t2;
    double  tt, sumaX, sumaY;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <tamanyo>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      n         = Integer.parseInt( args[ 1 ] );
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      n         = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    // Crea los vectores.
    double vectorX[] = new double[ n ];
    double vectorY[] = new double[ n ];
    

    
    //Implementacion secuencial.
    
    inicializaVectorX( vectorX );
    inicializaVectorY( vectorY );
    t1 = System.nanoTime();
    for( int i = 0; i < n; i++ ) {
      vectorY[ i ] = evaluaFuncion( vectorX[ i ] );
    }
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Programación secuencial-----------------------------------------"  );
    System.out.println( "Tiempo secuencial (seg.):                    " + tt );
    //// imprimeResultado( vectorX, vectorY );
    
    // Comprueba el resultado. 
    sumaX = sumaVector( vectorX );
    sumaY = sumaVector( vectorY );
    System.out.println( "Suma del vector X:          " + sumaX );
    System.out.println( "Suma del vector Y:          " + sumaY );
    System.out.println( "Fin del programa secuencial." );
    
    //Implementación paralela con distribución cíclica
    
    inicializaVectorX( vectorX );
    inicializaVectorY( vectorY );

    t1 = System.nanoTime();
    //Creamos el vector de hebras y las arrancamos
    MiHebraCurranta[] vhebras = new MiHebraCurranta[numHebras];
    for(int i = 0; i<numHebras; i++){
    	vhebras[i] = new MiHebraCurranta(i,numHebras,n,vectorX,vectorY);
 		vhebras[i].start();

    }

  //ponemos otro bucle for para que el programa principal se espere que terminen todas las hebras
    for(int i = 0; i<numHebras; i++){
    	try{
    		vhebras[i].join();
    	}catch(InterruptedException ex){
    		ex.printStackTrace();
    	}
    }
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Programación paralelo-----------------------------------------"  );
    System.out.println( "Tiempo paralelo (seg.):                    " + tt );

    sumaX = sumaVector( vectorX );
    sumaY = sumaVector( vectorY );
    System.out.println( "Suma del vector X:          " + sumaX );
    System.out.println( "Suma del vector Y:          " + sumaY );
    System.out.println( "Fin del programa paralelo." );
  }

    
  

  // --------------------------------------------------------------------------
  static void inicializaVectorX( double vectorX[] ) {
    if( vectorX.length == 1 ) {
      vectorX[ 0 ] = 0.0;
    } else {
      for( int i = 0; i < vectorX.length; i++ ) {
        vectorX[ i ] = 10.0 * ( double ) i / ( ( double ) vectorX.length - 1 );
      }
    }
  }

  // --------------------------------------------------------------------------
  static void inicializaVectorY( double vectorY[] ) {
    for( int i = 0; i < vectorY.length; i++ ) {
      vectorY[ i ] = 0.0;
    }
  }

  // --------------------------------------------------------------------------
  static double sumaVector( double vector[] ) {
    double  suma = 0.0;
    for( int i = 0; i < vector.length; i++ ) {
      suma += vector[ i ];
    }
    return suma;
  }

  // --------------------------------------------------------------------------
  static double evaluaFuncion( double x ) {
    return Math.sin( Math.exp( -x ) + Math.log( 1 + x ) );
  }

  // --------------------------------------------------------------------------
  static void imprimeVector( double vector[] ) {
    for( int i = 0; i < vector.length; i++ ) {
      System.out.println( " vector[ " + i + " ] = " + vector[ i ] );
    }
  }

  // --------------------------------------------------------------------------
  static void imprimeResultado( double vectorX[], double vectorY[] ) {
    for( int i = 0; i < Math.min( vectorX.length, vectorY.length ); i++ ) {
      System.out.println( "  i: " + i + 
                          "  x: " + vectorX[ i ] +
                          "  y: " + vectorY[ i ] );
    }
  }
} 




//===================================HebraCurranta===================================

class MiHebraCurranta extends Thread{
	int miId, numHebras, n;
	double vectorX[], vectorY[];

	static double evaluaFuncion( double x ) {
		return Math.sin( Math.exp( -x ) + Math.log( 1 + x ) );
	}

	public MiHebraCurranta(int miId, int numHebras, int n,double vectorX [],double vectorY []){
		this.miId = miId;
		this.numHebras = numHebras ;
		this.n = n ;
		this.vectorX = vectorX ;
		this.vectorY = vectorY ;


	}


	@Override
	public void run(){
		for(int i = miId;i<n;i+=numHebras){
			vectorY[ i ] = evaluaFuncion( vectorX[ i ] );
			//System.out.println("Hebra:"+miId+" imprimo número Y: " +  vectorY[ i ]);

		}
	}
}
