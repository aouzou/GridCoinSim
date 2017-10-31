import java.util.ArrayList;

public class Main {
	
	public static ArrayList<Master_Node> master_nodes;
	public static ArrayList<Miner_Node> miner_nodes; 
	
	static int num_master_nodes = 2;
	static int num_miner_nodes = 10;
	static int tot_jobs_done = 0;
	static long prev_time = System.currentTimeMillis();
	static int prev_blockchain_length = 0;
	static double txs;
	static int desired_txs = 5;
	static int blocks_dumped = 0;

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
				Thread.sleep((int)(Math.random()*10000/num_master_nodes));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			m.start();
		}
		for(Miner_Node m: miner_nodes){
			
			try {
				Thread.sleep((int)(Math.random()*20000/num_miner_nodes));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			m.start();
		}
		System.out.println("Nodes Created");
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
			
			long time = System.currentTimeMillis();
			long dt = time - prev_time;

			if(dt > 10000){
				int tx = chain_size - prev_blockchain_length;
				txs = (((double)tx)/((double)(dt/1000)));
				
				prev_blockchain_length = chain_size;
				prev_time = time;
			}
			
			if(tot_jobs_done > 0){
			
			int percentage = (100*chain_size)/tot_jobs_done;
			System.out.println("");
			System.out.println("Transactions per Second is: " + txs);
			System.out.println("");
			System.out.println("Percentage of blocks added is: " + percentage + "%");
			System.out.println("");
			System.out.println("Length of chain is: " + chain_size);
			System.out.println("");
			System.out.println("Number of nodes to be added: " + master_nodes.get(0).to_add.size());
			System.out.println("");
			System.out.println("Blocks Dumped: " + blocks_dumped);
			}
		}
		
	}

}
