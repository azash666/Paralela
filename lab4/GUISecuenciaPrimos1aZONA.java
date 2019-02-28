package lab4;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

// ===========================================================================
public class GUISecuenciaPrimos1aZONA {
	// ===========================================================================
	JFrame      container;
	JPanel      jpanel;
	JTextField  txfMensajes;
	JButton     btnComienzaSecuencia, btnCancelaSecuencia;
	JSlider     sldEspera;
	HebraCurrantaZONA hebraCurranta;
	ZonaIntercambio zonaTiempos;
	// -------------------------------------------------------------------------
	public static void main( String args[] ) {
		GUISecuenciaPrimos1aZONA gui = new GUISecuenciaPrimos1aZONA();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				gui.go();
			}
		});
	}

	// -------------------------------------------------------------------------
	public void go() {
		// Constantes.
		final int valorMaximo = 1000;
		final int valorMedio  = 500;
		zonaTiempos = new ZonaIntercambio();

		// Variables.
		JPanel  tempPanel;

		// Crea el JFrame principal.
		container = new JFrame( " **ZONA** GUI Secuencia de Primos 1a " );

		// Consigue el panel principal del Frame "container".
		jpanel = ( JPanel ) container.getContentPane();
		jpanel.setLayout( new GridLayout( 3, 1 ) );

		// Crea e inserta la etiqueta y el campo de texto para los mensajes.
		txfMensajes = new JTextField( 20 );
		txfMensajes.setEditable( false );
		tempPanel = new JPanel();
		tempPanel.setLayout( new FlowLayout() );
		tempPanel.add( new JLabel( "Secuencia: " ) );
		tempPanel.add( txfMensajes );
		jpanel.add( tempPanel );

		// Crea e inserta los botones de Comienza secuencia y Cancela secuencia.
		btnComienzaSecuencia = new JButton( "Comienza secuencia" );
		btnCancelaSecuencia = new JButton( "Cancela secuencia" );
		tempPanel = new JPanel();
		tempPanel.setLayout( new FlowLayout() );
		tempPanel.add( btnComienzaSecuencia );
		tempPanel.add( btnCancelaSecuencia );
		jpanel.add( tempPanel );

		// Crea e inserta el slider para controlar el tiempo de espera.
		sldEspera = new JSlider( JSlider.HORIZONTAL, 0, valorMaximo , valorMedio );
		tempPanel = new JPanel();
		tempPanel.setLayout( new BorderLayout() );
		tempPanel.add( new JLabel( "Tiempo de espera: " ) );
		tempPanel.add( sldEspera );
		jpanel.add( tempPanel );

		zonaTiempos.modificar(valorMedio);

		// Activa inicialmente los 2 botones.
		btnComienzaSecuencia.setEnabled( true );
		btnCancelaSecuencia.setEnabled( false );

		// Anyade codigo para procesar el evento del boton de Comienza secuencia.
		btnComienzaSecuencia.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				hebraCurranta = new HebraCurrantaZONA(txfMensajes, zonaTiempos);
				hebraCurranta.start();
				btnComienzaSecuencia.setEnabled(false);
				btnCancelaSecuencia.setEnabled(true);
			}
		} );

		// Anyade codigo para procesar el evento del boton de Cancela secuencia.
		btnCancelaSecuencia.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				hebraCurranta.finalizar();
				btnComienzaSecuencia.setEnabled(true);
				btnCancelaSecuencia.setEnabled(false);
			}
		} );
		// Anyade codigo para procesar el evento del slider " Espera " .
		sldEspera.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				JSlider sl = ( JSlider ) e.getSource();
				long tiempoIntervalo = 0L;
				if ( ! sl.getValueIsAdjusting() ) {
					zonaTiempos.modificar(( long ) sl.getValue());
				}
			}
		} );

		// Fija caracteristicas del container.
		container.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		container.pack();
		container.setResizable( false );
		container.setVisible( true );

		System.out.println( "% End of routine: go.\n" );
	}

	// -------------------------------------------------------------------------
	static boolean esPrimo( long num ) {
		boolean primo;
		if( num < 2 ) {
			primo = false;
		} else {
			primo = true;
			long i = 2;
			while( ( i < num )&&( primo ) ) {
				primo = ( num % i != 0 );
				i++;
			}
		}
		return( primo );
	}
}


class HebraCurrantaZONA extends Thread {


	JTextField cuadroTexto;
	volatile Boolean parar;
	ZonaIntercambio zona;

	HebraCurrantaZONA(JTextField texto, ZonaIntercambio zona){
		this.cuadroTexto = texto;
		this.parar = false;
		this.zona = zona;

	}
	public void run(){
		long num = 2;
		while(!parar){
			if(GUISecuenciaPrimos1a.esPrimo(num)){
				long copia = num;
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						cuadroTexto.setText(Long.toString(copia));
					}
				});
				try{
					Thread.sleep(zona.devolver());
				}catch(InterruptedException e){
					e.printStackTrace();
				}

			}
			num++;
		}
	}
	public void finalizar(){
		parar = true;
	}

}



class ZonaIntercambio{

	long tiempo;
	ZonaIntercambio(){}

	synchronized void modificar (long tiempo){
		this.tiempo = tiempo;
	}
	synchronized long devolver(){
		return tiempo;

	}
}

