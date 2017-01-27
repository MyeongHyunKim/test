package dpit;

public class ConnRunEx {

	public static void main(String[] args) {
		Thread th = new Thread(new conn());
		
		th.start();
	}
}