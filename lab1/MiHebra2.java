//package lab1;

public class MiHebra2 extends Thread {
	private int miId;
	private int ini, fin;
	public MiHebra2(int id, int ini, int fin) {
		this.miId = id;
		this.ini=ini;
		this.fin = fin;
	}
	
	public void run() {
		for (int i=ini; i<fin; i++) {
			System.out.println(i);
		}
	}

}
