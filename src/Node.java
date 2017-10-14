


public class Node extends Thread{
	
	int id;
	protected Blockchain blockchain;
	boolean running = false;

	
	public void run(){
		running = true;
		while(running){
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Hello from node: " + id);
		}
	}
}
