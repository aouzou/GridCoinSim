import java.util.ArrayList;

public class Main {
	
	public static ArrayList<Master_Node> master_nodes;
	public static ArrayList<Miner_Node> miner_nodes; 
	
	static int num_master_nodes = 10;
	static int num_miner_nodes = 200;
	static int tot_jobs_done = 0;

	public static void main(String[] args) {
		
		master_nodes = new ArrayList<Master_Node>();
		miner_nodes = new ArrayList<Miner_Node>();
		
		for(int i = 0; i < num_master_nodes; i++){
			master_nodes.add(new Master_Node(i));
		}
		
		for(int i = 0; i < num_miner_nodes; i++){
			miner_nodes.add(new Miner_Node(i+num_master_nodes));
		}
		
		for(Master_Node m: master_nodes){
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			m.start();
		}
		for(Miner_Node m: miner_nodes){
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			m.start();
		}
		
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int chain_size = 0;
			for(Master_Node m: Main.master_nodes){
				if(m.blockchain.length() > chain_size){
					chain_size = m.blockchain.length();
				}
			}
			if(tot_jobs_done > 0){
			int percentage = (100*chain_size)/tot_jobs_done;
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("Percentage of blocks added is: " + percentage + "%");
			System.out.println("Length of chain is: " + chain_size);
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			}
		}
		
	}

}
